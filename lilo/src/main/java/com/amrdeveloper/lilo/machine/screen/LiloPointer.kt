package com.amrdeveloper.lilo.machine.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

class LiloPointer(
    var x: Float = 0f,
    var y: Float = 0f,
    val degree: Float = 0f,
    var path: Path = Path(),

    var color: Color = Color.Black,
    val pen: Stroke = Stroke(width = 5.0f),
    val penDown: Boolean = true,
    var visible: Boolean = true
)
