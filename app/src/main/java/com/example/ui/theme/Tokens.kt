package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design Tokens for Olfactory AI - Luxury Perfumery & Digital Atelier.
 * Defines standard spacing, elevation, corner radiuses, and touch targets.
 */
object OlfactorySpacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val section: Dp = 40.dp
    val hero: Dp = 48.dp
}

object OlfactoryRadii {
    val sharp = RoundedCornerShape(2.dp)
    val sm = RoundedCornerShape(6.dp)
    val md = RoundedCornerShape(10.dp)
    val lg = RoundedCornerShape(16.dp)
    val xl = RoundedCornerShape(24.dp)
    val pill = RoundedCornerShape(50)
}

object OlfactoryElevation {
    val none: Dp = 0.dp
    val subtle: Dp = 2.dp
    val card: Dp = 4.dp
    val modal: Dp = 8.dp
    val popover: Dp = 12.dp
}

object OlfactoryTouchTargets {
    val minTarget: Dp = 48.dp
    val iconButton: Dp = 48.dp
    val primaryButtonHeight: Dp = 52.dp
    val chipHeight: Dp = 34.dp
    val bottomBarHeight: Dp = 68.dp
}
