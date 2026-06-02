package com.amrdeveloper.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.amrdeveloper.colorschema.core.EditorSchema
import com.amrdeveloper.editor.core.Gutter
import com.amrdeveloper.editor.core.StatusBar
import com.amrdeveloper.editor.plugin.SyntaxHighlighter
import com.amrdeveloper.editor.plugin.buildPluginTransformation

@Composable
fun CodeEditor(
    editorState: TextFieldState,
    colorSchema: EditorSchema,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(value = null) }
    val activeLine = remember(editorState.selection, textLayoutResult) {
        textLayoutResult?.getLineForOffset(editorState.selection.start) ?: -1
    }

    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val syntaxHighlighter = remember(colorSchema) { SyntaxHighlighter(colorSchema) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f)) {
            Gutter(
                textLayoutResult = textLayoutResult,
                scrollState = scrollState,
                activeLine = activeLine,
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
                    .horizontalScroll(horizontalScrollState)
                    .drawBehind {
                        if (activeLine != -1 && textLayoutResult != null) {
                            val top = textLayoutResult!!.getLineTop(lineIndex = activeLine)
                            val bottom = textLayoutResult!!.getLineBottom(lineIndex = activeLine)
                            drawRect(
                                color = highlightColor,
                                topLeft = Offset(x = 0f, y = top),
                                size = Size(size.width, height = bottom - top)
                            )
                        }
                    }
                    .padding(start = 8.dp),
                textStyle = TextStyle(
                    color = colorSchema.textColor,
                    fontFamily = FontFamily.Monospace
                ),
                onTextLayout = { textLayoutResult = it.invoke() },
                cursorBrush = SolidColor(value = MaterialTheme.colorScheme.primary),
                inputTransformation = buildPluginTransformation(),
                outputTransformation = syntaxHighlighter,
            )
        }

        StatusBar(editorState, textLayoutResult)
    }
}
