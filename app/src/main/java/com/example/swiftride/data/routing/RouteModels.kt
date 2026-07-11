package com.example.swiftride.data.routing

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class RouteData(
    val distanceMeters: Double,
    val durationSeconds: Double,
    val encodedPolyline: String?,
    val decodedPolylinePoints: List<GeoPoint>,
    val pickupCoordinate: GeoPoint,
    val destinationCoordinate: GeoPoint,
    
    // Future compatibility placeholders (not implemented yet)
    val waypoints: List<GeoPoint> = emptyList(),
    val trafficDelaySeconds: Double? = null,
    val tollCost: Double? = null,
    val alternativeRoutes: List<RouteData> = emptyList(),
    val isLiveTrafficAware: Boolean = false,
    val etaUpdatesCount: Int = 0
) : Serializable
