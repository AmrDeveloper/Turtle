package com.amrdeveloper.turtle

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amrdeveloper.colorschema.VSCodeDarkLiloColorSchema
import com.amrdeveloper.colorschema.VSCodeLightLiloColorSchema
import com.amrdeveloper.turtle.ui.home.HomeScreen
import com.amrdeveloper.turtle.ui.files.LiloFilesScreen
import com.amrdeveloper.turtle.ui.settings.SettingsScreen
import com.amrdeveloper.turtle.ui.theme.TurtleAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colorSchema =
                if (isSystemInDarkTheme()) VSCodeDarkLiloColorSchema else VSCodeLightLiloColorSchema
            TurtleAppTheme(colorSchema) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            colorSchema = colorSchema,
                            navController = navController
                        )
                    }
                    composable("files") {
                        LiloFilesScreen(navController = navController)
                    }
                    composable("settings") {
                        SettingsScreen(navController = navController)
                    }
                }
            }
        }
    }
}
