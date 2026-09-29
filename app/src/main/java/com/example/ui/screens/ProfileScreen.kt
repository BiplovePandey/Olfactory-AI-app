package com.example.ui.screens

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fragrance
import com.example.data.model.OlfactoryVector8D
import com.example.data.model.SavedCombination
import com.example.olfactory.ml.OlfactoryEngine
import com.example.ui.components.AtmosphericFragranceCanvas
import com.example.ui.components.ChapterDivider
import com.example.ui.components.OlfactoryAtelierTopBar
import com.example.ui.components.OlfactoryVectorRadar
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
import com.example.ui.theme.OudDark
import com.example.ui.theme.ParchmentFaint
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite
import com.example.ui.viewmodel.OlfactoryViewModel
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: OlfactoryViewModel,
    onSignOut: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val preferences by viewModel.userPreferences.collectAsState()
    val wardrobe by viewModel.wardrobe.collectAsState()
    val allFragrances by viewModel.allFragrances.collectAsState()
    val savedCombinations by viewModel.savedCombinations.collectAsState()
    val backendConnected by viewModel.backendConnected.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()

    var editingUrl by remember { mutableStateOf(serverUrl) }
    var showUrlConfig by remember { mutableStateOf(false) }

    // Derive composite Living Olfactory DNA vector from user preferences & wardrobe
    val compositeDnaVector = remember(preferences, wardrobe) {
        val baseVec = OlfactoryEngine.createUserPreferenceVector(preferences)
        if (wardrobe.isEmpty()) {
            OlfactoryVector8D(
                freshness = (baseVec[0] * 100).roundToInt(),
                sweetness = (baseVec[1] * 100).roundToInt(),
                intensity = (baseVec[2] * 100).roundToInt(),
                woody = (baseVec[3] * 100).roundToInt(),
                floral = (baseVec[4] * 100).roundToInt(),
                warm_resinous_spices = (baseVec[5] * 100).roundToInt(),
                earthy_clay = (baseVec[6] * 100).roundToInt(),
                longevity_fixative = (baseVec[7] * 100).roundToInt()
            )
        } else {
            // Aggregate wardrobe vector bias
            var freshnessSum = baseVec[0] * 100
            var sweetnessSum = baseVec[1] * 100
            var intensitySum = baseVec[2] * 100
            var woodySum = baseVec[3] * 100
            var floralSum = baseVec[4] * 100
            var spicySum = baseVec[5] * 100
            var earthySum = baseVec[6] * 100
            var longevitySum = baseVec[7] * 100

            wardrobe.forEach { flacon ->
                val v = OlfactoryEngine.getVector8D100(flacon)
                freshnessSum += v.freshness * 0.4f
                sweetnessSum += v.sweetness * 0.4f
                intensitySum += v.intensity * 0.4f
                woodySum += v.woody * 0.4f
                floralSum += v.floral * 0.4f
                spicySum += v.warm_resinous_spices * 0.4f
                earthySum += v.earthy_clay * 0.4f
                longevitySum += v.longevity_fixative * 0.4f
            }

            val scale = 1.0f + (wardrobe.size * 0.4f)
            OlfactoryVector8D(
                freshness = (freshnessSum / scale).roundToInt().coerceIn(10, 100),
                sweetness = (sweetnessSum / scale).roundToInt().coerceIn(10, 100),
                intensity = (intensitySum / scale).roundToInt().coerceIn(10, 100),
                woody = (woodySum / scale).roundToInt().coerceIn(10, 100),
                floral = (floralSum / scale).roundToInt().coerceIn(10, 100),
                warm_resinous_spices = (spicySum / scale).roundToInt().coerceIn(10, 100),
                earthy_clay = (earthySum / scale).roundToInt().coerceIn(10, 100),
                longevity_fixative = (longevitySum / scale).roundToInt().coerceIn(10, 100)
            )
        }
    }

    // Determine archetype title & signature aura based on highest vector affinities
    val archetypeTitle = remember(compositeDnaVector) {
        val scores = listOf(
            "The Earth & Petrichor Alchemist" to compositeDnaVector.earthy_clay,
            "The Sacred Woods & Resins Connoisseur" to compositeDnaVector.woody,
            "The Radiant Solar Botanist" to compositeDnaVector.freshness,
            "The Opulent Spice Custodian" to compositeDnaVector.warm_resinous_spices,
            "The Velvet Flora Virtuoso" to compositeDnaVector.floral,
            "The High-Sillage Master" to compositeDnaVector.intensity
        ).sortedByDescending { it.second }
        scores.firstOrNull()?.first ?: "The Bespoke Atelier Connoisseur"
    }

    val primaryFamilyPreference = preferences.favorite_family.firstOrNull() ?: wardrobe.firstOrNull()?.fragrance_family

    Box(modifier = Modifier.fillMaxSize()) {
        AtmosphericFragranceCanvas(
            modifier = Modifier.fillMaxSize(),
            activeAtmosphere = primaryFamilyPreference
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("profile_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Top Bar
            item {
                OlfactoryAtelierTopBar(
                    title = "Living Olfactory DNA",
                    subtitle = "Portrait • Constellation • Cabinet Affinities"
                )
            }

            // 1. PORTRAIT HERO
            item {
                PortraitHeroSection(
                    userName = userProfile.name,
                    userEmail = userProfile.email,
                    archetypeTitle = archetypeTitle,
                    wardrobeCount = wardrobe.size,
                    savedChordsCount = savedCombinations.size,
                    dnaVector = compositeDnaVector
                )
            }

            // 2. INTERACTIVE SCENT CONSTELLATION
            item {
                ChapterDivider(
                    chapter = "CONSTELLATION",
                    title = "INTERACTIVE SCENT CONSTELLATION",
                    subtitle = "Dynamic harmonic orbit of your wardrobe nodes across olfactory coordinate space"
                )
            }

            item {
                InteractiveScentConstellationCard(
                    wardrobe = wardrobe,
                    allFragrances = allFragrances,
                    dnaVector = compositeDnaVector
                )
            }

            // 3. SCENT TERRITORIES (8D RADAR & DIMENSIONAL BREAKDOWN)
            item {
                ChapterDivider(
                    chapter = "TERRITORIES",
                    title = "SCENT TERRITORIES & VECTOR GEOMETRY",
                    subtitle = "Normalized 8-dimensional olfactory coordinates and volatility tendencies"
                )
            }

            item {
                ScentTerritoriesCard(
                    vector = compositeDnaVector,
                    preferences = preferences,
                    onUpdatePreferences = { viewModel.updatePreferences(it) }
                )
            }

            // 4. CABINET AFFINITIES & CROSS-ORIGIN HARMONY
            item {
                ChapterDivider(
                    chapter = "AFFINITIES",
                    title = "CABINET AFFINITIES & BOTANICAL ANCHORS",
                    subtitle = "Distribution between heritage Indian attars and western haute perfumery"
                )
            }

            item {
                CabinetAffinitiesCard(
                    wardrobe = wardrobe
                )
            }

            // 5. CLIMATE & SEASONAL RESONANCE DOSSIER
            item {
                ChapterDivider(
                    chapter = "RESONANCE",
                    title = "CLIMATE & SEASONAL RESONANCE",
                    subtitle = "Atmospheric volatility compatibility across monsoon, humidity, and temperate drydown"
                )
            }

            item {
                PersonalDossierSummaryCard(
                    dnaVector = compositeDnaVector,
                    wardrobe = wardrobe,
                    preferences = preferences
                )
            }

            // 6. SAVED SCENT CHORDS ARCHIVE (Room DB)
            item {
                ChapterDivider(
                    chapter = "ARCHIVES",
                    title = "BESPOKE SCENT CHORDS ARCHIVE",
                    subtitle = "Persistent formulation vault of compounded harmonies (${savedCombinations.size})"
                )
            }

            if (savedCombinations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianCard)
                            .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No saved scent chords in your vault. Create a pairing in the Layering Studio and save it to compound your living archive.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ParchmentMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(savedCombinations) { combo ->
                    SavedChordArchiveCard(
                        combo = combo,
                        onDelete = { viewModel.deleteSavedCombination(combo.id) }
                    )
                }
            }

            // 7. SHARED BACKEND INTEGRATION & SESSION TELEMETRY
            item {
                ChapterDivider(
                    chapter = "TELEMETRY",
                    title = "SHARED ARCHITECTURE & ATELIER SESSION",
                    subtitle = "Real-time synchronization with Olfactory AI backend service"
                )
            }

            item {
                BackendSessionTelemetryCard(
                    backendConnected = backendConnected,
                    serverUrl = serverUrl,
                    editingUrl = editingUrl,
                    showUrlConfig = showUrlConfig,
                    onToggleConfig = { showUrlConfig = !showUrlConfig },
                    onEditingUrlChange = { editingUrl = it },
                    onSaveUrl = {
                        viewModel.updateServerUrl(editingUrl)
                        showUrlConfig = false
                    },
                    onRefresh = { viewModel.checkBackendConnection() }
                )
            }

            // Disconnect Atelier Session
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = onSignOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianCard),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = ParchmentMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DISCONNECT ATELIER SESSION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ParchmentMuted,
                                letterSpacing = 1.1.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 1. PORTRAIT HERO
// ---------------------------------------------------------------------------
@Composable
private fun PortraitHeroSection(
    userName: String,
    userEmail: String,
    archetypeTitle: String,
    wardrobeCount: Int,
    savedChordsCount: Int,
    dnaVector: OlfactoryVector8D
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.6f), Color.Transparent))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Crest Flacon Medallion
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(GoldPrimary.copy(alpha = 0.25f), ObsidianElevated)
                            )
                        )
                        .border(1.5.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LIVING OLFACTORY DNA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.8.sp
                        ),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userName.ifBlank { "Atelier Connoisseur" },
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = ParchmentWhite
                    )
                    Text(
                        text = userEmail.ifBlank { "guest@atelier.olfactory.ai" },
                        style = MaterialTheme.typography.bodySmall,
                        color = ParchmentMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Archetype Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianElevated)
                    .border(0.5.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "PRIMARY ARCHETYPE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                            color = GoldPrimary
                        )
                        Text(
                            text = archetypeTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ParchmentWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeroStatBadge(
                    label = "WARDROBE FLACONS",
                    value = wardrobeCount.toString(),
                    icon = Icons.Default.Grain,
                    modifier = Modifier.weight(1f)
                )
                HeroStatBadge(
                    label = "BESPOKE CHORDS",
                    value = savedChordsCount.toString(),
                    icon = Icons.Default.Hub,
                    modifier = Modifier.weight(1f)
                )
                HeroStatBadge(
                    label = "HARMONY SILLAGE",
                    value = "${dnaVector.intensity}%",
                    icon = Icons.Default.Thermostat,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeroStatBadge(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ObsidianElevated)
            .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, letterSpacing = 0.5.sp),
                    color = ParchmentMuted,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ParchmentWhite
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 2. INTERACTIVE SCENT CONSTELLATION
// ---------------------------------------------------------------------------
@Composable
private fun InteractiveScentConstellationCard(
    wardrobe: List<Fragrance>,
    allFragrances: List<Fragrance>,
    dnaVector: OlfactoryVector8D
) {
    val displayNodes = remember(wardrobe, allFragrances) {
        if (wardrobe.isNotEmpty()) wardrobe.take(8)
        else allFragrances.take(6)
    }

    var selectedNode by remember { mutableStateOf<Fragrance?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "constellation_spin")
    val orbitPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ATELIER SCENT CONSTELLATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "${displayNodes.size} NODES ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = GoldBright
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Constellation Canvas Orbit Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianElevated)
                    .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val outerRadius = minOf(cx, cy) * 0.78f
                    val innerRadius = outerRadius * 0.52f

                    // Draw orbital rings
                    drawCircle(
                        color = GoldPrimary.copy(alpha = 0.18f),
                        radius = outerRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1f)
                    )
                    drawCircle(
                        color = GoldPrimary.copy(alpha = 0.25f),
                        radius = innerRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1f)
                    )

                    // Draw core sun (Self Living DNA Node)
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(GoldBright, GoldPrimary, AmberAccent.copy(alpha = 0.4f), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = 28f
                        ),
                        radius = 20f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = GoldBright,
                        radius = 6f,
                        center = Offset(cx, cy)
                    )

                    // Draw flacon orbiting nodes & connective filaments
                    val nodeCount = displayNodes.size
                    if (nodeCount > 0) {
                        val angleStep = (2 * Math.PI / nodeCount).toFloat()
                        displayNodes.forEachIndexed { index, flacon ->
                            val radius = if (index % 2 == 0) outerRadius else innerRadius
                            val angle = (index * angleStep) + orbitPhase
                            val nx = cx + (radius * cos(angle))
                            val ny = cy + (radius * sin(angle))

                            val familyColor = when {
                                flacon.fragrance_family.contains("wood", true) -> MysoreSandal
                                flacon.fragrance_family.contains("earth", true) -> MittiClay
                                flacon.fragrance_family.contains("flora", true) -> IndianRose
                                flacon.fragrance_family.contains("khus", true) -> KannaujKhus
                                flacon.fragrance_family.contains("spic", true) -> AmberAccent
                                else -> GoldPrimary
                            }

                            // Harmonic connective line to center
                            drawLine(
                                color = familyColor.copy(alpha = 0.3f),
                                start = Offset(cx, cy),
                                end = Offset(nx, ny),
                                strokeWidth = 1f
                            )

                            // Cross filament between adjacent nodes
                            if (index > 0) {
                                val prevRadius = if ((index - 1) % 2 == 0) outerRadius else innerRadius
                                val prevAngle = ((index - 1) * angleStep) + orbitPhase
                                val px = cx + (prevRadius * cos(prevAngle))
                                val py = cy + (prevRadius * sin(prevAngle))
                                drawLine(
                                    color = GoldPrimary.copy(alpha = 0.15f),
                                    start = Offset(px, py),
                                    end = Offset(nx, ny),
                                    strokeWidth = 0.8f
                                )
                            }

                            // Node body
                            drawCircle(
                                color = familyColor.copy(alpha = 0.35f),
                                radius = 14f,
                                center = Offset(nx, ny)
                            )
                            drawCircle(
                                color = familyColor,
                                radius = 5.5f,
                                center = Offset(nx, ny)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Central gold nucleus reflects your composite 8D Olfactory DNA. Orbital satellites represent your active flacons anchored by chemical volatility.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ParchmentMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 3. SCENT TERRITORIES & VECTOR GEOMETRY
// ---------------------------------------------------------------------------
@Composable
private fun ScentTerritoriesCard(
    vector: OlfactoryVector8D,
    preferences: com.example.data.model.UserPreferences,
    onUpdatePreferences: (com.example.data.model.UserPreferences) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "SCENT TERRITORIES & VOLATILITY TUNING",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                letterSpacing = 1.2.sp
            )

            // Central 8D Radar Visualization
            OlfactoryVectorRadar(
                vector = vector,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "VOLATILITY CALIBRATION SLIDERS",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                color = GoldPrimary
            )

            // Scent Intensity Preference
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Scent Intensity & Sillage Threshold", style = MaterialTheme.typography.bodyMedium, color = ParchmentWhite)
                    Text("${preferences.intensity}/10", style = MaterialTheme.typography.labelSmall, color = GoldBright)
                }
                Slider(
                    value = preferences.intensity.toFloat(),
                    onValueChange = { onUpdatePreferences(preferences.copy(intensity = it.toInt())) },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldBright,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = ObsidianElevated
                    )
                )
            }

            // Sweetness Tolerance
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sweetness Tolerance (Gourmand / Amber)", style = MaterialTheme.typography.bodyMedium, color = ParchmentWhite)
                    Text("${preferences.sweetness}/10", style = MaterialTheme.typography.labelSmall, color = GoldBright)
                }
                Slider(
                    value = preferences.sweetness.toFloat(),
                    onValueChange = { onUpdatePreferences(preferences.copy(sweetness = it.toInt())) },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldBright,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = ObsidianElevated
                    )
                )
            }

            // Freshness Affinity
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Freshness Affinity (Citrus / Petrichor)", style = MaterialTheme.typography.bodyMedium, color = ParchmentWhite)
                    Text("${preferences.freshness}/10", style = MaterialTheme.typography.labelSmall, color = GoldBright)
                }
                Slider(
                    value = preferences.freshness.toFloat(),
                    onValueChange = { onUpdatePreferences(preferences.copy(freshness = it.toInt())) },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldBright,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = ObsidianElevated
                    )
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. CABINET AFFINITIES & BOTANICAL ANCHORS
// ---------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CabinetAffinitiesCard(
    wardrobe: List<Fragrance>
) {
    val totalCount = wardrobe.size
    val attarCount = wardrobe.count { it.is_oil_based || it.format == "Attar" }
    val sprayCount = totalCount - attarCount

    val families = remember(wardrobe) {
        wardrobe.groupBy { it.fragrance_family }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "CABINET BOTANICAL AFFINITIES",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                letterSpacing = 1.2.sp
            )

            // Ratio comparison bar: Indian Attar vs Spray EDP
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Deg-Bhapka Botanical Attars ($attarCount)",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldBright
                    )
                    Text(
                        text = "Western Luxury EDP ($sprayCount)",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParchmentMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val attarRatio = if (totalCount > 0) attarCount.toFloat() / totalCount else 0.5f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(ObsidianElevated)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(attarRatio.coerceAtLeast(0.01f))
                                .fillMaxSize()
                                .background(GoldPrimary)
                        )
                        Box(
                            modifier = Modifier
                                .weight((1f - attarRatio).coerceAtLeast(0.01f))
                                .fillMaxSize()
                                .background(AmberAccent.copy(alpha = 0.5f))
                        )
                    }
                }
            }

            // Top Olfactory Family Breakdown Tags
            Text(
                text = "PRIMARY FAMILY DENSITY",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                color = GoldPrimary
            )

            if (families.isEmpty()) {
                Text(
                    text = "Add flacons to your wardrobe to visualize family densities.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentMuted
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    families.forEach { (family, count) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ObsidianElevated)
                                .border(0.5.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$family: $count flacon${if (count > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = ParchmentWhite
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 5. CLIMATE & SEASONAL RESONANCE DOSSIER
// ---------------------------------------------------------------------------
@Composable
private fun PersonalDossierSummaryCard(
    dnaVector: OlfactoryVector8D,
    wardrobe: List<Fragrance>,
    preferences: com.example.data.model.UserPreferences
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CLIMATE & VOLATILITY COMPLIANCE",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                letterSpacing = 1.2.sp
            )

            DossierResonanceRow(
                title = "Monsoon & High Humidity",
                description = "High dampness enhances base fixatives like Khus and Mitti while taming sharp citrus top notes.",
                score = "Optimal",
                icon = Icons.Default.Public
            )

            DossierResonanceRow(
                title = "Dry Heat & Solar Radiance",
                description = "Warm climate rapidly accelerates volatile citrus; pairing with sandalwood base anchors evaporation.",
                score = "92% Fit",
                icon = Icons.Default.WbSunny
            )

            DossierResonanceRow(
                title = "Temperate Autumn / Winter",
                description = "Chilly air contracts molecular diffusion; high-intensity oud, resins, and spiced chai thrive.",
                score = "96% Fit",
                icon = Icons.Default.Park
            )
        }
    }
}

@Composable
private fun DossierResonanceRow(
    title: String,
    description: String,
    score: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ObsidianElevated)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldBright,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = ParchmentWhite
                )
                Text(
                    text = score,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = ParchmentMuted
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 6. SAVED CHORD ARCHIVE CARD
// ---------------------------------------------------------------------------
@Composable
private fun SavedChordArchiveCard(
    combo: SavedCombination,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .testTag("saved_combo_${combo.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = combo.chord_title,
                    style = MaterialTheme.typography.titleMedium,
                    color = ParchmentWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${combo.fragrance_a_name} × ${combo.fragrance_b_name}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = GoldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!combo.explanation.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = combo.explanation,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = ParchmentMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Compatibility Score Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoldPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${combo.compatibility_score}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_combo_${combo.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Chord",
                    tint = ParchmentFaint
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 7. BACKEND TELEMETRY CARD
// ---------------------------------------------------------------------------
@Composable
private fun BackendSessionTelemetryCard(
    backendConnected: Boolean,
    serverUrl: String,
    editingUrl: String,
    showUrlConfig: Boolean,
    onToggleConfig: () -> Unit,
    onEditingUrlChange: (String) -> Unit,
    onSaveUrl: () -> Unit,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (backendConnected) Icons.Default.Cloud else Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = if (backendConnected) KannaujKhus else AmberAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (backendConnected) "SHARED BACKEND: ONLINE" else "OFFLINE AUTONOMOUS ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (backendConnected) KannaujKhus else AmberAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retry Sync", tint = GoldPrimary)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (backendConnected)
                    "Connected to Olfactory AI Express backend. Layering scores, DNA vectors, and wardrobe synchronize across your devices."
                else
                    "Operating in high-performance autonomous offline mode with Room local database and native 8-dimensional vector engine.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = ParchmentMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Endpoint: ${serverUrl.take(28)}...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = ParchmentFaint
                )

                Text(
                    text = if (showUrlConfig) "HIDE" else "CONFIG",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = GoldBright),
                    modifier = Modifier.clickable { onToggleConfig() }
                )
            }

            if (showUrlConfig) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = editingUrl,
                    onValueChange = onEditingUrlChange,
                    label = { Text("Custom API Base URL", color = ParchmentMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = ParchmentWhite,
                        unfocusedTextColor = ParchmentWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onSaveUrl,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("UPDATE ENDPOINT & TEST CONNECTION", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

