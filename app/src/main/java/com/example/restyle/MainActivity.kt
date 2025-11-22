package com.example.restyle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.restyle.ui.screen.HomeScreen
import com.example.restyle.ui.theme.ReStyleTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
        }

        setContent {
            ReStyleTheme {
                HomeScreen()
            }
        }
    }
}