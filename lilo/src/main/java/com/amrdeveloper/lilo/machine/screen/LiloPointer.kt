package com.amrdeveloper.lilo.machine.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

class LiloPointer(
    var x: Float = 0f,
    var y: Float = 0f,
    var color: Color = Color.Black,
    var path: Path = Path(),
    var visible: Boolean = true,
)
