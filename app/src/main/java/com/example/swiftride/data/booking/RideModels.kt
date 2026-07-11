package com.example.swiftride.data.booking

import org.osmdroid.util.GeoPoint

enum class RideState {
    Idle,
    Searching,
    DriverAssigned,
    DriverAccepted,
    DriverArriving,
    DriverReachedPickup,
    RideStarted,
    RideCompleted,
    RideCancelled
}

data class RideBookingState(
    val status: RideState = RideState.Idle,
    val assignedDriver: SimulatedDriver? = null,
    val errorMessage: String? = null,
    val currentDriverLocation: GeoPoint? = null,
    val etaMinutes: Int = 0,
    val distanceMeters: Double = 0.0
)
