package com.example.restyle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.restyle.ui.screen.HomeScreen
import com.example.restyle.ui.theme.ReStyleTheme

/**
 * MainActivity adalah entry point untuk aplikasi ReStyle.
 * Aplikasi ini membantu pengguna untuk menjual kembali, mendonasikan,
 * atau mendaur ulang pakaian mereka dengan mudah.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReStyleTheme {
                HomeScreen()
            }
        }
    }
}