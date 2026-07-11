package com.example.swiftride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.swiftride.data.booking.BookingRepository
import com.example.swiftride.data.booking.RideBookingState
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    val bookingState: StateFlow<RideBookingState> = repository.bookingState

    fun confirmBooking(pickup: GeoPoint, destination: GeoPoint) {
        repository.confirmBooking(pickup, destination, viewModelScope)
    }

    fun cancelBooking() {
        repository.cancelBooking()
    }

    fun clearBookingState() {
        repository.clearBookingState()
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
