package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fragrance
import com.example.data.model.OlfactoryVector8D
import com.example.olfactory.ml.OlfactoryEngine
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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Returns color accent associated with fragrance family
 */
fun getFamilyColor(family: String): Color {
    val lower = family.lowercase()
    return when {
        lower.contains("wood") || lower.contains("oud") -> OudDark
        lower.contains("floral") || lower.contains("rose") -> IndianRose
        lower.contains("earth") || lower.contains("clay") || lower.contains("petrichor") -> MittiClay
        lower.contains("khus") || lower.contains("green") -> KannaujKhus
        lower.contains("spice") || lower.contains("amber") -> AmberAccent
        lower.contains("citrus") -> GoldBright
        else -> MysoreSandal
    }
}

/**
 * Custom luxury flacon illustration rendered dynamically via Canvas
 */
@Composable
fun AtelierFlaconGraphic(
    fragrance: Fragrance,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val familyColor = getFamilyColor(fragrance.fragrance_family)
    val isOil = fragrance.is_oil_based || fragrance.format == "Attar"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        familyColor.copy(alpha = 0.25f),
                        ObsidianSurface.copy(alpha = 0.8f),
                        ObsidianBlack
                    )
                )
            )
            .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(if (isCompact) 8.dp else 16.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f

            if (isOil) {
                // Traditional ornate attar flacon (Tola bottle with crystal stopper)
                // Stopper / Cap
                val capWidth = w * 0.22f
                val capHeight = h * 0.18f
                drawRect(
                    brush = Brush.verticalGradient(listOf(GoldBright, GoldPrimary, MysoreSandal)),
                    topLeft = Offset(cx - capWidth / 2f, h * 0.10f),
                    size = androidx.compose.ui.geometry.Size(capWidth, capHeight)
                )

                // Flacon body (carved hexagonal glass bottle)
                val bodyPath = Path().apply {
                    moveTo(cx - w * 0.28f, h * 0.32f)
                    lineTo(cx + w * 0.28f, h * 0.32f)
                    lineTo(cx + w * 0.36f, h * 0.76f)
                    lineTo(cx + w * 0.22f, h * 0.88f)
                    lineTo(cx - w * 0.22f, h * 0.88f)
                    lineTo(cx - w * 0.36f, h * 0.76f)
                    close()
                }
                drawPath(
                    path = bodyPath,
                    brush = Brush.verticalGradient(
                        listOf(
                            familyColor.copy(alpha = 0.6f),
                            GoldPrimary.copy(alpha = 0.4f),
                            ObsidianSurface
                        )
                    )
                )
                drawPath(
                    path = bodyPath,
                    color = GoldPrimary.copy(alpha = 0.6f),
                    style = Stroke(width = 2f)
                )

                // Golden essence oil drop inside
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(GoldBright, AmberAccent, familyColor)
                    ),
                    radius = w * 0.12f,
                    center = Offset(cx, h * 0.58f)
                )
            } else {
                // Modern Haute Parfumerie Spray Flacon (minimalist geometric crystal)
                val capWidth = w * 0.28f
                val capHeight = h * 0.15f
                drawRect(
                    brush = Brush.linearGradient(listOf(GoldBright, GoldPrimary, AmberAccent)),
                    topLeft = Offset(cx - capWidth / 2f, h * 0.14f),
                    size = androidx.compose.ui.geometry.Size(capWidth, capHeight)
                )

                // Spray atomizer ring
                drawRect(
                    color = GoldPrimary,
                    topLeft = Offset(cx - capWidth * 0.35f, h * 0.29f),
                    size = androidx.compose.ui.geometry.Size(capWidth * 0.7f, h * 0.05f)
                )

                // Crystal body
                val bodyPath = Path().apply {
                    moveTo(cx - w * 0.34f, h * 0.34f)
                    lineTo(cx + w * 0.34f, h * 0.34f)
                    lineTo(cx + w * 0.34f, h * 0.88f)
                    lineTo(cx - w * 0.34f, h * 0.88f)
                    close()
                }
                drawPath(
                    path = bodyPath,
                    brush = Brush.verticalGradient(
                        listOf(
                            familyColor.copy(alpha = 0.4f),
                            familyColor.copy(alpha = 0.15f),
                            ObsidianSurface.copy(alpha = 0.7f)
                        )
                    )
                )
                drawPath(
                    path = bodyPath,
                    color = GoldPrimary.copy(alpha = 0.5f),
                    style = Stroke(width = 1.5f)
                )

                // Vertical atelier pinstripe facet
                drawLine(
                    color = GoldBright.copy(alpha = 0.6f),
                    start = Offset(cx, h * 0.36f),
                    end = Offset(cx, h * 0.86f),
                    strokeWidth = 1.5f
                )
            }
        }
    }
}

