package com.amrdeveloper.turtle.ui.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amrdeveloper.colorschema.colorschema.colorSchemasMap
import com.amrdeveloper.colorschema.colorschema.defaultColorSchema
import com.amrdeveloper.editor.CodeEditor
import com.amrdeveloper.terminal.Terminal
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.ui.components.TurtleHomeTabLayout
import com.amrdeveloper.turtle.ui.components.TurtleTab
import com.amrdeveloper.turtle.ui.components.TurtleToolbar
import com.amrdeveloper.turtle.ui.files.LiloFilesScreen

private val turtleAppHomeTabs = listOf(
    TurtleTab(title = "Code", icon = R.drawable.ic_code),
    TurtleTab(title = "Draw", icon = R.drawable.ic_draw),
    TurtleTab(title = "Terminal", icon = R.drawable.ic_terminal),
    TurtleTab(title = "Files", icon = R.drawable.ic_files),
)

@Composable
fun HomeScreen(
    starterCode: String,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
) {
    var selectedTabIndex by remember { mutableIntStateOf(value = 0) }
    val currentCodeInEditor = rememberTextFieldState(initialText = starterCode)
    val tabActivation by viewModel.uiState.collectAsStateWithLifecycle()

    val currentColorSchema by viewModel.colorSchema.collectAsState()
    val colorSchema = colorSchemasMap()
        .getOrDefault(
            key = currentColorSchema,
            defaultValue = defaultColorSchema(isDarkTheme = isSystemInDarkTheme())
        )

    Scaffold(
        topBar = {
            TurtleToolbar(
                onRunActionClicked = {
                    viewModel.runLiloCode(source = currentCodeInEditor.text.toString())
                },
                navController = navController
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
            ) {
                TurtleHomeTabLayout(turtleAppHomeTabs, tabActivation) { selectedIndex ->
                    selectedTabIndex = selectedIndex
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (selectedTabIndex) {
                        0 -> CodeEditor(
                            editorState = currentCodeInEditor,
                            colorSchema = colorSchema.editorSchema
                        )
                        1 -> DrawScreen(
                            viewModel = viewModel,
                            instCount = viewModel.graphicInstCount
                        )
                        2 -> Terminal(
                            colorSchema = colorSchema.terminalSchema,
                            output = viewModel.terminalOutput
                        )
                        3 -> LiloFilesScreen(navController = navController)
                    }
                }
            }
        }
    )
}
