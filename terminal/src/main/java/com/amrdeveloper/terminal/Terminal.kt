package com.amrdeveloper.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amrdeveloper.colorschema.core.TerminalSchema
import kotlinx.coroutines.launch

@Composable
fun Terminal(
    colorSchema: TerminalSchema,
    output: SnapshotStateList<TerminalLine>
) {
    val scrollState = rememberLazyListState()
    LaunchedEffect(output.size) {
        if (output.isNotEmpty()) {
            launch {
                scrollState.animateScrollToItem(index = output.size - 1)
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = colorSchema.background) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(output) { line ->
                TerminalLineItem(colorSchema, line)
            }
        }
    }
}

@Composable
private fun TerminalLineItem(colorSchema: TerminalSchema, line: TerminalLine) {
    val color = when (line) {
        is TerminalLine.Start -> colorSchema.start
        is TerminalLine.Normal -> colorSchema.normal
        is TerminalLine.Warning -> colorSchema.warning
        is TerminalLine.Error -> colorSchema.error
        is TerminalLine.Exit -> colorSchema.normal
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        when (line) {
            is TerminalLine.Start -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "❯",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorSchema.prompt
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = line.text,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = color,
                        lineHeight = 20.sp
                    )
                }
            }

            is TerminalLine.Exit -> {
                Text(
                    text = line.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = color,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = colorSchema.divider
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            is TerminalLine.Error -> {
                Row {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(colorSchema.error)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = line.text,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = color,
                        lineHeight = 20.sp
                    )
                }
            }

            else -> {
                Text(
                    text = line.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = color,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 18.dp)
                )
            }
        }
    }
}
