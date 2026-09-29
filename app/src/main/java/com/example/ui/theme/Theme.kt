package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AtelierColorScheme = darkColorScheme(
  primary = GoldPrimary,
  onPrimary = ObsidianBlack,
  primaryContainer = ObsidianElevated,
  onPrimaryContainer = GoldBright,
  secondary = AmberAccent,
  onSecondary = ObsidianBlack,
  secondaryContainer = ObsidianCard,
  onSecondaryContainer = ParchmentWhite,
  tertiary = MysoreSandal,
  onTertiary = ObsidianBlack,
  background = ObsidianBlack,
  onBackground = ParchmentWhite,
  surface = ObsidianSurface,
  onSurface = ParchmentWhite,
  surfaceVariant = ObsidianCard,
  onSurfaceVariant = ParchmentMuted,
  outline = ObsidianCardBorder,
  outlineVariant = ParchmentFaint
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve bespoke atelier palette
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = AtelierColorScheme,
    typography = Typography,
    content = content
  )
}

