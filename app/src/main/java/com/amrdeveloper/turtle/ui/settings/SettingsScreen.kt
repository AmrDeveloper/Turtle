package com.amrdeveloper.turtle.ui.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.amrdeveloper.colorschema.colorschema.colorSchemasMap
import com.amrdeveloper.turtle.BuildConfig
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.ui.components.TurtleToolbar
import androidx.core.net.toUri

private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.amrdeveloper.turtle"
private const val REPOSITORY_URL = "https://github.com/AmrDeveloper/Turtle"
private const val REPOSITORY_ISSUES_URL = "$REPOSITORY_URL/issues"
private const val REPOSITORY_CONTRIBUTORS_URL = "$REPOSITORY_URL/graphs/contributors"
private const val REPOSITORY_SPONSORSHIP_URL = "https://github.com/sponsors/AmrDeveloper"

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController) {

    val scrollState = rememberScrollState()
    val currentColorSchema by viewModel.colorSchema.collectAsState()
    val selectedColorSchemaIndex = remember(currentColorSchema) {
        colorSchemasMap().keys.toList().indexOf(currentColorSchema).coerceAtLeast(minimumValue = 0)
    }

    var selectedUrlOptionToOpen by remember { mutableStateOf(value = "") }
    val currentContext = LocalContext.current

    LaunchedEffect(selectedUrlOptionToOpen) {
        if (selectedUrlOptionToOpen.isNotEmpty()) {
            openUrl(currentContext, selectedUrlOptionToOpen)
        }
    }

    Scaffold(
        topBar = {
            TurtleToolbar(navController = navController)
        },
        content = { padding ->
            Column(modifier = Modifier
                .padding(paddingValues = padding)
                .verticalScroll(scrollState)) {
                SettingSectionDivider(text = "App Info")

                SimpleSettingOption(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    icon = R.drawable.ic_version,
                )

                SettingSectionDivider(text = "UI Settings")

                DropdownSettingOption(
                    text = "ColorSchema",
                    icon = R.drawable.ic_dark_mode,
                    defaultSelectedIndex = selectedColorSchemaIndex,
                    options = colorSchemasMap().keys.toList(),
                    onOptionSelected = { colorSchema ->
                        viewModel.setColorSchema(colorSchema)
                    }
                )

                SettingSectionDivider(text = "Open source")

                SimpleSettingOption(
                    text = "Source code",
                    icon = R.drawable.ic_code,
                    onClick = { selectedUrlOptionToOpen = REPOSITORY_URL }
                )

                SimpleSettingOption(
                    text = "Sponsor",
                    icon = R.drawable.ic_github_sponsor,
                    onClick = {
                        selectedUrlOptionToOpen = REPOSITORY_SPONSORSHIP_URL
                    }
                )

                SimpleSettingOption(
                    text = "Contributors",
                    icon = R.drawable.ic_team,
                    onClick = {
                        selectedUrlOptionToOpen = REPOSITORY_CONTRIBUTORS_URL
                    }
                )

                SimpleSettingOption(
                    text = "Issues",
                    icon = R.drawable.ic_problems,
                    onClick = {
                        selectedUrlOptionToOpen = REPOSITORY_ISSUES_URL
                    }
                )

                SimpleSettingOption(
                    text = "Share",
                    icon = R.drawable.ic_share,
                    onClick = { selectedUrlOptionToOpen = PLAY_STORE_URL }
                )
            }
        }
    )
}

@Composable
private fun SimpleSettingOption(text: String, icon: Int, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Info Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SettingSectionDivider(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.primary)
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .background(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSettingOption(
    text: String,
    icon: Int,
    options: List<String>,
    defaultSelectedIndex: Int = 0,
    onOptionSelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember(defaultSelectedIndex) { mutableIntStateOf(defaultSelectedIndex) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Font Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }) {

                Row(
                    modifier = Modifier.clickable {
                        expanded = true
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_down),
                        contentDescription = "Arrow down",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(options[selectedIndex])
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .widthIn(min = 100.dp)
                ) {
                    options.forEachIndexed { index, selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                expanded = false
                                selectedIndex = index
                                onOptionSelected(selectionOption)
                            },
                        )
                    }
                }
            }
        }
    }
}

// TODO: Move to helper later
fun openUrl(context: Context, url: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
        true
    } catch (_: Exception) {
        false
    }
}
