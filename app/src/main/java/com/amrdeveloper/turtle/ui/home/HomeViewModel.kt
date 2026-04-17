package com.amrdeveloper.turtle.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.machine.LiloMachine
import com.amrdeveloper.lilo.machine.device.LiloWebGPU
import com.amrdeveloper.lilo.machine.host.LiloHost
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.terminal.TerminalLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    val terminalOutput = mutableStateListOf<TerminalLine>()

    private val liloHost = LiloHost { terminalOutput.add(TerminalLine.Normal(text = it)) }
    private val liloGPU = LiloWebGPU()
    private val liloMachine = LiloMachine(liloHost, liloGPU)

    init {
        viewModelScope.launch {
            liloMachine.initMachine()
        }
    }

    fun runLiloCode(source: String) {
        terminalOutput.add(TerminalLine.Start(text = "liloc main.lilo"))
        viewModelScope.launch(context = Dispatchers.IO) {
            val lexer = LiloLexer(source)
            val tokensResult = lexer.tokenize()
            if (tokensResult.isFailure()) {
                val lexerError =
                    tokensResult.toFailureError<LiloResult.Failure<LiloDiagnostic>>().error
                val errorMessage = "L${lexerError.loc.line}: ${lexerError.message}"
                terminalOutput.add(TerminalLine.Error(text = errorMessage))
                return@launch
            }

            val parser = LiloParser(tokensResult.toSuccessData())
            val programResult = parser.parse()
            if (programResult.isFailure()) {
                val lexerError =
                    programResult.toFailureError<LiloResult.Failure<LiloDiagnostic>>().error
                val errorMessage = "L${lexerError.loc.line}: ${lexerError.message}"
                terminalOutput.add(TerminalLine.Error(text = errorMessage))
                return@launch
            }

            val interpreter = LiloInterpreter(liloMachine = liloMachine)
            val result = interpreter.evaluate(programResult.toSuccessData())
            if (result.isFailure()) {
                val lexerError = result.toFailureError<LiloException>()
                val errorMessage = "Exception: ${lexerError.message}"
                terminalOutput.add(TerminalLine.Error(text = errorMessage))
                return@launch
            }

            terminalOutput.add(TerminalLine.Exit(text = "Exit: 0"))
        }
    }
}
