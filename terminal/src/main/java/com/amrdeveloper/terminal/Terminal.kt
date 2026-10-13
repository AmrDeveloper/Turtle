package com.amrdeveloper.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amrdeveloper.colorschema.core.TerminalSchema
import kotlinx.coroutines.launch

@Composable
fun Terminal(
    colorSchema: TerminalSchema,
    output: SnapshotStateList<TerminalLine>,
    onStdinSubmit: (String) -> Unit = {}
) {
    val scrollState = rememberLazyListState()
    LaunchedEffect(output.size) {
        if (output.isNotEmpty()) {
            launch {
                scrollState.animateScrollToItem(index = output.size - 1)
            }
        }
    }

    var isStdoutEnabled by remember { mutableStateOf(value = true) }
    Surface(modifier = Modifier.fillMaxSize(), color = colorSchema.background) {
        Column {
            TerminalStandardOutout(
                modifier = Modifier.fillMaxWidth().weight(0.8f),
                stdout = output,
                colorSchema = colorSchema,
                scrollState = scrollState,
                isStdinFinished = { isFinished ->
                    isStdoutEnabled = isFinished
                }
            )

            TerminalStandardInput(
                modifier = Modifier.fillMaxWidth().weight(0.2f),
                isStdoutEnabled = isStdoutEnabled,
                onStdinSubmit = onStdinSubmit
            )
        }
    }
}

@Composable
private fun TerminalStandardOutout(
    modifier: Modifier,
    stdout: SnapshotStateList<TerminalLine>,
    colorSchema: TerminalSchema,
    scrollState: LazyListState,
    isStdinFinished: (Boolean) -> Unit,
) {
    LazyColumn(
        state = scrollState,
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        content = {
            isStdinFinished(false)
            items(stdout) { line ->
                TerminalLineItem(colorSchema, line)
            }
            isStdinFinished(true)
        }
    )
}

@Composable
private fun TerminalStandardInput(
    modifier: Modifier,
    isStdoutEnabled :  Boolean = true,
    onStdinSubmit : (String) -> Unit = {},
) {
    var command by remember { mutableStateOf(value = "") }

    val submitTest = {
        if (command.isNotBlank()) {
            onStdinSubmit(command)
            command = ""
        }
    }

    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            TextField(
                enabled = isStdoutEnabled,
                value = command,
                onValueChange = { command = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter command...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { submitTest() })
            )

            IconButton(
                onClick = { submitTest() },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Execute command",
                        tint = Color.Unspecified
                    )
                }
            )
        }
    )
}


@Composable
private fun TerminalLineItem(colorSchema: TerminalSchema, line: TerminalLine) {
    val color = when (line) {
        is TerminalLine.Start -> colorSchema.start
        is TerminalLine.Normal -> colorSchema.normal
        is TerminalLine.Warning -> colorSchema.warning
        is TerminalLine.Error -> colorSchema.error
        is TerminalLine.Exit -> colorSchema.normal
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        when (line) {
            is TerminalLine.Start -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "❯",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorSchema.prompt
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = line.text,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = color,
                        lineHeight = 20.sp
                    )
                }
            }

            is TerminalLine.Exit -> {
                Text(
                    text = line.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = color,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = colorSchema.divider
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            is TerminalLine.Error -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(colorSchema.error)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = line.text,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = color,
                        lineHeight = 20.sp
                    )
                }
            }

            else -> {
                Text(
                    text = line.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = color,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 18.dp)
                )
            }
        }
    }
}
