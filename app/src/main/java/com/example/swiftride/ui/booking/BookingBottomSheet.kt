package com.example.swiftride.ui.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.booking.RideBookingState
import com.example.swiftride.data.booking.RideState
import com.example.swiftride.data.translate

@Composable
fun BookingBottomSheet(
    bookingState: RideBookingState,
    pickupName: String,
    destinationName: String,
    categoryName: String,
    farePrice: Double,
    distanceKm: Double,
    durationMinutes: Int,
    selectedLanguage: String,
    isDarkMode: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF151515) else Color(0xFFFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 20.dp, end = 20.dp, top = 16.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0),
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (bookingState.status) {
                RideState.Idle -> {
                    // Review details & confirm ride
                    Text(
                        text = "Confirm your Trip".translate(selectedLanguage),
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Locations Display
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF2F2F7),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Pickup",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Pickup Location".translate(selectedLanguage),
                                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Text(
                                    text = pickupName,
                                    color = if (isDarkMode) Color.White else Color.Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }

                        HorizontalDivider(color = if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Destination",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Destination".translate(selectedLanguage),
                                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Text(
                                    text = destinationName,
                                    color = if (isDarkMode) Color.White else Color.Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selected Ride details row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFE5E5EA)),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = Color(0xFF3A86FF),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = categoryName.translate(selectedLanguage),
                                    color = if (isDarkMode) Color.White else Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                                val distanceStr = if (distanceKm < 0.1) {
                                    "< 0.1 km"
                                } else {
                                    String.format(java.util.Locale.US, "%.1f km", distanceKm)
                                }
                                Text(
                                    text = "$distanceStr • $durationMinutes min",
                                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                        
                        Text(
                            text = String.format(java.util.Locale.US, "₹%.0f", farePrice),
                            color = if (isDarkMode) Color.White else Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isDarkMode) Color.White else Color.Black
                            )
                        ) {
                            Text(
                                text = "Cancel".translate(selectedLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3A86FF),
                                contentColor = if (isDarkMode) Color.Black else Color.White
                            )
                        ) {
                            Text(
                                text = "Confirm Ride".translate(selectedLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                RideState.Searching -> {
                    // Searching view handles via SearchingDialog overlay in HomeScreen
                }

                RideState.DriverAssigned,
                RideState.DriverAccepted,
                RideState.DriverArriving,
                RideState.DriverReachedPickup,
                RideState.RideStarted -> {
                    // Display Active Driver & Trip Status
                    Text(
                        text = "Trip Status".translate(selectedLanguage),
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RideStatusCard(
                        status = bookingState.status,
                        selectedLanguage = selectedLanguage,
                        isDarkMode = isDarkMode
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    bookingState.assignedDriver?.let { driver ->
                        DriverCard(
                            driver = driver,
                            etaMinutes = bookingState.etaMinutes,
                            distanceMeters = bookingState.distanceMeters,
                            selectedLanguage = selectedLanguage,
                            isDarkMode = isDarkMode
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (bookingState.status != RideState.RideStarted) {
                        // Allow cancel if driver is still arriving
                        Button(
                            onClick = onCancel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkMode) Color(0xFF222222) else Color(0xFFF2F2F7),
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                text = "Cancel Ride".translate(selectedLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                RideState.RideCompleted -> {
                    // Show Completion details
                    Text(
                        text = "Ride Completed!".translate(selectedLanguage),
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Hope you had a comfortable journey with SwiftRide".translate(selectedLanguage),
                        color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    bookingState.assignedDriver?.let { driver ->
                        DriverCard(
                            driver = driver,
                            etaMinutes = 0,
                            distanceMeters = 0.0,
                            selectedLanguage = selectedLanguage,
                            isDarkMode = isDarkMode
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onFinish,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3A86FF),
                            contentColor = if (isDarkMode) Color.Black else Color.White
                        )
                    ) {
                        Text(
                            text = "Done".translate(selectedLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                RideState.RideCancelled -> {
                    // Cancelled info
                    Text(
                        text = "Booking Cancelled".translate(selectedLanguage),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = bookingState.errorMessage ?: "The booking request has been cancelled.".translate(selectedLanguage),
                        color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onFinish,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkMode) Color(0xFF222222) else Color(0xFFF2F2F7),
                            contentColor = if (isDarkMode) Color.White else Color.Black
                        )
                    ) {
                        Text(
                            text = "Close".translate(selectedLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
