package com.amrdeveloper.turtle.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun DrawScreen(
    onSizeChanged: (IntSize) -> Unit = {}
) {
    var canvasSize by remember { mutableStateOf(value = IntSize.Zero) }
    LaunchedEffect(canvasSize) {
        onSizeChanged(canvasSize)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }) {

    }
}
