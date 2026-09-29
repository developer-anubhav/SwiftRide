package com.example.swiftride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.swiftride.data.booking.BookingRepository
import com.example.swiftride.data.booking.RideBookingState
import com.example.swiftride.data.local.entities.RideEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

enum class FilterOption {
    ALL, COMPLETED, CANCELLED, THIS_WEEK, THIS_MONTH
}

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    val bookingState: StateFlow<RideBookingState> = repository.bookingState

    val rideHistory: Flow<List<RideEntity>> = repository.getRideHistory()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterOption = MutableStateFlow(FilterOption.ALL)
    val filterOption: StateFlow<FilterOption> = _filterOption.asStateFlow()

    // Combined Flow to reactively filter rides based on Search & Filter options
    val filteredRides: Flow<List<RideEntity>> = combine(
        repository.getRideHistory(),
        _searchQuery,
        _filterOption
    ) { rides, query, filter ->
        rides.filter { ride ->
            val matchesQuery = query.isBlank() ||
                ride.pickupName.contains(query, ignoreCase = true) ||
                ride.destinationName.contains(query, ignoreCase = true) ||
                (ride.driverName?.contains(query, ignoreCase = true) == true)

            val matchesStatus = when (filter) {
                FilterOption.ALL -> true
                FilterOption.COMPLETED -> ride.status == "RideCompleted"
                FilterOption.CANCELLED -> ride.status == "RideCancelled"
                FilterOption.THIS_WEEK -> isThisCalendarWeek(ride.timestamp)
                FilterOption.THIS_MONTH -> isThisCalendarMonth(ride.timestamp)
            }

            matchesQuery && matchesStatus
        }
    }

    fun confirmBooking(
        pickup: GeoPoint,
        destination: GeoPoint,
        pickupName: String = "Pickup",
        pickupAddress: String = "",
        destinationName: String = "Destination",
        destinationAddress: String = "",
        fare: Double = 0.0,
        rideCategory: String = "SwiftX",
        durationMinutes: Int = 10
    ) {
        repository.confirmBooking(
            pickup = pickup,
            destination = destination,
            pickupName = pickupName,
            pickupAddress = pickupAddress,
            destinationName = destinationName,
            destinationAddress = destinationAddress,
            fare = fare,
            rideCategory = rideCategory,
            durationMinutes = durationMinutes,
            scope = viewModelScope
        )
    }

    fun cancelBooking() {
        repository.cancelBooking()
    }

    fun clearBookingState() {
        repository.clearBookingState()
    }

    fun deleteRide(rideId: String) {
        viewModelScope.launch {
            repository.deleteRide(rideId)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterOption(option: FilterOption) {
        _filterOption.value = option
    }

    // Pagination support
    suspend fun getRidesPaged(limit: Int, offset: Int): List<RideEntity> {
        return repository.getRidesPaged(limit, offset)
    }

    private fun isThisCalendarWeek(timestamp: Long): Boolean {
        val now = java.util.Calendar.getInstance()
        val time = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(java.util.Calendar.YEAR) == time.get(java.util.Calendar.YEAR) &&
                now.get(java.util.Calendar.WEEK_OF_YEAR) == time.get(java.util.Calendar.WEEK_OF_YEAR)
    }

    private fun isThisCalendarMonth(timestamp: Long): Boolean {
        val now = java.util.Calendar.getInstance()
        val time = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(java.util.Calendar.YEAR) == time.get(java.util.Calendar.YEAR) &&
                now.get(java.util.Calendar.MONTH) == time.get(java.util.Calendar.MONTH)
    }
}

class BookingViewModelFactory(
    private val repository: BookingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
