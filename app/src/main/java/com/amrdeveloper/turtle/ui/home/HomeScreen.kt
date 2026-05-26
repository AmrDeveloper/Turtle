package com.amrdeveloper.turtle.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amrdeveloper.colorschema.core.LiloColorSchema
import com.amrdeveloper.editor.CodeEditor
import com.amrdeveloper.terminal.Terminal
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.ui.components.TurtleHomeTabLayout
import com.amrdeveloper.turtle.ui.components.TurtleTab
import com.amrdeveloper.turtle.ui.components.TurtleToolbar

private val starterLiloCode = """
import turtle
import math
import colorsys

t = turtle.Turtle()

def matmul(A, B):
    rows = len(A)
    cols = len(B[0])
    result = []
    i = 0
    while i < rows:
        row = []
        j = 0
        while j < cols:
            total = 0
            k = 0
            while k < len(B):
                total = total + A[i][k] * B[k][j]
                k = k + 1
            row.append(total)
            j = j + 1
        result.append(row)
        i = i + 1
    return result

def rotation(theta):
    c = math.cos(theta)
    s = math.sin(theta)
    matrix = [[c, -s], [s,  c]]
    return matrix

def transform(point, matrix):
    vector = []
    vector.append([point[0]])
    vector.append([point[1]])
    result = matmul(matrix, vector)
    return (result[0][0], result[1][0])

def draw_petal(angle_offset, hue):
    R = rotation(angle_offset)
    rgb = colorsys.hsv_to_rgb(hue, 1.0, 1.0)
    t.pencolor(rgb)
    points = []
    i = 0
    while i < 255:
        a = i * 0.15
        r = 4 * math.sqrt(i)
        x = r * math.cos(a)
        y = r * math.sin(a)
        rotated = transform((x, y), R)
        points.append(rotated)
        i = i + 1
    t.penup()
    t.goto(points[0])
    t.pendown()
    i = 0
    while i < len(points):
        t.goto(points[i])
        i = i + 1

petals = 40
i = 0
while i < petals:
    angle = math.radians(i * (360 / petals))
    hue = i / petals
    draw_petal(angle, hue)
    i = i + 1
""".trimIndent()

private val turtleAppHomeTabs = listOf(
    TurtleTab(title = "Code", icon = R.drawable.ic_code),
    TurtleTab(title = "Draw", icon = R.drawable.ic_draw),
    TurtleTab(title = "Terminal", icon = R.drawable.ic_terminal),
)

@Composable
fun HomeScreen(
    colorSchema: LiloColorSchema,
    viewModel: HomeViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(value = 0) }
    val currentCodeInEditor = rememberTextFieldState(initialText = starterLiloCode)

    Scaffold(
        topBar = {
            TurtleToolbar {
                viewModel.runLiloCode(source = currentCodeInEditor.text.toString())
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
            ) {
                TurtleHomeTabLayout(turtleAppHomeTabs) { selectedIndex ->
                    selectedTabIndex = selectedIndex
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (selectedTabIndex) {
                        0 -> CodeEditor(
                            editorState = currentCodeInEditor,
                            colorSchema = colorSchema.editorSchema
                        )

                        1 -> DrawScreen(viewModel, value = viewModel.screenUpdate)
                        2 -> Terminal(
                            colorSchema = colorSchema.terminalSchema,
                            output = viewModel.terminalOutput
                        )
                    }
                }
            }
        }
    )
}
