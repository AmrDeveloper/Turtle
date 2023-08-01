package com.amrdeveloper.turtle.ui.preview

import android.graphics.Color

data class TurtlePointer(
    var x: Float,
    var y: Float,
    var degree: Float,
    var color : Int = Color.BLACK,
    var isVisible: Boolean = true,
)