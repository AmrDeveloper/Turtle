package com.amrdeveloper.turtle.ui.home

import android.icu.text.CompactDecimalFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.amrdeveloper.turtle.R
import java.util.Locale

private val formatter by lazy {
    CompactDecimalFormat.getInstance(
        Locale.US,
        CompactDecimalFormat.CompactStyle.SHORT
    )
}

@Composable
fun DrawScreen(viewModel: HomeViewModel, instCount: MutableLongState) {
    val screen = viewModel.getLiloMachine().getScreen() ?: return
    val logo = vectorToBitmap(id = R.drawable.ic_turtle_pointer)

    var canvasSize by remember { mutableStateOf(value = IntSize.Zero) }
    LaunchedEffect(canvasSize) {
        if (canvasSize == IntSize.Zero) {
            return@LaunchedEffect
        }
    }

    // Color of drawing screen should be white and controlled only from Lilo
    Box(modifier = Modifier.background(Color.White)) {
        DrawScreenTitlebar(instCount = instCount.longValue)

        Canvas(modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }) {
            // Similar to Turtle Graphics the turtle starts from (0, 0)
            // which is the center of the screen
            //
            //             y+
            //             ↑
            //             |
            //             |
            // x- ←------(0,0)------→ x+
            //             |
            //             |
            //             ↓
            //             y-
            //
            //
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            withTransform(transformBlock = {
                translate(left = centerX, top = centerY)
                scale(scaleX = 1f, scaleY = -1f, pivot = Offset.Zero)
            }){
                for (pointer in screen.getPointers()) {
                    val segmentIter = pointer.pathSegments.iterator()
                    while (segmentIter.hasNext()) {
                        val segment = segmentIter.next()
                        drawPath(path = segment.path, color = segment.color, style = segment.pen)
                    }

                    val lastPathSegment = pointer.pathSegments.last().path
                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(lastPathSegment, forceClosed = false)

                    if (pathMeasure.length > 0) {
                        val lastPoint = pathMeasure.getPosition(distance = pathMeasure.length)
                        pointer.x = lastPoint.x
                        pointer.y = lastPoint.y
                    }

                    if (pointer.visible) {
                        withTransform(transformBlock = { scale(scaleX = 1f, scaleY = -1f, pivot = Offset(pointer.x, pointer.y)) }) {
                            drawImage(
                                image = logo,
                                topLeft = Offset(
                                    x = pointer.x - logo.width / 2f,
                                    y = pointer.y - logo.height / 2f
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawScreenTitlebar(instCount: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Turtle",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .weight(1f)
                .padding(4.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Inst ${formatter.format(instCount)}",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(4.dp))
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
