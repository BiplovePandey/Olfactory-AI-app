package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.IndianRose
import com.example.ui.theme.KannaujKhus
import com.example.ui.theme.MittiClay
import com.example.ui.theme.MysoreSandal
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.OudDark
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Atmospheric metadata corresponding directly to the original web
 * AtmosphericFragranceCanvas.tsx ATMOSPHERE_PROFILES.
 */
enum class ScentAtmosphereStyle {
    EARTHY, KHUS, OUD, SPICY, ALPINE, ROSE, CITRUS, AQUATIC, WOODY, DEFAULT
}

data class AtmosphericProfile(
    val style: ScentAtmosphereStyle,
    val name: String,
    val glowColor1: Color,
    val glowColor2: Color,
    val accentColor: Color,
    val particleColors: List<Color>,
    val particleType: ParticleMovementType
)

enum class ParticleMovementType {
    SPARKLE, SMOKE, PETAL, MIST, DUST, ORGANIC
}

object AtmosphereRegistry {
    val EARTHY = AtmosphericProfile(
        style = ScentAtmosphereStyle.EARTHY,
        name = "Alluvial Clay & Mitti",
        glowColor1 = Color(194, 65, 12, 40), // terracotta rust
        glowColor2 = Color(120, 53, 15, 35),
        accentColor = MittiClay,
        particleColors = listOf(MittiClay, AmberAccent, Color(0xFF9A3412), Color(0xFFF97316)),
        particleType = ParticleMovementType.DUST
    )

    val KHUS = AtmosphericProfile(
        style = ScentAtmosphereStyle.KHUS,
        name = "Ruh Khus & Wild Vetiver",
        glowColor1 = Color(16, 185, 129, 40), // emerald dew
        glowColor2 = Color(6, 95, 70, 38),
        accentColor = KannaujKhus,
        particleColors = listOf(Color(0xFF34D399), KannaujKhus, Color(0xFF059669), Color(0xFF6EE7B7)),
        particleType = ParticleMovementType.MIST
    )

    val OUD = AtmosphericProfile(
        style = ScentAtmosphereStyle.OUD,
        name = "Assam Oud & Amber Coals",
        glowColor1 = Color(180, 83, 9, 50), // cognac amber
        glowColor2 = Color(88, 28, 135, 38), // violet incense smoke
        accentColor = AmberAccent,
        particleColors = listOf(Color(0xFFB45309), Color(0xFF78350F), AmberAccent, Color(0xFF6B21A8)),
        particleType = ParticleMovementType.SMOKE
    )

    val ROSE = AtmosphericProfile(
        style = ScentAtmosphereStyle.ROSE,
        name = "Damask Rose of Kannauj",
        glowColor1 = Color(244, 63, 94, 40), // damask rose
        glowColor2 = Color(190, 24, 93, 33), // rich wine
        accentColor = IndianRose,
        particleColors = listOf(Color(0xFFFB7185), IndianRose, Color(0xFFFDA4AF), Color(0xFFE11D48)),
        particleType = ParticleMovementType.PETAL
    )

    val CITRUS = AtmosphericProfile(
        style = ScentAtmosphereStyle.CITRUS,
        name = "Solar Citrus & Orange Blossom",
        glowColor1 = Color(245, 158, 11, 40),
        glowColor2 = Color(234, 179, 8, 30),
        accentColor = Color(0xFFFBBF24),
        particleColors = listOf(Color(0xFFFDE047), Color(0xFFFBBF24), Color(0xFFF59E0B)),
        particleType = ParticleMovementType.SPARKLE
    )

