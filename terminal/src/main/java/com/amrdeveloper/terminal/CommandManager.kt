package com.amrdeveloper.terminal

import kotlinx.coroutines.CoroutineScope

object CommandManager {

    private val commands = mutableMapOf<String, Command>()

    suspend fun executeCommand(scope : CoroutineScope, fullCommand : String) : List<TerminalLine> {
        val result = mutableListOf<TerminalLine>()
        val argv = fullCommand.split(" ")
        if (argv.isEmpty()) {
            result.add(TerminalLine.Error(text = "invalid command."))
            return result
        }

        val programName = argv[0]
        val command = commands[programName]
        if (command == null) {
            result.add(TerminalLine.Error(text = "command not found: `${programName}`."))
            return result
        }

        val args = argv.subList(1, argv.size)
        return command.execute(scope, args)
    }

    fun registerCommand(command : Command) {
        commands[command.getName()] = command
    }
}
