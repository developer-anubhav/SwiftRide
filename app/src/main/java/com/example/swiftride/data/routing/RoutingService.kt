package com.example.swiftride.data.routing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random

sealed interface RouteResult {
    data class Success(val routeData: RouteData) : RouteResult
    data class Error(
        val errorType: RouteErrorType,
        val message: String,
        val throwable: Throwable? = null
    ) : RouteResult
}

enum class RouteErrorType {
    NO_ROUTE_FOUND,
    NETWORK_UNAVAILABLE,
    API_UNAVAILABLE,
    INVALID_COORDINATES,
    TIMEOUT,
    CANCELLED,
    UNKNOWN
}

enum class RouteProvider {
    OPENROUTE_SERVICE,
    GRAPH_HOPPER,
    OSRM,
    MOCK
}

object RoutingConfig {
    // Default to OSRM so that the app works instantly with real route calculation.
    var activeProvider: RouteProvider = RouteProvider.OSRM
    
    // User can set these keys at runtime or via configuration
    var openRouteServiceApiKey: String = ""
    var graphHopperApiKey: String = ""
}

interface RoutingService {
    suspend fun getRoute(start: GeoPoint, end: GeoPoint): RouteResult
}

class OpenRouteServiceRoutingService(private val getApiKey: () -> String) : RoutingService {
    private val userAgent = "SwiftRideBookingApp/1.0 (support@swiftride.com)"

    override suspend fun getRoute(start: GeoPoint, end: GeoPoint): RouteResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext RouteResult.Error(
                RouteErrorType.API_UNAVAILABLE,
                "OpenRouteService API key is missing. Please configure it."
            )
        }
        
        // OpenRouteService expects start and end coordinates as start=longitude,latitude&end=longitude,latitude
        val urlString = "https://api.openrouteservice.org/v2/directions/driving-car" +
                "?api_key=$apiKey" +
                "&start=${start.longitude},${start.latitude}" +
                "&end=${end.longitude},${end.latitude}"
                
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", userAgent)
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonResponse = JSONObject(response.toString())
                val features = jsonResponse.getJSONArray("features")
                if (features.length() == 0) {
                    return@withContext RouteResult.Error(
                        RouteErrorType.NO_ROUTE_FOUND,
                        "No route found between selected points."
                    )
                }

                val feature = features.getJSONObject(0)
                val properties = feature.getJSONObject("properties")
                val summary = properties.getJSONObject("summary")
                
                val distanceMeters = summary.getDouble("distance")
                val durationSeconds = summary.getDouble("duration")
                
                val geometry = feature.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                
                val points = mutableListOf<GeoPoint>()
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    // GeoJSON coordinates are [longitude, latitude]
                    val lon = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    points.add(GeoPoint(lat, lon))
                }
                
                val routeData = RouteData(
                    distanceMeters = distanceMeters,
                    durationSeconds = durationSeconds,
                    encodedPolyline = null,
                    decodedPolylinePoints = points,
                    pickupCoordinate = start,
                    destinationCoordinate = end
                )
                return@withContext RouteResult.Success(routeData)
            } else if (responseCode == 404 || responseCode == 400) {
                return@withContext RouteResult.Error(
                    RouteErrorType.NO_ROUTE_FOUND,
                    "No route found. Coordinates may be off-road or invalid."
                )
            } else if (responseCode == 401 || responseCode == 403) {
                return@withContext RouteResult.Error(
                    RouteErrorType.API_UNAVAILABLE,
                    "Invalid API key or unauthorized request to routing server."
                )
            } else {
                return@withContext RouteResult.Error(
                    RouteErrorType.API_UNAVAILABLE,
                    "Routing server returned status error: $responseCode"
                )
            }
        } catch (e: java.net.SocketTimeoutException) {
            return@withContext RouteResult.Error(
                RouteErrorType.TIMEOUT,
                "Routing request timed out. Please check your connection.",
                e
            )
        } catch (e: java.io.IOException) {
            return@withContext RouteResult.Error(
                RouteErrorType.NETWORK_UNAVAILABLE,
                "Network connection failed. Make sure you are online.",
                e
            )
        } catch (e: Exception) {
            return@withContext RouteResult.Error(
                RouteErrorType.UNKNOWN,
                "Failed to calculate route: ${e.localizedMessage}",
                e
            )
        } finally {
            connection?.disconnect()
        }
    }
}

