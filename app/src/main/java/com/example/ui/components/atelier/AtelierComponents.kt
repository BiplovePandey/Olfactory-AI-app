package com.example.ui.components.atelier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.olfactory.audio.AmbientSoundscapeEngine
import com.example.olfactory.weather.WeatherCondition
import com.example.olfactory.weather.WeatherEngine
import com.example.olfactory.weather.WeatherPreset
import com.example.ui.components.AtelierFlaconGraphic
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IndianRose
import com.example.ui.theme.KannaujKhus
import com.example.ui.theme.MittiClay
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite

/**
 * Atmospheric Weather Calibration Dialog allowing the user to select climate presets
 * or adjust ambient temperature and humidity to recalibrate volatility modifiers.
 */
@Composable
fun WeatherCalibrationDialog(
    currentWeather: WeatherCondition,
    onDismiss: () -> Unit,
    onSelectPreset: (WeatherPreset) -> Unit,
    onApplyCustom: (temp: Int, humidity: Int, conditionId: String) -> Unit
) {
    var temp by remember { mutableIntStateOf(currentWeather.temperatureC) }
    var humidity by remember { mutableIntStateOf(currentWeather.humidityPct) }
    var selectedConditionId by remember { mutableStateOf(currentWeather.conditionId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "RECALIBRATE ATMOSPHERE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ParchmentWhite
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Ambient temperature and relative humidity fundamentally alter top-note evaporation velocity and fragrance fixative anchoring.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentMuted
                )

                // Presets list
                Text(
                    text = "CLIMATIC PRESETS",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = GoldPrimary
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    WeatherEngine.PRESETS.forEach { preset ->
                        val isSelected = preset.id == selectedConditionId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else ObsidianElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldBright else ObsidianCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedConditionId = preset.id
                                    temp = preset.temp
                                    humidity = preset.humidity
                                    onSelectPreset(preset)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = preset.label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) GoldBright else ParchmentWhite
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Temperature Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TEMPERATURE",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary
                    )
                    Text(
                        text = "$temp°C",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberAccent
                    )
                }
                Slider(
                    value = temp.toFloat(),
                    onValueChange = { temp = it.toInt() },
                    valueRange = 10f..42f,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldBright,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = ObsidianCardBorder
                    )
                )

                // Humidity Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RELATIVE HUMIDITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary
                    )
                    Text(
                        text = "$humidity%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = KannaujKhus
                    )
                }
                Slider(
                    value = humidity.toFloat(),
                    onValueChange = { humidity = it.toInt() },
                    valueRange = 20f..95f,
                    colors = SliderDefaults.colors(
                        thumbColor = KannaujKhus,
                        activeTrackColor = KannaujKhus.copy(alpha = 0.8f),
                        inactiveTrackColor = ObsidianCardBorder
                    )
                )

                // Evaporation Dynamics Note
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianElevated)
                        .padding(10.dp)
                ) {
                    val statusText = when {
                        temp >= 30 -> "🔥 High Heat: Volatile alcohol evaporates rapidly; non-alcoholic attars and citrus sparkle."
                        temp <= 18 -> "❄️ Cool Atmosphere: Heavy ambers, oud, and sandalwood bloom with lingering projection."
                        humidity >= 65 -> "🌧️ Humid Atmosphere: Dense air traps petrichor, rose, and vetiver in an expansive sillage cloud."
                        else -> "✨ Temperate Climate: Linear, balanced three-phase evaporation pyramid."
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ParchmentMuted
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyCustom(temp, humidity, selectedConditionId)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("APPLY ATMOSPHERE", color = ObsidianBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ParchmentMuted)
            }
        },
        containerColor = ObsidianCard,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Interactive Evolution & Drydown Card (Chapter III)
 * Demonstrates the 3 distinct phases of fragrance evaporation.
 */
@Composable
fun InteractiveDrydownSection(
    fragrance: Fragrance,
    modifier: Modifier = Modifier
) {
    var selectedStage by remember { mutableIntStateOf(0) }

    val stages = listOf(
        Triple("Top Sparks", "0 – 30 Minutes", "Volatile monoterpenes and alcohol evaporate, releasing crisp radiant sparks."),
        Triple("Heart Bloom", "30 Min – 3 Hours", "Aromatic core unfolds. Florals, spices, and herbaceous distillates harmonise on warm skin."),
        Triple("Alluvial Base", "3 – 8+ Hours", "Heavy sesquiterpenes, sandalwood, aged resins, and musk anchor the intimate drydown skin chord.")
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "ISOTHERMAL DRYDOWN KINETICS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = GoldBright,
                        letterSpacing = 1.2.sp
                    )
                }

                Text(
                    text = "Lifespan: ${fragrance.longevity}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ParchmentMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Observe how ${fragrance.name} transforms on living skin:",
                style = MaterialTheme.typography.bodyMedium,
                color = ParchmentWhite
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stage Selector Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stages.forEachIndexed { index, (title, time, _) ->
                    val isSelected = selectedStage == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) GoldPrimary else ObsidianElevated)
                            .border(
                                1.dp,
                                if (isSelected) GoldBright else ObsidianCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedStage = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) ObsidianBlack else ParchmentWhite
                            )
                            Text(
                                text = time,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = if (isSelected) ObsidianBlack.copy(alpha = 0.8f) else ParchmentMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active stage content
            val (_, _, desc) = stages[selectedStage]
            val activeNotes = when (selectedStage) {
                0 -> fragrance.top_notes
                1 -> fragrance.middle_notes
                else -> fragrance.base_notes
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianElevated)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = ParchmentMuted,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prominent notes:",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary
                        )
                        activeNotes.take(4).forEach { note ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ObsidianSurface)
                                    .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = ParchmentWhite
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Chapter IV: Atelier Atmosphere & Soundscapes
 * Allows changing the dynamic Scent Family canvas backdrop and listening to
 * real-time procedural synthesised soundscapes of ancient Kannauj hydrodistillation.
 */
