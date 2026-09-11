package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrushedGold,
    onPrimary = ObsidianBackground,
    primaryContainer = ObsidianCardElevated,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = ObsidianBackground,
    secondaryContainer = ObsidianCard,
    onSecondaryContainer = CreamWhite,
    tertiary = AmberGlow,
    onTertiary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = CreamWhite,
    surface = ObsidianSurface,
    onSurface = CreamWhite,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = SoftMutedText,
    outline = ObsidianBorder,
    outlineVariant = DarkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = GoldMuted,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF7F1E1),
    onPrimaryContainer = Color(0xFF332909),
    secondary = Color(0xFF705D00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF1BD),
    onSecondaryContainer = Color(0xFF221B00),
    tertiary = Color(0xFF885200),
    onTertiary = Color.White,
    background = Color(0xFFFCF8F2),
    onBackground = Color(0xFF1E1B16),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E1B16),
    surfaceVariant = Color(0xFFF1EDE6),
    onSurfaceVariant = Color(0xFF4C4639),
    outline = Color(0xFFDDD7CC),
    outlineVariant = Color(0xFFEBE6DC)
)

@Composable
fun BookVerseTheme(
    darkTheme: Boolean = true, // Dark-first aesthetic for Netflix/Spotify/Audible entertainment feel
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BookVerseTheme(darkTheme = darkTheme, content = content)
}
