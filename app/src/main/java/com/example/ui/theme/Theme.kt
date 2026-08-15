package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreenDeep,
    onPrimaryContainer = Color.White,
    secondary = InkMuted,
    onSecondary = Color.White,
    tertiary = MustardGold,
    onTertiary = Color.White,
    background = PaperBg,
    onBackground = InkPrimary,
    surface = CardBg,
    onSurface = InkPrimary,
    error = BrickRed,
    onError = Color.White,
    outline = HairlineColor
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreenDeep,
    onPrimaryContainer = Color.White,
    secondary = InkMuted,
    onSecondary = Color.White,
    tertiary = MustardGold,
    onTertiary = Color.White,
    background = InkPrimary, // A clean, dark-forest slate theme for dark mode
    onBackground = PaperBg,
    surface = Color(0xFF2C3529),
    onSurface = CardBg,
    error = BrickRed,
    onError = Color.White,
    outline = HairlineColor
)

@Composable
fun WochenzettelTheme(
    darkTheme: Boolean = false, // Force Light paper theme by default for distinct aesthetic, but support toggle
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    WochenzettelTheme(darkTheme = darkTheme, content = content)
}
