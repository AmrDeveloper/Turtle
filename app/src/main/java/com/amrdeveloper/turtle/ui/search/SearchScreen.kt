package com.amrdeveloper.turtle.ui.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import com.amrdeveloper.turtle.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(modifier: Modifier = Modifier, onSearchExpandedChanged: (Boolean) -> Unit = {}) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(value = false) }

    LaunchedEffect(expanded) {
        onSearchExpandedChanged(expanded)
    }

    SearchBar(
        modifier = modifier.semantics { traversalIndex = 0f },
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                },
                onSearch = {
                    expanded = false
                },
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                },
                placeholder = {
                    Text(
                        text = "Search",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = Color.Unspecified,
                    )
                },
                trailingIcon = {
                    if (expanded) {
                        IconButton(
                            onClick = {
                                if (searchQuery.isNotEmpty()) searchQuery = ""
                                else expanded = false
                            },
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete),
                                    contentDescription = "Delete",
                                    tint = Color.Unspecified
                                )
                            }
                        )
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = {
            expanded = it

            // When SearchBar is collapsed, clear search query
            if (!expanded) {
                searchQuery = ""
            }
        },
        content = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {

                }
            }
        }
    )
}
