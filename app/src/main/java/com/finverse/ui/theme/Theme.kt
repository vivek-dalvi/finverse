package com.finverse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueLight,
    onPrimary = DarkBackground,
    primaryContainer = ElectricBlueDark,
    onPrimaryContainer = PureWhite,
    secondary = EmeraldNeon,
    onSecondary = DarkBackground,
    secondaryContainer = EmeraldOnContainer,
    onSecondaryContainer = EmeraldContainer,
    tertiary = VioletNeon,
    onTertiary = PureWhite,
    tertiaryContainer = VioletOnContainer,
    onTertiaryContainer = VioletContainer,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF4B5563)
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = PureWhite,
    primaryContainer = ElectricBlueContainer,
    onPrimaryContainer = ElectricBlueDark,
    secondary = EmeraldNeon,
    onSecondary = PureWhite,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = EmeraldOnContainer,
    tertiary = VioletNeon,
    onTertiary = PureWhite,
    tertiaryContainer = VioletContainer,
    onTertiaryContainer = VioletOnContainer,
    background = SlateBackground,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
    outlineVariant = SlateTextMuted
)

@Composable
fun MyApplicationTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme by ThemeManager.isDarkTheme.collectAsState()
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
