package com.amrdeveloper.colorschema.colorschema

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.amrdeveloper.colorschema.core.EditorSchema
import com.amrdeveloper.colorschema.core.LiloColorSchema
import com.amrdeveloper.colorschema.core.TerminalSchema

// GitHub Dark Colors
private val GitHubDarkBackground = Color(0xFF0D1117)
private val GitHubDarkSurface = Color(0xFF161B22)
private val GitHubDarkPrimary = Color(0xFF58A6FF)
private val GitHubDarkOnSurface = Color(0xFFC9D1D9)

// GitHub Light Colors
private val GitHubLightBackground = Color(0xFFFFFFFF)
private val GitHubLightSurface = Color(0xFFF6F8FA)
private val GitHubLightPrimary = Color(0xFF0969DA)
private val GitHubLightOnSurface = Color(0xFF24292F)

private val GitHubDarkColorScheme = darkColorScheme(
    primary = GitHubDarkPrimary,
    background = GitHubDarkBackground,
    surface = GitHubDarkSurface,
    onPrimary = Color.White,
    onBackground = GitHubDarkOnSurface,
    onSurface = GitHubDarkOnSurface
)

private val GitHubLightColorScheme = lightColorScheme(
    primary = GitHubLightPrimary,
    background = GitHubLightBackground,
    surface = GitHubLightSurface,
    onPrimary = Color.White,
    onBackground = GitHubLightOnSurface,
    onSurface = GitHubLightOnSurface
)

val GitHubDarkLiloColorSchema = LiloColorSchema(
    appColorSchema = GitHubDarkColorScheme,
    editorSchema = EditorSchema(
        background = GitHubDarkBackground,
        textColor = Color(0xFFC9D1D9),
        cursorColor = Color(0xFFC9D1D9),
        lineHighlightColor = Color(0xFF161B22),
        selectionColor = Color(0xFF264F78),
        keyword = Color(0xFFFF7B72),
        number = Color(0xFF79C0FF),
        string = Color(0xFFA5D6FF),
        comment = Color(0xFF8B949E),
        classType = Color(0xFFFFA657),
        function = Color(0xFFD2A8FF),
        operator = Color(0xFF79C0FF),
        bracket = Color(0xFFC9D1D9)
    ),
    terminalSchema = TerminalSchema(
        background = GitHubDarkBackground,
        prompt = Color(0xFFD2A8FF),
        start = Color(0xFF58A6FF),
        warning = Color(0xFFD29922),
        error = Color(0xFFF85149),
        normal = Color(0xFFC9D1D9),
        end = Color(0xFFC9D1D9),
        divider = Color(0xFF30363D)
    )
)

val GitHubLightLiloColorSchema = LiloColorSchema(
    appColorSchema = GitHubLightColorScheme,
    editorSchema = EditorSchema(
        background = GitHubLightBackground,
        textColor = Color(0xFF24292F),
        cursorColor = Color(0xFF24292F),
        lineHighlightColor = Color(0xFFF6F8FA),
        selectionColor = Color(0xFFADD6FF),
        keyword = Color(0xFFCF222E),
        number = Color(0xFF0550AE),
        string = Color(0xFF0A3069),
        comment = Color(0xFF6E7781),
        classType = Color(0xFF953800),
        function = Color(0xFF8250DF),
        operator = Color(0xFF0550AE),
        bracket = Color(0xFF24292F)
    ),
    terminalSchema = TerminalSchema(
        background = GitHubLightBackground,
        prompt = Color(0xFF8250DF),
        start = Color(0xFF0969DA),
        warning = Color(0xFF9A6700),
        error = Color(0xFFCF222E),
        normal = Color(0xFF24292F),
        end = Color(0xFF24292F),
        divider = Color(0xFFD0D7DE)
    )
)
