package com.example.swiftride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.swiftride.data.fare.FareBreakdown
import com.example.swiftride.data.fare.FareRepository
import com.example.swiftride.data.fare.RideCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FareUiState {
    object Idle : FareUiState
    object Loading : FareUiState
    data class Success(
        val fares: Map<String, FareBreakdown>,
        val categories: List<RideCategory>
    ) : FareUiState
    data class Error(val message: String) : FareUiState
}

class FareViewModel(private val repository: FareRepository) : ViewModel() {

    private val _fareUiState = MutableStateFlow<FareUiState>(FareUiState.Idle)
    val fareUiState: StateFlow<FareUiState> = _fareUiState.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    fun calculateFares(distanceMeters: Double, durationSeconds: Double) {
        // Validate coordinates / route details locally (Rule 9)
        if (distanceMeters <= 0.0 || durationSeconds <= 0.0) {
            _fareUiState.value = FareUiState.Error("Zero distance or duration returned for the route.")
            return
        }

        _fareUiState.value = FareUiState.Loading
        viewModelScope.launch {
            try {
                val fares = repository.calculateFares(distanceMeters, durationSeconds)
                val categories = repository.getRideCategories()
                
                _fareUiState.value = FareUiState.Success(fares, categories)

                // Select default category if none chosen yet
                if (_selectedCategoryId.value == null && categories.isNotEmpty()) {
                    _selectedCategoryId.value = categories.first().id
                }
            } catch (e: Exception) {
                _fareUiState.value = FareUiState.Error("An error occurred during fare estimation.")
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun clearFareState() {
        _selectedCategoryId.value = null
        _fareUiState.value = FareUiState.Idle
    }
}

class FareViewModelFactory(private val repository: FareRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FareViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FareViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
