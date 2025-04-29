package com.example.foodorderingapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base Application class for the Food Ordering App
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class FoodOrderingApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any required libraries or configurations here
    }
}