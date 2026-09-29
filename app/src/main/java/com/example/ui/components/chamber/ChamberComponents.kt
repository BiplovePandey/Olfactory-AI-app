package com.example.ui.components.chamber

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fragrance
import com.example.data.model.OlfactoryVector8D
import com.example.data.model.LayeringResult
import com.example.ui.components.AtelierFlaconGraphic
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
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Chamber atmosphere palette configuration matching web original ChamberAtmosphere.ts
 */
data class ChamberPalette(
    val id: String,
    val name: String,
    val accentColor: Color,
    val glowColor: Color,
    val pedestalGlow: Color,
    val tagBg: Color,
    val tagText: Color,
    val borderAccent: Color
)

fun getChamberPalette(fragranceFamily: String, name: String = ""): ChamberPalette {
    val text = "$fragranceFamily $name".lowercase()
    return when {
        text.contains("rose") || text.contains("floral") || text.contains("jasmine") || text.contains("mogra") -> {
            ChamberPalette(
                id = "floral",
                name = "Damask Rose & Floral Radiance",
                accentColor = IndianRose,
                glowColor = Color(0xFFF43F5E),
                pedestalGlow = Color(0x59F43F5E),
                tagBg = Color(0x334C0519),
                tagText = Color(0xFFFECDD3),
                borderAccent = Color(0x4DF43F5E)
            )
        }
        text.contains("oud") || text.contains("oriental") || text.contains("spicy") || text.contains("amber") -> {
            ChamberPalette(
                id = "spicy",
                name = "Assam Oud & Warm Resins",
                accentColor = AmberAccent,
                glowColor = Color(0xFFB45309),
                pedestalGlow = Color(0x66B45309),
                tagBg = Color(0x4D451A03),
                tagText = Color(0xFFFDE68A),
                borderAccent = Color(0x4DB45309)
            )
        }
        text.contains("citrus") || text.contains("fresh") || text.contains("neroli") || text.contains("bergamot") -> {
            ChamberPalette(
                id = "citrus",
                name = "Solar Citrus & Fresh Zest",
                accentColor = Color(0xFFFBBF24),
                glowColor = Color(0xFFF59E0B),
                pedestalGlow = Color(0x61F59E0B),
                tagBg = Color(0x33451A03),
                tagText = Color(0xFFFEF08A),
                borderAccent = Color(0x4DFBBF24)
            )
        }
        text.contains("earth") || text.contains("mitti") || text.contains("clay") || text.contains("petrichor") -> {
            ChamberPalette(
                id = "earthy",
                name = "Mitti Terracotta & Petrichor",
                accentColor = MittiClay,
                glowColor = Color(0xFFEA580C),
                pedestalGlow = Color(0x61EA580C),
                tagBg = Color(0x33431407),
                tagText = Color(0xFFFED7AA),
                borderAccent = Color(0x4DEA580C)
            )
        }
        text.contains("aquatic") || text.contains("marine") || text.contains("ocean") || text.contains("ozone") -> {
            ChamberPalette(
                id = "aquatic",
                name = "Marine Ozone & Cool Waters",
                accentColor = Color(0xFF38BDF8),
                glowColor = Color(0xFF0EA5E9),
                pedestalGlow = Color(0x590EA5E9),
                tagBg = Color(0x33082F49),
                tagText = Color(0xFFBAE6FD),
                borderAccent = Color(0x4D38BDF8)
            )
        }
        text.contains("khus") || text.contains("vetiver") || text.contains("green") -> {
            ChamberPalette(
                id = "khus",
                name = "Kannauj Ruh Khus & Green Vetiver",
                accentColor = KannaujKhus,
                glowColor = Color(0xFF10B981),
                pedestalGlow = Color(0x5910B981),
                tagBg = Color(0x33064E3B),
                tagText = Color(0xFFA7F3D0),
                borderAccent = Color(0x4D10B981)
            )
        }
        else -> {
            ChamberPalette(
                id = "woody",
                name = "Sandalwood & Noble Woods",
                accentColor = MysoreSandal,
                glowColor = Color(0xFFD97706),
                pedestalGlow = Color(0x59D97706),
                tagBg = Color(0x33451A03),
                tagText = Color(0xFFFDE68A),
                borderAccent = Color(0x4DD97706)
            )
        }
    }
}

/**
 * Chamber Inspection Tabs Enum
 */
enum class ChamberTab(val title: String, val subtitle: String) {
    SIGNATURE("Signature 8D", "Vector Fingerprint"),
    PYRAMID("Pyramid", "Volatile Accords"),
    DRYDOWN("Drydown", "12h Time Machine"),
    RATIONALE("Rationale", "Harmonic Level 4"),
    LAYERING("Layering", "Cross-Family Partners")
}

