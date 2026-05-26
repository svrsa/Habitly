package com.example.habitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.habitly.ui.navigation.HabitlyApp
import com.example.habitly.ui.theme.HabitlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitlyTheme {
                HabitlyApp()
            }
        }
    }
}
