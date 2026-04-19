package com.amrdeveloper.turtle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import com.amrdeveloper.colorschema.core.LiloColorSchema

@Composable
fun TurtleAppTheme(
    colorSchema: LiloColorSchema,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorSchema.appColorSchema,
        typography = createTypographyWithFontFamily(FontFamily.Monospace),
        content = content
    )
}
