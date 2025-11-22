package com.example.restyle

import android.app.Application
import com.google.firebase.FirebaseApp

class ReStyleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)
    }
}