package com.amrdeveloper.turtle.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.amrdeveloper.turtle.R

data class Tab(val title: String, val icon: Int, val content: @Composable () -> Unit)

private val homeTabs = listOf(
    Tab(title = "Code", icon = R.drawable.ic_code, content = {}),
    Tab(title = "Draw", icon = R.drawable.ic_draw, content = {}),
    Tab(title = "Terminal", icon = R.drawable.ic_terminal, content = {}),
)

@Composable
fun TurtleHomeTabLayout(tabs: List<Tab> = homeTabs) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            tabs = {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
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

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            tabs[selectedTabIndex].content()
        }
    }
}