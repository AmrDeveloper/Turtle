package com.amrdeveloper.turtle.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloHost
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    val terminalOutput = mutableStateListOf<String>()

    class LiloMachine(val onStdout: (String) -> Unit) : LiloHost {
        override fun write(message: String) {
            onStdout(message)
        }
    }

    val host = LiloMachine {
        terminalOutput.add(it)
    }

    fun runLiloCode(source: String) {
        terminalOutput.clear()
        viewModelScope.launch(Dispatchers.Default) {
            val lexer = LiloLexer(source)
            val tokensResult = lexer.tokenize()
            if (tokensResult.isFailure()) {
                terminalOutput.add("Lexer Error: ${tokensResult}")
                return@launch
            }

            val parser = LiloParser(tokensResult.toSuccessData())
            val programResult = parser.parse()
            if (programResult.isFailure()) {
                terminalOutput.add("Parser Error: ${programResult}")
                return@launch
            }

            val interpreter = LiloInterpreter(liloHost = host)
            val result = interpreter.evaluate(programResult.toSuccessData())
            if (result.isFailure()) {
                terminalOutput.add("Runtime Error: ${result}")
            }
        }
    }
}
