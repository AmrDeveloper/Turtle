package com.amrdeveloper.turtle.ui.files

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amrdeveloper.turtle.ui.components.LiloFile
import com.amrdeveloper.turtle.ui.components.TurtleToolbar
import com.amrdeveloper.turtle.ui.navigation.AppRoute

@Composable
fun LiloFilesScreen(
    viewModel: FilesViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TurtleToolbar(
                isRunActionEnabled = false,
                navController = navController
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn {
                    items(uiState.data) { file ->
                        LiloFile(
                            file = file,
                            onClick = {
                                navController.navigate(route = AppRoute.Home(file.sourceCode))
                            },
                            onLongClick = {}
                        )
                    }
                }
            }
        }
    )
}
