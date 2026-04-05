package com.amrdeveloper.turtle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.ui.search.SearchScreen

@Composable
fun TurtleToolbar() {
    var expanded by rememberSaveable { mutableStateOf(value = false) }

    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 3.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!expanded) {
                Icon(
                    painter = painterResource(R.drawable.ic_turtle_pointer),
                    contentDescription = "Turtle",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(22.dp)
                        .weight(0.2f)
                )
            }

            SearchScreen(
                modifier = Modifier.weight(1f),
                onSearchExpandedChanged = { isExpanded ->
                    expanded = isExpanded
                }
            )

            if (!expanded) {
                OptionsMenuWithDropDownActions()
            }
        }
    }
}

@Composable
private fun OptionsMenuWithDropDownActions() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_options),
                contentDescription = "Options",
                tint = Color.Unspecified
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Settings") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = "Settings",
                        tint = Color.Unspecified
                    )
                },
                onClick = {
                    expanded = false
                }
            )
        }
    }
}
