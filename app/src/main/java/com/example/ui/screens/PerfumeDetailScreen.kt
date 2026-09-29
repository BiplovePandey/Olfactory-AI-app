package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fragrance
import com.example.data.model.LayeringResult
import com.example.olfactory.ml.OlfactoryEngine
import com.example.ui.components.AtelierFlaconGraphic
import com.example.ui.components.AtmosphericFragranceCanvas
import com.example.ui.components.OlfactoryAtelierTopBar
import com.example.ui.components.chamber.ChamberDrydownMachine
import com.example.ui.components.chamber.ChamberFlaconPedestal
import com.example.ui.components.chamber.ChamberHeritageProvenance
import com.example.ui.components.chamber.ChamberTab
import com.example.ui.components.chamber.ChamberWhyThisFragrance
import com.example.ui.components.chamber.ScentSignatureVisualizer
import com.example.ui.components.chamber.VisualNotePyramid
import com.example.ui.components.chamber.getChamberPalette
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ParchmentFaint
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite
import com.example.ui.viewmodel.OlfactoryViewModel

@Composable
fun PerfumeDetailScreen(
    viewModel: OlfactoryViewModel,
    fragrance: Fragrance,
    onBackClick: () -> Unit,
    onNavigateToLayeringStudio: () -> Unit,
    onPartnerClick: (Fragrance) -> Unit,
    onExploreHeritage: (() -> Unit)? = null
) {
    val wardrobe by viewModel.wardrobe.collectAsState()
    val isInWardrobe = wardrobe.any { it.id == fragrance.id }
    val vector8D = OlfactoryEngine.getVector8D100(fragrance)
    val partners = viewModel.getPartnersFor(fragrance)

    val chamberPalette = remember(fragrance) {
        getChamberPalette(fragrance.fragrance_family, fragrance.name)
    }

    var selectedTab by remember { mutableStateOf(ChamberTab.SIGNATURE) }
    var hasWornToday by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Atmospheric Ambient Background
        AtmosphericFragranceCanvas(
            modifier = Modifier.fillMaxSize(),
            activeAtmosphere = fragrance.fragrance_family
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("perfume_detail_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Top Bar
            item {
                OlfactoryAtelierTopBar(
                    title = fragrance.name,
                    subtitle = "Maison ${fragrance.brand_name}",
                    onBackClick = onBackClick,
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleWardrobe(fragrance.id, !isInWardrobe) },
                            modifier = Modifier.testTag("detail_wardrobe_toggle")
                        ) {
                            Icon(
                                imageVector = if (isInWardrobe) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isInWardrobe) "In Wardrobe" else "Save to Wardrobe",
                                tint = if (isInWardrobe) GoldPrimary else ParchmentMuted
                            )
                        }
                    }
                )
            }

            // SECTION 1: THE HERO OBJECT — FLACON ON LABORATORY PEDESTAL
            item {
                ChamberFlaconPedestal(
                    fragrance = fragrance,
                    palette = chamberPalette,
                    vector8D = vector8D
                )
            }

            // SECTION 2: IDENTITY & HAUTE PARFUMERIE SPECIFICATION
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Concentration & Origin Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(chamberPalette.tagBg)
                                .border(1.dp, chamberPalette.borderAccent, RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = (fragrance.concentration ?: fragrance.format).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                color = chamberPalette.tagText,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(ObsidianElevated)
                                .border(1.dp, ObsidianCardBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${fragrance.brand_country ?: "Heritage"} • ${fragrance.gender}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                color = ParchmentMuted
                            )
                        }

                        if (fragrance.price_inr != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(GoldPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "₹${fragrance.price_inr.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                    color = GoldBright,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = fragrance.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = ParchmentWhite,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Maison ${fragrance.brand_name}${if (!fragrance.category.isNullOrBlank()) " • ${fragrance.category}" else ""}",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                        color = GoldPrimary,
                        textAlign = TextAlign.Center
                    )

                    if (!fragrance.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = fragrance.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ParchmentMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ACTION BUTTONS (Wear Today, Formulate in Lab, Wardrobe)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // WEAR THIS TODAY BUTTON
                        Button(
                            onClick = { hasWornToday = !hasWornToday },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("detail_wear_today_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasWornToday) Color(0xFF065F46) else GoldPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (hasWornToday) Icons.Default.Check else Icons.Default.Spa,
                                contentDescription = null,
                                tint = if (hasWornToday) Color(0xFFA7F3D0) else ObsidianBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasWornToday) "Worn Today" else "Wear Today",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (hasWornToday) Color(0xFFA7F3D0) else ObsidianBlack,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        // FORMULATE CHORDS IN STUDIO BUTTON
                        Button(
                            onClick = {
                                viewModel.startLayeringWith(fragrance)
                                onNavigateToLayeringStudio()
                            },
                            modifier = Modifier
                                .weight(1.1f)
                                .height(46.dp)
                                .testTag("detail_layer_this_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ObsidianElevated
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(GoldPrimary, AmberAccent))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Formulate in Lab",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = GoldBright,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // SECTION 3: TACTILE CHAMBER NAVIGATION TABS
            item {
                Spacer(modifier = Modifier.height(20.dp))
                val tabScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tabScrollState)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChamberTab.values().forEach { tab ->
                        val isSelected = tab == selectedTab
                        val tabIcon = when (tab) {
                            ChamberTab.SIGNATURE -> Icons.Default.AutoAwesome
                            ChamberTab.PYRAMID -> Icons.Default.WaterDrop
                            ChamberTab.DRYDOWN -> Icons.Default.History
                            ChamberTab.RATIONALE -> Icons.Default.CompassCalibration
                            ChamberTab.LAYERING -> Icons.Default.Layers
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GoldPrimary.copy(alpha = 0.22f) else ObsidianElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldPrimary else ObsidianCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (selectedTab != tab) {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedTab = tab
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("chamber_tab_${tab.name.lowercase()}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = tabIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) GoldBright else ParchmentMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = if (isSelected) GoldBright else ParchmentMuted,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // SECTION 4: CHAMBER INSPECTION MODULE VIEWS
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Crossfade(targetState = selectedTab, label = "chamberTabCrossfade") { currentTab ->
                        when (currentTab) {
                            ChamberTab.SIGNATURE -> {
                                ScentSignatureVisualizer(
                                    vector8D = vector8D,
                                    palette = chamberPalette
                                )
                            }
                            ChamberTab.PYRAMID -> {
                                VisualNotePyramid(
                                    fragrance = fragrance,
                                    palette = chamberPalette
                                )
                            }
                            ChamberTab.DRYDOWN -> {
                                ChamberDrydownMachine(
                                    fragrance = fragrance,
                                    palette = chamberPalette
                                )
                            }
                            ChamberTab.RATIONALE -> {
                                ChamberWhyThisFragrance(
                                    fragrance = fragrance,
                                    vector8D = vector8D,
                                    palette = chamberPalette
                                )
                            }
                            ChamberTab.LAYERING -> {
                                LayeringPartnersInspectionModule(
                                    fragrance = fragrance,
                                    partners = partners,
                                    onSelectPartner = { partner ->
                                        viewModel.selectFragrances(fragrance, partner)
                                        onNavigateToLayeringStudio()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 5: INDIAN HERITAGE PROVENANCE / ARCHIVAL SECTION
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ChamberHeritageProvenance(
                        fragrance = fragrance,
                        palette = chamberPalette,
                        onExploreHeritage = onExploreHeritage
                    )
                }
            }
        }
    }
}

/**
 * Layering Partners Inspection Module
 */
@Composable
private fun LayeringPartnersInspectionModule(
    fragrance: Fragrance,
    partners: List<LayeringResult>,
    onSelectPartner: (Fragrance) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chamber_layering_module"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.5f), ObsidianCardBorder))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "AI COMPUTATIONAL ACCORD SYNTHESIS",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = GoldPrimary,
                    letterSpacing = 1.3.sp
                )
            }
            Text(
                text = "Recommended Layering Partners",
                style = MaterialTheme.typography.titleLarge,
                color = ParchmentWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Scientifically paired based on 8D vector complementarity and opposite polarity volatility balancing.",
                style = MaterialTheme.typography.bodySmall,
                color = ParchmentMuted,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (partners.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Exploring harmonious layering counterparts...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ParchmentMuted
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (pairing in partners) {
                        val partnerFragrance = pairing.fragrance_b
                        val haptics = LocalHapticFeedback.current
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelectPartner(partnerFragrance)
                                }
                                .testTag("partner_card_${partnerFragrance.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
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
                                    fragrance = partnerFragrance,
                                    modifier = Modifier.size(52.dp),
                                    isCompact = true
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pairing.chord_title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = AmberAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = partnerFragrance.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                        color = ParchmentWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${partnerFragrance.brand_name} • ${partnerFragrance.format}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                        color = ParchmentMuted
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GoldPrimary.copy(alpha = 0.15f))
                                        .border(0.5.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${pairing.compatibility_score}%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldBright
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
}