/**
 * Flacon on Laboratory Pedestal with interactive drag tilt and illuminated aura
 */
@Composable
fun ChamberFlaconPedestal(
    fragrance: Fragrance,
    palette: ChamberPalette,
    vector8D: OlfactoryVector8D,
    modifier: Modifier = Modifier
) {
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val animatedTiltX by animateFloatAsState(
        targetValue = (-dragOffsetY.coerceIn(-120f, 120f) / 12f),
        animationSpec = tween(150),
        label = "pedestalTiltX"
    )
    val animatedTiltY by animateFloatAsState(
        targetValue = (dragOffsetX.coerceIn(-120f, 120f) / 12f),
        animationSpec = tween(150),
        label = "pedestalTiltY"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pedestalPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.70f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetX = (dragOffsetX + dragAmount.x).coerceIn(-150f, 150f)
                        dragOffsetY = (dragOffsetY + dragAmount.y).coerceIn(-150f, 150f)
                    },
                    onDragEnd = {
                        dragOffsetX = 0f
                        dragOffsetY = 0f
                    },
                    onDragCancel = {
                        dragOffsetX = 0f
                        dragOffsetY = 0f
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Atmospheric Scent Aura
        Canvas(
            modifier = Modifier
                .size(260.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val maxR = size.width / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        palette.pedestalGlow.copy(alpha = pulseAlpha),
                        palette.glowColor.copy(alpha = pulseAlpha * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = maxR
                ),
                radius = maxR,
                center = Offset(cx, cy)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                rotationX = animatedTiltX
                rotationY = animatedTiltY
                cameraDistance = 16 * density
            }
        ) {
            // Laboratory Flacon Graphic
            AtelierFlaconGraphic(
                fragrance = fragrance,
                modifier = Modifier
                    .size(160.dp)
                    .padding(8.dp)
                    .testTag("chamber_flacon_graphic"),
                isCompact = false
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Laboratory Brass Pedestal Base
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                GoldPrimary.copy(alpha = 0.5f),
                                ObsidianSurface,
                                ObsidianBlack
                            )
                        )
                    )
                    .border(1.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Gold pinstripe accent on pedestal
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(2.dp)
                        .background(GoldBright.copy(alpha = 0.8f))
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "DRAG TO ROTATE IN ATELIER CHAMBER",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = ParchmentFaint,
                letterSpacing = 1.2.sp
            )
        }
    }
}

/**
 * 8D Scent Signature Radar Visualizer with Interactive Data Sheet Mode
 */
