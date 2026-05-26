package com.amrdeveloper.lilo.machine.screen

import androidx.compose.ui.unit.IntSize

class LiloScreen(
    val update: () -> Unit
) : LiloAbstractScreen {

    val turtles: MutableList<LiloPointer> = mutableListOf()
    var size: IntSize = IntSize.Zero

    fun initPointer(): Int {
        val pointerIdx = turtles.size
        turtles.add(LiloPointer())
        return pointerIdx
    }

    fun getPointerAt(idx: Int): LiloPointer? {
        return turtles.getOrNull(index = idx)
    }

    override fun getPointers(): MutableList<LiloPointer> = turtles

    fun updateScreen() {
        update()
    }

    fun clearScreen() {
        turtles.clear()
    }
}
