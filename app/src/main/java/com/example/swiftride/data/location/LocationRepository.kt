package com.example.swiftride.data.location

import android.location.Location

class LocationRepository(
    private val nominatimService: NominatimService,
    private val locationProvider: LocationProvider
) {
    // Session cache for forward geocoding
    private val searchCache = HashMap<String, List<LocationData>>()
    
    // Session cache for reverse geocoding
    private val reverseCache = HashMap<String, LocationData>()

    suspend fun searchLocations(query: String): List<LocationData> {
        val cleanQuery = query.trim().lowercase()
        if (cleanQuery.isBlank()) return emptyList()
        
        searchCache[cleanQuery]?.let { return it }

        val results = nominatimService.searchLocations(query)
        if (results.isNotEmpty()) {
            searchCache[cleanQuery] = results
        }
        return results
    }

    suspend fun getDeviceLocation(): Location? {
        return locationProvider.getCurrentLocation()
    }

    suspend fun reverseGeocode(lat: Double, lon: Double): LocationData? {
        val cacheKey = String.format(java.util.Locale.US, "%.5f,%.5f", lat, lon)
        reverseCache[cacheKey]?.let { return it }

        val result = nominatimService.reverseGeocode(lat, lon)
        if (result != null) {
            reverseCache[cacheKey] = result
        }
        return result
    }
}
