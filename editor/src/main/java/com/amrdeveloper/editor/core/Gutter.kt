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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

@Composable
internal fun Gutter(
    textLayoutResult: TextLayoutResult?,
    scrollState: ScrollState,
    activeLine: Int = -1,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
    activeTextStyle: TextStyle = textStyle.copy(color = Color.DarkGray, fontWeight = FontWeight.Bold),
    horizontalPadding: Dp = 8.dp,
    modifier: Modifier = Modifier,
) {
    val lineCount = textLayoutResult?.lineCount ?: 1

    // Dynamically calculate gutter width based on max line digits
    val maxLineDigits = lineCount.toString().length
    val gutterWidth = max(44.dp, (maxLineDigits * 8 + 16).dp)

    Box(
        modifier = modifier
            .width(gutterWidth)
            .fillMaxHeight()
            .verticalScroll(scrollState)
            .padding(horizontal = horizontalPadding)
    ) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
            for (i in 0 until lineCount) {
                val isSelected = i == activeLine
                val lineHeight = if (textLayoutResult != null) {
                    val lineBottom = textLayoutResult.getLineBottom(i)
                    val lineTop = textLayoutResult.getLineTop(i)
                    with(LocalDensity.current) { (lineBottom - lineTop).toDp() }
                } else {
                    with(LocalDensity.current) { textStyle.lineHeight.toDp() }
                }

                Box(modifier = Modifier.height(lineHeight), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = (i + 1).toString(),
                        style = if (isSelected) activeTextStyle else textStyle,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
