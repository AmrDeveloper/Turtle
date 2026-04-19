package com.amrdeveloper.colorschema.core

import androidx.compose.ui.graphics.Color

data class TerminalSchema(
    val background: Color,
    val prompt: Color,

    val start: Color,
    val warning: Color,
    val error: Color,
    val normal: Color,
    val end: Color,

    val divider: Color,
)
