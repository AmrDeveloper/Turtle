package com.amrdeveloper.terminal

import kotlinx.coroutines.CoroutineScope

interface Command {

    fun getName(): String

    suspend fun execute(
        scope: CoroutineScope,
        args: List<String>
    ): List<TerminalLine>
}
