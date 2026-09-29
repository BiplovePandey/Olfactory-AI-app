package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fragrance
import com.example.data.model.LayeringResult
import com.example.ui.components.AtelierFlaconGraphic
import com.example.ui.components.AtmosphericFragranceCanvas
import com.example.ui.components.ChapterDivider
import com.example.ui.components.OlfactoryAtelierTopBar
import com.example.ui.components.OlfactoryScoreBadge
import com.example.ui.components.getFamilyColor
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
import com.example.ui.viewmodel.OlfactoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayeringStudioScreen(
    viewModel: OlfactoryViewModel,
    onNavigateToChordResult: (LayeringResult) -> Unit
) {
    val context = LocalContext.current
    val selectedA by viewModel.selectedFragranceA.collectAsState()
    val selectedB by viewModel.selectedFragranceB.collectAsState()
    val layeringResult by viewModel.activeLayeringResult.collectAsState()
    val allFragrances by viewModel.allFragrances.collectAsState()
    val wardrobe by viewModel.wardrobe.collectAsState()
    val savedCombinations by viewModel.savedCombinations.collectAsState()

    var showPickerForSlot by remember { mutableStateOf<String?>(null) } // "A" or "B"
    var saveSuccessMessage by remember { mutableStateOf(false) }

    val isCurrentSaved = layeringResult != null && savedCombinations.any {
        (it.fragrance_a_id == selectedA?.id && it.fragrance_b_id == selectedB?.id) ||
        (it.fragrance_a_id == selectedB?.id && it.fragrance_b_id == selectedA?.id)
    }

    val layeringAtmosphere = selectedA?.fragrance_family ?: selectedB?.fragrance_family

    Box(modifier = Modifier.fillMaxSize()) {
        AtmosphericFragranceCanvas(
            modifier = Modifier.fillMaxSize(),
            activeAtmosphere = layeringAtmosphere
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("layering_studio_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
        // Top Bar
        item {
            OlfactoryAtelierTopBar(
                title = "Layer Laboratory",
                subtitle = "Olfactory Vector Harmony & Compounding Engine"
            )
        }

        // Section: Dual Flacon Pair Selection
        item {
            ChapterDivider(
                chapter = "LABORATORY I",
                title = "ACCORD COMPOSITION PAIR",
                subtitle = "Select base fixative flacon and atmospheric diffusion flacon"
            )
        }

        // Dual Flacon Selection Canvas
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SELECT COMPILATION PAIR",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Flacon A Box
                FlaconSelectorCard(
                    slot = "FLACON A (BASE ANCHOR)",
                    fragrance = selectedA,
                    onClick = { showPickerForSlot = "A" },
                    testTag = "picker_slot_a"
                )

                // Central Swap / Compounding Axis
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ObsidianElevated)
                            .border(1.dp, GoldPrimary, CircleShape)
                            .clickable { viewModel.swapFragrances() }
                            .testTag("swap_flacons_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap Flacons",
                            tint = GoldBright,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Flacon B Box
                FlaconSelectorCard(
                    slot = "FLACON B (DIFFUSION MIST)",
                    fragrance = selectedB,
                    onClick = { showPickerForSlot = "B" },
                    testTag = "picker_slot_b"
                )
            }
        }

        // Evaluation Results Section
        if (layeringResult != null) {
            val result = layeringResult!!

            item {
                ChapterDivider(
                    chapter = "LABORATORY II",
                    title = "ACCORD COMPATIBILITY EVALUATION",
                    subtitle = "8D chemical vector synergy, olfactory breakdown, and application ritual"
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Result Header & Score
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("layering_result_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(GoldPrimary.copy(alpha = 0.8f), Color.Transparent)
                            )
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (result.is_cross_origin) "EAST-MEETS-WEST FUSION" else "HARMONIC CHORD",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (result.is_cross_origin) AmberAccent else GoldPrimary,
                                        letterSpacing = 1.2.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = result.chord_title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = ParchmentWhite
                                    )
                                }

                                OlfactoryScoreBadge(
                                    score = result.compatibility_score,
                                    label = "SYNERGY"
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Explanation
                            Text(
                                text = result.explanation,
                                style = MaterialTheme.typography.bodyLarge,
                                color = ParchmentWhite,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Layering Method / East-Meets-West Ritual
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ObsidianElevated)
                                    .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "COMPOUNDING RITUAL & TECHNIQUE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = GoldBright,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = result.layering_method,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                        color = ParchmentWhite
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = result.why_it_works.application_tip,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ParchmentMuted,
                                        lineHeight = 19.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Compatibility Breakdown Factors
                            Text(
                                text = "OLFACTORY SYNERGY BREAKDOWN",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPrimary,
                                letterSpacing = 1.1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val factors = listOf(
                                Pair("Note Compatibility (40%)", result.breakdown.note_compatibility),
                                Pair("User DNA Preference (20%)", result.breakdown.user_preference_match),
                                Pair("Season Harmonization (15%)", result.breakdown.season_compatibility),
                                Pair("Occasion Alignment (15%)", result.breakdown.occasion_compatibility),
                                Pair("Contrast & Diversity (10%)", result.breakdown.diversity_factor)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                factors.forEach { (label, score) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = ParchmentMuted,
                                            modifier = Modifier.width(180.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(5.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(ObsidianElevated)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(score / 100f)
                                                    .fillMaxHeight()
                                                    .background(
                                                        Brush.horizontalGradient(
                                                            listOf(GoldPrimary, GoldBright)
                                                        )
                                                    )
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "$score%",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = GoldBright
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Actions: Save & Share & Deep Inspect
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Save Button
                                Button(
                                    onClick = {
                                        viewModel.saveActiveCombination()
                                        saveSuccessMessage = true
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("save_combination_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCurrentSaved) ObsidianElevated else GoldPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCurrentSaved) Icons.Default.Check else Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = if (isCurrentSaved) GoldBright else ObsidianBlack,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isCurrentSaved) "SAVED" else "SAVE CHORD",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isCurrentSaved) GoldBright else ObsidianBlack,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                // Share Button
                                Button(
                                    onClick = {
                                        val shareText = "✨ Olfactory AI Scent Chord: ${result.chord_title} (${result.compatibility_score}% Harmony)\n" +
                                                "Flacon A: ${result.fragrance_a.name} (${result.fragrance_a.brand_name})\n" +
                                                "Flacon B: ${result.fragrance_b.name} (${result.fragrance_b.brand_name})\n\n" +
                                                "Ritual: ${result.layering_method}\n" +
                                                result.why_it_works.application_tip + "\n\n" +
                                                "Compounded with Olfactory AI"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Scent Chord"))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("share_combination_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianElevated),
                                    border = ButtonDefaults.outlinedButtonBorder().copy(
                                        brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = ParchmentWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SHARE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ParchmentWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Deep Inspect Button
                            Button(
                                onClick = {
                                    viewModel.setActiveChordDetail(result)
                                    onNavigateToChordResult(result)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("inspect_chord_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                border = ButtonDefaults.outlinedButtonBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(GoldPrimary, GoldBright))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "VIEW FULL OLFACTORY RESONANCE & RADAR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GoldBright,
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
}

    // Modal Bottom Sheet to Pick Fragrance for Slot A or Slot B
    if (showPickerForSlot != null) {
        val slot = showPickerForSlot!!
        var pickerSearchQuery by remember { mutableStateOf("") }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        val pickerCandidates = allFragrances.filter {
            pickerSearchQuery.isBlank() ||
            it.name.contains(pickerSearchQuery, ignoreCase = true) ||
            it.brand_name.contains(pickerSearchQuery, ignoreCase = true) ||
            it.fragrance_family.contains(pickerSearchQuery, ignoreCase = true)
        }

        ModalBottomSheet(
            onDismissRequest = { showPickerForSlot = null },
            sheetState = sheetState,
            containerColor = ObsidianSurface,
            contentColor = ParchmentWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .testTag("flacon_picker_bottom_sheet")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT FLACON FOR SLOT $slot",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        letterSpacing = 1.2.sp
                    )
                    IconButton(onClick = { showPickerForSlot = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = ParchmentMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pickerSearchQuery,
                    onValueChange = { pickerSearchQuery = it },
                    placeholder = { Text("Search 93 perfumes by name, brand or note...", color = ParchmentFaint) },
                    modifier = Modifier.fillMaxWidth().testTag("picker_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = ParchmentWhite,
                        unfocusedTextColor = ParchmentWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pickerCandidates) { fragrance ->
                        val isAlreadySelected = (slot == "A" && selectedB?.id == fragrance.id) ||
                                (slot == "B" && selectedA?.id == fragrance.id)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (slot == "A") {
                                        viewModel.selectFragranceA(fragrance)
                                    } else {
                                        viewModel.selectFragranceB(fragrance)
                                    }
                                    showPickerForSlot = null
                                }
                                .testTag("picker_item_${fragrance.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAlreadySelected) ObsidianElevated else ObsidianCard
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AtelierFlaconGraphic(
                                    fragrance = fragrance,
                                    modifier = Modifier.size(48.dp),
                                    isCompact = true
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fragrance.brand_name.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = GoldPrimary
                                    )
                                    Text(
                                        text = fragrance.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                        color = ParchmentWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${fragrance.format} • ${fragrance.fragrance_family}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                        color = ParchmentMuted
                                    )
                                }
                                if (isAlreadySelected) {
                                    Text(
                                        text = "In other slot",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = AmberAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlaconSelectorCard(
    slot: String,
    fragrance: Fragrance?,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
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
            if (fragrance != null) {
                AtelierFlaconGraphic(
                    fragrance = fragrance,
                    modifier = Modifier.size(60.dp),
                    isCompact = true
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = ParchmentFaint
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = slot,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = GoldPrimary,
                    letterSpacing = 1.0.sp
                )
                if (fragrance != null) {
                    Text(
                        text = fragrance.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = ParchmentWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${fragrance.brand_name} • ${fragrance.format}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = ParchmentMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "Tap to choose flacon...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ParchmentFaint
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select",
                tint = GoldPrimary
            )
        }
    }
}
