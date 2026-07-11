package com.example.swiftride.data.location

import java.io.Serializable

data class LocationData(
    val name: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double
) : Serializable