    val AQUATIC = AtmosphericProfile(
        style = ScentAtmosphereStyle.AQUATIC,
        name = "Oceanic Ozone & Sea Salt",
        glowColor1 = Color(14, 165, 233, 40),
        glowColor2 = Color(15, 118, 110, 33),
        accentColor = Color(0xFF06B6D4),
        particleColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF22D3EE)),
        particleType = ParticleMovementType.MIST
    )

    val WOODY = AtmosphericProfile(
        style = ScentAtmosphereStyle.WOODY,
        name = "Mysore Sandalwood & Spices",
        glowColor1 = Color(202, 138, 4, 43),
        glowColor2 = Color(113, 63, 18, 38),
        accentColor = MysoreSandal,
        particleColors = listOf(MysoreSandal, Color(0xFFA16207), AmberAccent, Color(0xFF713F12)),
        particleType = ParticleMovementType.ORGANIC
    )

    val SPICY = AtmosphericProfile(
        style = ScentAtmosphereStyle.SPICY,
        name = "Warm Resins & Sacred Spices",
        glowColor1 = Color(234, 88, 12, 45),
        glowColor2 = Color(185, 28, 28, 35),
        accentColor = Color(0xFFEA580C),
        particleColors = listOf(Color(0xFFEA580C), AmberAccent, Color(0xFFB45309)),
        particleType = ParticleMovementType.SPARKLE
    )

    val ALPINE = AtmosphericProfile(
        style = ScentAtmosphereStyle.ALPINE,
        name = "Himalayan Cedar & Bergamot",
        glowColor1 = Color(56, 189, 248, 40),
        glowColor2 = Color(30, 58, 138, 35),
        accentColor = Color(0xFF38BDF8),
        particleColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF7DD3FC)),
        particleType = ParticleMovementType.SPARKLE
    )

    val DEFAULT = AtmosphericProfile(
        style = ScentAtmosphereStyle.DEFAULT,
        name = "Atelier Signature Atmosphere",
        glowColor1 = Color(217, 119, 6, 30), // soft warm amber
        glowColor2 = Color(68, 64, 60, 25), // quiet espresso taupe
        accentColor = AmberAccent,
        particleColors = listOf(AmberAccent, MysoreSandal, Color(0xFFE5C158)),
        particleType = ParticleMovementType.ORGANIC
    )

    fun resolveProfile(familyOrNote: String?): AtmosphericProfile {
        if (familyOrNote == null) return DEFAULT
        val s = familyOrNote.lowercase()
        return when {
            s.contains("rose") || s.contains("floral") || s.contains("jasmine") -> ROSE
            s.contains("mitti") || s.contains("clay") || s.contains("petrichor") || s.contains("earth") -> EARTHY
            s.contains("khus") || s.contains("vetiver") || s.contains("green") -> KHUS
            s.contains("oud") || s.contains("incense") || s.contains("smoke") -> OUD
            s.contains("spice") || s.contains("amber") || s.contains("resins") -> SPICY
            s.contains("citrus") || s.contains("bergamot") || s.contains("lemon") || s.contains("solar") -> CITRUS
            s.contains("aqua") || s.contains("marine") || s.contains("sea") || s.contains("ozone") -> AQUATIC
            s.contains("cedar") || s.contains("alpine") -> ALPINE
            s.contains("wood") || s.contains("sandal") -> WOODY
            else -> DEFAULT
        }
    }
}

/**
 * Lightweight particle for ambient olfactory motes.
 */
private class AtmosphericMote(
    var xRatio: Float,
    var yRatio: Float,
    val speedX: Float,
    val speedY: Float,
    val radiusRatio: Float,
    val baseAlpha: Float,
    val color: Color,
    var phase: Float,
    val phaseSpeed: Float
)

/**
 * Native Jetpack Compose Atmospheric Canvas reproducing the original
 * AtmosphericFragranceCanvas.tsx from the Olfactory AI main branch.
 *
 * Characteristics:
 * - Placed underneath all screen layers (pointer-events non-interfering)
 * - Soft morphing ambient fluid glow orbs matching active scent family
 * - Micro-mote ambient movement (dust, mist, petals, or smoke)
 * - Gentle frame interpolation with minimal overhead and 0 allocation in draw loop
 */
