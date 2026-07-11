package com.example.swiftride.data.fare

class FareCalculator(
    private val baseRule: BaseFareRule,
    private val additionalRules: List<PricingRule> = emptyList()
) {
    fun calculateFare(distanceMeters: Double, durationSeconds: Double): Double {
        var fare = baseRule.calculateAdjustment(0.0, distanceMeters, durationSeconds)
        for (rule in additionalRules) {
            fare += rule.calculateAdjustment(fare, distanceMeters, durationSeconds)
        }
        return fare
    }

    fun calculateDetailedFare(
        baseFare: Double,
        pricePerKm: Double,
        pricePerMinute: Double,
        distanceMeters: Double,
        durationSeconds: Double
    ): FareBreakdown {
        val distanceKm = distanceMeters / 1000.0
        val durationMinutes = durationSeconds / 60.0

        val baseCharge = baseFare
        val distanceCharge = distanceKm * pricePerKm
        val timeCharge = durationMinutes * pricePerMinute
        
        // Sum total
        var total = baseCharge + distanceCharge + timeCharge

        // Calculate any additional pricing rules adjustments if defined
        var surge = 0.0
        var tolls = 0.0
        var discount = 0.0
        var platform = 0.0
        var tax = 0.0
        
        // Return full breakdown
        return FareBreakdown(
            baseFare = baseCharge,
            distanceCharge = distanceCharge,
            timeCharge = timeCharge,
            totalFare = total,
            surgeCharge = surge,
            tollFees = tolls,
            couponDiscount = discount,
            platformFee = platform,
            taxes = tax
        )
    }
}
