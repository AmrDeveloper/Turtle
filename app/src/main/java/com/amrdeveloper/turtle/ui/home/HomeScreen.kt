package com.amrdeveloper.turtle.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amrdeveloper.turtle.ui.components.TurtleHomeTabLayout
import com.amrdeveloper.turtle.ui.components.TurtleToolbar

@Composable
fun HomeScreen() {
    Scaffold(
        topBar = { TurtleToolbar() },
        content = { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                TurtleHomeTabLayout()
            }
        }
    )
}

