package com.example.swiftride.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import com.example.swiftride.R
import com.example.swiftride.data.AppLanguage
import com.example.swiftride.data.translate
import com.example.swiftride.data.location.LocationData
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.VerticalDivider
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import com.example.swiftride.ui.theme.Blue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import com.example.swiftride.ui.location.LocationSearchBar
import com.example.swiftride.ui.location.LocationSearchField
import com.example.swiftride.ui.location.SearchSuggestionItem
import com.example.swiftride.ui.location.CurrentLocationButton
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import com.example.swiftride.viewmodel.RouteUiState
import com.example.swiftride.ui.route.TripSummaryCard
import com.example.swiftride.ui.route.RouteLoading
import androidx.compose.foundation.layout.statusBarsPadding
import kotlinx.coroutines.CoroutineScope
import com.example.swiftride.viewmodel.FareUiState
import com.example.swiftride.ui.fare.RideSelectionBottomSheet
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

// Model for ride options
data class RideOption(
    val id: String,
    val name: String,
    val price: String,
    val eta: String,
    val description: String
)

data class ServiceItem(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector
)

val GeoPointSaver = mapSaver<GeoPoint>(
    save = { mapOf("lat" to it.latitude, "lon" to it.longitude) },
    restore = { GeoPoint(it["lat"] as Double, it["lon"] as Double) }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    isDarkMode: Boolean,
    selectedLanguage: String,
    profilePicturePath: String?,
    locationViewModel: com.example.swiftride.viewmodel.LocationViewModel,
    routeViewModel: com.example.swiftride.viewmodel.RouteViewModel,
    fareViewModel: com.example.swiftride.viewmodel.FareViewModel,
    bookingViewModel: com.example.swiftride.viewmodel.BookingViewModel,
    onProfilePictureChange: (String?) -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rideOptions = remember {
        listOf(
            RideOption("go", "SwiftX", "$12.50", "3 min away", "Affordable everyday rides"),
            RideOption("xl", "SwiftXL", "$21.00", "5 min away", "Spacious SUVs for groups"),
            RideOption("premium", "SwiftPremium", "$32.50", "2 min away", "High-end luxury cars")
        )
    }

    var selectedRideId by remember { mutableStateOf("go") }
    val selectedRide = rideOptions.first { it.id == selectedRideId }

    // Bottom Navigation Tab Index
    var selectedTab by remember { mutableIntStateOf(0) }

    var selectedServiceId by remember { mutableStateOf<String?>(null) }

    val bookingState by bookingViewModel.bookingState.collectAsState()
    var showBookingReview by remember { mutableStateOf(false) }

    // Room Database flows
    val savedPlacesFromDb by locationViewModel.savedPlaces.collectAsState(initial = emptyList())
    val rideHistoryFromDb by bookingViewModel.rideHistory.collectAsState(initial = emptyList())
    val recentSearchesFromDb by locationViewModel.recentSearches.collectAsState(initial = emptyList())

    val services = remember {
        listOf(
            ServiceItem("auto", "Auto", "Affordable local rickshaw rides", Icons.Default.ElectricRickshaw),
            ServiceItem("cab", "Cab", "Request a fast everyday ride", Icons.Default.DirectionsCar),
            ServiceItem("bus_train", "Bus & Train", "Public transit tickets & timetables", Icons.Default.DirectionsBus),
            ServiceItem("bike", "Bike", "Fast and economic bike rides", Icons.Default.Moped),
            ServiceItem("rentals", "Rentals", "Rent vehicles for self-drive", Icons.Default.CarRental),
            ServiceItem("intercity", "Intercity", "Comfortable outstation travel", Icons.Default.Commute),
            ServiceItem("reserve", "Reserve", "Schedule your rides in advance", Icons.Default.Schedule),
            ServiceItem("teens", "Teens", "Monitored rides for students", Icons.Default.School)
        )
    }


    // Calculate user profile initials
    val initials = remember(userName) {
        userName.split(" ")
            .mapNotNull { it.firstOrNull() }
            .joinToString("")
            .take(2)
            .uppercase()
    }

    val context = LocalContext.current

    val profileBitmap = remember(profilePicturePath) {
        if (profilePicturePath != null) {
            try {
                BitmapFactory.decodeFile(profilePicturePath)
            } catch (e: Throwable) {
                null
            }
        } else {
            null
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            locationViewModel.fetchCurrentLocation()
        } else {
            android.widget.Toast.makeText(context, "Location permission denied", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val profilePicPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val mimeType = context.contentResolver.getType(uri)
                val isJpgOrPng = if (mimeType != null) {
                    mimeType == "image/jpeg" || mimeType == "image/png" || mimeType == "image/jpg"
                } else {
                    val path = uri.path?.lowercase() ?: ""
                    path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")
                }
                
                if (isJpgOrPng) {
                    // 1. Decode bounds to find original dimensions
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        BitmapFactory.decodeStream(input, null, options)
                    }
                    
                    val width = options.outWidth
                    val height = options.outHeight
                    
                    if (width > 0 && height > 0) {
                        // 2. Calculate subsample size (inSampleSize)
                        val targetSize = 512
                        var inSampleSize = 1
                        while ((width / inSampleSize) > targetSize || (height / inSampleSize) > targetSize) {
                            inSampleSize *= 2
                        }
                        
                        // 3. Decode sampled bitmap into memory
                        val decodeOptions = BitmapFactory.Options().apply {
                            this.inSampleSize = inSampleSize
                        }
                        val sampledBitmap = context.contentResolver.openInputStream(uri)?.use { input ->
                            BitmapFactory.decodeStream(input, null, decodeOptions)
                        }
                        
                        if (sampledBitmap != null) {
                            // 4. Scale to exact dimensions (max 512px) if it exceeds target size
                            val scaledBitmap = if (sampledBitmap.width > targetSize || sampledBitmap.height > targetSize) {
                                val scale = targetSize.toFloat() / maxOf(sampledBitmap.width, sampledBitmap.height)
                                val newWidth = (sampledBitmap.width * scale).toInt()
                                val newHeight = (sampledBitmap.height * scale).toInt()
                                android.graphics.Bitmap.createScaledBitmap(sampledBitmap, newWidth, newHeight, true)
                            } else {
                                sampledBitmap
                            }
                            
                            // 5. Save to private files directory
                            val file = java.io.File(context.filesDir, "profile_pic_${System.currentTimeMillis()}.jpg")
                            
                            context.filesDir.listFiles { _, name -> name.startsWith("profile_pic_") }?.forEach {
                                it.delete()
                            }
                            
                            java.io.FileOutputStream(file).use { out ->
                                scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, out)
                            }
                            
                            // 6. Recycle bitmaps to free up native memory immediately
                            if (scaledBitmap != sampledBitmap) {
                                scaledBitmap.recycle()
                            }
                            sampledBitmap.recycle()
                            
                            onProfilePictureChange(file.absolutePath)
                        } else {
                            android.widget.Toast.makeText(context, "Error reading image data", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        android.widget.Toast.makeText(context, "Invalid image file", android.widget.Toast.LENGTH_SHORT).show()
                    }
                } else {
                    android.widget.Toast.makeText(context, "Only .jpg and .png formats are supported", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                android.widget.Toast.makeText(context, "Error saving profile picture", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Routing and Map State variables
    var showLocationInputs by rememberSaveable { mutableStateOf(false) }
    var hasSelectedRoute by rememberSaveable { mutableStateOf(false) }
    var validationError by rememberSaveable { mutableStateOf<String?>(null) }
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var showLanguageDropdown by rememberSaveable { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        BackHandler {
            showDeleteConfirmation = false
        }
    } else if (showSettingsDialog) {
        BackHandler {
            showSettingsDialog = false
        }
    }

    var pickupCoords by rememberSaveable(stateSaver = GeoPointSaver) { mutableStateOf(GeoPoint(12.9779, 77.5730)) }
    var destinationCoords by rememberSaveable(stateSaver = GeoPointSaver) { mutableStateOf(GeoPoint(12.9716, 77.5946)) }

    val routeUiState by routeViewModel.routeUiState.collectAsState()
    val pickupLocationState by locationViewModel.pickupLocation.collectAsState()
    val destinationLocationState by locationViewModel.destinationLocation.collectAsState()
    val scope = rememberCoroutineScope()

    val fareUiState by fareViewModel.fareUiState.collectAsState()
    val selectedCategoryId by fareViewModel.selectedCategoryId.collectAsState()

    LaunchedEffect(pickupLocationState, destinationLocationState) {
        val pickup = pickupLocationState
        val dest = destinationLocationState
        if (pickup != null && dest != null) {
            val pickupGp = GeoPoint(pickup.latitude, pickup.longitude)
            val destGp = GeoPoint(dest.latitude, dest.longitude)
            pickupCoords = pickupGp
            destinationCoords = destGp
            
            routeViewModel.calculateRoute(pickupGp, destGp)
            hasSelectedRoute = true
        } else {
            routeViewModel.clearRoute()
            fareViewModel.clearFareState()
            hasSelectedRoute = false
        }
    }

    LaunchedEffect(routeUiState) {
        val routeState = routeUiState
        if (routeState is RouteUiState.Success) {
            fareViewModel.calculateFares(
                routeState.routeData.distanceMeters,
                routeState.routeData.durationSeconds
            )
        } else if (routeState is RouteUiState.Idle) {
            fareViewModel.clearFareState()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Floating Capsule Bottom Navigation Bar with sliding active indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp, start = 20.dp, end = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .background(
                            color = if (isDarkMode) Color(0xFF151515) else Color(0xFFFFFFFF),
                            shape = CircleShape
                        )
                        .height(72.dp)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val tabWidth = maxWidth / 3
                        val animatedOffset by animateDpAsState(
                            targetValue = tabWidth * selectedTab,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "indicatorOffset"
                        )
                        
                        // Active tab background capsule indicator
                        Box(
                            modifier = Modifier
                                .width(tabWidth)
                                .fillMaxHeight()
                                .offset(x = animatedOffset)
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                .background(
                                    color = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFEFEFEF),
                                    shape = CircleShape
                                )
                        )
                        
                        // Navigation Items
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BottomNavItem(
                                selected = selectedTab == 0,
                                onClick = { 
                                    selectedTab = 0 
                                    selectedServiceId = null
                                    showSettingsDialog = false
                                    showDeleteConfirmation = false
                                    showLanguageDropdown = false
                                },
                                icon = { tint -> HomeIcon(tint) },
                                label = "Home".translate(selectedLanguage),
                                isDarkMode = isDarkMode,
                                modifier = Modifier.weight(1f)
                            )
                            BottomNavItem(
                                selected = selectedTab == 1,
                                onClick = { 
                                    selectedTab = 1 
                                    selectedServiceId = null
                                    showSettingsDialog = false
                                    showDeleteConfirmation = false
                                    showLanguageDropdown = false
                                },
                                icon = { tint -> ActivityIcon(tint) },
                                label = "Activity".translate(selectedLanguage),
                                isDarkMode = isDarkMode,
                                modifier = Modifier.weight(1f)
                            )
                            BottomNavItem(
                                selected = selectedTab == 2,
                                onClick = { 
                                    selectedTab = 2 
                                    selectedServiceId = null
                                    showSettingsDialog = false
                                    showDeleteConfirmation = false
                                    showLanguageDropdown = false
                                },
                                icon = { tint -> AccountIcon(tint) },
                                label = "Account".translate(selectedLanguage),
                                isDarkMode = isDarkMode,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )  {
            when (selectedTab) {
                0 -> {
                    if (routeUiState is RouteUiState.Idle) {
                        // Ride Tab: Premium Apple-inspired layout
                        val scrollState = rememberScrollState()
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            ) {
                                Spacer(modifier = Modifier.height(28.dp))
                                
                                // Top Section: Logo & Greeting Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "SwiftRide",
                                            color = Color(0xFF3A86FF),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Good Morning 👋 \n$userName".translate(selectedLanguage),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(BorderStroke(1.5.dp, Color(0xFF3A86FF)), CircleShape)
                                            .clickable { selectedTab = 2 }
                                    ) {
                                        if (profileBitmap != null) {
                                            Image(
                                                bitmap = profileBitmap.asImageBitmap(),
                                                contentDescription = "Profile Picture",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                text = userName.take(2).uppercase(),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Interactive Map Card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .padding(horizontal = 20.dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(28.dp))
                                        .border(
                                            BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                                            RoundedCornerShape(28.dp)
                                        )
                                ) {
                                    OpenStreetMapBackground(
                                        pickupCoords = pickupCoords,
                                        destinationCoords = destinationCoords,
                                        hasSelectedRoute = false,
                                        driverLoc = bookingState.currentDriverLocation,
                                        driverName = bookingState.assignedDriver?.name,
                                        scope = scope
                                    )
                                    
                                    // Floating search destination card at the bottom of the map
                                    Card(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                                            .clickable { showLocationInputs = true },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFEFEFEF))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "📍",
                                                    fontSize = 16.sp
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = "Where to?".translate(selectedLanguage),
                                                    color = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f),
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontFamily = FontFamily.SansSerif
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                VerticalDivider(
                                                    color = if (isDarkMode) Color(0xFF333333) else Color(0xFFE5E5EA),
                                                    modifier = Modifier.height(16.dp).padding(horizontal = 12.dp)
                                                )
                                                Text(
                                                    text = "Now ▼".translate(selectedLanguage),
                                                    color = Color(0xFF3A86FF),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.SansSerif
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Quick Ride Options
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Quick Ride Options".translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif,
                                        modifier = Modifier.padding(horizontal = 20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    val quickOptions = listOf(
                                        Triple("Ride", "4 min", "Popular ride"),
                                        Triple("Premium", "2 min", "Luxury comfort"),
                                        Triple("XL", "5 min", "Spacious SUVs"),
                                        Triple("Auto", "3 min", "Local rickshaw"),
                                        Triple("Bike", "2 min", "Fastest option"),
                                        Triple("Eco", "6 min", "Green ride")
                                    )
                                    
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(quickOptions.size) { index ->
                                            val (name, eta, desc) = quickOptions[index]
                                            val icon = when (name) {
                                                "Bike" -> androidx.compose.material.icons.Icons.Default.Moped
                                                "Auto" -> androidx.compose.material.icons.Icons.Default.ElectricRickshaw
                                                else -> androidx.compose.material.icons.Icons.Default.DirectionsCar
                                            }
                                            Card(
                                                modifier = Modifier
                                                    .width(110.dp)
                                                    .clickable { showLocationInputs = true },
                                                shape = RoundedCornerShape(18.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
                                                ),
                                                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFEFEFEF))
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalAlignment = Alignment.Start
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = Color(0xFF3A86FF),
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text(
                                                        text = name.translate(selectedLanguage),
                                                        color = if (isDarkMode) Color.White else Color.Black,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.SansSerif
                                                    )
                                                    Text(
                                                        text = eta.translate(selectedLanguage),
                                                        color = Color(0xFF3A86FF).copy(alpha = 0.8f),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        fontFamily = FontFamily.SansSerif
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Promotional Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                colors = if (isDarkMode) {
                                                    listOf(Color(0xFF1E1E1E), Color(0xFF1A2A3A))
                                                } else {
                                                    listOf(Color(0xFFFFFFFF), Color(0xFFE3F2FD))
                                                }
                                            )
                                        )
                                        .border(
                                            BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFE5E5EA)),
                                            RoundedCornerShape(24.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Save on every ride".translate(selectedLanguage),
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.SansSerif
                                            )
                                            Text(
                                                text = "Unlock exclusive offers".translate(selectedLanguage),
                                                color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.SansSerif
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Button(
                                                onClick = { /* Promo trigger */ },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF3A86FF),
                                                    contentColor = if (isDarkMode) Color.Black else Color.White
                                                ),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Text(
                                                    text = "View Offers".translate(selectedLanguage),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.CreditCard,
                                            contentDescription = null,
                                            tint = Color(0xFF3A86FF).copy(alpha = 0.7f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Saved Places: Plan your next trip
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Text(
                                        text = "Plan your next trip".translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    val savedPlaces = savedPlacesFromDb.map { entity ->
                                        val icon = when (entity.iconType) {
                                            "HOME" -> androidx.compose.material.icons.Icons.Default.Home
                                            "WORK" -> androidx.compose.material.icons.Icons.Default.Work
                                            else -> androidx.compose.material.icons.Icons.Default.LocalTaxi
                                        }
                                        Triple(entity.name, entity.fullAddress, icon) to (LocationData(entity.name, entity.fullAddress, entity.latitude, entity.longitude) as LocationData?)
                                    }.toMutableList().apply {
                                        add(Triple("Add New Place", "Set location on map", androidx.compose.material.icons.Icons.Default.Add) to null)
                                    }
                                    
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
                                        ),
                                        border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFEFEFEF))
                                    ) {
                                        Column {
                                            savedPlaces.forEachIndexed { idx, pair ->
                                                val (info, locData) = pair
                                                val (label, desc, icon) = info
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            if (locData != null) {
                                                                locationViewModel.setDestination(locData)
                                                            }
                                                            showLocationInputs = true
                                                        }
                                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = Color(0xFF3A86FF),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = label.translate(selectedLanguage),
                                                            color = if (isDarkMode) Color.White else Color.Black,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFamily = FontFamily.SansSerif
                                                        )
                                                        Text(
                                                            text = desc.translate(selectedLanguage),
                                                            color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                                            fontSize = 11.sp,
                                                            fontFamily = FontFamily.SansSerif
                                                        )
                                                    }
                                                    Icon(
                                                        imageVector = androidx.compose.material.icons.Icons.Default.ChevronRight,
                                                        contentDescription = null,
                                                        tint = if (isDarkMode) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.3f),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                if (idx < savedPlaces.size - 1) {
                                                    HorizontalDivider(
                                                        color = if (isDarkMode) Color(0xFF333333) else Color(0xFFEFEFEF),
                                                        modifier = Modifier.padding(horizontal = 16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Recent Trips
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Text(
                                        text = "Recent Trips".translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    val recentTrips = rideHistoryFromDb.map { entity ->
                                         val dateStr = try {
                                             val sdf = java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault())
                                             sdf.format(java.util.Date(entity.timestamp))
                                         } catch (e: Exception) {
                                             "Recent"
                                         }
                                         val details = "₹${entity.fare.toInt()} • $dateStr"
                                         Triple(entity.pickupName, entity.destinationName, details) to (
                                             LocationData(entity.pickupName, entity.pickupAddress, entity.pickupLatitude, entity.pickupLongitude) to
                                             LocationData(entity.destinationName, entity.destinationAddress, entity.destinationLatitude, entity.destinationLongitude)
                                         )
                                     }
                                     
                                     Card(
                                         modifier = Modifier.fillMaxWidth(),
                                         shape = RoundedCornerShape(16.dp),
                                         colors = CardDefaults.cardColors(
                                             containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
                                         ),
                                         border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFEFEFEF))
                                     ) {
                                         Column {
                                             if (recentTrips.isEmpty()) {
                                                 Text(
                                                     text = "No recent trips".translate(selectedLanguage),
                                                     color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                                     fontSize = 13.sp,
                                                     fontFamily = FontFamily.SansSerif,
                                                     modifier = Modifier.padding(16.dp)
                                                 )
                                             } else {
                                                 recentTrips.forEachIndexed { idx, pair ->
                                                     val (info, coords) = pair
                                                     val (pickup, dest, details) = info
                                                     Row(
                                                         modifier = Modifier
                                                             .fillMaxWidth()
                                                             .clickable {
                                                                 locationViewModel.setPickup(coords.component1())
                                                                 locationViewModel.setDestination(coords.component2())
                                                                 showLocationInputs = true
                                                             }
                                                             .padding(horizontal = 16.dp, vertical = 12.dp),
                                                         verticalAlignment = Alignment.CenterVertically
                                                     ) {
                                                         Icon(
                                                             imageVector = androidx.compose.material.icons.Icons.Default.History,
                                                             contentDescription = null,
                                                             tint = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                                             modifier = Modifier.size(20.dp)
                                                         )
                                                         Spacer(modifier = Modifier.width(16.dp))
                                                         Column(modifier = Modifier.weight(1f)) {
                                                             Text(
                                                                 text = "$pickup ➔ $dest".translate(selectedLanguage),
                                                                 color = if (isDarkMode) Color.White else Color.Black,
                                                                 fontSize = 13.sp,
                                                                 fontWeight = FontWeight.SemiBold,
                                                                 fontFamily = FontFamily.SansSerif
                                                             )
                                                             Text(
                                                                 text = details.translate(selectedLanguage),
                                                                 color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                                                 fontSize = 11.sp,
                                                                 fontFamily = FontFamily.SansSerif
                                                             )
                                                         }
                                                     }
                                                     if (idx < recentTrips.size - 1) {
                                                         HorizontalDivider(
                                                             color = if (isDarkMode) Color(0xFF333333) else Color(0xFFEFEFEF),
                                                             modifier = Modifier.padding(horizontal = 16.dp)
                                                         )
                                                     }
                                                 }
                                             }
                                         }
                                     }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Recommended Destinations
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Recommended Destinations".translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif,
                                        modifier = Modifier.padding(horizontal = 20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    val recommendations = listOf(
                                        Pair("Airport", "✈️"),
                                        Pair("Railway Station", "🚆"),
                                        Pair("Mall", "🛍️"),
                                        Pair("Office", "💼"),
                                        Pair("College", "🎓")
                                    )
                                    
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(recommendations.size) { index ->
                                            val (name, emoji) = recommendations[index]
                                            Card(
                                                modifier = Modifier
                                                    .width(110.dp)
                                                    .clickable { showLocationInputs = true },
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
                                                ),
                                                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFEFEFEF))
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        text = emoji,
                                                        fontSize = 24.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = name.translate(selectedLanguage),
                                                        color = if (isDarkMode) Color.White else Color.Black,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.SansSerif,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
                            }
                        }
                    } else {
                        // Immersive full-screen map layout for routing
                        Box(
                            modifier = Modifier.fillMaxSize()
) {
                            val routePoints = (routeUiState as? RouteUiState.Success)?.routeData?.decodedPolylinePoints ?: emptyList()
                            
                            OpenStreetMapBackground(
                                pickupCoords = pickupCoords,
                                destinationCoords = destinationCoords,
                                hasSelectedRoute = true,
                                routePoints = routePoints,
                                driverLoc = bookingState.currentDriverLocation,
                                driverName = bookingState.assignedDriver?.name,
                                scope = scope
                            )
                            
                            // Close button at top right
                            Box(
                                modifier = Modifier
                                    .statusBarsPadding()
                                    .padding(16.dp)
                                    .align(Alignment.TopEnd)
                                    .size(36.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .clickable {
                                        locationViewModel.setPickup(null)
                                        locationViewModel.setDestination(null)
                                        routeViewModel.clearRoute()
                                        fareViewModel.clearFareState()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✕",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            // Routing card overlays at the bottom
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .navigationBarsPadding()
                                    .padding(16.dp)
                            ) {
                                when (val routeState = routeUiState) {
                                    is RouteUiState.Loading -> {
                                        RouteLoading(
                                            selectedLanguage = selectedLanguage,
                                            isDarkMode = isDarkMode
                                        )
                                    }
                                    is RouteUiState.Error -> {
                                        com.example.swiftride.ui.route.RouteErrorCard(
                                            message = routeState.message,
                                            selectedLanguage = selectedLanguage,
                                            isDarkMode = isDarkMode,
                                            onRetry = {
                                                routeViewModel.calculateRoute(pickupCoords, destinationCoords)
                                            },
                                            onCancel = {
                                                locationViewModel.setPickup(null)
                                                locationViewModel.setDestination(null)
                                                routeViewModel.clearRoute()
                                                fareViewModel.clearFareState()
                                            }
                                        )
                                    }
                                    is RouteUiState.Success -> {
                                        when (val fareState = fareUiState) {
                                            is FareUiState.Loading -> {
                                                com.example.swiftride.ui.fare.FareUpdatingCard(
                                                    selectedLanguage = selectedLanguage,
                                                    isDarkMode = isDarkMode
                                                )
                                            }
                                            is FareUiState.Success -> {
                                                val selectedCategory = fareState.categories.find { it.id == selectedCategoryId }
                                                val price = selectedCategoryId?.let { fareState.fares[it]?.totalFare } ?: 0.0
                                                val categoryName = selectedCategory?.name ?: ""

                                                if (bookingState.status == com.example.swiftride.data.booking.RideState.Searching) {
                                                    com.example.swiftride.ui.booking.SearchingDialog(
                                                        selectedLanguage = selectedLanguage,
                                                        isDarkMode = isDarkMode,
                                                        onCancel = {
                                                            bookingViewModel.cancelBooking()
                                                            showBookingReview = false
                                                        }
                                                    )
                                                } else if (showBookingReview || bookingState.status != com.example.swiftride.data.booking.RideState.Idle) {
                                                    com.example.swiftride.ui.booking.BookingBottomSheet(
                                                        bookingState = bookingState,
                                                        pickupName = pickupLocationState?.name ?: "Pickup",
                                                        destinationName = destinationLocationState?.name ?: "Destination",
                                                         categoryName = categoryName,
                                                        farePrice = price,
                                                        distanceKm = routeState.routeData.distanceMeters / 1000.0,
                                                        durationMinutes = (routeState.routeData.durationSeconds / 60.0).toInt(),
                                                        selectedLanguage = selectedLanguage,
                                                        isDarkMode = isDarkMode,
                                                        onConfirm = {
                                                             bookingViewModel.confirmBooking(
                                                                 pickup = pickupCoords,
                                                                 destination = destinationCoords,
                                                                 pickupName = pickupLocationState?.name ?: "Pickup",
                                                                 pickupAddress = pickupLocationState?.fullAddress ?: "",
                                                                 destinationName = destinationLocationState?.name ?: "Destination",
                                                                 destinationAddress = destinationLocationState?.fullAddress ?: "",
                                                                 fare = price,
                                                                 rideCategory = categoryName,
                                                                 durationMinutes = (routeState.routeData.durationSeconds / 60.0).toInt().coerceAtLeast(1)
                                                             )
                                                         },
                                                        onCancel = {
                                                            if (bookingState.status == com.example.swiftride.data.booking.RideState.Idle) {
                                                                showBookingReview = false
                                                            } else {
                                                                bookingViewModel.cancelBooking()
                                                                showBookingReview = false
                                                            }
                                                        },
                                                        onFinish = {
                                                            bookingViewModel.clearBookingState()
                                                            showBookingReview = false
                                                            locationViewModel.setPickup(null)
                                                            locationViewModel.setDestination(null)
                                                            routeViewModel.clearRoute()
                                                            fareViewModel.clearFareState()
                                                        }
                                                    )
                                                } else {
                                                    RideSelectionBottomSheet(
                                                        categories = fareState.categories,
                                                        fares = fareState.fares,
                                                        selectedCategoryId = selectedCategoryId,
                                                        distanceMeters = routeState.routeData.distanceMeters,
                                                        durationSeconds = routeState.routeData.durationSeconds,
                                                        selectedLanguage = selectedLanguage,
                                                        isDarkMode = isDarkMode,
                                                        onCategorySelected = { fareViewModel.selectCategory(it) },
                                                        onCancel = {
                                                            locationViewModel.setPickup(null)
                                                            locationViewModel.setDestination(null)
                                                            routeViewModel.clearRoute()
                                                            fareViewModel.clearFareState()
                                                        },
                                                        onConfirm = {
                                                            if (selectedCategoryId != null) {
                                                                showBookingReview = true
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                            is FareUiState.Error -> {
                                                com.example.swiftride.ui.fare.FareErrorCard(
                                                    message = fareState.message,
                                                    selectedLanguage = selectedLanguage,
                                                    isDarkMode = isDarkMode,
                                                    onRetry = {
                                                        fareViewModel.calculateFares(
                                                            routeState.routeData.distanceMeters,
                                                            routeState.routeData.durationSeconds
                                                        )
                                                    },
                                                    onCancel = {
                                                        locationViewModel.setPickup(null)
                                                        locationViewModel.setDestination(null)
                                                        routeViewModel.clearRoute()
                                                        fareViewModel.clearFareState()
                                                    }
                                                )
                                            }
                                            else -> {}
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
                1 -> {
                    ActivityScreen(selectedLanguage = selectedLanguage)
                }
                2 -> {
                    // Profile Tab: Premium user details layout
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(28.dp))
                                Text(
                                    text = "Account Profile".translate(selectedLanguage),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(32.dp))

                                // Profile details card
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(
                                                BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary),
                                                CircleShape
                                            )
                                            .clickable {
                                                profilePicPickerLauncher.launch("image/*")
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (profileBitmap != null) {
                                            Image(
                                                bitmap = profileBitmap.asImageBitmap(),
                                                contentDescription = "Profile Picture",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                text = initials,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.SansSerif
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(18.dp))
                                    Column {
                                        Text(
                                            text = userName,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "★ 4.95  •  Rider Member",
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(40.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(16.dp))

                                // List of Profile Account Options
                                val profileItems = listOf(
                                    "Your Trips" to "View history and trip receipts",
                                    "Wallet & Payment" to "Manage credit cards & payment options",
                                    "App Settings" to "Manage navigation preferences",
                                    "Privacy & Security" to "Configure two-step verification",
                                    "Help Center" to "Access client support and articles"
                                )
                                
                                profileItems.forEach { (title, subtitle) ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (title == "App Settings") {
                                                    showSettingsDialog = true
                                                } else if (title == "Your Trips") {
                                                    onNavigateToHistory()
                                                }
                                            }
                                            .padding(vertical = 14.dp, horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = title.translate(selectedLanguage),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = subtitle.translate(selectedLanguage),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    }
                                }
                            }

                            // Placeholder Red Logout Option (as Logout will be done later)
                            Column {
                                HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onLogout() } // Preserving callback function for future use
                                        .padding(vertical = 12.dp, horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = "Logout".translate(selectedLanguage),
                                        color = Color(0xFFE91E63), // Red highlight text
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                                Spacer(modifier = Modifier.height(88.dp)) // Extra spacer to clear the bottom floating bar
                            }
                        }
                    }
                }
            }

            if (showSettingsDialog) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = true, onClick = {}), // block clicks to background
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        // Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                                        CircleShape
                                    )
                                    .clickable {
                                        showSettingsDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "←",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "App Settings".translate(selectedLanguage),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Accounts Section
                        Text(
                            text = "ACCOUNT".translate(selectedLanguage),
                            color = Color(0xFF3A86FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF3A86FF).copy(alpha = 0.1f))
                                            .clickable {
                                                profilePicPickerLauncher.launch("image/*")
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (profileBitmap != null) {
                                            Image(
                                                bitmap = profileBitmap.asImageBitmap(),
                                                contentDescription = "Profile Picture",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                text = initials,
                                                color = Color(0xFF3A86FF),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = userName,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Active Account Session".translate(selectedLanguage),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Delete account trigger option
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showDeleteConfirmation = true
                                        }
                                        .padding(vertical = 4.dp)
                                 ) {
                                    Text(
                                        text = "Delete Account".translate(selectedLanguage),
                                        color = Color(0xFFE91E63),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Preferences Section
                        Text(
                            text = "PREFERENCES".translate(selectedLanguage),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "UI Mode".translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    )
                                    ThemeToggleSlider(
                                        isDarkMode = isDarkMode,
                                        onThemeToggle = onThemeToggle,
                                        selectedLanguage = selectedLanguage
                                    )
                                }
                                
                                HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 1.dp)
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "Map Provider".translate(selectedLanguage),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                        Text(
                                            text = "Selected navigation layer".translate(selectedLanguage),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    }
                                    Text(
                                        text = "OpenStreetMap",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                                
                                HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 1.dp)
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "Language".translate(selectedLanguage),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                        Text(
                                            text = "Choose your preferred language".translate(selectedLanguage),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    }
                                    
                                    Box {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                                .border(
                                                    BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                                                     RoundedCornerShape(12.dp)
                                                )
                                                .clickable { showLanguageDropdown = true }
                                                .padding(horizontal = 14.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = AppLanguage.fromCode(selectedLanguage).displayName,
                                                color = Color(0xFF3A86FF),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.SansSerif,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "▼",
                                                color = Color(0xFF3A86FF),
                                                fontSize = 10.sp
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showLanguageDropdown,
                                            onDismissRequest = { showLanguageDropdown = false },
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                                                .border(
                                                    BorderStroke(
                                                        1.dp,
                                                        Brush.linearGradient(
                                                            listOf(
                                                                Color.White.copy(alpha = 0.3f),
                                                                Color.White.copy(alpha = 0.05f)
                                                            )
                                                        )
                                                    ),
                                                    RoundedCornerShape(16.dp)
                                                )
                                                .clip(RoundedCornerShape(16.dp))
                                        ) {
                                             AppLanguage.values().forEach { appLang ->
                                                 val isSelected = appLang.code == selectedLanguage
                                                 DropdownMenuItem(
                                                     text = {
                                                         Text(
                                                             text = appLang.displayName,
                                                             color = if (isSelected) Color(0xFF3A86FF) else MaterialTheme.colorScheme.onSurface,
                                                             fontSize = 14.sp,
                                                             fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                             fontFamily = FontFamily.SansSerif
                                                         )
                                                     },
                                                     onClick = {
                                                         onLanguageChange(appLang.code)
                                                         showLanguageDropdown = false
                                                     },
                                                     modifier = Modifier.background(
                                                         if (isSelected) Color(0xFF3A86FF).copy(alpha = 0.08f)
                                                         else Color.Transparent
                                                     )
                                                 )
                                             }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showDeleteConfirmation) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .clickable(enabled = true, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFFE91E63).copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⚠️", fontSize = 24.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Delete Account?".translate(selectedLanguage),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Text(
                                text = "This will permanently remove your account history, credentials, and settings. This action is irreversible.".translate(selectedLanguage),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.SansSerif,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { showDeleteConfirmation = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDarkMode) Color(0xFF2C2C30) else Color(0xFFE5E5EA),
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Cancel".translate(selectedLanguage),
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                                
                                Button(
                                    onClick = {
                                        showDeleteConfirmation = false
                                        showSettingsDialog = false
                                        onDeleteAccount()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE91E63),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Delete".translate(selectedLanguage),
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                        }
                    }
                }
            }



            if (showLocationInputs) {
                var pickupInputText by remember { mutableStateOf(locationViewModel.pickupLocation.value?.name ?: "") }
                var destinationInputText by remember { mutableStateOf(locationViewModel.destinationLocation.value?.name ?: "") }
                var activeField by remember { mutableStateOf(LocationSearchField.DESTINATION) }

                val suggestions by locationViewModel.searchSuggestions.collectAsState()
                val isLoading by locationViewModel.isLoading.collectAsState()
                val error by locationViewModel.error.collectAsState()
                
                val pickupLoc by locationViewModel.pickupLocation.collectAsState()
                val destLoc by locationViewModel.destinationLocation.collectAsState()

                LaunchedEffect(showLocationInputs) {
                    if (showLocationInputs) {
                        pickupInputText = locationViewModel.pickupLocation.value?.name ?: ""
                        destinationInputText = locationViewModel.destinationLocation.value?.name ?: ""
                        locationViewModel.clearSuggestions()
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    var sheetOffsetY by remember { mutableStateOf(0f) }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.95f)
                            .align(Alignment.BottomCenter)
                            .offset { IntOffset(0, sheetOffsetY.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        sheetOffsetY = (sheetOffsetY + dragAmount.y).coerceAtLeast(0f)
                                    },
                                    onDragEnd = {
                                        if (sheetOffsetY > 180f) {
                                            showLocationInputs = false
                                            validationError = null
                                            locationViewModel.clearSuggestions()
                                        }
                                        sheetOffsetY = 0f
                                    }
                                )
                            }
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(Color(0xFF121214))
                            .border(
                                BorderStroke(1.dp, Color(0xFF1E1E24)),
                                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            )
                            .clickable(enabled = false) {}
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                        ) {
                            // Drag Handle
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 12.dp)
                                    .size(width = 40.dp, height = 4.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            )
                            
                            // Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.05f),
                                                CircleShape
                                            )
                                            .clickable {
                                                showLocationInputs = false
                                                validationError = null
                                                locationViewModel.clearSuggestions()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "←",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Where to?".translate(selectedLanguage),
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))

                            LocationSearchBar(
                                pickupText = pickupInputText,
                                onPickupTextChange = { query ->
                                    pickupInputText = query
                                    validationError = null
                                    if (activeField == LocationSearchField.PICKUP) {
                                        locationViewModel.onSearchQueryChanged(query)
                                    }
                                },
                                destinationText = destinationInputText,
                                onDestinationTextChange = { query ->
                                    destinationInputText = query
                                    validationError = null
                                    if (activeField == LocationSearchField.DESTINATION) {
                                        locationViewModel.onSearchQueryChanged(query)
                                    }
                                },
                                activeField = activeField,
                                onActiveFieldChange = { field ->
                                    activeField = field
                                    val currentText = if (field == LocationSearchField.PICKUP) pickupInputText else destinationInputText
                                    locationViewModel.onSearchQueryChanged(currentText)
                                },
                                selectedLanguage = selectedLanguage
                            )

                            validationError?.let { err ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = err.translate(selectedLanguage),
                                    color = Color(0xFFE91E63),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.TopCenter),
                                        color = Color(0xFF3A86FF)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (activeField == LocationSearchField.PICKUP && pickupInputText.isEmpty()) {
                                        CurrentLocationButton(
                                            selectedLanguage = selectedLanguage,
                                            onClick = {
                                                val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
                                                    context,
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                                val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
                                                    context,
                                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                                if (hasFine || hasCoarse) {
                                                    locationViewModel.fetchCurrentLocation { loc ->
                                                        pickupInputText = loc.name
                                                    }
                                                } else {
                                                    locationPermissionLauncher.launch(
                                                        arrayOf(
                                                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                                        )
                                                    )
                                                }
                                            }
                                        )
                                    }

                                    if (error != null && suggestions.isEmpty() && !isLoading) {
                                        Text(
                                            text = error!!.translate(selectedLanguage),
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }

                                    suggestions.forEach { suggestion ->
                                        SearchSuggestionItem(
                                            suggestion = suggestion,
                                            selectedLanguage = selectedLanguage,
                                            onClick = {
                                                if (activeField == LocationSearchField.PICKUP) {
                                                    locationViewModel.setPickup(suggestion)
                                                    pickupInputText = suggestion.name
                                                } else {
                                                    locationViewModel.setDestination(suggestion)
                                                    destinationInputText = suggestion.name
                                                }
                                                locationViewModel.clearSuggestions()
                                            }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        showLocationInputs = false
                                        validationError = null
                                        locationViewModel.clearSuggestions()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.White.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("Cancel".translate(selectedLanguage))
                                }

                                Button(
                                    onClick = {
                                        if (pickupLoc == null || destLoc == null) {
                                            validationError = "Please select both locations."
                                            return@Button
                                        }

                                        val dist = calculateDistanceKm(
                                            pickupLoc!!.latitude, pickupLoc!!.longitude,
                                            destLoc!!.latitude, destLoc!!.longitude
                                        )

                                        if (dist > 150.0) {
                                            validationError = "Distance cannot exceed 150 km. (Selected: ${String.format(java.util.Locale.US, "%.1f", dist)} km)"
                                            return@Button
                                        }

                                        pickupCoords = GeoPoint(pickupLoc!!.latitude, pickupLoc!!.longitude)
                                        destinationCoords = GeoPoint(destLoc!!.latitude, destLoc!!.longitude)
                                        hasSelectedRoute = true
                                        showLocationInputs = false
                                        validationError = null
                                        locationViewModel.clearSuggestions()
                                    },
                                    enabled = pickupLoc != null && destLoc != null,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Done".translate(selectedLanguage))
                                }
                            }
                        }
                    }
                }
            }

            selectedServiceId?.let { serviceId ->
                val service = services.find { it.id == serviceId }
                if (service != null) {
                    var pickupText by remember(serviceId) { mutableStateOf(locationViewModel.pickupLocation.value?.name ?: "Current Location") }
                    var destText by remember(serviceId) { mutableStateOf(locationViewModel.destinationLocation.value?.name ?: "") }
                    var bookingSuccess by remember { mutableStateOf(false) }
                    var bookingLoading by remember { mutableStateOf(false) }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.75f))
                            .clickable(enabled = true, onClick = { selectedServiceId = null }),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .padding(16.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = { /* Intercept click inside Card to prevent closing */ }
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (!bookingSuccess) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .background(Color(0xFF3A86FF).copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = service.icon,
                                            contentDescription = service.name,
                                            tint = Color(0xFF3A86FF),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Text(
                                        text = "Book ${service.name}".translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Text(
                                        text = service.description.translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    androidx.compose.material3.TextField(
                                        value = pickupText,
                                        onValueChange = { pickupText = it },
                                        placeholder = { Text("Pickup Location".translate(selectedLanguage), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                                        singleLine = true,
                                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.background,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    androidx.compose.material3.TextField(
                                        value = destText,
                                        onValueChange = { destText = it },
                                        placeholder = { Text("Destination Location".translate(selectedLanguage), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                                        singleLine = true,
                                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.background,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = { selectedServiceId = null },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isDarkMode) Color(0xFF2C2C30) else Color(0xFFE5E5EA),
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Cancel".translate(selectedLanguage), fontWeight = FontWeight.Bold)
                                        }
                                        
                                        Button(
                                            onClick = {
                                                if (pickupText.isNotEmpty() && destText.isNotEmpty()) {
                                                    bookingSuccess = true
                                                }
                                            },
                                            enabled = pickupText.isNotEmpty() && destText.isNotEmpty(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF3A86FF),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1.2f)
                                        ) {
                                            Text("Confirm".translate(selectedLanguage), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .background(Color(0xFF4CAF50).copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "✅", fontSize = 28.sp)
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Text(
                                        text = "Booking Confirmed!".translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "Your booking for ${service.name} from $pickupText to $destText has been scheduled successfully.".translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    Button(
                                        onClick = {
                                            selectedServiceId = null
                                            bookingSuccess = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF3A86FF),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth(0.6f)
                                    ) {
                                        Text("Done".translate(selectedLanguage), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Renders an aesthetic dark map mock vector background with glow paths
@Composable
fun OpenStreetMapBackground(
    pickupCoords: GeoPoint,
    destinationCoords: GeoPoint,
    hasSelectedRoute: Boolean,
    routePoints: List<GeoPoint> = emptyList(),
    driverLoc: GeoPoint? = null,
    driverName: String? = null,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Initialize osmdroid configuration
    remember {
        Configuration.getInstance().userAgentValue = context.packageName
        true
    }

    // 1. Remember the MapView to avoid recreation on every recomposition.
    val mapView = remember {
        MapView(context).apply {
            setMultiTouchControls(true)
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
            
            // Premium Dark Matter Tile Provider
            tileProvider.tileSource = XYTileSource(
                "CartoDB_DarkMatter",
                0, 18, 256, ".png",
                arrayOf("https://a.basemaps.cartocdn.com/dark_all/",
                        "https://b.basemaps.cartocdn.com/dark_all/",
                        "https://c.basemaps.cartocdn.com/dark_all/")
            )

            // Centered on Bengaluru initially
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(12.9716, 77.5946))

            // 2. Custom touch listener to prevent scroll parent from intercepting touch events
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN,
                    android.view.MotionEvent.ACTION_MOVE -> {
                        v.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }
        }
    }

    // 3. Lifecycle observer to handle pause/resume and detach to avoid memory leaks
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mapView) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // Track state to prevent unnecessary updates
    val lastState = remember { mutableStateOf<Triple<GeoPoint, GeoPoint, Boolean>?>(null) }
    val routeRenderer = remember(mapView) { com.example.swiftride.map.RouteRenderer(context, mapView) }

    LaunchedEffect(driverLoc, driverName) {
        if (driverLoc != null && driverName != null) {
            routeRenderer.updateDriverMarker(driverLoc, driverName)
        } else {
            routeRenderer.removeDriverMarker()
        }
    }

    AndroidView(
        factory = { mapView },
        update = { mv ->
            val currentState = Triple(pickupCoords, destinationCoords, hasSelectedRoute)
            if (lastState.value != currentState || (hasSelectedRoute && routePoints.isNotEmpty())) {
                lastState.value = currentState
                
                if (hasSelectedRoute) {
                    val pointsToDraw = if (routePoints.isNotEmpty()) routePoints else generateSimulatedRoute(pickupCoords, destinationCoords)
                    routeRenderer.renderRoute(
                        pickup = pickupCoords,
                        destination = destinationCoords,
                        routePoints = pointsToDraw,
                        animatePolyline = true,
                        scope = scope
                    )
                } else {
                    routeRenderer.clearAll()
                    // Default center on Bengaluru
                    mv.controller.setCenter(GeoPoint(12.9716, 77.5946))
                    mv.controller.setZoom(12.0)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun createMinimalCircleMarker(context: android.content.Context, sizeDp: Int = 16): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val px = (sizeDp * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(px, px, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    
    // Draw white outer ring
    paint.style = android.graphics.Paint.Style.FILL
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(px / 2f, px / 2f, px / 2f, paint)
    
    // Draw black inner circle
    paint.color = android.graphics.Color.BLACK
    canvas.drawCircle(px / 2f, px / 2f, px / 2f - (2 * density).coerceAtLeast(1f), paint)

    // Draw white center dot
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(px / 2f, px / 2f, px / 2f - (5 * density).coerceAtLeast(2f), paint)
    
    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

fun createMinimalSquareMarker(context: android.content.Context, sizeDp: Int = 16): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val px = (sizeDp * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(px, px, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    
    // Draw white outer square
    paint.style = android.graphics.Paint.Style.FILL
    paint.color = android.graphics.Color.WHITE
    canvas.drawRect(0f, 0f, px.toFloat(), px.toFloat(), paint)
    
    // Draw black inner square
    paint.color = android.graphics.Color.BLACK
    val border1 = (2 * density).coerceAtLeast(1f)
    canvas.drawRect(border1, border1, px - border1, px - border1, paint)

    // Draw white center square
    paint.color = android.graphics.Color.WHITE
    val border2 = (5 * density).coerceAtLeast(2f)
    canvas.drawRect(border2, border2, px - border2, px - border2, paint)
    
    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

fun generateSimulatedRoute(start: GeoPoint, end: GeoPoint): List<GeoPoint> {
    val points = mutableListOf<GeoPoint>()
    points.add(start)

    val lat1 = start.latitude
    val lon1 = start.longitude
    val lat2 = end.latitude
    val lon2 = end.longitude

    val dLat = lat2 - lat1
    val dLon = lon2 - lon1

    // Deterministic random generator based on coordinates to make the route consistent
    val seed = (lat1 * 100000 + lon1 * 10000 + lat2 * 100 + lon2).toLong()
    val random = java.util.Random(seed)

    // We will generate 8 intermediate steps
    val steps = 8
    var currentLat = lat1
    var currentLon = lon1

    for (i in 1 until steps) {
        val fraction = i.toDouble() / steps
        // Alternate between changing lat and lon to simulate street turns
        if (i % 2 == 1) {
            currentLat = lat1 + fraction * dLat
            // Add a small jitter (e.g. up to 15% of dLon) to look like real streets
            val jitter = (random.nextDouble() - 0.5) * 0.15 * dLon
            currentLon = lon1 + (fraction - 0.05) * dLon + jitter
        } else {
            currentLon = lon1 + fraction * dLon
            // Add a small jitter (e.g. up to 15% of dLat) to look like real streets
            val jitter = (random.nextDouble() - 0.5) * 0.15 * dLat
            currentLat = lat1 + (fraction - 0.05) * dLat + jitter
        }
        
        // Ensure coordinates are bound reasonably between start and end
        val minLat = minOf(lat1, lat2) - 0.005
        val maxLat = maxOf(lat1, lat2) + 0.005
        val minLon = minOf(lon1, lon2) - 0.005
        val maxLon = maxOf(lon1, lon2) + 0.005
        currentLat = currentLat.coerceIn(minLat, maxLat)
        currentLon = currentLon.coerceIn(minLon, maxLon)

        points.add(GeoPoint(currentLat, currentLon))
    }

    points.add(end)
    return points
}



fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Earth radius in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}

@Composable
fun BottomNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (tint: Color) -> Unit,
    label: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) {
        Color(0xFF3A86FF)
    } else {
        if (isDarkMode) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.4f)
    }
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            icon(contentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun HomeIcon(tint: Color, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_home),
        contentDescription = "Home",
        tint = tint,
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun ServicesIcon(tint: Color, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_services),
        contentDescription = "Services",
        tint = tint,
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun ActivityIcon(tint: Color, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_activity),
        contentDescription = "Activity",
        tint = tint,
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun AccountIcon(tint: Color, modifier: Modifier = Modifier, hasNotification: Boolean = true) {
    Box(modifier = modifier.size(24.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_account),
            contentDescription = "Account",
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
        if (hasNotification) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFF2979FF), CircleShape)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun ServicesScreen(
    services: List<ServiceItem>,
    selectedServiceId: String?,
    selectedLanguage: String,
    onServiceClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Services".translate(selectedLanguage),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Go anywhere, get anything".translate(selectedLanguage),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(28.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in services.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (j in 0..1) {
                            if (i + j < services.size) {
                                val item = services[i + j]
                                val isSelected = selectedServiceId == item.id
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(120.dp)
                                        .clickable { onServiceClick(item.id) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF3A86FF) else MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    if (isSelected) Color(0xFF3A86FF).copy(alpha = 0.1f)
                                                    else MaterialTheme.colorScheme.secondary,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.name,
                                                tint = if (isSelected) Color(0xFF3A86FF) else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = item.name.translate(selectedLanguage),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.SansSerif
                                            )
                                            Text(
                                                text = item.description.translate(selectedLanguage),
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
        }
    }
}

@Composable
fun ActivityScreen(selectedLanguage: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Activity".translate(selectedLanguage),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Past & upcoming trips".translate(selectedLanguage),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(28.dp))

            val pastTrips = listOf(
                Triple("SwiftX • Airport Terminal 2", "May 28 • 5:24 PM • $24.50", "Completed"),
                Triple("SwiftPremium • City Center Mall", "May 25 • 8:12 PM • $32.50", "Completed"),
                Triple("SwiftXL • Central Station", "May 20 • 2:30 PM • $18.20", "Completed")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                pastTrips.forEach { trip ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Detail View */ },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.secondary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🚗", fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = trip.first.translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                    Text(
                                        text = trip.second.translate(selectedLanguage),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                            Text(
                                text = trip.third.translate(selectedLanguage),
                                color = Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember {
        androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.example.swiftride.data.local.SwiftRideDatabase::class.java
        ).allowMainThreadQueries().build()
    }
    val mockRepository = remember {
        com.example.swiftride.data.location.LocationRepository(
            com.example.swiftride.data.location.NominatimService(),
            com.example.swiftride.data.location.LocationProvider(context),
            db.savedPlaceDao(),
            db.recentSearchDao()
        )
    }
    val mockViewModel = remember { com.example.swiftride.viewmodel.LocationViewModel(mockRepository) }
    val mockRouteRepository = remember {
        com.example.swiftride.data.routing.RoutingRepository(
            com.example.swiftride.data.routing.MockRoutingService(),
            com.example.swiftride.data.routing.MockRoutingService(),
            com.example.swiftride.data.routing.MockRoutingService(),
            com.example.swiftride.data.routing.MockRoutingService()
        )
    }
    val mockRouteViewModel = remember { com.example.swiftride.viewmodel.RouteViewModel(mockRouteRepository) }
    val mockFareRepository = remember { com.example.swiftride.data.fare.FareRepository() }
    val mockFareViewModel = remember { com.example.swiftride.viewmodel.FareViewModel(mockFareRepository) }
    val mockBookingRepository = remember {
        com.example.swiftride.data.booking.SimulatedBookingRepository(
            com.example.swiftride.data.booking.SimulatedDriverRepository(),
            mockRouteRepository,
            db.rideDao()
        )
    }
    val mockBookingViewModel = remember { com.example.swiftride.viewmodel.BookingViewModel(mockBookingRepository) }
    HomeScreen(
        userName = "Alice",
        isDarkMode = true,
        selectedLanguage = "en-US",
        profilePicturePath = null,
        locationViewModel = mockViewModel,
        routeViewModel = mockRouteViewModel,
        fareViewModel = mockFareViewModel,
        bookingViewModel = mockBookingViewModel,
        onProfilePictureChange = {},
        onThemeToggle = {},
        onLanguageChange = {},
        onLogout = {},
        onDeleteAccount = {},
        onNavigateToHistory = {}
    )
}

@Composable
fun ThemeToggleSlider(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    // Total dimensions
    val width = 140.dp
    val height = 36.dp
    val thumbWidth = 66.dp
    val padding = 3.dp
    
    val targetOffset = if (isDarkMode) (width - thumbWidth - padding) else padding
    val animatedOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "themeSliderOffset"
    )
    
    val trackBg = if (isDarkMode) Color(0xFF1E1E24) else Color(0xFFE5E5EA)
    val thumbBg = if (isDarkMode) Color(0xFF3A86FF) else Color(0xFFFFFFFF)
    
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(CircleShape)
            .background(trackBg)
            .border(
                BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF2C2C35) else Color(0xFFD1D1D6)
                ),
                CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onThemeToggle(!isDarkMode)
            }
            .padding(vertical = padding),
        contentAlignment = Alignment.CenterStart
    ) {
        // Sliding Thumb
        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .width(thumbWidth)
                .fillMaxHeight()
                .shadow(
                    elevation = if (isDarkMode) 0.dp else 3.dp,
                    shape = CircleShape
                )
                .background(
                    color = thumbBg,
                    shape = CircleShape
                )
        )
        
        // Interactive labels
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Light side
            Box(
                modifier = Modifier
                    .width(width / 2)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isDarkMode) onThemeToggle(false)
                    },
                contentAlignment = Alignment.Center
            ) {
                val lightTextColor = if (!isDarkMode) {
                    Color.Black // On white thumb
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "☀️",
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Light".translate(selectedLanguage),
                        color = lightTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
            
            // Dark side
            Box(
                modifier = Modifier
                    .width(width / 2)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isDarkMode) onThemeToggle(true)
                    },
                contentAlignment = Alignment.Center
            ) {
                val darkTextColor = if (isDarkMode) {
                    Color.White // On blue thumb
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🌙",
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dark".translate(selectedLanguage),
                        color = darkTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceBubble(
    name: String,
    icon: @Composable () -> Unit,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .shadow(elevation = 4.dp, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun ForYouBubble(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        // Circular bubble
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF222222))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