class GraphHopperRoutingService(private val getApiKey: () -> String) : RoutingService {
    private val userAgent = "SwiftRideBookingApp/1.0 (support@swiftride.com)"

    override suspend fun getRoute(start: GeoPoint, end: GeoPoint): RouteResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext RouteResult.Error(
                RouteErrorType.API_UNAVAILABLE,
                "GraphHopper API key is missing. Please configure it."
            )
        }

        // GraphHopper uses point=lat,lon parameter format
        val urlString = "https://graphhopper.com/api/1/route" +
                "?point=${start.latitude},${start.longitude}" +
                "&point=${end.latitude},${end.longitude}" +
                "&vehicle=car" +
                "&points_encoded=false" +
                "&key=$apiKey"

        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", userAgent)
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonResponse = JSONObject(response.toString())
                val paths = jsonResponse.getJSONArray("paths")
                if (paths.length() == 0) {
                    return@withContext RouteResult.Error(
                        RouteErrorType.NO_ROUTE_FOUND,
                        "No routing paths found."
                    )
                }

                val path = paths.getJSONObject(0)
                val distanceMeters = path.getDouble("distance")
                // time is returned in milliseconds, convert to seconds
                val durationSeconds = path.getDouble("time") / 1000.0

                val pointsObj = path.getJSONObject("points")
                val coordinates = pointsObj.getJSONArray("coordinates")

                val points = mutableListOf<GeoPoint>()
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    // coordinates in path JSON are [longitude, latitude]
                    val lon = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    points.add(GeoPoint(lat, lon))
                }

                val routeData = RouteData(
                    distanceMeters = distanceMeters,
                    durationSeconds = durationSeconds,
                    encodedPolyline = null,
                    decodedPolylinePoints = points,
                    pickupCoordinate = start,
                    destinationCoordinate = end
                )
                return@withContext RouteResult.Success(routeData)
            } else if (responseCode == 400 || responseCode == 404) {
                return@withContext RouteResult.Error(
                    RouteErrorType.NO_ROUTE_FOUND,
                    "No route found. Coordinates may be off-road or invalid."
                )
            } else if (responseCode == 401 || responseCode == 403) {
                return@withContext RouteResult.Error(
                    RouteErrorType.API_UNAVAILABLE,
                    "Invalid API key or unauthorized request to GraphHopper server."
                )
            } else {
                return@withContext RouteResult.Error(
                    RouteErrorType.API_UNAVAILABLE,
                    "GraphHopper server returned status error: $responseCode"
                )
            }
        } catch (e: java.net.SocketTimeoutException) {
            return@withContext RouteResult.Error(
                RouteErrorType.TIMEOUT,
                "Routing request timed out. Please check your connection.",
                e
            )
        } catch (e: java.io.IOException) {
            return@withContext RouteResult.Error(
                RouteErrorType.NETWORK_UNAVAILABLE,
                "Network connection failed. Make sure you are online.",
                e
            )
        } catch (e: Exception) {
            return@withContext RouteResult.Error(
                RouteErrorType.UNKNOWN,
                "Failed to calculate route: ${e.localizedMessage}",
                e
            )
        } finally {
            connection?.disconnect()
        }
    }
}

