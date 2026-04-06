package com.amrdeveloper.editor.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun StatusBar(editorState: TextFieldState, textLayoutResult: TextLayoutResult?) {
    val selection = editorState.selection
    val text = editorState.text

    // Calculate line and column
    val cursorIndex = selection.start
    val line = if (textLayoutResult != null && cursorIndex <= text.length) {
        textLayoutResult.getLineForOffset(cursorIndex) + 1
    } else 1

    val column = if (textLayoutResult != null && cursorIndex <= text.length) {
        cursorIndex - textLayoutResult.getLineStart(line - 1) + 1
    } else 1

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ln $line, Col $column",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Lilo",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
