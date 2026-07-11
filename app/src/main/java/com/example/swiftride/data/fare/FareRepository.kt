package com.example.swiftride.data.fare

class FareRepository {
    private val categories = listOf(
        RideCategory("bike", "Bike", 30.0, 8.0, 1.0, 1, "Fastest option", 2),
        RideCategory("auto", "Auto", 40.0, 10.0, 1.5, 3, "Affordable local rickshaw rides", 3),
        RideCategory("mini", "Mini", 50.0, 12.0, 2.0, 4, "Affordable ride", 4),
        RideCategory("sedan", "Sedan", 80.0, 15.0, 3.0, 4, "Premium comfort", 6),
        RideCategory("suv", "SUV", 120.0, 20.0, 4.0, 6, "Extra space", 8)
    )

    fun getRideCategories(): List<RideCategory> = categories

    fun calculateFares(distanceMeters: Double, durationSeconds: Double): Map<String, FareBreakdown> {
        return categories.associate { category ->
            val calculator = FareCalculator(
                BaseFareRule(category.baseFare, category.pricePerKm, category.pricePerMinute)
            )
            category.id to calculator.calculateDetailedFare(
                category.baseFare,
                category.pricePerKm,
                category.pricePerMinute,
                distanceMeters,
                durationSeconds
            )
        }
    }
}
