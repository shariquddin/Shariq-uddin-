package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldDark,
    secondary = GoldPrimary,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldDark,
    tertiary = GoldDark,
    onTertiary = Color.White,
    background = SurfaceCream,
    onBackground = TextPrimaryDark,
    surface = Color.White,
    onSurface = TextPrimaryDark,
    surfaceVariant = WarmWhite,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderGold
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldLight,
    onPrimary = EmeraldDark,
    primaryContainer = EmeraldMedium,
    onPrimaryContainer = GoldContainer,
    secondary = GoldPrimary,
    onSecondary = Color.Black,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldContainer,
    tertiary = EmeraldLight,
    onTertiary = Color.White,
    background = Color(0xFF0B1F18),
    onBackground = Color(0xFFF0F4F2),
    surface = Color(0xFF112A21),
    onSurface = Color(0xFFF0F4F2),
    surfaceVariant = Color(0xFF183B2E),
    onSurfaceVariant = Color(0xFFC2D4CC),
    outline = GoldDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
