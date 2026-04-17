package com.amrdeveloper.editor.plugin

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.then

private val transformations = listOf(
    autoBracketTransformation,
    smartBackspaceTransformation
)

fun buildPluginTransformation(): InputTransformation? {
    return transformations
        .map { it }
        .fold(null as InputTransformation?) { acc, transformation ->
            acc?.then(transformation) ?: transformation
        }
}
