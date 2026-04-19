package com.amrdeveloper.colorschema

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.amrdeveloper.colorschema.core.EditorSchema
import com.amrdeveloper.colorschema.core.LiloColorSchema
import com.amrdeveloper.colorschema.core.TerminalSchema

// VS Code Dark Colors
private val VSCodeDarkBackground = Color(0xFF1E1E1E)
private val VSCodeDarkSurface = Color(0xFF252526)
private val VSCodeDarkTitleBar = Color(0xFF3C3C3C)
private val VSCodeDarkStatusBar = Color(0xFF007ACC)
private val VSCodeDarkPrimary = Color(0xFF007ACC)
private val VSCodeDarkOnSurface = Color(0xFFD4D4D4)

// VS Code Light Colors
private val VSCodeLightBackground = Color(0xFFFFFFFF)
private val VSCodeLightSurface = Color(0xFFF3F3F3)
private val VSCodeLightTitleBar = Color(0xFFDDDDDD)
private val VSCodeLightStatusBar = Color(0xFF007ACC)
private val VSCodeLightPrimary = Color(0xFF007ACC)
private val VSCodeLightOnSurface = Color(0xFF000000)

private val VSCodeDarkColorScheme = darkColorScheme(
    primary = VSCodeDarkPrimary,
    background = VSCodeDarkBackground,
    surface = VSCodeDarkSurface,
    surfaceContainer = VSCodeDarkTitleBar,
    onPrimary = Color.White,
    onBackground = VSCodeDarkOnSurface,
    onSurface = VSCodeDarkOnSurface,
    secondaryContainer = VSCodeDarkStatusBar,
    onSecondaryContainer = Color.White
)

private val VSCodeLightColorScheme = lightColorScheme(
    primary = VSCodeLightPrimary,
    background = VSCodeLightBackground,
    surface = VSCodeLightSurface,
    surfaceContainer = VSCodeLightTitleBar,
    onPrimary = Color.White,
    onBackground = VSCodeLightOnSurface,
    onSurface = VSCodeLightOnSurface,
    secondaryContainer = VSCodeLightStatusBar,
    onSecondaryContainer = Color.White
)

val VSCodeDarkLiloColorSchema = LiloColorSchema(
    appColorSchema = VSCodeDarkColorScheme,
    editorSchema = EditorSchema(
        background = VSCodeDarkBackground,
        textColor = Color(0xFFD4D4D4),
        cursorColor = Color(0xFFAEAFAD),
        lineHighlightColor = Color(0xFF2F3333),
        selectionColor = Color(0xFF264F78),
        keyword = Color(0xFF569CD6),
        number = Color(0xFFB5CEA8),
        string = Color(0xFFCE9178),
        comment = Color(0xFF6A9955),
        classType = Color(0xFF4EC9B0),
        function = Color(0xFFDCDCAA),
        operator = Color(0xFFD4D4D4)
    ),
    terminalSchema = TerminalSchema(
        background = Color(0xFF1E1E1E),
        prompt = Color(0xFFB267E6),
        start = Color(0xFF3794FF),
        warning = Color(0xFFCCA700),
        error = Color(0xFFF14C4C),
        normal = Color(0xFFCCCCCC),
        end = Color(0xFFCCCCCC),
        divider = Color(0xFF333333)
    )
)

val VSCodeLightLiloColorSchema = LiloColorSchema(
    appColorSchema = VSCodeLightColorScheme,
    editorSchema = EditorSchema(
        background = VSCodeLightBackground,
        textColor = Color(0xFF000000),
        cursorColor = Color(0xFF000000),
        lineHighlightColor = Color(0xFFEEEEEE),
        selectionColor = Color(0xFFADD6FF),
        keyword = Color(0xFF0000FF),
        number = Color(0xFF098658),
        string = Color(0xFFA31515),
        comment = Color(0xFF008000),
        classType = Color(0xFF267F99),
        function = Color(0xFF795E26),
        operator = Color(0xFF000000)
    ),
    terminalSchema = TerminalSchema(
        background = Color(0xFFFFFFFF),
        prompt = Color(0xFFAF5700),
        start = Color(0xFF005FB8),
        warning = Color(0xFFBF8800),
        error = Color(0xFFE51400),
        normal = Color(0xFF333333),
        end = Color(0xFF333333),
        divider = Color(0xFFE5E5E5)
    )
)
