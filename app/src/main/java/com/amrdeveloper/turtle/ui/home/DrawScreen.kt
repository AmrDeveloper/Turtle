package com.amrdeveloper.turtle.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.amrdeveloper.turtle.R

@Composable
fun DrawScreen(viewModel: HomeViewModel, value: MutableLongState) {
    val screen = viewModel.getLiloMachine().getScreen() ?: return

    val logo = vectorToBitmap(id = R.drawable.ic_turtle_pointer)

    var canvasSize by remember { mutableStateOf(value = IntSize.Zero) }
    LaunchedEffect(canvasSize) {
        if (canvasSize == IntSize.Zero) {
            return@LaunchedEffect
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }) {
        for (pointer in screen.getPointers()) {
            val turtlePath = pointer.path
            if (turtlePath.isEmpty) {
                drawImage(image = logo, topLeft = Offset(x = 0f, y = 0f))
                continue
            }

            drawPath(path = turtlePath, color = pointer.color, style = Stroke(width = 5f))

            val pathMeasure = PathMeasure()
            pathMeasure.setPath(turtlePath, false)

            val lastPoint = if (pathMeasure.length > 0) {
                pathMeasure.getPosition(distance = pathMeasure.length)
            } else {
                Offset.Zero
            }

            drawImage(image = logo, topLeft = Offset(x = lastPoint.x, y = lastPoint.y))
        }
    }
}

@Composable
fun vectorToBitmap(id: Int): ImageBitmap {
    val context = LocalContext.current
    return remember(id) {
        val drawable = ContextCompat.getDrawable(context, id) ?: return@remember ImageBitmap(1, 1)

        // Create a blank bitmap based on the drawable's intrinsic size
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)

        // Use a traditional Android Canvas to draw the vector into the bitmap
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        // Convert to Compose-friendly ImageBitmap
        bitmap.asImageBitmap()
    }
}
