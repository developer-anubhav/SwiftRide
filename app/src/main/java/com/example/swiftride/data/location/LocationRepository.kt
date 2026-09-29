package com.example.swiftride.data.location

import android.location.Location
import com.example.swiftride.data.local.dao.SavedPlaceDao
import com.example.swiftride.data.local.dao.RecentSearchDao
import com.example.swiftride.data.local.entities.SavedPlaceEntity
import com.example.swiftride.data.local.entities.RecentSearchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository(
    private val nominatimService: NominatimService,
    private val locationProvider: LocationProvider,
    private val savedPlaceDao: SavedPlaceDao,
    private val recentSearchDao: RecentSearchDao
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

    // Saved Places persistence
    fun getSavedPlaces(): Flow<List<SavedPlaceEntity>> = savedPlaceDao.getAllSavedPlaces()

    suspend fun saveSavedPlace(place: SavedPlaceEntity) {
        withContext(Dispatchers.IO) {
            savedPlaceDao.insertSavedPlace(place)
        }
    }

    suspend fun deleteSavedPlace(place: SavedPlaceEntity) {
        withContext(Dispatchers.IO) {
            savedPlaceDao.deleteSavedPlace(place)
        }
    }

    // Recent Searches persistence
    fun getRecentSearches(limit: Int = 10): Flow<List<RecentSearchEntity>> = recentSearchDao.getRecentSearches(limit)

    suspend fun saveRecentSearch(query: String, latitude: Double? = null, longitude: Double? = null, address: String? = null) {
        withContext(Dispatchers.IO) {
            recentSearchDao.insertRecentSearch(
                RecentSearchEntity(
                    query = query,
                    timestamp = System.currentTimeMillis(),
                    latitude = latitude,
                    longitude = longitude,
                    address = address
                )
            )
        }
    }

    suspend fun deleteRecentSearch(query: String) {
        withContext(Dispatchers.IO) {
            recentSearchDao.deleteSearchByQuery(query)
        }
    }

    suspend fun clearRecentSearches() {
        withContext(Dispatchers.IO) {
            recentSearchDao.deleteAllRecentSearches()
        }
    }

    suspend fun seedDefaultPlacesIfEmpty() {
        withContext(Dispatchers.IO) {
            if (savedPlaceDao.getCount() == 0) {
                savedPlaceDao.insertSavedPlace(
                    SavedPlaceEntity(
                        name = "Home",
                        fullAddress = "123 Main St, Bengaluru",
                        latitude = 12.9784,
                        longitude = 77.6408,
                        iconType = "HOME"
                    )
                )
                savedPlaceDao.insertSavedPlace(
                    SavedPlaceEntity(
                        name = "Work",
                        fullAddress = "Tech Park Building 4, Bengaluru",
                        latitude = 12.9348,
                        longitude = 77.6189,
                        iconType = "WORK"
                    )
                )
            }
        }
    }
}
