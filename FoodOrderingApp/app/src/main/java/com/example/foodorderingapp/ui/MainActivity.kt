package com.example.foodorderingapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity serves as the container for all fragments in the app's main flow
 * It manages navigation between Home, Cart, and Profile through a bottom navigation bar
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation controller with NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Connect bottom navigation with navigation controller
        binding.bottomNavigationView.setupWithNavController(navController)

        // Handle visibility of bottom navigation for specific destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideBottomNavDestinations = listOf(
                R.id.restaurantDetailFragment,
                R.id.checkoutFragment,
                R.id.orderConfirmationFragment,
                R.id.orderTrackingFragment
            )

            binding.bottomNavigationView.visibility = if (destination.id in hideBottomNavDestinations) {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }
        }
    }

    // Handle back press with the navigation component
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}