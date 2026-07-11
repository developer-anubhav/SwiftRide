package com.example.swiftride.data.booking

import org.osmdroid.util.GeoPoint

data class SimulatedDriver(
    val id: String,
    val name: String,
    val profilePhoto: String? = null,
    val rating: Double,
    val vehicleName: String,
    val vehicleModel: String,
    val vehicleNumber: String,
    val vehicleColor: String,
    val estimatedArrivalMinutes: Int,
    val currentCoordinate: GeoPoint,
    val distanceFromPickupMeters: Double,
    val isAvailable: Boolean = true
)
