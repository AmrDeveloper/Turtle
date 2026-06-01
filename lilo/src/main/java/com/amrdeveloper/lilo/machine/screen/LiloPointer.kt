package com.amrdeveloper.lilo.machine.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import java.util.concurrent.CopyOnWriteArrayList

data class LiloPathSegment(
    val path: Path = Path(),
    val color: Color = Color.Black,
    val pen: Stroke = Stroke(width = 5.0f)
)

class LiloPointer(
    var x: Float = 0f,
    var y: Float = 0f,
    var degree: Double = 0.0,
    var penDown: Boolean = true,
    var visible: Boolean = true
) {
    val pathSegments = CopyOnWriteArrayList<LiloPathSegment>().apply { add(LiloPathSegment()) }

    fun path() = pathSegments.last().path
    fun pen() = pathSegments.last().pen

    fun color() = pathSegments.last().color
    fun setColor(color: Color) {
        if (color == color()) return
        val newPath = Path().apply { moveTo(x, y) }
        pathSegments.add(LiloPathSegment(newPath, color, pen()))
    }
}
