package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LayeringResult
import com.example.olfactory.ml.OlfactoryEngine
import com.example.ui.components.AtelierFlaconGraphic
import com.example.ui.components.AtmosphericFragranceCanvas
import com.example.ui.components.OlfactoryAtelierTopBar
import com.example.ui.components.OlfactoryScoreBadge
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
import com.example.ui.theme.ParchmentFaint
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite
import com.example.ui.viewmodel.OlfactoryViewModel

@Composable
fun ChordResultScreen(
    viewModel: OlfactoryViewModel,
    result: LayeringResult,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val vectorA = OlfactoryEngine.getVector8D100(result.fragrance_a)
    val vectorB = OlfactoryEngine.getVector8D100(result.fragrance_b)

    val chordAtmosphere = result.fragrance_a.fragrance_family

    Box(modifier = Modifier.fillMaxSize()) {
        AtmosphericFragranceCanvas(
            modifier = Modifier.fillMaxSize(),
            activeAtmosphere = chordAtmosphere
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("chord_result_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
        // Top Bar
        item {
            OlfactoryAtelierTopBar(
                title = result.chord_title,
                subtitle = "Olfactory Scent Resonance",
                onBackClick = onBackClick,
                actions = {
                    Button(
                        onClick = {
                            val shareText = "✨ Olfactory AI Resonance: ${result.chord_title}\n" +
                                    "${result.fragrance_a.name} × ${result.fragrance_b.name}\n" +
                                    "Score: ${result.compatibility_score}%\n\n" +
                                    "Compounding Method: ${result.layering_method}\n" +
                                    result.why_it_works.application_tip
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Chord"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianCard),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = GoldBright, modifier = Modifier.size(14.dp))
                    }
                }
            )
        }

        // Hero Chord Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.8f), Color.Transparent))
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
                                text = if (result.is_cross_origin) "EAST-MEETS-WEST COMPOUNDING" else "HAUTE HARMONY",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPrimary,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.chord_title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = ParchmentWhite
                            )
                        }
                        OlfactoryScoreBadge(score = result.compatibility_score)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = result.explanation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = ParchmentWhite,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Dual Flacon Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianElevated)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            AtelierFlaconGraphic(
                                fragrance = result.fragrance_a,
                                modifier = Modifier.size(60.dp),
                                isCompact = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = result.fragrance_a.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                color = ParchmentWhite
                            )
                            Text(
                                text = result.fragrance_a.brand_name,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = GoldPrimary
                            )
                        }

                        Text(
                            text = "×",
                            style = MaterialTheme.typography.headlineMedium,
                            color = GoldBright
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            AtelierFlaconGraphic(
                                fragrance = result.fragrance_b,
                                modifier = Modifier.size(60.dp),
                                isCompact = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = result.fragrance_b.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                color = ParchmentWhite
                            )
                            Text(
                                text = result.fragrance_b.brand_name,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = GoldPrimary
                            )
                        }
                    }
                }
            }
        }

        // Evaporation Timeline Card
        item {
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
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "EVAPORATION & EVOLUTION TIMELINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        letterSpacing = 1.2.sp
                    )

                    // Phase 1
                    TimelinePhase(
                        phase = "PHASE 1: OPENING DIFFUSION (0–30 MIN)",
                        description = result.why_it_works.opening_harmony,
                        accent = GoldBright
                    )

                    // Phase 2
                    TimelinePhase(
                        phase = "PHASE 2: DRYDOWN RESONANCE (2–6 HOURS)",
                        description = result.why_it_works.drydown_depth,
                        accent = AmberAccent
                    )

                    // Phase 3
                    TimelinePhase(
                        phase = "PHASE 3: SKIN FIXATION & DRYDOWN (8–24 HOURS)",
                        description = "Botanical sandalwood, attar oils, and fixative resins meld intimately with your body chemistry, leaving a seductive sillage skin scent.",
                        accent = MysoreSandal
                    )
                }
            }
        }

        // Radar Vector of Flacon A
        item {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = "FLACON A VECTOR: ${result.fragrance_a.name.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OlfactoryVectorRadar(vector = vectorA)
            }
        }

        // Radar Vector of Flacon B
        item {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = "FLACON B VECTOR: ${result.fragrance_b.name.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OlfactoryVectorRadar(vector = vectorB)
            }
        }
    }
}
}

@Composable
private fun TimelinePhase(
    phase: String,
    description: String,
    accent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = phase,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = accent
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = ParchmentMuted,
                lineHeight = 18.sp
            )
        }
    }
}
