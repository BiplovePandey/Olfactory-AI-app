package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single source of truth for Olfactory AI authentication state.
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(val userId: Int, val name: String, val email: String) : AuthState()
    data class Guest(val guestId: String = "guest_perfumer") : AuthState()
    object SessionExpired : AuthState()
    data class AuthError(val message: String) : AuthState()
}

/**
 * Secure Session Manager using private Android SharedPreferences.
 * Stores session tokens/user identifiers and never stores plaintext passwords.
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "olfactory_atelier_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_GUEST = "is_guest"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_SESSION_TOKEN = "session_token"
    }

    fun saveUserSession(userId: Int, name: String, email: String, token: String = "atelier_token_${userId}") {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putBoolean(KEY_IS_GUEST, false)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_SESSION_TOKEN, token)
            .apply()
    }

    fun saveGuestSession() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putBoolean(KEY_IS_GUEST, true)
            .putInt(KEY_USER_ID, 0)
            .putString(KEY_USER_NAME, "Guest Perfumer")
            .putString(KEY_USER_EMAIL, "guest@olfactory.ai")
            .putString(KEY_SESSION_TOKEN, "guest_session")
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_IS_LOGGED_IN)
            .remove(KEY_IS_GUEST)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_SESSION_TOKEN)
            .apply()
    }

    fun restoreAuthState(): AuthState {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return AuthState.Unauthenticated

        val isGuest = prefs.getBoolean(KEY_IS_GUEST, false)
        if (isGuest) return AuthState.Guest()

        val userId = prefs.getInt(KEY_USER_ID, 1)
        val userName = prefs.getString(KEY_USER_NAME, "Connoisseur") ?: "Connoisseur"
        val userEmail = prefs.getString(KEY_USER_EMAIL, "user@olfactory.ai") ?: "user@olfactory.ai"

        return AuthState.Authenticated(userId, userName, userEmail)
    }

    val currentUserId: Int
        get() = prefs.getInt(KEY_USER_ID, 1)

    val currentUserName: String
        get() = prefs.getString(KEY_USER_NAME, "Guest") ?: "Guest"

    val currentUserEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    val isGuest: Boolean
        get() = prefs.getBoolean(KEY_IS_GUEST, false)
}
