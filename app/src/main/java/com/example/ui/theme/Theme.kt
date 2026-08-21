package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StyleAIDarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = ObsidianBlack,
    primaryContainer = SurfaceCardElevated,
    onPrimaryContainer = WarmChampagne,
    secondary = IndigoAccent,
    onSecondary = SoftIvory,
    secondaryContainer = IndigoSoft,
    onSecondaryContainer = SoftIvory,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = NavyCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderHighlight,
    error = RoseError,
    onError = SoftIvory,
    errorContainer = RoseSoft,
    onErrorContainer = SoftIvory
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StyleAIDarkColorScheme,
        typography = Typography,
        content = content
    )
}
