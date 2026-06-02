package com.amrdeveloper.turtle.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.amrdeveloper.colorschema.colorschema.colorSchemasMap
import com.amrdeveloper.colorschema.colorschema.defaultColorSchema
import com.amrdeveloper.turtle.ui.home.HomeScreen
import com.amrdeveloper.turtle.ui.files.LiloFilesScreen
import com.amrdeveloper.turtle.ui.navigation.AppRoute
import com.amrdeveloper.turtle.ui.navigation.AppRoute.Home
import com.amrdeveloper.turtle.ui.settings.SettingsScreen
import com.amrdeveloper.turtle.ui.theme.TurtleAppTheme
import dagger.hilt.android.AndroidEntryPoint

private val starterLiloCode = """
import turtle
import math

t = turtle.Turtle()

def project(x, y, z, scale, distance):
    factor = scale / (z + distance)
    px = x * factor
    py = y * factor
    return px, py

def rotate_x(x, y, z, angle):
    r = math.radians(angle)
    temp_y = y * math.cos(r) - z * math.sin(r)
    temp_z = y * math.sin(r) + z * math.cos(r)
    y = temp_y
    z = temp_z
    return x, y, z

def rotate_y(x, y, z, angle):
    r = math.radians(angle)
    temp_x = x * math.cos(r) + z * math.sin(r)
    temp_z = -x * math.sin(r) + z * math.cos(r)
    x = temp_x
    z = temp_z
    return x, y, z

points = []

R = 3
r = 1.2

steps_u = 40
steps_v = 24

scale = 800
distance = 8

i = 0

while i < steps_u:
    u = (2 * math.pi / steps_u) * i
    row = []
    j = 0
    while j < steps_v:
        v = (2 * math.pi / steps_v) * j
        x = (R + r * math.cos(v)) * math.cos(u)
        y = (R + r * math.cos(v)) * math.sin(u)
        z = r * math.sin(v)
        rotated = rotate_x(x, y, z, 60)
        x = rotated[0]
        y = rotated[1]
        z = rotated[2]
        rotated = rotate_y(x, y, z, 35)
        x = rotated[0]
        y = rotated[1]
        z = rotated[2]
        projected = project(x, y, z, scale, distance)
        px = projected[0]
        py = projected[1]
        row.append((px, py))
        j = j + 1
    points.append(row)
    i = i + 1

i = 0
while i < steps_u:
    j = 0
    while j < steps_v:
        p1 = points[i][j]
        next_i = (i + 1) % steps_u
        next_j = (j + 1) % steps_v
        p2 = points[next_i][j]
        p3 = points[i][next_j]
        t.penup()
        t.goto(p1)
        t.pendown()
        t.goto(p2)
        t.penup()
        t.goto(p1)
        t.pendown()
        t.goto(p3)
        j = j + 1
    i = i + 1
""".trimIndent()

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            val currentColorSchema by viewModel.colorSchema.collectAsState()
            val isDarkTheme = isSystemInDarkTheme()
            val colorSchema =  colorSchemasMap()
                .getOrDefault(
                    key = currentColorSchema,
                    defaultValue = defaultColorSchema(isDarkTheme)
                )
            TurtleAppTheme(colorSchema) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Home(sourceCode = starterLiloCode)) {
                    composable<Home> {
                        val currentRoute = navController.currentBackStackEntry?.toRoute<Home>()
                        HomeScreen(
                            starterCode = currentRoute?.sourceCode.orEmpty(),
                            navController = navController
                        )
                    }
                    composable<AppRoute.Files> {
                        LiloFilesScreen(navController = navController)
                    }
                    composable<AppRoute.Settings> {
                        SettingsScreen(navController = navController)
                    }
                }
            }
        }
    }
}
