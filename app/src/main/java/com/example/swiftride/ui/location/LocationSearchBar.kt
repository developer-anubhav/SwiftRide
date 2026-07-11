package com.example.swiftride.ui.location

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.translate

enum class LocationSearchField {
    PICKUP, DESTINATION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchBar(
    pickupText: String,
    onPickupTextChange: (String) -> Unit,
    destinationText: String,
    onDestinationTextChange: (String) -> Unit,
    activeField: LocationSearchField,
    onActiveFieldChange: (LocationSearchField) -> Unit,
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E24), RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF00E5FF), CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(44.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.White, RoundedCornerShape(2.dp))
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                value = pickupText,
                onValueChange = onPickupTextChange,
                placeholder = {
                    Text(
                        text = "Pickup Location".translate(selectedLanguage),
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF16161E),
                    unfocusedContainerColor = Color(0xFF121216),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = if (activeField == LocationSearchField.PICKUP) Color(0xFF00E5FF) else Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    if (pickupText.isNotEmpty()) {
                        Text(
                            text = "✕",
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clickable { onPickupTextChange("") }
                                .padding(8.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .onFocusChanged { state ->
                        if (state.isFocused) {
                            onActiveFieldChange(LocationSearchField.PICKUP)
                        }
                    }
            )

            TextField(
                value = destinationText,
                onValueChange = onDestinationTextChange,
                placeholder = {
                    Text(
                        text = "Where to?".translate(selectedLanguage),
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF16161E),
                    unfocusedContainerColor = Color(0xFF121216),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = if (activeField == LocationSearchField.DESTINATION) Color(0xFF00E5FF) else Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    if (destinationText.isNotEmpty()) {
                        Text(
                            text = "✕",
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clickable { onDestinationTextChange("") }
                                .padding(8.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .onFocusChanged { state ->
                        if (state.isFocused) {
                            onActiveFieldChange(LocationSearchField.DESTINATION)
                        }
                    }
            )
        }
    }
}
