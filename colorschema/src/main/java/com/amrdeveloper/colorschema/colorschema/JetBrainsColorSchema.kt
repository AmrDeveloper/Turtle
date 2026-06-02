package com.amrdeveloper.colorschema.colorschema

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.amrdeveloper.colorschema.core.EditorSchema
import com.amrdeveloper.colorschema.core.LiloColorSchema
import com.amrdeveloper.colorschema.core.TerminalSchema

// JetBrains Dark Colors
private val JetBrainsDarkBackground = Color(0xFF1E1F22)
private val JetBrainsDarkSurface = Color(0xFF2B2D30)
private val JetBrainsDarkPrimary = Color(0xFF3574F0)
private val JetBrainsDarkOnSurface = Color(0xFFBCBEC4)

// JetBrains Light Colors
private val JetBrainsLightBackground = Color(0xFFFFFFFF)
private val JetBrainsLightSurface = Color(0xFFF7F8FA)
private val JetBrainsLightPrimary = Color(0xFF3574F0)
private val JetBrainsLightOnSurface = Color(0xFF1F1F1F)

private val JetBrainsDarkColorScheme = darkColorScheme(
    primary = JetBrainsDarkPrimary,
    background = JetBrainsDarkBackground,
    surface = JetBrainsDarkSurface,
    onPrimary = Color.White,
    onBackground = JetBrainsDarkOnSurface,
    onSurface = JetBrainsDarkOnSurface
)

private val JetBrainsLightColorScheme = lightColorScheme(
    primary = JetBrainsLightPrimary,
    background = JetBrainsLightBackground,
    surface = JetBrainsLightSurface,
    onPrimary = Color.White,
    onBackground = JetBrainsLightOnSurface,
    onSurface = JetBrainsLightOnSurface
)

val JetBrainsDarkLiloColorSchema = LiloColorSchema(
    appColorSchema = JetBrainsDarkColorScheme,
    editorSchema = EditorSchema(
        background = JetBrainsDarkBackground,
        textColor = Color(0xFFBCBEC4),
        cursorColor = Color(0xFFBCBEC4),
        lineHighlightColor = Color(0xFF262626),
        selectionColor = Color(0xFF214283),
        keyword = Color(0xFFCF8E6D),
        number = Color(0xFF2AACB8),
        string = Color(0xFF6AAB73),
        comment = Color(0xFF7A7E85),
        classType = Color(0xFFBCBEC4),
        function = Color(0xFF56A8F5),
        operator = Color(0xFFBCBEC4),
        bracket = Color(0xFFBCBEC4)
    ),
    terminalSchema = TerminalSchema(
        background = JetBrainsDarkBackground,
        prompt = Color(0xFF3574F0),
        start = Color(0xFF56A8F5),
        warning = Color(0xFFEBC66D),
        error = Color(0xFFF75464),
        normal = Color(0xFFBCBEC4),
        end = Color(0xFFBCBEC4),
        divider = Color(0xFF43454A)
    )
)

val JetBrainsLightLiloColorSchema = LiloColorSchema(
    appColorSchema = JetBrainsLightColorScheme,
    editorSchema = EditorSchema(
        background = JetBrainsLightBackground,
        textColor = Color(0xFF1F1F1F),
        cursorColor = Color(0xFF000000),
        lineHighlightColor = Color(0xFFF5F8FE),
        selectionColor = Color(0xFF214283),
        keyword = Color(0xFF0033B3),
        number = Color(0xFF1750EB),
        string = Color(0xFF067D17),
        comment = Color(0xFF8C8C8C),
        classType = Color(0xFF000000),
        function = Color(0xFF00627A),
        operator = Color(0xFF000000),
        bracket = Color(0xFF000000)
    ),
    terminalSchema = TerminalSchema(
        background = JetBrainsLightBackground,
        prompt = Color(0xFF3574F0),
        start = Color(0xFF00627A),
        warning = Color(0xFFAD8D1F),
        error = Color(0xFFE51400),
        normal = Color(0xFF1F1F1F),
        end = Color(0xFF1F1F1F),
        divider = Color(0xFFD1D1D1)
    )
)
