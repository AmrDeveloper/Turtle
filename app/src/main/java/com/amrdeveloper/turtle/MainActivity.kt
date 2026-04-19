package com.amrdeveloper.turtle

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import com.amrdeveloper.colorschema.VSCodeDarkLiloColorSchema
import com.amrdeveloper.colorschema.VSCodeLightLiloColorSchema
import com.amrdeveloper.turtle.ui.home.HomeScreen
import com.amrdeveloper.turtle.ui.theme.TurtleAppTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colorSchema =
                if (isSystemInDarkTheme()) VSCodeDarkLiloColorSchema else VSCodeLightLiloColorSchema
            TurtleAppTheme(colorSchema) {
                HomeScreen(colorSchema)
            }
        }
    }
}
