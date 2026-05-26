package com.amrdeveloper.colorschema.core

import androidx.compose.ui.graphics.Color

data class EditorSchema(
    val background: Color,
    val textColor: Color,
    val cursorColor: Color,
    val lineHighlightColor: Color,
    val selectionColor: Color,

    val keyword: Color,
    val number: Color,
    val string: Color,
    val comment: Color,
    val classType: Color,
    val function: Color,
    val operator: Color,
    val bracket: Color
)
