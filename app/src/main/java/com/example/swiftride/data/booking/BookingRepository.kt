package com.example.swiftride.data.booking

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint
import kotlin.random.Random
import com.example.swiftride.data.routing.RoutingRepository
import com.example.swiftride.data.routing.RouteResult
import com.example.swiftride.data.routing.RouteProvider
import kotlinx.coroutines.flow.Flow
import com.example.swiftride.data.local.dao.RideDao
import com.example.swiftride.data.local.entities.RideEntity

interface BookingRepository {
    val bookingState: StateFlow<RideBookingState>
    fun confirmBooking(
        pickup: GeoPoint,
        destination: GeoPoint,
        pickupName: String = "Pickup",
        pickupAddress: String = "",
        destinationName: String = "Destination",
        destinationAddress: String = "",
        fare: Double = 0.0,
        rideCategory: String = "SwiftX",
        durationMinutes: Int = 10,
        scope: CoroutineScope
    )
    fun cancelBooking()
    fun clearBookingState()
    fun getRideHistory(): Flow<List<RideEntity>>
    fun getRideHistory(limit: Int): Flow<List<RideEntity>>
    suspend fun getRidesPaged(limit: Int, offset: Int): List<RideEntity>
    suspend fun saveRide(ride: RideEntity)
    suspend fun deleteRide(rideId: String)
}

