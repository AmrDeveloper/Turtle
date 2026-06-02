package com.amrdeveloper.colorschema.colorschema

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.amrdeveloper.colorschema.core.EditorSchema
import com.amrdeveloper.colorschema.core.LiloColorSchema
import com.amrdeveloper.colorschema.core.TerminalSchema

// Tokyo Night Dark (Storm) Colors
private val TokyoNightDarkBackground = Color(0xFF24283B)
private val TokyoNightDarkSurface = Color(0xFF1F2335)
private val TokyoNightDarkPrimary = Color(0xFF7AA2F7)
private val TokyoNightDarkOnSurface = Color(0xFFC0CAF5)

// Tokyo Night Light Colors
private val TokyoNightLightBackground = Color(0xFFD5D6DB)
private val TokyoNightLightSurface = Color(0xFFCBCCD1)
private val TokyoNightLightPrimary = Color(0xFF34548A)
private val TokyoNightLightOnSurface = Color(0xFF343B58)

private val TokyoNightDarkColorScheme = darkColorScheme(
    primary = TokyoNightDarkPrimary,
    background = TokyoNightDarkBackground,
    surface = TokyoNightDarkSurface,
    onPrimary = Color.White,
    onBackground = TokyoNightDarkOnSurface,
    onSurface = TokyoNightDarkOnSurface
)

private val TokyoNightLightColorScheme = lightColorScheme(
    primary = TokyoNightLightPrimary,
    background = TokyoNightLightBackground,
    surface = TokyoNightLightSurface,
    onPrimary = Color.White,
    onBackground = TokyoNightLightOnSurface,
    onSurface = TokyoNightLightOnSurface
)

val TokyoNightDarkLiloColorSchema = LiloColorSchema(
    appColorSchema = TokyoNightDarkColorScheme,
    editorSchema = EditorSchema(
        background = TokyoNightDarkBackground,
        textColor = Color(0xFFC0CAF5),
        cursorColor = Color(0xFFC0CAF5),
        lineHighlightColor = Color(0xFF2E3440),
        selectionColor = Color(0xFF33467C),
        keyword = Color(0xFFBB9AF7),
        number = Color(0xFFFF9E64),
        string = Color(0xFF9ECE6A),
        comment = Color(0xFF565F89),
        classType = Color(0xFF2AC3DE),
        function = Color(0xFF7AA2F7),
        operator = Color(0xFF89DDFF),
        bracket = Color(0xFFBB9AF7)
    ),
    terminalSchema = TerminalSchema(
        background = TokyoNightDarkBackground,
        prompt = Color(0xFFBB9AF7),
        start = Color(0xFF7AA2F7),
        warning = Color(0xFFE0AF68),
        error = Color(0xFFF7768E),
        normal = Color(0xFFC0CAF5),
        end = Color(0xFFC0CAF5),
        divider = Color(0xFF414868)
    )
)

val TokyoNightLightLiloColorSchema = LiloColorSchema(
    appColorSchema = TokyoNightLightColorScheme,
    editorSchema = EditorSchema(
        background = TokyoNightLightBackground,
        textColor = Color(0xFF343B58),
        cursorColor = Color(0xFF343B58),
        lineHighlightColor = Color(0xFFE1E2E7),
        selectionColor = Color(0xFFB4B5B9),
        keyword = Color(0xFF9854F1),
        number = Color(0xFFB15C00),
        string = Color(0xFF485E30),
        comment = Color(0xFF9699A3),
        classType = Color(0xFF166775),
        function = Color(0xFF34548A),
        operator = Color(0xFF006A83),
        bracket = Color(0xFF9854F1)
    ),
    terminalSchema = TerminalSchema(
        background = TokyoNightLightBackground,
        prompt = Color(0xFF9854F1),
        start = Color(0xFF34548A),
        warning = Color(0xFF8C6C3E),
        error = Color(0xFF8C4351),
        normal = Color(0xFF343B58),
        end = Color(0xFF343B58),
        divider = Color(0xFFA8A9B1)
    )
)
