package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fragrance
import com.example.ui.components.AmbientAtmosphereBadge
import com.example.ui.components.AtelierFlaconGraphic
import com.example.ui.components.AtmosphericFragranceCanvas
import com.example.ui.components.ChapterDivider
import com.example.ui.components.OlfactoryFlaconCard
import com.example.ui.components.atelier.AtmosphereSoundscapeSection
import com.example.ui.components.atelier.InteractiveDrydownSection
import com.example.ui.components.atelier.ScentAcademyDialog
import com.example.ui.components.atelier.WeatherCalibrationDialog
import com.example.ui.components.atelier.WhatShouldIWearDialog
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.KannaujKhus
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite
import com.example.ui.viewmodel.OlfactoryViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: OlfactoryViewModel,
    onNavigateToLayeringStudio: () -> Unit,
    onNavigateToDetail: (Fragrance) -> Unit,
    onNavigateToHeritage: () -> Unit
) {
    val fragrances by viewModel.allFragrances.collectAsState()
    val wardrobe by viewModel.wardrobe.collectAsState()
    val wardrobeIds = wardrobe.map { it.id }.toSet()

    val weather by viewModel.weatherCondition.collectAsState()
    val scentOfTheDay by viewModel.scentOfTheDay.collectAsState()
    val alignment by viewModel.weatherAlignment.collectAsState()
    val activeAtmosphere by viewModel.currentAtmosphere.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // Dialog visibility states
    var showWeatherDialog by remember { mutableStateOf(false) }
    var showAdvisorDialog by remember { mutableStateOf(false) }
    var showAcademyDialog by remember { mutableStateOf(false) }
    var ritualAppliedSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(ritualAppliedSuccess) {
        if (ritualAppliedSuccess) {
            delay(3000)
            ritualAppliedSuccess = false
        }
    }

    // Curated Ritual of the Day pair:
    val ritualFlaconA = scentOfTheDay ?: fragrances.firstOrNull { it.format == "Attar" || it.is_oil_based } ?: fragrances.getOrNull(0)
    val ritualFlaconB = fragrances.firstOrNull { it.id != ritualFlaconA?.id && !it.is_oil_based } ?: fragrances.getOrNull(1)

    Box(modifier = Modifier.fillMaxSize()) {
        // Dynamic Scent Family Atmospheric Canvas Background
        AtmosphericFragranceCanvas(
            modifier = Modifier.fillMaxSize(),
            activeAtmosphere = activeAtmosphere ?: ritualFlaconA?.fragrance_family
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen"),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // =========================================================================
            // TOP BAR: METEOROLOGICAL CONTEXT & CEREMONIAL GREETING
            // =========================================================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val displayName = when (authState) {
                        is com.example.data.auth.AuthState.Authenticated -> (authState as com.example.data.auth.AuthState.Authenticated).name
                        is com.example.data.auth.AuthState.Guest -> "Guest Perfumer"
                        else -> userProfile.name
                    }

                    // Weather calibration capsule pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(ObsidianElevated)
                                .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .clickable { showWeatherDialog = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("weather_recalibrate_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${weather.temperatureC}°C · ${weather.humidityPct}% · ${weather.season} ${weather.timeOfDay}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = ParchmentWhite
                                )
                                Text(
                                    text = "Recalibrate",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = GoldBright
                                )
                            }
                        }

                        AmbientAtmosphereBadge(
                            currentAtmosphere = activeAtmosphere ?: ritualFlaconA?.fragrance_family
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GOOD ${weather.timeOfDay.uppercase()}, ${displayName.uppercase()}.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldPrimary,
                            letterSpacing = 1.8.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "“What does the atmosphere call for today?”",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.4.sp
                        ),
                        color = ParchmentWhite
                    )
                }
            }

            // =========================================================================
            // CHAPTER I: THE ATELIER HERO (SCENT OF THE DAY & FIT SCORE)
            // =========================================================================
            item {
                ChapterDivider(
                    chapter = "CHAPTER I",
                    title = "THE PROTAGONIST FLACON",
                    subtitle = "Meteo-calibrated olfactory protagonist and real-time vapor affinity"
                )
            }

            if (scentOfTheDay != null) {
                val hero = scentOfTheDay!!
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("hero_flacon_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(GoldBright.copy(alpha = 0.8f), GoldPrimary.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Atmospheric Score Ring Badge
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(ObsidianElevated)
                                    .border(2.dp, GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${alignment.score}%",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = GoldBright
                                    )
                                    Text(
                                        text = "HARMONY",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                        color = ParchmentMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Large Flacon Graphic
                            AtelierFlaconGraphic(
                                fragrance = hero,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clickable {
                                        viewModel.setDetailFragrance(hero)
                                        onNavigateToDetail(hero)
                                    },
                                isCompact = false
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = hero.brand_name.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = GoldPrimary,
                                letterSpacing = 1.4.sp
                            )

                            Text(
                                text = hero.name,
                                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                                color = ParchmentWhite,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "${hero.concentration} · ${hero.fragrance_family}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ParchmentMuted
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Poetic Advisory
                            Text(
                                text = "“${alignment.advisory}”",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                color = ParchmentWhite.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Note badges
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                hero.top_notes.take(2).forEach { note ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ObsidianElevated)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("🌿 $note", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ParchmentWhite)
                                    }
                                }
                                hero.base_notes.take(1).forEach { note ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ObsidianElevated)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("🪵 $note", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = GoldPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Main Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Discover My Scent button
                                Button(
                                    onClick = { showAdvisorDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = null,
                                        tint = ObsidianBlack,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DISCOVER",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ObsidianBlack,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                // Surprise Me button
                                OutlinedButton(
                                    onClick = { viewModel.surpriseScentOfTheDay() },
                                    modifier = Modifier.weight(1f),
                                    border = ButtonDefaults.outlinedButtonBorder().copy(
                                        brush = Brush.linearGradient(listOf(GoldPrimary, GoldBright))
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shuffle,
                                        contentDescription = null,
                                        tint = GoldBright,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SURPRISE ME",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GoldBright,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Wear Today CTA
                            Button(
                                onClick = {
                                    viewModel.recordWearRitual(hero, ritualFlaconB)
                                    ritualAppliedSuccess = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (ritualAppliedSuccess) KannaujKhus else ObsidianElevated
                                )
                            ) {
                                Icon(
                                    imageVector = if (ritualAppliedSuccess) Icons.Default.Check else Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = if (ritualAppliedSuccess) ObsidianBlack else GoldBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (ritualAppliedSuccess) "RITUAL APPLIED TO SKIN" else "WEAR TODAY'S RITUAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (ritualAppliedSuccess) ObsidianBlack else ParchmentWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // CHAPTER II: THE DAY'S ACCORD (HARMONIC COMPOUNDING)
            // =========================================================================
            if (ritualFlaconA != null && ritualFlaconB != null) {
                item {
                    ChapterDivider(
                        chapter = "CHAPTER II",
                        title = "THE DAY'S ACCORD",
                        subtitle = "Harmonic synergy of volatile sparks and alluvial fixative foundations"
                    )
                }

                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectFragrances(ritualFlaconA, ritualFlaconB)
                                    onNavigateToLayeringStudio()
                                }
                                .testTag("hero_ritual_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    listOf(GoldPrimary.copy(alpha = 0.6f), Color.Transparent)
                                )
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "CURATED HARMONIC DUET",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldPrimary,
                                        letterSpacing = 1.2.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(GoldPrimary.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "97% HARMONY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = GoldBright
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${ritualFlaconA.name} & ${ritualFlaconB.name}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ParchmentWhite
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Damp Kannauj baked earth attar paired with effervescent Italian bergamot spray creates an enchanting post-monsoon aroma.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ParchmentMuted
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Dual flacon preview
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ObsidianElevated)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AtelierFlaconGraphic(
                                            fragrance = ritualFlaconA,
                                            modifier = Modifier.size(54.dp),
                                            isCompact = true
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = ritualFlaconA.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ParchmentWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Base Anchor",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = GoldPrimary
                                        )
                                    }

                                    Text(
                                        text = "+",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = GoldBright
                                    )

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AtelierFlaconGraphic(
                                            fragrance = ritualFlaconB,
                                            modifier = Modifier.size(54.dp),
                                            isCompact = true
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = ritualFlaconB.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ParchmentWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Diffusion Mist",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = GoldPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(GoldPrimary)
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Science,
                                            contentDescription = null,
                                            tint = ObsidianBlack,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "OPEN IN LAYERING STUDIO",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = ObsidianBlack,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // CHAPTER III: TIME REVEALS THE COMPOSITION (DRYDOWN KINETICS)
            // =========================================================================
            if (scentOfTheDay != null) {
                item {
                    ChapterDivider(
                        chapter = "CHAPTER III",
                        title = "TIME REVEALS THE COMPOSITION",
                        subtitle = "Isothermal evaporation dynamics and continuous skin drydown kinetics"
                    )
                }

                item {
                    InteractiveDrydownSection(fragrance = scentOfTheDay!!)
                }
            }

            // =========================================================================
            // CHAPTER IV: THE AIR AROUND THE SCENT (ATMOSPHERE & SOUNDSCAPES)
            // =========================================================================
            item {
                ChapterDivider(
                    chapter = "CHAPTER IV",
                    title = "THE AIR AROUND THE SCENT",
                    subtitle = "Synesthetic acoustics, atmospheric humidity, and Kannauj hydrodistillation soundscapes"
                )
            }

            item {
                AtmosphereSoundscapeSection(
                    soundscapeEngine = viewModel.soundscapeEngine,
                    currentAtmosphere = activeAtmosphere,
                    onSetAtmosphere = { viewModel.setAtmosphere(it) },
                    onOpenWeatherModal = { showWeatherDialog = true }
                )
            }

            // =========================================================================
            // CHAPTER V: THE ATELIER'S INSTRUMENTS (PORTALS, HERITAGE & ARCHIVE)
            // =========================================================================
            item {
                ChapterDivider(
                    chapter = "CHAPTER V",
                    title = "THE ATELIER'S INSTRUMENTS",
                    subtitle = "Artisanal heritage portals and computational olfactory intelligence"
                )
            }

            // Interactive Portals
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Scent Academy Portal
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showAcademyDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Scent Academy",
                                style = MaterialTheme.typography.titleSmall,
                                color = ParchmentWhite
                            )
                            Text(
                                text = "Kannauj Heritage",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ParchmentMuted
                            )
                        }
                    }

                    // Layering Studio Portal
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToLayeringStudio() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.5f), Color.Transparent))
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Layering Studio",
                                style = MaterialTheme.typography.titleSmall,
                                color = ParchmentWhite
                            )
                            Text(
                                text = "Compound Chords",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ParchmentMuted
                            )
                        }
                    }

                    // Heritage Chamber Portal
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToHeritage() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Heritage Degs",
                                style = MaterialTheme.typography.titleSmall,
                                color = ParchmentWhite
                            )
                            Text(
                                text = "Hydrodistillates",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ParchmentMuted
                            )
                        }
                    }
                }
            }

            // Indian Heritage Carousel
            val heritageAttars = fragrances.filter { it.is_oil_based || it.format == "Attar" }
            if (heritageAttars.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INDIAN HERITAGE DISTILLATIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPrimary,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(heritageAttars) { fragrance ->
                                Card(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .clickable {
                                            viewModel.setDetailFragrance(fragrance)
                                            onNavigateToDetail(fragrance)
                                        }
                                        .testTag("home_attar_card_${fragrance.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        AtelierFlaconGraphic(
                                            fragrance = fragrance,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp),
                                            isCompact = false
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = fragrance.brand_name.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = GoldPrimary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = fragrance.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                            color = ParchmentWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = fragrance.format,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = ParchmentMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Featured Atelier Archive
            val westernEdps = fragrances.filter { !it.is_oil_based && it.format != "Attar" }.take(6)
            if (westernEdps.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                        Text(
                            text = "FEATURED DIFFUSION FLACONS",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPrimary,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                items(westernEdps) { fragrance ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        OlfactoryFlaconCard(
                            fragrance = fragrance,
                            isInWardrobe = wardrobeIds.contains(fragrance.id),
                            onCardClick = {
                                viewModel.setDetailFragrance(fragrance)
                                onNavigateToDetail(fragrance)
                            },
                            onWardrobeToggle = {
                                viewModel.toggleWardrobe(fragrance.id, !wardrobeIds.contains(fragrance.id))
                            },
                            onLayerClick = {
                                viewModel.startLayeringWith(fragrance)
                                onNavigateToLayeringStudio()
                            }
                        )
                    }
                }
            }
        }

        // =========================================================================
        // MODALS & DIALOGS
        // =========================================================================
        if (showWeatherDialog) {
            WeatherCalibrationDialog(
                currentWeather = weather,
                onDismiss = { showWeatherDialog = false },
                onSelectPreset = { preset ->
                    viewModel.setWeatherPreset(preset)
                },
                onApplyCustom = { temp, hum, cond ->
                    viewModel.setWeather(temp, hum, cond)
                }
            )
        }

        if (showAdvisorDialog) {
            WhatShouldIWearDialog(
                allFragrances = fragrances,
                currentWeather = weather,
                onDismiss = { showAdvisorDialog = false },
                onOpenInLayeringStudio = { fragA, fragB ->
                    if (fragB != null) {
                        viewModel.selectFragrances(fragA, fragB)
                    } else {
                        viewModel.selectFragranceA(fragA)
                    }
                    onNavigateToLayeringStudio()
                },
                onApplyWear = { frag, partner ->
                    viewModel.setHeroFragrance(frag)
                    viewModel.recordWearRitual(frag, partner)
                    ritualAppliedSuccess = true
                }
            )
        }

        if (showAcademyDialog) {
            ScentAcademyDialog(onDismiss = { showAcademyDialog = false })
        }
    }
}
