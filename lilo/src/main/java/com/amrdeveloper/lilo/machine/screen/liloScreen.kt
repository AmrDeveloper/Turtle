package com.amrdeveloper.lilo.machine.screen

import androidx.compose.ui.unit.IntSize

class LiloScreen(
    val update: () -> Unit
) : LiloAbstractScreen {

    val turtles: MutableList<LiloPointer> = mutableListOf()
    var size: IntSize = IntSize.Zero

    override fun getPointers(): MutableList<LiloPointer> = turtles

    fun updateScreen() {
        update()
    }
}
