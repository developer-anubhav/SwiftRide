package com.example.swiftride.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.booking.SimulatedDriver
import com.example.swiftride.data.translate

@Composable
fun DriverCard(
    driver: SimulatedDriver,
    etaMinutes: Int,
    distanceMeters: Double,
    selectedLanguage: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val distanceKm = distanceMeters / 1000.0
    val distanceStr = if (distanceKm < 0.1) {
        "Nearby".translate(selectedLanguage)
    } else {
        String.format(java.util.Locale.US, "%.1f km away", distanceKm).translate(selectedLanguage)
    }

    val etaStr = if (etaMinutes <= 0) {
        "Arrived".translate(selectedLanguage)
    } else {
        "Arriving in $etaMinutes min".translate(selectedLanguage)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF9F9F9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row 1: Profile Initials, Name, Rating, ETA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Driver Avatar Circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A86FF).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = driver.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase(),
                        color = Color(0xFF3A86FF),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name & Rating
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = driver.name,
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", driver.rating),
                            color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                // ETA & Distance Info
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = etaStr,
                        color = Color(0xFF3A86FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = distanceStr,
                        color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
            Spacer(modifier = Modifier.height(16.dp))

            // Row 2: Vehicle details and Call/Message buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vehicle Details
                Column {
                    Text(
                        text = "${driver.vehicleColor} ${driver.vehicleName}",
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = driver.vehicleNumber,
                        color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                // Call & Message buttons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledIconButton(
                        onClick = { /* Call placeholder */ },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
                            contentColor = if (isDarkMode) Color.White else Color.Black
                        ),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = { /* Message placeholder */ },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF3A86FF).copy(alpha = 0.15f),
                            contentColor = Color(0xFF3A86FF)
                        ),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "Message",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
