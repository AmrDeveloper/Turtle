package com.amrdeveloper.turtle.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

data class TurtleTab(val title: String, val icon: Int)

@Composable
fun TurtleHomeTabLayout(tabs: List<TurtleTab>, onTabSelected: (Int) -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        tabs = {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        onTabSelected(selectedTabIndex)
                    },
                    icon = {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(id = tab.icon),
                            contentDescription = tab.title,
                            tint = Color.Black
                        )
                    }
                )
            }
        }
    )
}