package com.amrdeveloper.turtle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

private val VSCodeDarkColorScheme = darkColorScheme(
    primary = VSCodeDarkPrimary,
    background = VSCodeDarkBackground,
    surface = VSCodeDarkSurface,
    surfaceContainer = VSCodeDarkTitleBar,
    onPrimary = Color.White,
    onBackground = VSCodeDarkOnSurface,
    onSurface = VSCodeDarkOnSurface,
)

private val VSCodeLightColorScheme = lightColorScheme(
    primary = VSCodeLightPrimary,
    background = VSCodeLightBackground,
    surface = VSCodeLightSurface,
    surfaceContainer = VSCodeLightTitleBar,
    onPrimary = Color.White,
    onBackground = VSCodeLightOnSurface,
    onSurface = VSCodeLightOnSurface,
)

@Composable
fun TurtleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VSCodeDarkColorScheme else VSCodeLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = createTypographyWithFontFamily(FontFamily.Monospace),
        content = content
    )
}