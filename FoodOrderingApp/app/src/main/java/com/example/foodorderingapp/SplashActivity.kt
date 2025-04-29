package com.example.foodorderingapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorderingapp.databinding.ActivitySplashBinding
import com.example.foodorderingapp.ui.auth.LoginActivity
import com.example.foodorderingapp.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager
    private val SPLASH_DELAY: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Using a Handler to delay loading the next screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is logged in
            if (sessionManager.isLoggedIn()) {
                // User is logged in, go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not logged in, go to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            // Close the SplashActivity
            finish()
        }, SPLASH_DELAY)
    }
}
