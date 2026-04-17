package com.amrdeveloper.terminal

sealed class TerminalLine {
    abstract val text: String

    data class Start(override val text: String) : TerminalLine()
    data class Normal(override val text: String) : TerminalLine()
    data class Warning(override val text: String) : TerminalLine()
    data class Error(override val text: String) : TerminalLine()
    data class Exit(override val text: String) : TerminalLine()
}