data class DimensionConfig(
    val keyName: String,
    val label: String,
    val shortLabel: String,
    val angleDeg: Float,
    val color: Color,
    val description: String,
    val value: Int
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScentSignatureVisualizer(
    vector8D: OlfactoryVector8D,
    palette: ChamberPalette,
    modifier: Modifier = Modifier
) {
    var isDataSheetMode by remember { mutableStateOf(false) }
    var selectedDimName by remember { mutableStateOf<String?>(null) }

    val dimensions = remember(vector8D) {
        listOf(
            DimensionConfig("freshness", "Freshness", "Fresh", -90f, Color(0xFF34D399), "High-volatility citrus terpenes, aldehydes, sparkling ozone accords.", vector8D.freshness),
            DimensionConfig("floral", "Floral Radiance", "Floral", -45f, Color(0xFFF472B6), "Lush Damask petals, jasmine sambac, neroli, white floral indoles.", vector8D.floral),
            DimensionConfig("sweetness", "Sweetness", "Sweet", 0f, Color(0xFFFB7185), "Balsamic gourmand lactones, golden honey, rich vanilla pods.", vector8D.sweetness),
            DimensionConfig("resins", "Warm Resins & Spices", "Resins/Spice", 45f, Color(0xFFF59E0B), "Frankincense, benzoin, cardamom, saffron, clove bud warmth.", vector8D.warm_resinous_spices),
            DimensionConfig("intensity", "Projection & Intensity", "Intensity", 90f, Color(0xFFEF4444), "Vapor pressure force and ambient diffusion radius off warm skin.", vector8D.intensity),
            DimensionConfig("earthy", "Earthy Clay (Mitti)", "Mitti/Clay", 135f, Color(0xFFEA580C), "Alluvial geosmin, petrichor, terra cotta, wet rain-quenched soil.", vector8D.earthy_clay),
            DimensionConfig("woody", "Woody Depth", "Woody", 180f, Color(0xFFD97706), "Mysore sandalwood, cedarwood, aged agarwood oud, dry patchouli.", vector8D.woody),
            DimensionConfig("longevity", "Fixative Retention", "Fixative", 225f, Color(0xFF2DD4BF), "Macrocyclic musks, ambroxan, heavy fixatives binding to skin lipid.", vector8D.longevity_fixative)
        )
    }

    val activeDim = dimensions.firstOrNull { it.keyName == selectedDimName } ?: dimensions[0]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chamber_signature_visualizer"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(palette.borderAccent, ObsidianCardBorder))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "8-DIMENSIONAL OLFACTORY FINGERPRINT",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary,
                            letterSpacing = 1.3.sp
                        )
                    }
                    Text(
                        text = "Scent Signature",
                        style = MaterialTheme.typography.titleLarge,
                        color = ParchmentWhite
                    )
                }

                // Interactive / Data Sheet Switch Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .clickable { isDataSheetMode = !isDataSheetMode }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("signature_toggle_datasheet")
                ) {
                    Text(
                        text = if (isDataSheetMode) "RADIAL MAP" else "EXACT VECTOR",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isDataSheetMode) {
                // Interactive Radar Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val maxR = minOf(cx, cy) * 0.78f
                        val minR = maxR * 0.15f
                        val angleStep = (2 * Math.PI / 8).toFloat()

                        // Draw brass calibration concentric rings
                        val steps = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                        steps.forEachIndexed { idx, step ->
                            val r = minR + step * (maxR - minR)
                            val ringPath = Path()
                            for (i in 0 until 8) {
                                val a = (i * angleStep) - (Math.PI / 2).toFloat()
                                val px = cx + r * cos(a)
                                val py = cy + r * sin(a)
                                if (i == 0) ringPath.moveTo(px, py) else ringPath.lineTo(px, py)
                            }
                            ringPath.close()
                            drawPath(
                                path = ringPath,
                                color = if (idx == 3) GoldPrimary.copy(alpha = 0.45f) else ParchmentFaint.copy(alpha = 0.18f),
                                style = Stroke(width = if (idx == 3) 1.5f else 1f)
                            )
                        }

                        // Draw spoke lines
                        for (i in 0 until 8) {
                            val a = (i * angleStep) - (Math.PI / 2).toFloat()
                            val ex = cx + maxR * cos(a)
                            val ey = cy + maxR * sin(a)
                            drawLine(
                                color = ParchmentFaint.copy(alpha = 0.25f),
                                start = Offset(cx, cy),
                                end = Offset(ex, ey),
                                strokeWidth = 1f
                            )
                        }

                        // Construct Vector Polygon
                        val polyPath = Path()
                        val vertexPoints = mutableListOf<Offset>()
                        dimensions.forEachIndexed { i, dim ->
                            val a = (dim.angleDeg * Math.PI / 180f).toFloat()
                            val frac = (dim.value.coerceIn(10, 100)) / 100f
                            val r = minR + frac * (maxR - minR)
                            val px = cx + r * cos(a)
                            val py = cy + r * sin(a)
                            vertexPoints.add(Offset(px, py))
                            if (i == 0) polyPath.moveTo(px, py) else polyPath.lineTo(px, py)
                        }
                        polyPath.close()

                        // Draw shaded polygon
                        drawPath(
                            path = polyPath,
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    palette.accentColor.copy(alpha = 0.5f),
                                    GoldPrimary.copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy),
                                radius = maxR
                            )
                        )
                        drawPath(
                            path = polyPath,
                            color = GoldBright,
                            style = Stroke(width = 2.5f)
                        )

                        // Central core golden bead
                        drawCircle(color = GoldBright, radius = 6f, center = Offset(cx, cy))
                        drawCircle(color = ObsidianBlack, radius = 3f, center = Offset(cx, cy))

                        // Draw vertices beads
                        vertexPoints.forEachIndexed { idx, pt ->
                            val dim = dimensions[idx]
                            val isSelected = dim.keyName == activeDim.keyName
                            drawCircle(
                                color = if (isSelected) dim.color else GoldPrimary,
                                radius = if (isSelected) 8f else 5f,
                                center = pt
                            )
                            drawCircle(
                                color = ObsidianBlack,
                                radius = if (isSelected) 4f else 2.5f,
                                center = pt
                            )
                        }
                    }
                }

                // Interactive dimension pills for touch selection
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dimensions.forEach { dim ->
                        val isSelected = dim.keyName == activeDim.keyName
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) dim.color.copy(alpha = 0.25f) else ObsidianElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) dim.color else ObsidianCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedDimName = dim.keyName }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(dim.color)
                                )
                                Text(
                                    text = "${dim.shortLabel} ${dim.value}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isSelected) ParchmentWhite else ParchmentMuted,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Active Dimension Card Popover
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, activeDim.color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activeDim.label,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                color = ParchmentWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activeDim.value}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                color = GoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activeDim.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = ParchmentMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                // Exact Vector Data Sheet breakdown bars
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    dimensions.forEach { dim ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(dim.color)
                                    )
                                    Text(
                                        text = dim.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = ParchmentWhite
                                    )
                                }
                                Text(
                                    text = "${dim.value}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = GoldBright,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(ObsidianElevated)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(dim.value / 100f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(dim.color)
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
 * Intelligent Note Sensory Profile helper
 */
data class NoteSensoryDetail(
    val name: String,
    val tier: String,
    val tierLabel: String,
    val glyph: String,
    val volatilityRate: String,
    val thermodynamicRange: String,
    val aromaProfile: String,
    val fixativeRole: String
)

fun getNoteSensoryDetail(name: String, tier: String): NoteSensoryDetail {
    val lower = name.lowercase()
    val glyph = when {
        lower.contains("rose") || lower.contains("flower") || lower.contains("jasmine") || lower.contains("mogra") -> "🌸"
        lower.contains("bergamot") || lower.contains("lemon") || lower.contains("citrus") || lower.contains("orange") || lower.contains("lime") -> "🍋"
        lower.contains("sandalwood") || lower.contains("chandan") || lower.contains("cedar") || lower.contains("wood") || lower.contains("oud") || lower.contains("vetiver") || lower.contains("khus") -> "🪵"
        lower.contains("amber") || lower.contains("resin") || lower.contains("benzoin") || lower.contains("frankincense") -> "✨"
        lower.contains("cardamom") || lower.contains("pepper") || lower.contains("saffron") || lower.contains("cinnamon") || lower.contains("clove") -> "🌶️"
        lower.contains("musk") || lower.contains("leather") || lower.contains("civet") -> "🦌"
        lower.contains("mitti") || lower.contains("clay") || lower.contains("earth") || lower.contains("petrichor") -> "🏺"
        lower.contains("marine") || lower.contains("sea") || lower.contains("water") || lower.contains("ozone") -> "🌊"
        lower.contains("vanilla") || lower.contains("tonka") || lower.contains("honey") || lower.contains("sugar") -> "🍯"
        else -> "🌿"
    }

    return when (tier) {
        "top" -> NoteSensoryDetail(
            name = name,
            tier = "top",
            tierLabel = "Top Volatiles (Opening Accord)",
            glyph = glyph,
            volatilityRate = "High Flash (0 to 30 mins)",
            thermodynamicRange = "< 250°C (Low MW Terpenes)",
            aromaProfile = "Crisp, effervescent opening providing the initial olfactory impact upon skin application.",
            fixativeRole = "Diffuses rapidly into immediate headspace; creates initial lift and impression."
        )
        "heart" -> NoteSensoryDetail(
            name = name,
            tier = "heart",
            tierLabel = "Heart / Middle Accord (Core Theme)",
            glyph = glyph,
            volatilityRate = "Steady Isothermal (30 mins to 3 hours)",
            thermodynamicRange = "250°C – 320°C (Medium MW Esters & Alcohols)",
            aromaProfile = "The emotional signature of the fragrance, blossoming as top notes gently dissipate.",
            fixativeRole = "Forms the harmonious bridge between fleeting top terpenes and heavy base fixatives."
        )
        else -> NoteSensoryDetail(
            name = name,
            tier = "base",
            tierLabel = "Base Fixatives (Drydown Shadow)",
            glyph = glyph,
            volatilityRate = "Persistent Substantivity (3 to 12+ hours)",
            thermodynamicRange = "> 320°C (High MW Sesquiterpenes & Musks)",
            aromaProfile = "Deep, grounding resins and noble woods anchoring the entire composition to skin lipids.",
            fixativeRole = "Reduces overall vapor pressure; prolongs the diffusion life of heart notes."
        )
    }
}

/**
 * Visual Note Pyramid with Interactive Note Popover Inspector
 */
@Composable
fun VisualNotePyramid(
    fragrance: Fragrance,
    palette: ChamberPalette,
    modifier: Modifier = Modifier
) {
    var inspectedNote by remember { mutableStateOf<NoteSensoryDetail?>(null) }

    val topNotes = if (fragrance.top_notes.isNotEmpty()) fragrance.top_notes else listOf("Bergamot", "Sparkling Terpenes")
    val heartNotes = if (fragrance.middle_notes.isNotEmpty()) fragrance.middle_notes else listOf("Damask Petals", "Spiced Accord")
    val baseNotes = if (fragrance.base_notes.isNotEmpty()) fragrance.base_notes else listOf("Mysore Santal", "Amber Resin", "Musk")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chamber_visual_note_pyramid"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(palette.borderAccent, ObsidianCardBorder))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "VOLATILE KINETIC ARCHITECTURE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary,
                            letterSpacing = 1.3.sp
                        )
                    }
                    Text(
                        text = "Evolving Note Pyramid",
                        style = MaterialTheme.typography.titleLarge,
                        color = ParchmentWhite
                    )
                }

                Text(
                    text = "TAP NOTE TO INSPECT",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = ParchmentFaint
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TIER 1: TOP VOLATILES
            PyramidTierBox(
                tierTitle = "TOP VOLATILES • FAST OPENING (0m – 30m)",
                thermoSpec = "Vapor Flash: < 250°C",
                notes = topNotes,
                tierType = "top",
                accentColor = GoldBright,
                onNoteClick = { noteName ->
                    inspectedNote = getNoteSensoryDetail(noteName, "top")
                }
            )

            // Connector arrow
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = ParchmentFaint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // TIER 2: HEART ACCORD
            PyramidTierBox(
                tierTitle = "HEART ACCORD • CORE DIFFUSION (30m – 3h)",
                thermoSpec = "Isothermal Core: 250°C – 320°C",
                notes = heartNotes,
                tierType = "heart",
                accentColor = IndianRose,
                onNoteClick = { noteName ->
                    inspectedNote = getNoteSensoryDetail(noteName, "heart")
                }
            )

            // Connector arrow
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = ParchmentFaint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // TIER 3: BASE FIXATIVES
            PyramidTierBox(
                tierTitle = "BASE FIXATIVES • DRYDOWN SHADOW (3h – 12h+)",
                thermoSpec = "Macromolecules: > 320°C",
                notes = baseNotes,
                tierType = "base",
                accentColor = AmberAccent,
                onNoteClick = { noteName ->
                    inspectedNote = getNoteSensoryDetail(noteName, "base")
                }
            )

            // Inspected Note Popover
            AnimatedVisibility(
                visible = inspectedNote != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                inspectedNote?.let { note ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianElevated)
                            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = note.glyph,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Column {
                                        Text(
                                            text = note.tierLabel,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = GoldPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = note.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = ParchmentWhite
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { inspectedNote = null },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = ParchmentMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = note.aromaProfile,
                                style = MaterialTheme.typography.bodySmall,
                                color = ParchmentMuted,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ObsidianSurface)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "EVAPORATION RATE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                            color = ParchmentFaint
                                        )
                                        Text(
                                            text = note.volatilityRate,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                            color = GoldBright,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ObsidianSurface)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "BOILING RANGE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                            color = ParchmentFaint
                                        )
                                        Text(
                                            text = note.thermodynamicRange,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                            color = ParchmentWhite
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• ${note.fixativeRole}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ParchmentFaint
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PyramidTierBox(
    tierTitle: String,
    thermoSpec: String,
    notes: List<String>,
    tierType: String,
    accentColor: Color,
    onNoteClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianElevated)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = tierTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = thermoSpec,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                    color = ParchmentFaint
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                notes.forEach { note ->
                    val sensory = getNoteSensoryDetail(note, tierType)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(accentColor.copy(alpha = 0.12f))
                            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                            .clickable { onNoteClick(note) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = sensory.glyph, fontSize = 12.sp)
                            Text(
                                text = note,
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                color = ParchmentWhite,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Drydown Time Machine with Sillage Diffusion Halo & Timeline Scrubber
 */
data class DrydownTimelineStage(
    val timeKey: String,
    val label: String,
    val stageName: String,
    val topOpacity: Float,
    val heartOpacity: Float,
    val baseOpacity: Float,
    val sillageLabel: String,
    val sillageRadiusPct: Float,
    val projectionDescriptor: String,
    val chemicalBehavior: String
)

@Composable
fun ChamberDrydownMachine(
    fragrance: Fragrance,
    palette: ChamberPalette,
    modifier: Modifier = Modifier
) {
    val stages = remember {
        listOf(
            DrydownTimelineStage(
                timeKey = "0m",
                label = "Spritz",
                stageName = "Initial Volatile Flash",
                topOpacity = 1.0f,
                heartOpacity = 0.25f,
                baseOpacity = 0.10f,
                sillageLabel = "Expansive Radiance (1.8m radius)",
                sillageRadiusPct = 0.92f,
                projectionDescriptor = "Volatile esters, aldehydes & citrus terpenes rapidly vaporize off skin lipid barrier.",
                chemicalBehavior = "Ethanol flash carries volatile high-vapor pressure molecules into immediate headspace."
            ),
            DrydownTimelineStage(
                timeKey = "15m",
                label = "15 min",
                stageName = "Early Bloom & Accord Bridging",
                topOpacity = 0.75f,
                heartOpacity = 0.65f,
                baseOpacity = 0.30f,
                sillageLabel = "Moderate Expansive (1.4m radius)",
                sillageRadiusPct = 0.76f,
                projectionDescriptor = "Top molecules temper; delicate mid florals and peppery aromachemicals dock into focus.",
                chemicalBehavior = "Hedione & light lactones begin binding top terpenes to mid-weight phenyl aldehydes."
            ),
            DrydownTimelineStage(
                timeKey = "2h",
                label = "2 hours",
                stageName = "Full Heart & Core Accord",
                topOpacity = 0.15f,
                heartOpacity = 1.0f,
                baseOpacity = 0.65f,
                sillageLabel = "Intimate to Moderate Aura (0.8m radius)",
                sillageRadiusPct = 0.56f,
                projectionDescriptor = "Heart petals, spices, and herbaceous notes achieve steady-state isothermal diffusion.",
                chemicalBehavior = "Iso E Super / Ambroxan matrices anchor mid-weight molecules against ambient air currents."
            ),
            DrydownTimelineStage(
                timeKey = "6h",
                label = "6 hours",
                stageName = "Deep Basenote Fixation",
                topOpacity = 0.0f,
                heartOpacity = 0.35f,
                baseOpacity = 0.95f,
                sillageLabel = "Personal Skin Scent (0.3m radius)",
                sillageRadiusPct = 0.36f,
                projectionDescriptor = "Heavy santalols, resins, oud sesquiterpenes, and vanillin linger close to warm pulse points.",
                chemicalBehavior = "High molecular weight aromachemicals (MW > 250 g/mol) maintain adhesive skin affinity."
            ),
            DrydownTimelineStage(
                timeKey = "12h",
                label = "12+ hours",
                stageName = "The Drydown Shadow",
                topOpacity = 0.0f,
                heartOpacity = 0.05f,
                baseOpacity = 0.85f,
                sillageLabel = "Intimate Sensory Scent-Film",
                sillageRadiusPct = 0.22f,
                projectionDescriptor = "Skin-absorbed musks and noble woody fixatives persist as a private, lingering signature.",
                chemicalBehavior = "Hydrophobic amber and macrocyclic musks remain locked in keratin protein matrix."
            )
        )
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(2800)
            selectedIndex = (selectedIndex + 1) % stages.size
        }
    }

    val activeStage = stages[selectedIndex]

    val topNotes = if (fragrance.top_notes.isNotEmpty()) fragrance.top_notes else listOf("Opening Terpenes")
    val heartNotes = if (fragrance.middle_notes.isNotEmpty()) fragrance.middle_notes else listOf("Heart Florals & Spices")
    val baseNotes = if (fragrance.base_notes.isNotEmpty()) fragrance.base_notes else listOf("Woody Resinous Fixatives")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chamber_drydown_machine"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(palette.borderAccent, ObsidianCardBorder))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "EVAPORATION KINETICS ENGINE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary,
                            letterSpacing = 1.3.sp
                        )
                    }
                    Text(
                        text = "Drydown Time Machine",
                        style = MaterialTheme.typography.titleLarge,
                        color = ParchmentWhite
                    )
                }

                // Play / Pause Cycle button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPlaying) GoldPrimary.copy(alpha = 0.25f) else ObsidianElevated)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { isPlaying = !isPlaying }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isPlaying) "PAUSE" else "SIMULATE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldBright,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Scrubber Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stages.forEachIndexed { idx, st ->
                    val isSelected = idx == selectedIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) GoldPrimary.copy(alpha = 0.5f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                isPlaying = false
                                selectedIndex = idx
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = st.timeKey,
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                color = if (isSelected) GoldBright else ParchmentMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = st.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                color = if (isSelected) ParchmentWhite else ParchmentFaint
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sillage Halo Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val maxR = size.width / 2f

                    // Concentric Guide Rings
                    drawCircle(color = ParchmentFaint.copy(alpha = 0.15f), radius = maxR, center = Offset(cx, cy), style = Stroke(width = 1f))
                    drawCircle(color = ParchmentFaint.copy(alpha = 0.12f), radius = maxR * 0.65f, center = Offset(cx, cy), style = Stroke(width = 1f))
                    drawCircle(color = ParchmentFaint.copy(alpha = 0.08f), radius = maxR * 0.35f, center = Offset(cx, cy), style = Stroke(width = 1f))

                    // Dynamic Sillage Radius Aura
                    val animatedR = maxR * activeStage.sillageRadiusPct
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                palette.accentColor.copy(alpha = 0.5f),
                                palette.pedestalGlow.copy(alpha = 0.35f),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = animatedR
                        ),
                        radius = animatedR,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = GoldBright.copy(alpha = 0.7f),
                        radius = animatedR,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.5f)
                    )

                    // Central pulse point
                    drawCircle(color = GoldBright, radius = 5f, center = Offset(cx, cy))
                    drawCircle(color = ObsidianBlack, radius = 2.5f, center = Offset(cx, cy))
                }

                // Sillage readout pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianBlack.copy(alpha = 0.8f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = activeStage.sillageLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = GoldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stage Description
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianElevated)
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeStage.stageName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Skin Vapor Kinetics",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = ParchmentFaint
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“${activeStage.projectionDescriptor}”",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParchmentWhite,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Molecular Behavior: ${activeStage.chemicalBehavior}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = ParchmentMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Headspace Concentration Bars
            Text(
                text = "ACTIVE HEADSPACE CONCENTRATION",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = ParchmentFaint,
                letterSpacing = 1.1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            HeadspaceConcentrationBar(
                label = "Top (${topNotes.take(2).joinToString(", ")})",
                fraction = activeStage.topOpacity,
                color = GoldBright
            )
            Spacer(modifier = Modifier.height(6.dp))
            HeadspaceConcentrationBar(
                label = "Heart (${heartNotes.take(2).joinToString(", ")})",
                fraction = activeStage.heartOpacity,
                color = IndianRose
            )
            Spacer(modifier = Modifier.height(6.dp))
            HeadspaceConcentrationBar(
                label = "Base (${baseNotes.take(2).joinToString(", ")})",
                fraction = activeStage.baseOpacity,
                color = AmberAccent
            )
        }
    }
}

