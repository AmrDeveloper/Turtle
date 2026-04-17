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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

object TerminalColors {
    val Background = Color(0xFF1E1E1E)
    val Prompt = Color(0xFFB267E6)
    val Start = Color(0xFF3794FF)
    val Warning = Color(0xFFCCA700)
    val Error = Color(0xFFF14C4C)
    val Normal = Color(0xFFCCCCCC)
    val Divider = Color(0xFF333333)
}

@Composable
fun Terminal(output: SnapshotStateList<TerminalLine>) {
    val scrollState = rememberLazyListState()
    LaunchedEffect(output.size) {
        if (output.isNotEmpty()) {
            launch {
                scrollState.animateScrollToItem(index = output.size - 1)
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = TerminalColors.Background) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(output) { line ->
                TerminalLineItem(line)
            }
        }
    }
}

@Composable
private fun TerminalLineItem(line: TerminalLine) {
    val color = when (line) {
        is TerminalLine.Start -> TerminalColors.Start
        is TerminalLine.Normal -> TerminalColors.Normal
        is TerminalLine.Warning -> TerminalColors.Warning
        is TerminalLine.Error -> TerminalColors.Error
        is TerminalLine.Exit -> TerminalColors.Normal
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
                        color = TerminalColors.Prompt
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
                    color = TerminalColors.Divider
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
                            .background(TerminalColors.Error)
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
