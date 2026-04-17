package com.amrdeveloper.terminal

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Terminal(output: SnapshotStateList<String>) {
    val scrollState = rememberLazyListState()

    // Auto-scroll to bottom when output changes
    LaunchedEffect(output.size) {
        if (output.isNotEmpty()) {
            scrollState.animateScrollToItem(index = output.size - 1)
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(output) { line ->
            Text(
                text = line,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}
