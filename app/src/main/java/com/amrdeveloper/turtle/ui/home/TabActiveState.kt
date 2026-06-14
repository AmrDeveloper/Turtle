package com.amrdeveloper.turtle.ui.home

data class TabActiveState(
    val code: Boolean = false,
    val draw: Boolean = false,
    val terminal: Boolean = false,
    val files: Boolean = false
) {
    fun isTabActive(index: Int): Boolean {
        return when (index) {
            0 -> code
            1 -> draw
            2 -> terminal
            3 -> files
            else -> false
        }
    }
}
