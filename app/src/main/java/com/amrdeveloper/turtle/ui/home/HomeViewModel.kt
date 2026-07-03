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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(uiConfig: UIConfig) : ViewModel() {

    val colorSchema: StateFlow<String> = uiConfig.selectedColorSchema
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = "Default"
        )

    val terminalOutput = mutableStateListOf<TerminalLine>()
    val graphicInstCount = mutableLongStateOf(value = 0)

    private val _uiState = MutableStateFlow(value = TabActiveState())
    val uiState: StateFlow<TabActiveState> = _uiState.asStateFlow()

    private val liloHost = LiloHost(
        onStdout = { terminalOutput.add(TerminalLine.Normal(text = it)) })

    private val liloScreen = LiloScreen(update = {
        graphicInstCount.longValue++
        if (graphicInstCount.longValue > 0)
            _uiState.update { it.copy(draw = true) }
    })

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
        graphicInstCount.longValue = 0
        liloScreen.clearScreen()
        _uiState.value = TabActiveState()

        terminalOutput.add(TerminalLine.Start(text = "liloc main.lilo"))
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val lexer = LiloLexer(source)
                val tokensResult = lexer.tokenize()
                if (tokensResult.isFailure()) {
                    val lexerError =
                        tokensResult.toFailureError<LiloDiagnostic>()
                    val errorMessage = "L${lexerError.loc.line}: ${lexerError.message}"
                    terminalOutput.add(TerminalLine.Error(text = errorMessage))
                    _uiState.update { it.copy(terminal = true) }
                    return@launch
                }

                val parser = LiloParser(tokensResult.toSuccessData())
                val programResult = parser.parse()
                if (programResult.isFailure()) {
                    val lexerError =
                        programResult.toFailureError<LiloDiagnostic>()
                    val errorMessage = "L${lexerError.loc.line}: ${lexerError.message}"
                    terminalOutput.add(TerminalLine.Error(text = errorMessage))
                    _uiState.update { it.copy(terminal = true) }
                    return@launch
                }

                val interpreter = LiloInterpreter(liloMachine = liloMachine)
                val result = interpreter.evaluate(programResult.toSuccessData())
                if (result.isFailure()) {
                    val runtimeError = result.toFailureError<LiloExceptionMessage>()
                    terminalOutput.add(TerminalLine.Error(text = runtimeError.message))
                    _uiState.update { it.copy(terminal = true) }
                    return@launch
                }

                terminalOutput.add(TerminalLine.Exit(text = "Exit: 0"))
            } catch (_: Exception) {
                terminalOutput.add(TerminalLine.Exit(text = "Unhandled Exception, Please report to Github"))
                _uiState.update { it.copy(terminal = true) }
            }
        }

    }

    fun getLiloMachine() = liloMachine
}
