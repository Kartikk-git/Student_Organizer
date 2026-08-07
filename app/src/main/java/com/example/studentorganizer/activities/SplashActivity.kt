package com.example.studentorganizer.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getSharedPreferences("Details", MODE_PRIVATE)

        val login = sharedPref.getBoolean("isLoggedIn", false)
        val first = sharedPref.getBoolean("isFirstTime", true)

        if (first) {
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else if (login) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}