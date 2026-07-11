package com.example.swiftride.data.fare

import java.io.Serializable

data class RideCategory(
    val id: String,
    val name: String,
    val baseFare: Double,
    val pricePerKm: Double,
    val pricePerMinute: Double,
    val maxPassengers: Int,
    val description: String,
    val simulatedEtaMinutes: Int
) : Serializable
