package com.amrdeveloper.colorschema.colorschema

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.amrdeveloper.colorschema.core.EditorSchema
import com.amrdeveloper.colorschema.core.LiloColorSchema
import com.amrdeveloper.colorschema.core.TerminalSchema

// Monokai Dark Colors
private val MonokaiDarkBackground = Color(0xFF272822)
private val MonokaiDarkSurface = Color(0xFF1E1F1C)
private val MonokaiDarkPrimary = Color(0xFFF92672)
private val MonokaiDarkOnSurface = Color(0xFFF8F8F2)

// Monokai Light Colors
private val MonokaiLightBackground = Color(0xFFF8F8F2)
private val MonokaiLightSurface = Color(0xFFE6E6E1)
private val MonokaiLightPrimary = Color(0xFFF92672)
private val MonokaiLightOnSurface = Color(0xFF272822)

private val MonokaiDarkColorScheme = darkColorScheme(
    primary = MonokaiDarkPrimary,
    background = MonokaiDarkBackground,
    surface = MonokaiDarkSurface,
    onPrimary = Color.White,
    onBackground = MonokaiDarkOnSurface,
    onSurface = MonokaiDarkOnSurface
)

private val MonokaiLightColorScheme = lightColorScheme(
    primary = MonokaiLightPrimary,
    background = MonokaiLightBackground,
    surface = MonokaiLightSurface,
    onPrimary = Color.White,
    onBackground = MonokaiLightOnSurface,
    onSurface = MonokaiLightOnSurface
)

val MonokaiLiloColorSchema = LiloColorSchema(
    appColorSchema = MonokaiDarkColorScheme,
    editorSchema = EditorSchema(
        background = MonokaiDarkBackground,
        textColor = Color(0xFFF8F8F2),
        cursorColor = Color(0xFFF8F8F2),
        lineHighlightColor = Color(0xFF3E3D32),
        selectionColor = Color(0xFF49483E),
        keyword = Color(0xFFF92672),
        number = Color(0xFFAE81FF),
        string = Color(0xFFE6DB74),
        comment = Color(0xFF75715E),
        classType = Color(0xFF66D9EF),
        function = Color(0xFFA6E22E),
        operator = Color(0xFFF92672),
        bracket = Color(0xFFF8F8F2)
    ),
    terminalSchema = TerminalSchema(
        background = MonokaiDarkBackground,
        prompt = Color(0xFFF92672),
        start = Color(0xFFA6E22E),
        warning = Color(0xFFE6DB74),
        error = Color(0xFFF92672),
        normal = Color(0xFFF8F8F2),
        end = Color(0xFFF8F8F2),
        divider = Color(0xFF49483E)
    )
)

val MonokaiLightLiloColorSchema = LiloColorSchema(
    appColorSchema = MonokaiLightColorScheme,
    editorSchema = EditorSchema(
        background = MonokaiLightBackground,
        textColor = Color(0xFF272822),
        cursorColor = Color(0xFF272822),
        lineHighlightColor = Color(0xFFE6E6E1),
        selectionColor = Color(0xFFC1C1BF),
        keyword = Color(0xFFF92672),
        number = Color(0xFFAE81FF),
        string = Color(0xFFE6DB74),
        comment = Color(0xFF75715E),
        classType = Color(0xFF66D9EF),
        function = Color(0xFFA6E22E),
        operator = Color(0xFFF92672),
        bracket = Color(0xFF272822)
    ),
    terminalSchema = TerminalSchema(
        background = MonokaiLightBackground,
        prompt = Color(0xFFF92672),
        start = Color(0xFFA6E22E),
        warning = Color(0xFF827D00),
        error = Color(0xFFF92672),
        normal = Color(0xFF272822),
        end = Color(0xFF272822),
        divider = Color(0xFFC1C1BF)
    )
)
