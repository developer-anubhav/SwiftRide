package com.example.swiftride.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.booking.RideState
import com.example.swiftride.data.translate

@Composable
fun RideStatusCard(
    status: RideState,
    selectedLanguage: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val statusText = when (status) {
        RideState.DriverAssigned -> "Driver Assigned"
        RideState.DriverAccepted -> "Driver Accepted your request"
        RideState.DriverArriving -> "Driver is arriving at pickup point"
        RideState.DriverReachedPickup -> "Driver has reached your pickup point"
        RideState.RideStarted -> "Your trip has started! Have a safe ride"
        RideState.RideCompleted -> "Trip Completed! Thank you for riding with us"
        else -> ""
    }.translate(selectedLanguage)

    val progress = when (status) {
        RideState.DriverAssigned -> 0.15f
        RideState.DriverAccepted -> 0.3f
        RideState.DriverArriving -> 0.6f
        RideState.DriverReachedPickup -> 0.8f
        RideState.RideStarted -> 0.95f
        RideState.RideCompleted -> 1.0f
        else -> 0.0f
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF3A86FF), CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = statusText,
                color = if (isDarkMode) Color.White else Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = Color(0xFF3A86FF),
            trackColor = if (isDarkMode) Color(0xFF222222) else Color(0xFFE5E5EA)
        )
    }
}
