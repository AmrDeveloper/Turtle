package com.amrdeveloper.editor.core

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun EditorGutter(
    textLayoutResult: TextLayoutResult?,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    gutterWidth: Dp = 40.dp,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
    horizontalPadding: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .width(gutterWidth)
            .fillMaxHeight()
            .verticalScroll(scrollState)
            .padding(horizontal = horizontalPadding)
    ) {
        val lineCount = textLayoutResult?.lineCount ?: 1

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
            for (i in 1..lineCount) {
                // We fetch the height of each line specifically from the layout
                // to ensure numbers align even if lines wrap.
                val lineHeight = if (textLayoutResult != null) {
                    val lineBottom = textLayoutResult.getLineBottom(i - 1)
                    val lineTop = textLayoutResult.getLineTop(i - 1)
                    with(LocalDensity.current) { (lineBottom - lineTop).toDp() }
                } else {
                    // Fallback to style line height if layout isn't ready
                    with(LocalDensity.current) { textStyle.lineHeight.toDp() }
                }

                Box(modifier = Modifier.height(lineHeight), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = i.toString(),
                        style = textStyle,
                        maxLines = 1
                    )
                }
            }
        }
    }
}