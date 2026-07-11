package com.example.swiftride.data.booking

import org.osmdroid.util.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface DriverRepository {
    fun getAvailableDrivers(pickup: GeoPoint): List<SimulatedDriver>
    fun findNearestDriver(pickup: GeoPoint): SimulatedDriver?
    fun markDriverAsBusy(driverId: String)
    fun releaseDriver(driverId: String)
}

class SimulatedDriverRepository : DriverRepository {

    private val busyDrivers = mutableSetOf<String>()

    private val baseDrivers = listOf(
        SimulatedDriverTemplate("d1", "Ramesh Kumar", 4.8, "Suzuki Swift", "Swift VXI", "KA-01-MJ-1234", "Silver"),
        SimulatedDriverTemplate("d2", "Suresh Pillai", 4.7, "Toyota Etios", "Etios Liva", "KA-03-MK-5678", "White"),
        SimulatedDriverTemplate("d3", "Anita Sen", 4.9, "Hyundai i20", "i20 Asta", "KA-05-MN-9012", "Red"),
        SimulatedDriverTemplate("d4", "David D'Souza", 4.6, "Honda Amaze", "Amaze S", "KA-02-MP-3456", "Grey"),
        SimulatedDriverTemplate("d5", "Priya Nair", 4.95, "Tata Nexon", "Nexon XM", "KA-04-MQ-7890", "Blue")
    )

    override fun getAvailableDrivers(pickup: GeoPoint): List<SimulatedDriver> {
        return baseDrivers
            .filter { it.id !in busyDrivers }
            .mapIndexed { index, template ->
                val latOffset = (index + 1) * 0.002 - 0.005
                val lonOffset = (index + 1) * -0.0015 + 0.003
                val driverLoc = GeoPoint(pickup.latitude + latOffset, pickup.longitude + lonOffset)
                
                val distanceMeters = calculateDistance(pickup, driverLoc)
                val etaMinutes = (distanceMeters / 300.0).toInt().coerceAtLeast(1)

                SimulatedDriver(
                    id = template.id,
                    name = template.name,
                    rating = template.rating,
                    vehicleName = template.vehicleName,
                    vehicleModel = template.vehicleModel,
                    vehicleNumber = template.vehicleNumber,
                    vehicleColor = template.vehicleColor,
                    estimatedArrivalMinutes = etaMinutes,
                    currentCoordinate = driverLoc,
                    distanceFromPickupMeters = distanceMeters,
                    isAvailable = true
                )
            }
    }

    override fun findNearestDriver(pickup: GeoPoint): SimulatedDriver? {
        val available = getAvailableDrivers(pickup)
        return available.minByOrNull { it.distanceFromPickupMeters }
    }

    override fun markDriverAsBusy(driverId: String) {
        busyDrivers.add(driverId)
    }

    override fun releaseDriver(driverId: String) {
        busyDrivers.remove(driverId)
    }

    private fun calculateDistance(start: GeoPoint, end: GeoPoint): Double {
        val r = 6371e3 // meters
        val phi1 = Math.toRadians(start.latitude)
        val phi2 = Math.toRadians(end.latitude)
        val deltaPhi = Math.toRadians(end.latitude - start.latitude)
        val deltaLambda = Math.toRadians(end.longitude - start.longitude)

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }
}

private data class SimulatedDriverTemplate(
    val id: String,
    val name: String,
    val rating: Double,
    val vehicleName: String,
    val vehicleModel: String,
    val vehicleNumber: String,
    val vehicleColor: String
)