/**
 * Clean card for displaying fragrance in lists, grids, and picker sheets
 */
@Composable
fun OlfactoryFlaconCard(
    fragrance: Fragrance,
    isInWardrobe: Boolean,
    onCardClick: () -> Unit,
    onWardrobeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onLayerClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("fragrance_card_${fragrance.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flacon Graphic Preview
            AtelierFlaconGraphic(
                fragrance = fragrance,
                modifier = Modifier
                    .size(68.dp)
                    .aspectRatio(1f),
                isCompact = true
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fragrance.brand_name.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = fragrance.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = ParchmentWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = fragrance.format,
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentMuted
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentFaint
                    )
                    Text(
                        text = fragrance.fragrance_family,
                        style = MaterialTheme.typography.labelSmall,
                        color = getFamilyColor(fragrance.fragrance_family)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                // Notes snippet
                val topNotesSample = fragrance.top_notes.take(2).joinToString(", ")
                if (topNotesSample.isNotEmpty()) {
                    Text(
                        text = "Notes: $topNotesSample",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = ParchmentFaint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Actions (Wardrobe bookmark + Layer button)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onWardrobeToggle,
                    modifier = Modifier.size(36.dp).testTag("toggle_wardrobe_${fragrance.id}")
                ) {
                    Icon(
                        imageVector = if (isInWardrobe) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isInWardrobe) "In Wardrobe" else "Add to Wardrobe",
                        tint = if (isInWardrobe) GoldPrimary else ParchmentMuted
                    )
                }

                if (onLayerClick != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianElevated)
                            .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(8.dp))
                            .clickable { onLayerClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("layer_action_${fragrance.id}")
                    ) {
                        Text(
                            text = "LAYER",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldBright,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 8-Dimensional Olfactory Vector Space Radar Canvas
 * Renders the 8 dimensions with animated vertices and glowing gold polygon
 */
@Composable
fun OlfactoryVectorRadar(
    vector: OlfactoryVector8D,
    modifier: Modifier = Modifier
) {
    val dimensions = listOf(
        Pair("Freshness", vector.freshness),
        Pair("Sweetness", vector.sweetness),
        Pair("Intensity", vector.intensity),
        Pair("Woody", vector.woody),
        Pair("Floral", vector.floral),
        Pair("Spices & Resins", vector.warm_resinous_spices),
        Pair("Earthy & Clay", vector.earthy_clay),
        Pair("Longevity", vector.longevity_fixative)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianCard)
            .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "8-DIMENSIONAL OLFACTORY VECTOR SPACE",
            style = MaterialTheme.typography.labelSmall,
            color = GoldPrimary,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Radar Canvas
        Box(
            modifier = Modifier
                .size(240.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val maxRadius = minOf(cx, cy) * 0.85f
                val angleStep = (2 * Math.PI / 8).toFloat()

                // Draw background concentric web octagons
                val webSteps = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                webSteps.forEach { step ->
                    val webPath = Path()
                    for (i in 0 until 8) {
                        val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                        val x = cx + (maxRadius * step * cos(angle))
                        val y = cy + (maxRadius * step * sin(angle))
                        if (i == 0) webPath.moveTo(x, y) else webPath.lineTo(x, y)
                    }
                    webPath.close()
                    drawPath(
                        path = webPath,
                        color = if (step == 1.0f) GoldPrimary.copy(alpha = 0.35f) else ParchmentFaint.copy(alpha = 0.2f),
                        style = Stroke(width = if (step == 1.0f) 1.5f else 1f)
                    )
                }

                // Draw spokes from center
                for (i in 0 until 8) {
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val ex = cx + (maxRadius * cos(angle))
                    val ey = cy + (maxRadius * sin(angle))
                    drawLine(
                        color = ParchmentFaint.copy(alpha = 0.2f),
                        start = Offset(cx, cy),
                        end = Offset(ex, ey),
                        strokeWidth = 1f
                    )
                }

                // Draw Olfactory DNA Polygon
                val polygonPath = Path()
                for (i in 0 until 8) {
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val valueFraction = (dimensions[i].second.coerceIn(10, 100)) / 100f
                    val r = maxRadius * valueFraction
                    val px = cx + (r * cos(angle))
                    val py = cy + (r * sin(angle))
                    if (i == 0) polygonPath.moveTo(px, py) else polygonPath.lineTo(px, py)
                }
                polygonPath.close()

                // Filled aura
                drawPath(
                    path = polygonPath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldBright.copy(alpha = 0.45f),
                            AmberAccent.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = maxRadius
                    )
                )

                // Glowing border
                drawPath(
                    path = polygonPath,
                    color = GoldBright,
                    style = Stroke(width = 2.5f)
                )

                // Vertex beads
                for (i in 0 until 8) {
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val valueFraction = (dimensions[i].second.coerceIn(10, 100)) / 100f
                    val r = maxRadius * valueFraction
                    val px = cx + (r * cos(angle))
                    val py = cy + (r * sin(angle))
                    drawCircle(color = ObsidianBlack, radius = 5f, center = Offset(px, py))
                    drawCircle(color = GoldBright, radius = 3.5f, center = Offset(px, py))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Attribute Bars breakdown
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dimensions.forEach { (name, score) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = ParchmentMuted,
                        modifier = Modifier.width(130.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ObsidianElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(score / 100f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(GoldPrimary, AmberAccent, GoldBright)
                                    )
                                )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$score%",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = GoldBright,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

/**
 * 3-Tier Notes Pyramid Component (Top, Heart, Base)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScentNotesPyramid(
    fragrance: Fragrance,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianCard)
            .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "OLFACTORY PYRAMID & ACCORDS",
            style = MaterialTheme.typography.labelSmall,
            color = GoldPrimary,
            letterSpacing = 1.2.sp
        )

        // Top Notes Tier
        PyramidTier(
            title = "TOP NOTES (15–30 mins)",
            subtitle = "Luminous volatile opening accents",
            notes = fragrance.top_notes,
            chipColor = GoldBright.copy(alpha = 0.15f),
            textColor = GoldBright
        )

        // Heart Notes Tier
        PyramidTier(
            title = "HEART NOTES (2–4 hours)",
            subtitle = "The central botanical essence & character",
            notes = fragrance.middle_notes,
            chipColor = IndianRose.copy(alpha = 0.15f),
            textColor = IndianRose
        )

        // Base Notes Tier
        PyramidTier(
            title = "BASE NOTES (8–24 hours)",
            subtitle = "Deep fixative woods, attar oils & resins",
            notes = fragrance.base_notes,
            chipColor = MittiClay.copy(alpha = 0.15f),
            textColor = MysoreSandal
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PyramidTier(
    title: String,
    subtitle: String,
    notes: List<String>,
    chipColor: Color,
    textColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = ParchmentFaint
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (notes.isEmpty()) {
                Text(
                    text = "Traditional compounded blend",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = ParchmentMuted
                )
            } else {
                notes.forEach { note ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(chipColor)
                            .border(0.5.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = ParchmentWhite
                        )
                    }
                }
            }
        }
    }
}

/**
 * Olfactory AI Score Pill / Circular badge
 */
@Composable
fun OlfactoryScoreBadge(
    score: Int,
    modifier: Modifier = Modifier,
    label: String = "HARMONY SCORE"
) {
    val color = when {
        score >= 90 -> GoldBright
        score >= 80 -> AmberAccent
        score >= 70 -> MysoreSandal
        else -> ParchmentMuted
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        color.copy(alpha = 0.18f),
                        ObsidianSurface
                    )
                )
            )
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$score%",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp, fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = color.copy(alpha = 0.8f),
            letterSpacing = 1.0.sp
        )
    }
}

/**
 * Luxury Atelier Top Bar with golden accent divider
 */
@Composable
fun OlfactoryAtelierTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianBlack)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp).testTag("top_bar_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GoldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = ParchmentWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = GoldPrimary,
                        letterSpacing = 1.1.sp
                    )
                }
            }

            actions()
        }

        // Elegant golden thread separator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            GoldPrimary.copy(alpha = 0.5f),
                            GoldBright.copy(alpha = 0.8f),
                            GoldPrimary.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * Editorial Chapter Divider conforming to the original web application's ChapterDivider.
 * Features hairline amber rules, diamond glyphs, Roman chapter prefix, Cinzel-styled title, and poetic subtitle.
 */
@Composable
fun ChapterDivider(
    chapter: String,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decorative Hairline Rule with Amber Center Glyph
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, GoldPrimary.copy(alpha = 0.5f))
                        )
                    )
            )

            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(GoldPrimary.copy(alpha = 0.6f))
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(GoldBright)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(GoldPrimary.copy(alpha = 0.6f))
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(GoldPrimary.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Chapter Marker
        Text(
            text = chapter.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = GoldPrimary
        )

        // Chapter Title
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleMedium.copy(
                letterSpacing = 1.6.sp,
                fontWeight = FontWeight.Medium
            ),
            color = ParchmentWhite
        )

        // Poetic Subtitle
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = ParchmentMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