class MockRoutingService : RoutingService {
    override suspend fun getRoute(start: GeoPoint, end: GeoPoint): RouteResult {
        // Simulate network latency for a realistic loading feel
        delay(1200)

        val distanceKm = calculateDistanceKm(start.latitude, start.longitude, end.latitude, end.longitude)
        
        // Generate realistic street turns instead of a straight line
        val points = generateSimulatedRoute(start, end)

        // Average speed of 35 km/h in traffic
        val speedKmh = 35.0
        val durationHours = distanceKm / speedKmh
        val durationSeconds = durationHours * 3600.0

        val routeData = RouteData(
            distanceMeters = distanceKm * 1000.0,
            durationSeconds = durationSeconds,
            encodedPolyline = null,
            decodedPolylinePoints = points,
            pickupCoordinate = start,
            destinationCoordinate = end
        )
        return RouteResult.Success(routeData)
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    private fun generateSimulatedRoute(start: GeoPoint, end: GeoPoint): List<GeoPoint> {
        val points = mutableListOf<GeoPoint>()
        points.add(start)

        val lat1 = start.latitude
        val lon1 = start.longitude
        val lat2 = end.latitude
        val lon2 = end.longitude

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val seed = (lat1 * 100000 + lon1 * 10000 + lat2 * 100 + lon2).toLong()
        val random = Random(seed)

        val steps = 15 // smooth route path
        var currentLat = lat1
        var currentLon = lon1

        for (i in 1 until steps) {
            val fraction = i.toDouble() / steps
            if (i % 2 == 1) {
                currentLat = lat1 + fraction * dLat
                val jitter = (random.nextDouble() - 0.5) * 0.15 * dLon
                currentLon = lon1 + (fraction - 0.05) * dLon + jitter
            } else {
                currentLon = lon1 + fraction * dLon
                val jitter = (random.nextDouble() - 0.5) * 0.15 * dLat
                currentLat = lat1 + (fraction - 0.05) * dLat + jitter
            }
            
            val minLat = minOf(lat1, lat2) - 0.005
            val maxLat = maxOf(lat1, lat2) + 0.005
            val minLon = minOf(lon1, lon2) - 0.005
            val maxLon = maxOf(lon1, lon2) + 0.005
            currentLat = currentLat.coerceIn(minLat, maxLat)
            currentLon = currentLon.coerceIn(minLon, maxLon)

            points.add(GeoPoint(currentLat, currentLon))
        }

        points.add(end)
        return points
    }
}

class OsrmRoutingService : RoutingService {
    private val userAgent = "SwiftRideBookingApp/1.0 (support@swiftride.com)"

    override suspend fun getRoute(start: GeoPoint, end: GeoPoint): RouteResult = withContext(Dispatchers.IO) {
        // OSRM expects coordinates as longitude,latitude separated by semicolons
        val urlString = "https://router.project-osrm.org/route/v1/driving/" +
                "${start.longitude},${start.latitude};${end.longitude},${end.latitude}" +
                "?overview=full&geometries=geojson"
                
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", userAgent)
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonResponse = JSONObject(response.toString())
                val code = jsonResponse.optString("code", "")
                if (code != "Ok") {
                    return@withContext RouteResult.Error(
                        RouteErrorType.NO_ROUTE_FOUND,
                        "OSRM routing failed with code: $code"
                    )
                }

                val routes = jsonResponse.getJSONArray("routes")
                if (routes.length() == 0) {
                    return@withContext RouteResult.Error(
                        RouteErrorType.NO_ROUTE_FOUND,
                        "No route found between selected points."
                    )
                }

                val route = routes.getJSONObject(0)
                val distanceMeters = route.getDouble("distance")
                val durationSeconds = route.getDouble("duration")
                
                val geometry = route.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                
                val points = mutableListOf<GeoPoint>()
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    // GeoJSON coordinates are [longitude, latitude]
                    val lon = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    points.add(GeoPoint(lat, lon))
                }
                
                val routeData = RouteData(
                    distanceMeters = distanceMeters,
                    durationSeconds = durationSeconds,
                    encodedPolyline = null,
                    decodedPolylinePoints = points,
                    pickupCoordinate = start,
                    destinationCoordinate = end
                )
                return@withContext RouteResult.Success(routeData)
            } else if (responseCode == 400 || responseCode == 404) {
                return@withContext RouteResult.Error(
                    RouteErrorType.NO_ROUTE_FOUND,
                    "No route found. Coordinates may be off-road or invalid."
                )
            } else {
                return@withContext RouteResult.Error(
                    RouteErrorType.API_UNAVAILABLE,
                    "OSRM server returned status error: $responseCode"
                )
            }
        } catch (e: java.net.SocketTimeoutException) {
            return@withContext RouteResult.Error(
                RouteErrorType.TIMEOUT,
                "OSRM request timed out. Please check your connection.",
                e
            )
        } catch (e: java.io.IOException) {
            return@withContext RouteResult.Error(
                RouteErrorType.NETWORK_UNAVAILABLE,
                "Network connection failed. Make sure you are online.",
                e
            )
        } catch (e: Exception) {
            return@withContext RouteResult.Error(
                RouteErrorType.UNKNOWN,
                "Failed to calculate route via OSRM: ${e.localizedMessage}",
                e
            )
        } finally {
            connection?.disconnect()
        }
    }
}