@Composable
private fun HeadspaceConcentrationBar(
    label: String,
    fraction: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ParchmentMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${(fraction * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(ObsidianElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.5.dp))
                    .background(color)
            )
        }
    }
}

/**
 * Chamber Harmonic Rationale (Why This Fragrance?)
 */
@Composable
fun ChamberWhyThisFragrance(
    fragrance: Fragrance,
    vector8D: OlfactoryVector8D,
    palette: ChamberPalette,
    modifier: Modifier = Modifier
) {
    var isScienceExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chamber_why_this_fragrance"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(palette.borderAccent, ObsidianCardBorder))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "HARMONIC INTELLIGENCE • LEVEL 4 RATIONALE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldPrimary,
                            letterSpacing = 1.3.sp
                        )
                    }
                    Text(
                        text = "Why This Fragrance?",
                        style = MaterialTheme.typography.titleLarge,
                        color = ParchmentWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Olfactory Character
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.accentColor.copy(alpha = 0.1f))
                    .border(1.dp, palette.accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "OLFACTORY CHARACTER & EMOTIONAL TEXTURE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = GoldBright,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“${fragrance.name} embodies the ${fragrance.fragrance_family} archetype. With prominent ${fragrance.top_notes.take(2).joinToString(" & ")} diffusing into deep ${fragrance.base_notes.take(2).joinToString(" & ")}, it delivers a contemplative, luxurious presence.”",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ParchmentWhite,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Environmental & Performance Matrix
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Climate Match
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, ObsidianCardBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.DeviceThermostat, contentDescription = null, tint = Color(0xFF2DD4BF), modifier = Modifier.size(12.dp))
                            Text(text = "CLIMATE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = Color(0xFF2DD4BF))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (fragrance.season.isNotEmpty()) fragrance.season.first() else "All Season",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = ParchmentWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Temperate & Warm",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                            color = ParchmentMuted
                        )
                    }
                }

                // Longevity Rating
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, ObsidianCardBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.History, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
                            Text(text = "LONGEVITY", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = GoldPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fragrance.longevity,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = ParchmentWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Skin Substantivity",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                            color = ParchmentMuted
                        )
                    }
                }

                // Sillage Projection
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, ObsidianCardBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = IndianRose, modifier = Modifier.size(12.dp))
                            Text(text = "INTENSITY", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = IndianRose)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${vector8D.intensity}/100",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = ParchmentWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Vapor Pressure",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                            color = ParchmentMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Optimal Occasions & Layering Family
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianElevated)
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "OPTIMAL OCCASIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                        color = ParchmentFaint
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val occasions = if (fragrance.occasion.isNotEmpty()) fragrance.occasion else listOf("Evening", "Intimate", "Ceremonial")
                        occasions.forEach { occ ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ObsidianSurface)
                                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = occ,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                    color = ParchmentMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "RECOMMENDED COUNTERPART FAMILY",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                        color = ParchmentFaint
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (fragrance.fragrance_family.contains("Floral", ignoreCase = true)) "Woody Sandalwood / Musk" else "Sparkling Citrus / Damask Rose",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Level 5 Scientific Evaporation Principles Disclosure Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianElevated)
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(10.dp))
                    .clickable { isScienceExpanded = !isScienceExpanded }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Level 5: Modelled Evaporation Principles",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = ParchmentWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = if (isScienceExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = ParchmentMuted
                    )
                }
            }

            AnimatedVisibility(visible = isScienceExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianSurface)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "COMPUTATIONAL EVAPORATION MODEL",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                            color = GoldBright,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vaporization kinetics are computationally simulated using multi-component volatility indices. High vapor-pressure monoterpenes exhibit rapid early diffusion rates (0–45 min), while sesquiterpene alcohols and resinous fixatives maintain persistent skin substantivity over 8–12 hours.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ParchmentMuted,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Freshness: ${vector8D.freshness}%", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = Color(0xFF34D399))
                            Text(text = "Woody: ${vector8D.woody}%", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = Color(0xFFD97706))
                            Text(text = "Resinous: ${vector8D.warm_resinous_spices}%", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = Color(0xFFF59E0B))
                            Text(text = "Fixative: ${vector8D.longevity_fixative}%", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = Color(0xFF2DD4BF))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Chamber Indian Heritage Provenance Card
 */
