package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.ui.viewmodel.OlfactoryViewModel
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IndianRose
import com.example.ui.theme.KannaujKhus
import com.example.ui.theme.MittiClay
import com.example.ui.theme.MysoreSandal
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ParchmentFaint
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite
import kotlinx.coroutines.delay

/**
 * Atmospheric Luxury Editorial Splash
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.92f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        scaleAnim.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        delay(1400)
        onSplashComplete()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient gold radial background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GoldPrimary.copy(alpha = haloPulse * 0.5f),
                        AmberAccent.copy(alpha = haloPulse * 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.7f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Emblem: Hexagonal flacon core with radiating gold threads
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ObsidianElevated, ObsidianSurface)
                        )
                    )
                    .border(1.dp, GoldPrimary.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(64.dp)) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f

                    // Cap
                    drawRect(
                        brush = Brush.linearGradient(listOf(GoldBright, GoldPrimary, MysoreSandal)),
                        topLeft = Offset(cx - w * 0.16f, h * 0.12f),
                        size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.18f)
                    )
                    // Bottle
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(cx - w * 0.28f, h * 0.35f)
                        lineTo(cx + w * 0.28f, h * 0.35f)
                        lineTo(cx + w * 0.38f, h * 0.82f)
                        lineTo(cx - w * 0.38f, h * 0.82f)
                        close()
                    }
                    drawPath(path, color = GoldPrimary.copy(alpha = 0.85f))
                    // Essence core
                    drawCircle(
                        color = GoldBright,
                        radius = w * 0.12f,
                        center = Offset(cx, h * 0.58f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "OLFACTORY AI",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 32.sp,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Light
                ),
                color = ParchmentWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "HAUTE PARFUMERIE & SCENT COMPILATION ATELIER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 2.0.sp
                ),
                color = GoldPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(1.dp)
                    .background(GoldPrimary.copy(alpha = 0.6f))
            )
        }
    }
}

/**
 * 3-Card Interactive Atelier Onboarding
 */
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        Triple(
            "The 8-Dimensional Olfactory Space",
            "Transcend generic note pyramids. Our proprietary AI vector space maps each fragrance across 8 physical and sensorial axes—Freshness, Sweetness, Intensity, Woody, Floral, Warm Resinous Spices, Earthy Clay, and Fixative Longevity.",
            Icons.Default.Science
        ),
        Triple(
            "East-Meets-West Layering",
            "Bridge centuries of Kannauj hydro-distillation with contemporary Parisian Haute Parfumerie. Compound pulse-point botanical attars with luminous diffusion sprays for unprecedented depth.",
            Icons.Default.Spa
        ),
        Triple(
            "Algorithmic Scent Chords",
            "Compose harmonious bespoke signatures. The Olfactory AI engine evaluates pairwise chord synergy, molecular evaporation rates, and weather thermodynamics to orchestrate your daily ritual.",
            Icons.Default.AutoAwesome
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(24.dp)
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ATELIER ORIENTATION",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                letterSpacing = 1.2.sp
            )
            TextButton(onClick = onFinishOnboarding) {
                Text(
                    text = "SKIP",
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentMuted
                )
            }
        }

        // Center card
        val current = pages[currentPage]
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(ObsidianCardBorder, Color.Transparent)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(ObsidianElevated)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = current.third,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = current.first,
                    style = MaterialTheme.typography.headlineSmall,
                    color = ParchmentWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = current.second,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ParchmentMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        // Footer controls (Dots + Action Button)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Page indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in pages.indices) {
                    val isSelected = i == currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) GoldPrimary else ParchmentFaint.copy(alpha = 0.4f))
                    )
                }
            }

            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onFinishOnboarding()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_continue_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (currentPage < pages.size - 1) "EXPLORE NEXT PRINCIPLE" else "ENTER THE ATELIER",
                    style = MaterialTheme.typography.labelLarge.copy(color = ObsidianBlack, fontWeight = FontWeight.Bold),
                    letterSpacing = 1.0.sp
                )
            }
        }
    }
}

/**
 * Minimalist Haute Parfumerie Atelier Login & Access
 */
@Composable
fun AuthScreen(
    viewModel: OlfactoryViewModel? = null,
    onAuthSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("connoisseur@olfactory.ai") }
    var password by remember { mutableStateOf("atelier2026") }
    var name by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("auth_screen"),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) {
            Text(
                text = "OLFACTORY AI",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isSignUp) "Begin Your Collection" else "Enter Your Olfactory World",
                style = MaterialTheme.typography.headlineMedium,
                color = ParchmentWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isSignUp)
                    "Request private atelier credentials to harmonize bespoke fragrance wardrobes."
                else
                    "Access your personal fragrance wardrobe, custom chords & compounding laboratory.",
                style = MaterialTheme.typography.bodyMedium,
                color = ParchmentMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error banner
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(IndianRose, Color.Transparent))),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = IndianRose),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Optional Name Field for Signup
            if (isSignUp) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Your Name / Title", color = ParchmentMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = ParchmentWhite,
                        unfocusedTextColor = ParchmentWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = { Text("Atelier Email", color = ParchmentMuted) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GoldPrimary) },
                modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = ObsidianCardBorder,
                    focusedTextColor = ParchmentWhite,
                    unfocusedTextColor = ParchmentWhite
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field with toggle
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Atelier Passkey", color = ParchmentMuted) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = ParchmentMuted
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = ObsidianCardBorder,
                    focusedTextColor = ParchmentWhite,
                    unfocusedTextColor = ParchmentWhite
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Button
            Button(
                onClick = {
                    if (viewModel != null) {
                        isLoading = true
                        errorMessage = null
                        if (isSignUp) {
                            viewModel.signUp(name, email, password) { success, err ->
                                isLoading = false
                                if (success) {
                                    onAuthSuccess()
                                } else {
                                    errorMessage = err ?: "Unable to create atelier profile."
                                }
                            }
                        } else {
                            viewModel.signIn(email, password) { success, err ->
                                isLoading = false
                                if (success) {
                                    onAuthSuccess()
                                } else {
                                    errorMessage = err ?: "Those credentials didn't open the atelier."
                                }
                            }
                        }
                    } else {
                        onAuthSuccess()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("auth_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = ObsidianBlack,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = if (isSignUp) "COMMENCE ATELIER APPRENTICESHIP" else "ENTER WITH CONNOISSEUR PROFILE",
                        style = MaterialTheme.typography.labelLarge.copy(color = ObsidianBlack, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Guest / Demo Access Button
            Button(
                onClick = {
                    if (viewModel != null) {
                        viewModel.continueAsGuest {
                            onAuthSuccess()
                        }
                    } else {
                        onAuthSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_guest_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianCard),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "CONTINUE AS GUEST PERFUMER",
                    style = MaterialTheme.typography.labelSmall.copy(color = ParchmentWhite)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    isSignUp = !isSignUp
                    errorMessage = null
                }
            ) {
                Text(
                    text = if (isSignUp) "Already holding membership? Enter here" else "New to Olfactory AI? Request private atelier credentials",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = GoldBright
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
