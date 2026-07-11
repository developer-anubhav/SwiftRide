package com.example.swiftride.data.fare

import java.io.Serializable

interface PricingRule : Serializable {
    fun calculateAdjustment(baseFare: Double, distanceMeters: Double, durationSeconds: Double): Double
}

class BaseFareRule(
    val baseFare: Double,
    val pricePerKm: Double,
    val pricePerMinute: Double
) : PricingRule {
    override fun calculateAdjustment(baseFare: Double, distanceMeters: Double, durationSeconds: Double): Double {
        val distanceKm = distanceMeters / 1000.0
        val durationMinutes = durationSeconds / 60.0
        return baseFare + (distanceKm * pricePerKm) + (durationMinutes * pricePerMinute)
    }
}

data class FareBreakdown(
    val baseFare: Double,
    val distanceCharge: Double,
    val timeCharge: Double,
    val totalFare: Double,
    
    // Future expansion fields (pre-configured but zeroed out for now)
    val surgeCharge: Double = 0.0,
    val tollFees: Double = 0.0,
    val couponDiscount: Double = 0.0,
    val platformFee: Double = 0.0,
    val taxes: Double = 0.0
) : Serializable
