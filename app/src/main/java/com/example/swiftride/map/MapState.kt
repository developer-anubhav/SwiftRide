package com.example.swiftride.map

import org.osmdroid.util.GeoPoint

data class MapState(
    val center: GeoPoint = GeoPoint(12.9716, 77.5946),
    val zoom: Double = 12.0,
    val pickupCoords: GeoPoint? = null,
    val destinationCoords: GeoPoint? = null,
    val hasRoute: Boolean = false
)
