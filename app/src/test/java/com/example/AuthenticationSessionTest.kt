package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.auth.AuthState
import com.example.data.auth.SessionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthenticationSessionTest {

    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sessionManager = SessionManager(context)
        sessionManager.clearSession()
    }

    @Test
    fun `initial state is unauthenticated when no session exists`() {
        val state = sessionManager.restoreAuthState()
        assertTrue(state is AuthState.Unauthenticated)
        assertFalse(sessionManager.isGuest)
    }

    @Test
    fun `successful login saves authenticated session`() {
        sessionManager.saveUserSession(1, "Connoisseur", "user@olfactory.ai")

        val state = sessionManager.restoreAuthState()
        assertTrue(state is AuthState.Authenticated)
        val authenticated = state as AuthState.Authenticated
        assertEquals(1, authenticated.userId)
        assertEquals("Connoisseur", authenticated.name)
        assertEquals("user@olfactory.ai", authenticated.email)
        assertFalse(sessionManager.isGuest)
    }

    @Test
    fun `guest session restores as guest auth state`() {
        sessionManager.saveGuestSession()

        val state = sessionManager.restoreAuthState()
        assertTrue(state is AuthState.Guest)
        assertTrue(sessionManager.isGuest)
        assertEquals("Guest Perfumer", sessionManager.currentUserName)
    }

    @Test
    fun `logout clears local authentication state and restores unauthenticated`() {
        sessionManager.saveUserSession(1, "Connoisseur", "user@olfactory.ai")
        assertTrue(sessionManager.restoreAuthState() is AuthState.Authenticated)

        sessionManager.clearSession()
        val postLogoutState = sessionManager.restoreAuthState()
        assertTrue(postLogoutState is AuthState.Unauthenticated)
    }

    @Test
    fun `custom user signup saves session credentials`() {
        sessionManager.saveUserSession(42, "Scent Artisan", "artisan@atelier.org")

        val state = sessionManager.restoreAuthState()
        assertTrue(state is AuthState.Authenticated)
        val authenticated = state as AuthState.Authenticated
        assertEquals(42, authenticated.userId)
        assertEquals("Scent Artisan", authenticated.name)
        assertEquals("artisan@atelier.org", authenticated.email)
    }
}
