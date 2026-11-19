package com.Ritzava1.Mewlup.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CourtColorScheme = darkColorScheme(
    primary = RoyalCrimson,
    onPrimary = TextPrimary,
    primaryContainer = CrimsonDeep,
    onPrimaryContainer = TextPrimary,
    secondary = RoyalBlue,
    onSecondary = TextPrimary,
    secondaryContainer = SurfacePanel,
    onSecondaryContainer = TextSecondary,
    tertiary = Parchment,
    onTertiary = DeepVoid,
    background = MidnightBlue,
    onBackground = TextPrimary,
    surface = SurfacePanel,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = PolishedSilver,
    outlineVariant = TextMuted,
    error = RoyalCrimson,
    onError = TextPrimary,
    scrim = DeepVoid
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CourtColorScheme,
        typography = AppTypography,
        content = content
    )
}
