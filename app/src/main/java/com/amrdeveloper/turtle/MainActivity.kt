package com.amrdeveloper.turtle

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.amrdeveloper.turtle.ui.home.HomeScreen
import com.amrdeveloper.turtle.ui.theme.TurtleAppTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TurtleAppTheme {
                HomeScreen()
            }
        }
    }
}