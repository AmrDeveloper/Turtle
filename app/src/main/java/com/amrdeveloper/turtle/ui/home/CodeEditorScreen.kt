package com.amrdeveloper.turtle.ui.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import com.amrdeveloper.editor.CodeEditor

@Composable
fun CodeEditorScreen(editorState: TextFieldState,) {
    CodeEditor(editorState)
}