@Composable
fun AtmosphereSoundscapeSection(
    soundscapeEngine: AmbientSoundscapeEngine,
    currentAtmosphere: String?,
    onSetAtmosphere: (String) -> Unit,
    onOpenWeatherModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying by soundscapeEngine.isPlaying.collectAsState()
    val activeSoundscape by soundscapeEngine.activeSoundscape.collectAsState()
    val volume by soundscapeEngine.volume.collectAsState()

    val atmosphereOptions = listOf(
        Triple("Earthy / Mitti", "earthy", MittiClay),
        Triple("Rose / Gulab", "rose", IndianRose),
        Triple("Khus / Vetiver", "khus", KannaujKhus),
        Triple("Oud / Resins", "oud", AmberAccent),
        Triple("Citrus Dawn", "citrus", GoldBright)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Atmosphere Canvas Color Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MIST ATMOSPHERE CANVAS",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = GoldPrimary
                )
                TextButton(
                    onClick = onOpenWeatherModal,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Recalibrate Climate",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = GoldBright
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(atmosphereOptions) { (name, key, color) ->
                    val isSelected = currentAtmosphere == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) color.copy(alpha = 0.25f) else ObsidianElevated)
                            .border(
                                1.dp,
                                if (isSelected) color else ObsidianCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSetAtmosphere(key) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) ParchmentWhite else ParchmentMuted
                            )
                        }
                    }
                }
            }

            // Procedural Kannauj Soundscape Player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianElevated)
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = if (isPlaying) GoldBright else ParchmentMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "SYNESTHETIC ACOUSTICS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = GoldPrimary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = AmbientSoundscapeEngine.PRESETS.firstOrNull { it.id == activeSoundscape }?.title
                                        ?: "Silent Atelier",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = ParchmentWhite
                                )
                            }
                        }

                        // Play/Pause Master Button
                        IconButton(
                            onClick = { soundscapeEngine.toggleSoundscape() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) GoldPrimary else ObsidianSurface)
                                .border(1.dp, GoldPrimary, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = if (isPlaying) ObsidianBlack else GoldBright,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Soundscape Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AmbientSoundscapeEngine.PRESETS.forEach { preset ->
                            val isSelected = activeSoundscape == preset.id && isPlaying
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else ObsidianSurface)
                                    .border(
                                        0.5.dp,
                                        if (isSelected) GoldBright else ObsidianCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        soundscapeEngine.start(preset.id)
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${preset.icon} ${preset.title.split(" ").first()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) GoldBright else ParchmentMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Volume slider (if playing)
                    if (isPlaying) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = ParchmentMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Slider(
                                value = volume,
                                onValueChange = { soundscapeEngine.setVolume(it) },
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldBright,
                                    activeTrackColor = GoldPrimary,
                                    inactiveTrackColor = ObsidianCardBorder
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * "What Should I Wear" Personalized AI Scent Advisor Dialog
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WhatShouldIWearDialog(
    allFragrances: List<Fragrance>,
    currentWeather: WeatherCondition,
    onDismiss: () -> Unit,
    onOpenInLayeringStudio: (Fragrance, Fragrance?) -> Unit,
    onApplyWear: (Fragrance, Fragrance?) -> Unit
) {
    var selectedMood by remember { mutableStateOf("Refined & Elevated ✨") }
    var selectedOccasion by remember { mutableStateOf("Office / Daytime Focus") }

    val moodOptions = listOf(
        "Refined & Elevated ✨",
        "Bold & Magnetic 🔥",
        "Serene & Calming 🌿",
        "Fresh & Energized ⚡",
        "Mysterious & Sensual 🌙",
        "Festive & Regal 👑"
    )

    val occasionOptions = listOf(
        "Office / Daytime Focus",
        "Evening Dinner & Date",
        "Casual Weekend Stroll",
        "Formal Gala / Festive",
        "Meditation & Solitude",
        "High-Heat Outdoor Transit"
    )

    // Compute tailored recommendation based on mood, occasion, and weather
    val recommendedFragrance = remember(selectedMood, selectedOccasion, currentWeather, allFragrances) {
        if (allFragrances.isEmpty()) null
        else {
            val familyKeyword = when {
                selectedMood.contains("Bold") || selectedMood.contains("Festive") -> listOf("oud", "oriental", "amber", "spicy")
                selectedMood.contains("Fresh") || occasionOptions.contains("High-Heat") -> listOf("citrus", "fresh", "aquatic")
                selectedMood.contains("Serene") || occasionOptions.contains("Meditation") -> listOf("khus", "mitti", "vetiver", "sandalwood")
                selectedMood.contains("Mysterious") -> listOf("rose", "patchouli", "musk", "oud")
                else -> listOf("floral", "woody", "fresh", "aromatic")
            }
            allFragrances.firstOrNull { f ->
                familyKeyword.any { k -> f.fragrance_family.contains(k, ignoreCase = true) || f.name.contains(k, ignoreCase = true) }
            } ?: allFragrances.firstOrNull()
        }
    }

    val complementaryPartner = remember(recommendedFragrance, allFragrances) {
        recommendedFragrance?.let { rec ->
            allFragrances.firstOrNull { it.id != rec.id && it.is_oil_based != rec.is_oil_based }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "DISCOVER MY SCENT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ParchmentWhite
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Calibrating for ${currentWeather.temperatureC}°C ${currentWeather.season} ${currentWeather.timeOfDay}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldPrimary
                )

                // Mood Selection
                Text(
                    text = "DESIRED AURA / MOOD",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ParchmentMuted
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moodOptions.forEach { mood ->
                        val isSelected = selectedMood == mood
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoldPrimary else ObsidianElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldBright else ObsidianCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedMood = mood }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = mood,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) ObsidianBlack else ParchmentWhite
                            )
                        }
                    }
                }

                // Occasion Selection
                Text(
                    text = "OCCASION & CONTEXT",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ParchmentMuted
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    occasionOptions.forEach { occ ->
                        val isSelected = selectedOccasion == occ
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else ObsidianElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldBright else ObsidianCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedOccasion = occ }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = occ,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) GoldBright else ParchmentWhite
                            )
                        }
                    }
                }

                // Calculated Recommendation Result
                if (recommendedFragrance != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianElevated)
                            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CURATED ATELIER PRESCRIPTION",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = GoldBright,
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldPrimary.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "97% HARMONY",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = GoldBright
                                    )
                                }
                            }

                            Text(
                                text = recommendedFragrance.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = ParchmentWhite
                            )

                            Text(
                                text = "By ${recommendedFragrance.brand_name} · ${recommendedFragrance.fragrance_family}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldPrimary
                            )

                            Text(
                                text = "Calibrated for $selectedMood during $selectedOccasion under ${currentWeather.temperatureC}°C ambient climate.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ParchmentMuted
                            )

                            if (complementaryPartner != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Recommended Layer Partner:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = ParchmentMuted
                                    )
                                    Text(
                                        text = complementaryPartner.name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        color = GoldBright
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (recommendedFragrance != null) {
                Button(
                    onClick = {
                        onApplyWear(recommendedFragrance, complementaryPartner)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("APPLY WEAR RITUAL", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (recommendedFragrance != null && complementaryPartner != null) {
                OutlinedButton(
                    onClick = {
                        onOpenInLayeringStudio(recommendedFragrance, complementaryPartner)
                        onDismiss()
                    },
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = Brush.linearGradient(listOf(GoldPrimary, GoldBright))
                    )
                ) {
                    Text("OPEN IN STUDIO", color = GoldBright)
                }
            }
        },
        containerColor = ObsidianCard,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Scent Academy Modal: Explores 500-year Kannauj Deg-Bhapka heritage distillation,
 * volatility thermodynamics, and molecular chord compounding principles.
 */
@Composable
fun ScentAcademyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "THE SCENT ACADEMY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ParchmentWhite
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Kannauj Deg-Bhapka
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianElevated)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "1. KANNAUJ DEG-BHAPKA HYDRODISTILLATION",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "For over 500 years, Kannauj distillers have practiced deg-bhapka. Unrefined Gangetic alluvial clay or Damask roses simmer in antique copper deg cauldrons over wood fire. Steam travels through bent bamboo chonga pipes into underwater bhapka receivers charged with sandalwood oil, capturing pure petrichor without alcohol.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ParchmentMuted,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Section 2: Evaporation Thermodynamics
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianElevated)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "2. MOLECULAR VOLATILITY PYRAMID",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Top notes (monoterpenes like limonene, linalool) diffuse within 30 minutes. Heart notes (geraniol, eugenol) radiate for 1 to 3 hours. Base notes (santalol, patchoulol, amber resins) persist for 8 to 24 hours, forming the fixative bedrock.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ParchmentMuted,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Section 3: Layering Rules
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianElevated)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "3. GOLDEN RATIO OF BESPOKE LAYERING",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Rule 1: Anchor first with high molecular weight oil-based attar on warm pulse points.\nRule 2: Mist volatile alcohol-based Eau de Parfum across the clavicle or air.\nRule 3: Complementary families (e.g. Mitti Earth + Citrus, Rose + Oud) create synergistic olfactory accords.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ParchmentMuted,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("CLOSE ACADEMY", color = ObsidianBlack, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = ObsidianCard,
        shape = RoundedCornerShape(20.dp)
    )
}