class SimulatedBookingRepository(
    private val driverRepository: DriverRepository,
    private val routingRepository: RoutingRepository,
    private val rideDao: RideDao
) : BookingRepository {

    private val _bookingState = MutableStateFlow(RideBookingState())
    override val bookingState: StateFlow<RideBookingState> = _bookingState.asStateFlow()

    private var simulationJob: Job? = null

    // Temporarily stored details to persist completed/cancelled rides correctly
    private var currentRideId: String? = null
    private var currentPickup: GeoPoint? = null
    private var currentDestination: GeoPoint? = null
    private var currentPickupName: String = ""
    private var currentPickupAddress: String = ""
    private var currentDestinationName: String = ""
    private var currentDestinationAddress: String = ""
    private var currentFare: Double = 0.0
    private var currentRideCategory: String = "SwiftX"
    private var currentDurationMinutes: Int = 10

    override fun confirmBooking(
        pickup: GeoPoint,
        destination: GeoPoint,
        pickupName: String,
        pickupAddress: String,
        destinationName: String,
        destinationAddress: String,
        fare: Double,
        rideCategory: String,
        durationMinutes: Int,
        scope: CoroutineScope
    ) {
        currentRideId = java.util.UUID.randomUUID().toString()
        currentPickup = pickup
        currentDestination = destination
        currentPickupName = pickupName
        currentPickupAddress = pickupAddress
        currentDestinationName = destinationName
        currentDestinationAddress = destinationAddress
        currentFare = fare
        currentRideCategory = rideCategory
        currentDurationMinutes = durationMinutes

        simulationJob?.cancel()
        simulationJob = scope.launch(Dispatchers.Default) {
            // 1. Searching state
            _bookingState.value = RideBookingState(status = RideState.Searching)
            
            // Search delay between 3 to 6 seconds
            val searchTime = Random.nextLong(3000, 6000)
            delay(searchTime)

            // 2. Driver Matching
            val nearestDriver = driverRepository.findNearestDriver(pickup)
            if (nearestDriver == null) {
                _bookingState.value = RideBookingState(
                    status = RideState.RideCancelled,
                    errorMessage = "No drivers available nearby."
                )
                return@launch
            }

            // Mark driver as busy
            driverRepository.markDriverAsBusy(nearestDriver.id)

            // 3. Driver Assigned state
            // Query OSRM to get real road distance and ETA from driver to pickup
            var driverToPickupPoints: List<GeoPoint>? = null
            var driverToPickupDistanceMeters = nearestDriver.distanceFromPickupMeters
            var driverToPickupDurationSeconds = nearestDriver.estimatedArrivalMinutes * 60.0

            val driverRouteResult = try {
                routingRepository.getRoute(nearestDriver.currentCoordinate, pickup, RouteProvider.OSRM)
            } catch (e: Exception) {
                null
            }

            if (driverRouteResult is RouteResult.Success) {
                driverToPickupPoints = driverRouteResult.routeData.decodedPolylinePoints
                driverToPickupDistanceMeters = driverRouteResult.routeData.distanceMeters
                driverToPickupDurationSeconds = driverRouteResult.routeData.durationSeconds
            }

            val finalEtaMinutes = (driverToPickupDurationSeconds / 60.0).toInt().coerceAtLeast(1)

            _bookingState.value = RideBookingState(
                status = RideState.DriverAssigned,
                assignedDriver = nearestDriver,
                currentDriverLocation = nearestDriver.currentCoordinate,
                etaMinutes = finalEtaMinutes,
                distanceMeters = driverToPickupDistanceMeters
            )
            delay(1500)

            // 4. Driver Accepted state
            _bookingState.value = _bookingState.value.copy(
                status = RideState.DriverAccepted
            )
            delay(1500)

            // 5. Driver Arriving state - simulate movement towards pickup
            _bookingState.value = _bookingState.value.copy(
                status = RideState.DriverArriving
            )

            val totalSteps = 10
            val delayTimeMs = 1200L

            if (driverToPickupPoints != null && driverToPickupPoints.isNotEmpty()) {
                val sampledPoints = sampleRoutePoints(driverToPickupPoints, totalSteps)
                for (step in 1..totalSteps) {
                    if (!isActive) return@launch
                    delay(delayTimeMs)

                    val currentPos = sampledPoints[step - 1]
                    val fractionRemaining = 1.0 - (step.toDouble() / totalSteps)
                    val remainingDistance = driverToPickupDistanceMeters * fractionRemaining
                    val remainingEta = (finalEtaMinutes * fractionRemaining).toInt().coerceAtLeast(1)

                    _bookingState.value = _bookingState.value.copy(
                        currentDriverLocation = currentPos,
                        distanceMeters = remainingDistance.coerceAtLeast(0.0),
                        etaMinutes = if (step == totalSteps) 0 else remainingEta
                    )
                }
            } else {
                // Fallback to straight-line interpolation
                val startLoc = nearestDriver.currentCoordinate
                val latStep = (pickup.latitude - startLoc.latitude) / totalSteps
                val lonStep = (pickup.longitude - startLoc.longitude) / totalSteps

                for (step in 1..totalSteps) {
                    if (!isActive) return@launch
                    delay(delayTimeMs)

                    val currentLat = startLoc.latitude + latStep * step
                    val currentLon = startLoc.longitude + lonStep * step
                    val currentPos = GeoPoint(currentLat, currentLon)

                    val fractionRemaining = 1.0 - (step.toDouble() / totalSteps)
                    val remainingDistance = nearestDriver.distanceFromPickupMeters * fractionRemaining
                    val remainingEta = (nearestDriver.estimatedArrivalMinutes * fractionRemaining).toInt().coerceAtLeast(1)

                    _bookingState.value = _bookingState.value.copy(
                        currentDriverLocation = currentPos,
                        distanceMeters = remainingDistance.coerceAtLeast(0.0),
                        etaMinutes = if (step == totalSteps) 0 else remainingEta
                    )
                }
            }

            // 6. Driver Reached Pickup
            _bookingState.value = _bookingState.value.copy(
                status = RideState.DriverReachedPickup,
                distanceMeters = 0.0,
                etaMinutes = 0
            )
            delay(2500)

            // 7. Ride Started - animate movement from pickup to destination
            _bookingState.value = _bookingState.value.copy(
                status = RideState.RideStarted,
                currentDriverLocation = pickup
            )

            // Query OSRM to get real road distance and ETA from pickup to destination
            var pickupToDestPoints: List<GeoPoint>? = null
            var tripDistanceMeters = 0.0
            var tripDurationSeconds = 0.0

            val tripRouteResult = try {
                routingRepository.getRoute(pickup, destination, RouteProvider.OSRM)
            } catch (e: Exception) {
                null
            }

            if (tripRouteResult is RouteResult.Success) {
                pickupToDestPoints = tripRouteResult.routeData.decodedPolylinePoints
                tripDistanceMeters = tripRouteResult.routeData.distanceMeters
                tripDurationSeconds = tripRouteResult.routeData.durationSeconds
            }

            val tripEtaMinutes = (tripDurationSeconds / 60.0).toInt().coerceAtLeast(1)

            if (pickupToDestPoints != null && pickupToDestPoints.isNotEmpty()) {
                val sampledPoints = sampleRoutePoints(pickupToDestPoints, totalSteps)
                for (step in 1..totalSteps) {
                    if (!isActive) return@launch
                    delay(delayTimeMs)

                    val currentPos = sampledPoints[step - 1]
                    val fractionRemaining = 1.0 - (step.toDouble() / totalSteps)
                    val remainingDistance = tripDistanceMeters * fractionRemaining
                    val remainingEta = (tripEtaMinutes * fractionRemaining).toInt().coerceAtLeast(1)

                    _bookingState.value = _bookingState.value.copy(
                        currentDriverLocation = currentPos,
                        distanceMeters = remainingDistance.coerceAtLeast(0.0),
                        etaMinutes = if (step == totalSteps) 0 else remainingEta
                    )
                }
            } else {
                // Fallback to straight-line interpolation
                val latStep = (destination.latitude - pickup.latitude) / totalSteps
                val lonStep = (destination.longitude - pickup.longitude) / totalSteps
                val fallbackDistance = calculateDistance(pickup, destination)
                val fallbackEtaMinutes = (fallbackDistance / 300.0).toInt().coerceAtLeast(1)

                for (step in 1..totalSteps) {
                    if (!isActive) return@launch
                    delay(delayTimeMs)

                    val currentLat = pickup.latitude + latStep * step
                    val currentLon = pickup.longitude + lonStep * step
                    val currentPos = GeoPoint(currentLat, currentLon)

                    val fractionRemaining = 1.0 - (step.toDouble() / totalSteps)
                    val remainingDistance = fallbackDistance * fractionRemaining
                    val remainingEta = (fallbackEtaMinutes * fractionRemaining).toInt().coerceAtLeast(1)

                    _bookingState.value = _bookingState.value.copy(
                        currentDriverLocation = currentPos,
                        distanceMeters = remainingDistance.coerceAtLeast(0.0),
                        etaMinutes = if (step == totalSteps) 0 else remainingEta
                    )
                }
            }

            // 8. Ride Completed
            val rideId = currentRideId ?: java.util.UUID.randomUUID().toString()
            val finalRide = RideEntity(
                id = rideId,
                pickupLatitude = pickup.latitude,
                pickupLongitude = pickup.longitude,
                pickupName = pickupName,
                pickupAddress = pickupAddress,
                destinationLatitude = destination.latitude,
                destinationLongitude = destination.longitude,
                destinationName = destinationName,
                destinationAddress = destinationAddress,
                status = RideState.RideCompleted.name,
                fare = fare,
                timestamp = System.currentTimeMillis(),
                driverId = nearestDriver.id,
                driverName = nearestDriver.name,
                driverPhone = null,
                distanceMeters = tripDistanceMeters,
                etaMinutes = 0,
                rideCategory = rideCategory,
                durationMinutes = durationMinutes
            )
            withContext(Dispatchers.IO) {
                rideDao.insertRide(finalRide)
            }

            _bookingState.value = _bookingState.value.copy(
                status = RideState.RideCompleted,
                currentDriverLocation = destination,
                distanceMeters = 0.0,
                etaMinutes = 0
            )
        }
    }

    override fun cancelBooking() {
        val current = _bookingState.value
        current.assignedDriver?.let {
            driverRepository.releaseDriver(it.id)
        }
        simulationJob?.cancel()

        // Persist cancelled ride information
        val pickup = currentPickup
        val destination = currentDestination
        val rideId = currentRideId ?: java.util.UUID.randomUUID().toString()
        if (pickup != null && destination != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val cancelledRide = RideEntity(
                    id = rideId,
                    pickupLatitude = pickup.latitude,
                    pickupLongitude = pickup.longitude,
                    pickupName = currentPickupName,
                    pickupAddress = currentPickupAddress,
                    destinationLatitude = destination.latitude,
                    destinationLongitude = destination.longitude,
                    destinationName = currentDestinationName,
                    destinationAddress = currentDestinationAddress,
                    status = RideState.RideCancelled.name,
                    fare = currentFare,
                    timestamp = System.currentTimeMillis(),
                    driverId = current.assignedDriver?.id,
                    driverName = current.assignedDriver?.name,
                    driverPhone = null,
                    distanceMeters = current.distanceMeters,
                    etaMinutes = current.etaMinutes,
                    rideCategory = currentRideCategory,
                    durationMinutes = currentDurationMinutes
                )
                rideDao.insertRide(cancelledRide)
            }
        }

        _bookingState.value = RideBookingState(status = RideState.RideCancelled)
    }

    override fun clearBookingState() {
        simulationJob?.cancel()
        _bookingState.value = RideBookingState(status = RideState.Idle)
    }

    override fun getRideHistory(): Flow<List<RideEntity>> = rideDao.getAllRides()

    override fun getRideHistory(limit: Int): Flow<List<RideEntity>> = rideDao.getRidesWithLimit(limit)

    override suspend fun getRidesPaged(limit: Int, offset: Int): List<RideEntity> {
        return withContext(Dispatchers.IO) {
            rideDao.getRidesPaged(limit, offset)
        }
    }

    override suspend fun saveRide(ride: RideEntity) {
        withContext(Dispatchers.IO) {
            rideDao.insertRide(ride)
        }
    }

    override suspend fun deleteRide(rideId: String) {
        withContext(Dispatchers.IO) {
            rideDao.deleteRideById(rideId)
        }
    }

    private fun sampleRoutePoints(points: List<GeoPoint>, numSamples: Int): List<GeoPoint> {
        if (points.isEmpty()) return emptyList()
        val samples = mutableListOf<GeoPoint>()
        for (i in 0 until numSamples) {
            val index = (i * (points.size - 1)) / (numSamples - 1).coerceAtLeast(1)
            samples.add(points[index.coerceIn(0, points.size - 1)])
        }
        return samples
    }

    private fun calculateDistance(start: GeoPoint, end: GeoPoint): Double {
        val r = 6371e3 // meters
        val phi1 = Math.toRadians(start.latitude)
        val phi2 = Math.toRadians(end.latitude)
        val deltaPhi = Math.toRadians(end.latitude - start.latitude)
        val deltaLambda = Math.toRadians(end.longitude - start.longitude)

        val a = kotlin.math.sin(deltaPhi / 2) * kotlin.math.sin(deltaPhi / 2) +
                kotlin.math.cos(phi1) * kotlin.math.cos(phi2) *
                kotlin.math.sin(deltaLambda / 2) * kotlin.math.sin(deltaLambda / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return r * c
    }
}
