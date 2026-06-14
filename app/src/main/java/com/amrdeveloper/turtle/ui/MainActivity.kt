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
import com.amrdeveloper.turtle.ui.navigation.AppRoute
import com.amrdeveloper.turtle.ui.navigation.AppRoute.Home
import com.amrdeveloper.turtle.ui.settings.SettingsScreen
import com.amrdeveloper.turtle.ui.theme.TurtleAppTheme
import dagger.hilt.android.AndroidEntryPoint

private val starterLiloCode = """
print("Hello, World!")
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
                    composable<AppRoute.Settings> {
                        SettingsScreen(navController = navController)
                    }
                }
            }
        }
    }
}
