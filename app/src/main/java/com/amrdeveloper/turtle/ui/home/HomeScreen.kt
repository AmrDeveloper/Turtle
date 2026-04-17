package com.amrdeveloper.turtle.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.ui.components.TurtleHomeTabLayout
import com.amrdeveloper.turtle.ui.components.TurtleTab
import com.amrdeveloper.turtle.ui.components.TurtleToolbar

private val starterLiloCode = """
    msg = "Hello, World!"
    print(msg)
""".trimIndent()

private val turtleAppHomeTabs = listOf(
    TurtleTab(title = "Code", icon = R.drawable.ic_code),
    TurtleTab(title = "Draw", icon = R.drawable.ic_draw),
    TurtleTab(title = "Terminal", icon = R.drawable.ic_terminal),
)

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableIntStateOf(value = 0) }
    val currentCodeInEditor = rememberTextFieldState(initialText = starterLiloCode)

    Scaffold(
        topBar = {
            TurtleToolbar {
                viewModel.runLiloCode(currentCodeInEditor.text.toString())
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
            ) {
                TurtleHomeTabLayout(turtleAppHomeTabs) { selectedIndex ->
                    selectedTabIndex = selectedIndex
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (selectedTabIndex) {
                        0 -> CodeEditorScreen(editorState = currentCodeInEditor)
                        1 -> DrawScreen()
                        2 -> TerminalScreen(output = viewModel.terminalOutput)
                    }
                }
            }
        }
    )
}
