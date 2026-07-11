package com.example.swiftride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.swiftride.data.routing.RouteData
import com.example.swiftride.data.routing.RouteResult
import com.example.swiftride.data.routing.RoutingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

sealed interface RouteUiState {
    object Idle : RouteUiState
    object Loading : RouteUiState
    data class Success(val routeData: RouteData) : RouteUiState
    data class Error(val message: String) : RouteUiState
}

class RouteViewModel(private val repository: RoutingRepository) : ViewModel() {

    private val _routeUiState = MutableStateFlow<RouteUiState>(RouteUiState.Idle)
    val routeUiState: StateFlow<RouteUiState> = _routeUiState.asStateFlow()

    private var routingJob: Job? = null
    
    // Cache coordinates of last successful/requested route to prevent redundant requests
    private var lastPickup: GeoPoint? = null
    private var lastDestination: GeoPoint? = null

    fun calculateRoute(pickup: GeoPoint, destination: GeoPoint) {
        // Prevent duplicate network calls for the exact same coords
        if (pickup.latitude == lastPickup?.latitude &&
            pickup.longitude == lastPickup?.longitude &&
            destination.latitude == lastDestination?.latitude &&
            destination.longitude == lastDestination?.longitude &&
            _routeUiState.value !is RouteUiState.Error
        ) {
            return
        }

        // Cancel the previous routing request automatically (Rule 9)
        routingJob?.cancel()

        lastPickup = pickup
        lastDestination = destination

        routingJob = viewModelScope.launch {
            _routeUiState.value = RouteUiState.Loading
            try {
                val result = repository.getRoute(pickup, destination)
                // Check if active job was cancelled before setting state
                if (routingJob?.isCancelled == true) return@launch

                when (result) {
                    is RouteResult.Success -> {
                        _routeUiState.value = RouteUiState.Success(result.routeData)
                    }
                    is RouteResult.Error -> {
                        _routeUiState.value = RouteUiState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                if (routingJob?.isCancelled != true) {
                    _routeUiState.value = RouteUiState.Error("An unexpected error occurred while calculating route.")
                }
            }
        }
    }

    fun clearRoute() {
        routingJob?.cancel()
        lastPickup = null
        lastDestination = null
        _routeUiState.value = RouteUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        routingJob?.cancel()
    }
}

class RouteViewModelFactory(private val repository: RoutingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RouteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
