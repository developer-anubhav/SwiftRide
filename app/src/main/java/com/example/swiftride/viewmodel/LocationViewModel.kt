package com.example.swiftride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swiftride.data.location.LocationData
import com.example.swiftride.data.location.LocationRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class LocationViewModel(private val repository: LocationRepository) : ViewModel() {

    private val _pickupLocation = MutableStateFlow<LocationData?>(null)
    val pickupLocation: StateFlow<LocationData?> = _pickupLocation.asStateFlow()

    private val _destinationLocation = MutableStateFlow<LocationData?>(null)
    val destinationLocation: StateFlow<LocationData?> = _destinationLocation.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<LocationData>>(emptyList())
    val searchSuggestions: StateFlow<List<LocationData>> = _searchSuggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchSuggestions.value = emptyList()
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _searchSuggestions.value = emptyList()
            return
        }
        
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = repository.searchLocations(query)
                _searchSuggestions.value = results
                if (results.isEmpty()) {
                    _error.value = "No results found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to search locations. Check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setPickup(location: LocationData?) {
        _pickupLocation.value = location
    }

    fun setDestination(location: LocationData?) {
        _destinationLocation.value = location
    }

    fun fetchCurrentLocation(onSuccess: (LocationData) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val location = repository.getDeviceLocation()
                if (location != null) {
                    val locationData = repository.reverseGeocode(location.latitude, location.longitude)
                    if (locationData != null) {
                        setPickup(locationData)
                        onSuccess(locationData)
                    } else {
                        val fallback = LocationData(
                            "Current Location",
                            "Coordinates: ${location.latitude}, ${location.longitude}",
                            location.latitude,
                            location.longitude
                        )
                        setPickup(fallback)
                        onSuccess(fallback)
                    }
                } else {
                    _error.value = "Could not detect location. Make sure GPS and permissions are enabled."
                }
            } catch (e: Exception) {
                _error.value = "Error getting location."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSuggestions() {
        _searchSuggestions.value = emptyList()
        _searchQuery.value = ""
    }
}

class LocationViewModelFactory(private val repository: LocationRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
