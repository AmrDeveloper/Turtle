package com.amrdeveloper.turtle.ui.home

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.machine.LiloMachine
import com.amrdeveloper.lilo.machine.device.LiloWebGPU
import com.amrdeveloper.lilo.machine.host.LiloHost
import com.amrdeveloper.lilo.machine.screen.LiloScreen
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.terminal.TerminalLine
import com.amrdeveloper.turtle.ui.config.UIConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val uiConfig: UIConfig) : ViewModel() {

    val colorSchema : StateFlow<String> = uiConfig.selectedColorSchema
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = "Default"
        )

    val terminalOutput = mutableStateListOf<TerminalLine>()
    val screenUpdate = mutableLongStateOf(value = 0)

    private val liloHost = LiloHost(
        onStdout = { terminalOutput.add(TerminalLine.Normal(text = it)) })
    private val liloScreen = LiloScreen(update = { screenUpdate.longValue++ })
    private val liloGPU = LiloWebGPU()
    private val liloMachine = LiloMachine(
        liloHost = liloHost,
        liloScreen = liloScreen,
        liloGPU = liloGPU
    )

    init {
        viewModelScope.launch {
            liloMachine.initMachine()
        }
    }

    fun runLiloCode(source: String) {
        liloScreen.clearScreen()

        terminalOutput.add(TerminalLine.Start(text = "liloc main.lilo"))
        viewModelScope.launch(context = Dispatchers.IO) {
            val lexer = LiloLexer(source)
            val tokensResult = lexer.tokenize()
            if (tokensResult.isFailure()) {
                val lexerError =
                    tokensResult.toFailureError<LiloDiagnostic>()
                val errorMessage = "L${lexerError.loc.line}: ${lexerError.message}"
                terminalOutput.add(TerminalLine.Error(text = errorMessage))
                return@launch
            }

            val parser = LiloParser(tokensResult.toSuccessData())
            val programResult = parser.parse()
            if (programResult.isFailure()) {
                val lexerError =
                    programResult.toFailureError<LiloDiagnostic>()
                val errorMessage = "L${lexerError.loc.line}: ${lexerError.message}"
                terminalOutput.add(TerminalLine.Error(text = errorMessage))
                return@launch
            }

            val interpreter = LiloInterpreter(liloMachine = liloMachine)
            val result = interpreter.evaluate(programResult.toSuccessData())
            if (result.isFailure()) {
                val lexerError = result.toFailureError<LiloExceptionMessage>()
                val errorMessage = "Exception: ${lexerError.message}"
                terminalOutput.add(TerminalLine.Error(text = errorMessage))
                return@launch
            }

            terminalOutput.add(TerminalLine.Exit(text = "Exit: 0"))
        }
    }

    fun getLiloMachine() = liloMachine
}
