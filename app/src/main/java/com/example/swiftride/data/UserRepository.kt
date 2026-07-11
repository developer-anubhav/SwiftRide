package com.example.swiftride.data

import android.content.Context
import android.content.SharedPreferences

class UserRepository (context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "swiftride_auth_prefs",
        Context.MODE_PRIVATE
    )

    init {
        // Seed Admin User
        val adminEmail = "anubhavfordev24@gmail.com"
        val normalizedAdminEmail = adminEmail.trim().lowercase()
        if (!prefs.contains("user_pw_$normalizedAdminEmail")) {
            prefs.edit().apply {
                putString("user_pw_$normalizedAdminEmail", "admin123") // Default testing password
                putString("user_name_$normalizedAdminEmail", "Anubhav Das")
                apply()
            }
        }
    }

    /**
     * Registers a new user.
     * Returns true if successful, false if user already exists or inputs are empty.
     */
    fun registerUser(email: String, password: String, name: String): Boolean {
        if (email.isBlank() || password.length < 4 || name.isBlank()) {
            return false
        }
        
        val normalizedEmail = email.trim().lowercase()
        
        // Check if user already exists
        if (prefs.contains("user_pw_$normalizedEmail")) {
            return false
        }

        // Save password and name
        prefs.edit().apply {
            putString("user_pw_$normalizedEmail", password)
            putString("user_name_$normalizedEmail", name.trim())
            putString("logged_in_user_email", normalizedEmail) // Automatically log in on register
            apply()
        }
        return true
    }

    /**
     * Checks user credentials.
     * Returns the user's name if verification succeeds, or null if it fails.
     */
    fun loginUser(email: String, password: String): String? {
        if (email.isBlank() || password.isBlank()) {
            return null
        }
        
        val normalizedEmail = email.trim().lowercase()
        val storedPassword = prefs.getString("user_pw_$normalizedEmail", null)
        
        if (storedPassword != null && storedPassword == password) {
            val name = prefs.getString("user_name_$normalizedEmail", "Rider") ?: "Rider"
            // Set session
            prefs.edit().putString("logged_in_user_email", normalizedEmail).apply()
            return name
        }
        return null
    }

    /**
     * Clears the current logged-in user state.
     */
    fun logout() {
        prefs.edit().remove("logged_in_user_email").apply()
    }

    /**
     * Deletes the currently logged-in user account.
     * Returns true if successful, false if there is no logged-in user.
     */
    fun deleteCurrentUser(): Boolean {
        val loggedInEmail = prefs.getString("logged_in_user_email", null) ?: return false
        prefs.edit().apply {
            remove("user_pw_$loggedInEmail")
            remove("user_name_$loggedInEmail")
            remove("logged_in_user_email")
            apply()
        }
        return true
    }

    /**
     * Returns the name of the currently logged-in user, if any.
     */
    fun getCurrentUser(): String? {
        val loggedInEmail = prefs.getString("logged_in_user_email", null) ?: return null
        return prefs.getString("user_name_$loggedInEmail", "Rider") ?: "Rider"
    }

    /**
     * Checks if dark mode is enabled. Defaults to true.
     */
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean("app_theme_dark", true)
    }

    /**
     * Sets dark mode preference.
     */
    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("app_theme_dark", enabled).apply()
    }

    /**
     * Gets the selected app language. Defaults to "en-US".
     */
    fun getSelectedLanguage(): String {
        return prefs.getString("app_language", "en-US") ?: "en-US"
    }

    /**
     * Sets the selected app language.
     */
    fun setSelectedLanguage(languageCode: String) {
        prefs.edit().putString("app_language", languageCode).apply()
    }

    /**
     * Gets the profile picture path of the currently logged-in user.
     */
    fun getProfilePicturePath(): String? {
        val loggedInEmail = prefs.getString("logged_in_user_email", null) ?: return null
        return prefs.getString("user_profile_pic_$loggedInEmail", null)
    }

    /**
     * Sets the profile picture path of the currently logged-in user.
     */
    fun setProfilePicturePath(path: String?) {
        val loggedInEmail = prefs.getString("logged_in_user_email", null) ?: return
        if (path == null) {
            prefs.edit().remove("user_profile_pic_$loggedInEmail").apply()
        } else {
            prefs.edit().putString("user_profile_pic_$loggedInEmail", path).apply()
        }
    }
}
