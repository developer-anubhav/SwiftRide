package com.example.swiftride.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftride.data.UserRepository
import com.example.swiftride.data.translate

@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val selectedLanguage = remember { userRepository.getSelectedLanguage() }

    // State to toggle between Login and Register modes
    var isLoginMode by remember { mutableStateOf(true) }

    // Input States
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Error Feedback State
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF3A86FF), // Royal blue background
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper Content
            Column {
                Spacer(modifier = Modifier.height(28.dp))

                // App Brand Name Header (matching Splash Screen styling)
                Text(
                    text = "SwiftRide",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contextual Subtitle
                Text(
                    text = (if (isLoginMode) "Welcome back! Let's get riding." else "Create your account to get started.").translate(selectedLanguage),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Conditionally display "Full Name" input in Register mode with smooth transition
                AnimatedVisibility(
                    visible = !isLoginMode,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Column {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name".translate(selectedLanguage)) },
                            placeholder = { Text("Enter your name".translate(selectedLanguage)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF121214),
                                unfocusedContainerColor = Color(0xFF121214),
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color(0xFF2C2C2E),
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                                focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                                cursorColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }
                }

                // Email / Phone Input Field
                OutlinedTextField(
                    value = emailOrPhone,
                    onValueChange = { emailOrPhone = it },
                    label = { Text("Email or Phone Number".translate(selectedLanguage)) },
                    placeholder = { Text("example@swiftride.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF121214),
                        unfocusedContainerColor = Color(0xFF121214),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0xFF2C2C2E),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password".translate(selectedLanguage)) },
                    placeholder = { Text("Min. 8 characters".translate(selectedLanguage)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        Text(
                            text = (if (passwordVisible) "HIDE" else "SHOW").translate(selectedLanguage),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable { passwordVisible = !passwordVisible }
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF121214),
                        unfocusedContainerColor = Color(0xFF121214),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0xFF2C2C2E),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Forgot Password link (only in Login mode)
                if (isLoginMode) {
                    Text(
                        text = "Forgot password?".translate(selectedLanguage),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { /* Handle Forgot Password */ }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Error message display
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!.translate(selectedLanguage),
                        color = Color(0xFFE91E63), // Pinkish-red error color matching dark theme
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary CTA: Uber-style solid White button with black text
                Button(
                    onClick = {
                        errorMessage = null
                        if (isLoginMode) {
                            val userName = userRepository.loginUser(emailOrPhone, password)
                            if (userName != null) {
                                onAuthSuccess(userName)
                            } else {
                                errorMessage = "Invalid email/phone or password"
                            }
                        } else {
                            if (name.isBlank() || emailOrPhone.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all fields"
                            } else if (password.length < 4) {
                                errorMessage = "Password must be at least 4 characters"
                            } else {
                                val success = userRepository.registerUser(emailOrPhone, password, name)
                                if (success) {
                                    onAuthSuccess(name)
                                } else {
                                    errorMessage = "User already exists"
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = (if (isLoginMode) "Continue" else "Create Account").translate(selectedLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // "Or" Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF2C2C2E),
                        thickness = 1.dp
                    )
                    Text(
                        text = "or".translate(selectedLanguage),
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontFamily = FontFamily.SansSerif
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF2C2C2E),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Buttons (Google & Apple Logins)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Google Auth Button
                    OutlinedButton(
                        onClick = { /* Handle Google Sign In */ },
                        border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Google".translate(selectedLanguage),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    // Apple Auth Button
                    OutlinedButton(
                        onClick = { /* Handle Apple Sign In */ },
                        border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "A",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Apple".translate(selectedLanguage),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }
            }

            // Bottom Mode Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (if (isLoginMode) "New to SwiftRide? " else "Already have an account? ").translate(selectedLanguage),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = (if (isLoginMode) "Sign Up" else "Log In").translate(selectedLanguage),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.clickable {
                        isLoginMode = !isLoginMode
                        errorMessage = null
                        // Reset forms
                        emailOrPhone = ""
                        password = ""
                        name = ""
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen(onAuthSuccess = {})
}
