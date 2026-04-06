package com.amrdeveloper.turtle.ui.home

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import com.amrdeveloper.editor.CodeEditor

@Composable
fun CodeEditorScreen() {
    val editorState = rememberTextFieldState(initialText = "")
    CodeEditor(editorState)
}