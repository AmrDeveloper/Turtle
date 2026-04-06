package com.amrdeveloper.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CodeEditor(editorState: TextFieldState, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        BasicTextField(
            state = editorState,
            modifier = modifier.fillMaxSize()
        )
    }
}