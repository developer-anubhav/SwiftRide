package com.example.swiftride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.swiftride.data.UserRepository
import com.example.swiftride.ui.components.AuthScreen
import com.example.swiftride.ui.components.HomeScreen
import com.example.swiftride.ui.components.SplashScreen
import com.example.swiftride.ui.theme.SwiftRideTheme
import com.example.swiftride.data.location.NominatimService
import com.example.swiftride.data.location.LocationProvider
import com.example.swiftride.data.location.LocationRepository
import com.example.swiftride.viewmodel.LocationViewModel
import com.example.swiftride.viewmodel.LocationViewModelFactory
import com.example.swiftride.data.routing.OpenRouteServiceRoutingService
import com.example.swiftride.data.routing.GraphHopperRoutingService
import com.example.swiftride.data.routing.OsrmRoutingService
import com.example.swiftride.data.routing.MockRoutingService
import com.example.swiftride.data.routing.RoutingRepository
import com.example.swiftride.data.routing.RoutingConfig
import com.example.swiftride.viewmodel.RouteViewModel
import com.example.swiftride.viewmodel.RouteViewModelFactory
import com.example.swiftride.data.fare.FareRepository
import com.example.swiftride.viewmodel.FareViewModel
import com.example.swiftride.viewmodel.FareViewModelFactory
import com.example.swiftride.data.booking.SimulatedDriverRepository
import com.example.swiftride.data.booking.SimulatedBookingRepository
import com.example.swiftride.viewmodel.BookingViewModel
import com.example.swiftride.viewmodel.BookingViewModelFactory
import com.example.swiftride.data.local.SwiftRideDatabase
import com.example.swiftride.ui.components.RideHistoryScreen
import com.example.swiftride.ui.components.RideDetailsScreen

sealed interface Screen {
    object Splash : Screen
    object Auth : Screen
    data class Home(val userName: String) : Screen
    object RideHistory : Screen
    data class RideDetails(val rideId: String) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install system splash screen transition for cold starts
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userRepository = remember { UserRepository(this@MainActivity) }
            
            // Local persistence Database initialization
            val database = remember { SwiftRideDatabase.getDatabase(this@MainActivity) }
            val rideDao = remember { database.rideDao() }
            val savedPlaceDao = remember { database.savedPlaceDao() }
            val recentSearchDao = remember { database.recentSearchDao() }

            val nominatimService = remember { NominatimService() }
            val locationProvider = remember { LocationProvider(this@MainActivity) }
            val locationRepository = remember { LocationRepository(nominatimService, locationProvider, savedPlaceDao, recentSearchDao) }
            val locationViewModel = remember {
                androidx.lifecycle.ViewModelProvider(
                    this@MainActivity,
                    LocationViewModelFactory(locationRepository)
                )[LocationViewModel::class.java]
            }
            val routingRepository = remember {
                val openRouteService = OpenRouteServiceRoutingService {
                    RoutingConfig.openRouteServiceApiKey
                }
                val graphHopperService = GraphHopperRoutingService {
                    RoutingConfig.graphHopperApiKey
                }
                val osrmService = OsrmRoutingService()
                val mockService = MockRoutingService()
                RoutingRepository(
                    openRouteService,
                    graphHopperService,
                    osrmService,
                    mockService
                )
            }
            val routeViewModel = remember {
                androidx.lifecycle.ViewModelProvider(
                    this@MainActivity,
                    RouteViewModelFactory(routingRepository)
                )[RouteViewModel::class.java]
            }
            val fareViewModel = remember {
                val fareRepository = FareRepository()
                androidx.lifecycle.ViewModelProvider(
                    this@MainActivity,
                    FareViewModelFactory(fareRepository)
                )[FareViewModel::class.java]
            }
            val bookingViewModel = remember {
                val driverRepository = SimulatedDriverRepository()
                val bookingRepository = SimulatedBookingRepository(driverRepository, routingRepository, rideDao)
                androidx.lifecycle.ViewModelProvider(
                    this@MainActivity,
                    BookingViewModelFactory(bookingRepository)
                )[BookingViewModel::class.java]
            }

            var isDarkMode by remember {
                mutableStateOf(userRepository.isDarkModeEnabled())
            }
            var selectedLanguage by remember {
                mutableStateOf(userRepository.getSelectedLanguage())
            }
            var profilePicturePath by remember {
                mutableStateOf<String?>(userRepository.getProfilePicturePath())
            }

            SwiftRideTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember {
                        mutableStateOf<Screen>(Screen.Splash)
                    }
                    
                    Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                        when (screen) {
                            is Screen.Splash -> {
                                SplashScreen(
                                    onTimeout = {
                                        val currentUser = userRepository.getCurrentUser()
                                        currentScreen = if (currentUser != null) {
                                            profilePicturePath = userRepository.getProfilePicturePath()
                                            Screen.Home(currentUser)
                                        } else {
                                            Screen.Auth
                                        }
                                    }
                                )
                            }
                            is Screen.Auth -> {
                                AuthScreen(
                                    onAuthSuccess = { userName ->
                                        profilePicturePath = userRepository.getProfilePicturePath()
                                        currentScreen = Screen.Home(userName)
                                    }
                                )
                            }
                            is Screen.Home -> {
                                HomeScreen(
                                    userName = screen.userName,
                                    isDarkMode = isDarkMode,
                                    selectedLanguage = selectedLanguage,
                                    profilePicturePath = profilePicturePath,
                                    locationViewModel = locationViewModel,
                                    routeViewModel = routeViewModel,
                                    fareViewModel = fareViewModel,
                                    bookingViewModel = bookingViewModel,
                                    onProfilePictureChange = { path ->
                                        userRepository.setProfilePicturePath(path)
                                        profilePicturePath = path
                                    },
                                    onThemeToggle = { enabled ->
                                        userRepository.setDarkModeEnabled(enabled)
                                        isDarkMode = enabled
                                    },
                                    onLanguageChange = { langCode ->
                                        userRepository.setSelectedLanguage(langCode)
                                        selectedLanguage = langCode
                                    },
                                    onLogout = {
                                        userRepository.logout()
                                        profilePicturePath = null
                                        currentScreen = Screen.Auth
                                    },
                                    onDeleteAccount = {
                                        userRepository.getProfilePicturePath()?.let { path ->
                                            try {
                                                java.io.File(path).delete()
                                            } catch (e: Exception) {}
                                        }
                                        userRepository.deleteCurrentUser()
                                        profilePicturePath = null
                                        currentScreen = Screen.Auth
                                    },
                                    onNavigateToHistory = {
                                        currentScreen = Screen.RideHistory
                                    }
                                )
                            }
                            is Screen.RideHistory -> {
                                RideHistoryScreen(
                                    bookingViewModel = bookingViewModel,
                                    selectedLanguage = selectedLanguage,
                                    isDarkMode = isDarkMode,
                                    onBack = {
                                        val currentUser = userRepository.getCurrentUser() ?: "Rider"
                                        currentScreen = Screen.Home(currentUser)
                                    },
                                    onRideSelect = { rideId ->
                                        currentScreen = Screen.RideDetails(rideId)
                                    }
                                )
                            }
                            is Screen.RideDetails -> {
                                RideDetailsScreen(
                                    rideId = screen.rideId,
                                    bookingViewModel = bookingViewModel,
                                    routeViewModel = routeViewModel,
                                    selectedLanguage = selectedLanguage,
                                    isDarkMode = isDarkMode,
                                    onBack = {
                                        currentScreen = Screen.RideHistory
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}