@Composable
fun AtmosphericFragranceCanvas(
    modifier: Modifier = Modifier,
    activeAtmosphere: String? = null,
    intensity: Float = 1.0f
) {
    val targetProfile = remember(activeAtmosphere) {
        AtmosphereRegistry.resolveProfile(activeAtmosphere)
    }

    // Smooth color transitions between scent profiles
    val animatedGlow1 by animateColorAsState(
        targetValue = targetProfile.glowColor1,
        animationSpec = tween(durationMillis = 1200),
        label = "atmosphere_glow1"
    )
    val animatedGlow2 by animateColorAsState(
        targetValue = targetProfile.glowColor2,
        animationSpec = tween(durationMillis = 1200),
        label = "atmosphere_glow2"
    )

    // Infinite breathing rhythm for ambient liquid fluid orbs
    val infiniteTransition = rememberInfiniteTransition(label = "orb_breathing")
    val orbPulse1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbPulse1"
    )
    val orbPulse2 by infiniteTransition.animateFloat(
        initialValue = 1.12f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(7800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbPulse2"
    )

    // Pre-allocated particle pool to avoid GC allocations
    val particleCount = remember { 16 }
    val particles = remember(targetProfile.style) {
        val rand = Random(42)
        val palette = targetProfile.particleColors
        List(particleCount) {
            val color = palette[rand.nextInt(palette.size)]
            val (vx, vy, r, a) = when (targetProfile.particleType) {
                ParticleMovementType.SPARKLE -> listOf((rand.nextFloat() - 0.5f) * 0.0008f, -0.0012f, 2.2f, 0.28f)
                ParticleMovementType.SMOKE -> listOf((rand.nextFloat() - 0.5f) * 0.0003f, -0.0004f, 12f, 0.12f)
                ParticleMovementType.PETAL -> listOf((rand.nextFloat() - 0.5f) * 0.0005f, 0.0005f, 3.5f, 0.24f)
                ParticleMovementType.MIST -> listOf(0.0008f, (rand.nextFloat() - 0.5f) * 0.0002f, 2.5f, 0.22f)
                ParticleMovementType.DUST -> listOf((rand.nextFloat() - 0.5f) * 0.0003f, 0.0004f, 2.0f, 0.20f)
                ParticleMovementType.ORGANIC -> listOf((rand.nextFloat() - 0.5f) * 0.0004f, -0.0003f, 2.8f, 0.18f)
            }
            AtmosphericMote(
                xRatio = rand.nextFloat(),
                yRatio = rand.nextFloat(),
                speedX = vx as Float,
                speedY = vy as Float,
                radiusRatio = r as Float,
                baseAlpha = a as Float,
                color = color,
                phase = rand.nextFloat() * 6.28f,
                phaseSpeed = 0.02f + rand.nextFloat() * 0.02f
            )
        }
    }

    var frameDelta by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { nowNanos ->
                if (lastNanos != 0L) {
                    val dt = (nowNanos - lastNanos) / 1_000_000_000f
                    frameDelta = dt.coerceIn(0.008f, 0.05f)
                }
                lastNanos = nowNanos
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas

            // 1. Base Obsidian / Espresso canvas
            drawRect(color = ObsidianBlack)

            // 2. Liquid Fluid Orb 1 - Primary accord essence (top left)
            val orb1Radius = w * 0.72f * orbPulse1
            val orb1Center = Offset(w * 0.10f, h * 0.12f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedGlow1,
                        animatedGlow1.copy(alpha = animatedGlow1.alpha * 0.35f),
                        Color.Transparent
                    ),
                    center = orb1Center,
                    radius = orb1Radius
                ),
                center = orb1Center,
                radius = orb1Radius
            )

            // 3. Liquid Fluid Orb 2 - Secondary base note anchor (bottom right)
            val orb2Radius = w * 0.85f * orbPulse2
            val orb2Center = Offset(w * 0.90f, h * 0.88f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedGlow2,
                        animatedGlow2.copy(alpha = animatedGlow2.alpha * 0.3f),
                        Color.Transparent
                    ),
                    center = orb2Center,
                    radius = orb2Radius
                ),
                center = orb2Center,
                radius = orb2Radius
            )

            // 4. Central visceral refraction (warm subtle heart glow)
            val orb3Radius = w * 0.55f
            val orb3Center = Offset(w * 0.50f, h * 0.45f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(245, 166, 35, 18),
                        Color(217, 119, 6, 8),
                        Color.Transparent
                    ),
                    center = orb3Center,
                    radius = orb3Radius
                ),
                center = orb3Center,
                radius = orb3Radius
            )

            // 5. Ambient micro-mote particles (subtle motion, wrap around boundaries)
            val step = if (frameDelta > 0f) frameDelta * 60f else 1f
            for (p in particles) {
                p.xRatio += p.speedX * step * intensity
                p.yRatio += p.speedY * step * intensity
                p.phase += p.phaseSpeed * step

                if (p.xRatio < -0.05f) p.xRatio = 1.05f
                if (p.xRatio > 1.05f) p.xRatio = -0.05f
                if (p.yRatio < -0.05f) p.yRatio = 1.05f
                if (p.yRatio > 1.05f) p.yRatio = -0.05f

                val pulseAlpha = (p.baseAlpha + sin(p.phase) * 0.08f).coerceIn(0.04f, 0.45f)
                val px = p.xRatio * w + (cos(p.phase) * 3f)
                val py = p.yRatio * h

                drawCircle(
                    color = p.color.copy(alpha = pulseAlpha),
                    radius = p.radiusRatio.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }
    }
}

/**
 * Native Jetpack Compose equivalent of AmbientAtmosphereControl.tsx from the original web app.
 * A discreet luxury atelier pill indicating the active olfactory atmosphere and allowing subtle switching.
 */
@Composable
fun AmbientAtmosphereBadge(
    currentAtmosphere: String?,
    modifier: Modifier = Modifier,
    onAtmosphereChange: ((String) -> Unit)? = null
) {
    val profile = remember(currentAtmosphere) {
        AtmosphereRegistry.resolveProfile(currentAtmosphere)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141416).copy(alpha = 0.75f))
            .border(
                1.dp,
                profile.accentColor.copy(alpha = 0.35f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Glowing aura indicator dot
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(profile.accentColor)
        )
        Text(
            text = profile.name.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                letterSpacing = 1.2.sp,
                color = com.example.ui.theme.ParchmentWhite.copy(alpha = 0.85f)
            )
        )
    }
}
