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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.ui.navigation.AppRoute
import com.amrdeveloper.turtle.ui.search.SearchScreen

@Composable
fun TurtleToolbar(
    isRunActionEnabled: Boolean = false,
    onRunActionClicked: () -> Unit = {},
    navController: NavController,
) {
    var expanded by rememberSaveable { mutableStateOf(value = false) }
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = borderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!expanded) {
                Icon(
                    painter = painterResource(R.drawable.ic_turtle_pointer),
                    contentDescription = "Turtle",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            SearchScreen(
                modifier = Modifier.weight(1f),
                navController = navController,
                onSearchExpandedChanged = { isExpanded ->
                    expanded = isExpanded
                }
            )

            if (!expanded) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isRunActionEnabled) {
                        IconButton(onClick = onRunActionClicked) {
                            Icon(
                                painter = painterResource(R.drawable.ic_run),
                                contentDescription = "Run",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    OptionsMenuWithDropDownActions(
                        onFilesClick = { navController.navigate(route = AppRoute.Files) },
                        onSettingsClick = { navController.navigate(route = AppRoute.Settings) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionsMenuWithDropDownActions(
    onFilesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_options),
                contentDescription = "Options",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Files") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_turtle_pack),
                        contentDescription = "Files",
                        tint = Color.Unspecified
                    )
                },
                onClick = {
                    expanded = false
                    onFilesClick()
                }
            )
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
                    onSettingsClick()
                }
            )
        }
    }
}