@Composable
fun ChamberHeritageProvenance(
    fragrance: Fragrance,
    palette: ChamberPalette,
    onExploreHeritage: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val isIndianOrigin = fragrance.origin_style.contains("indian", ignoreCase = true) ||
            (fragrance.brand_country ?: "").contains("india", ignoreCase = true) ||
            fragrance.is_oil_based ||
            fragrance.format == "Attar"

    val region = if (isIndianOrigin) {
        fragrance.brand_country ?: "Kannauj & Mysore, India"
    } else {
        fragrance.brand_country ?: "Grasse & Haute Parfumerie Heritage"
    }

    val distillation = if (fragrance.is_oil_based || fragrance.format == "Attar") {
        "Artisanal Copper Deg & Bhapka Hydro-distillation"
    } else {
        "Noble Fractionated Hydro-distillation & Solvent Extraction"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chamber_heritage_provenance"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.5f), ObsidianCardBorder))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with wax seal motif
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f))
                            .border(1.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ARCHIVAL COLLECTOR'S CATALOGUE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                            color = GoldPrimary,
                            letterSpacing = 1.4.sp
                        )
                        Text(
                            text = "Living Heritage Provenance",
                            style = MaterialTheme.typography.titleLarge,
                            color = ParchmentWhite
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldPrimary.copy(alpha = 0.15f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldBright, modifier = Modifier.size(10.dp))
                        Text(
                            text = region,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                            color = GoldBright
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Narrative quote
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianElevated)
                    .border(1.dp, GoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = if (isIndianOrigin) {
                        "“Rooted in 1,000 years of traditional Deg-Bhapka hydro-distillation on the banks of the Ganges, capturing pure botanical vapors into subterranean copper receivers.”"
                    } else {
                        "“Crafted under classical haute parfumerie traditions, balancing noble organic raw essences with contemporary olfactory architecture.”"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentWhite,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Distillation spec
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "EXTRACTION METHOD",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                        color = GoldPrimary
                    )
                    Text(
                        text = distillation,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ParchmentMuted
                    )
                }
            }

            if (onExploreHeritage != null && isIndianOrigin) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldPrimary.copy(alpha = 0.15f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { onExploreHeritage() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = GoldBright, modifier = Modifier.size(14.dp))
                        Text(
                            text = "EXPLORE HERITAGE ATLAS →",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = GoldBright,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
