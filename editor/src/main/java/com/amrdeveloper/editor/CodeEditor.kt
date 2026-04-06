package com.amrdeveloper.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.amrdeveloper.editor.core.Gutter
import com.amrdeveloper.editor.core.StatusBar
import com.amrdeveloper.editor.plugin.buildPluginTransformation

@Composable
fun CodeEditor(editorState: TextFieldState, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(value = null) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f)) {
            Gutter(
                editorState = editorState,
                textLayoutResult = textLayoutResult,
                scrollState = scrollState,
            )

            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            BasicTextField(
                state = editorState,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(start = 8.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(),
                onTextLayout = { textLayoutResult = it.invoke() },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                inputTransformation = buildPluginTransformation()
            )
        }

        StatusBar(editorState, textLayoutResult)
    }
}