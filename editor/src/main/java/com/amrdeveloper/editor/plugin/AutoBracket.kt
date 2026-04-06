package com.amrdeveloper.editor.plugin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.insert
import androidx.compose.ui.text.TextRange

private val bracketPairs = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '"' to '"',
    '\'' to '\''
)

@OptIn(ExperimentalFoundationApi::class)
val autoBracketTransformation = InputTransformation {
    if (changes.changeCount == 1 && changes.getRange(0).length == 1) {
        val changeRange = changes.getRange(0)
        val insertedChar = asCharSequence()[changeRange.min]

        val closingBracket = bracketPairs[insertedChar]
        if (closingBracket != null) {
            insert(changeRange.max, closingBracket.toString())
            selection = TextRange(changeRange.max)
        }
    }
}