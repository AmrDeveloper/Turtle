package com.amrdeveloper.turtle.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.amrdeveloper.terminal.Terminal

@Composable
fun TerminalScreen(output: SnapshotStateList<String>) {
    Terminal(output)
}
