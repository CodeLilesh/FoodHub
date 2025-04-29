package com.example.foodorderingapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.ActivityMainBinding
import com.example.foodorderingapp.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
    }
    
    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)
        
        // Handle cart badge updates when navigating
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Update cart badge when a relevant destination is reached
            if (destination.id == R.id.homeFragment || 
                destination.id == R.id.cartFragment ||
                destination.id == R.id.searchFragment) {
                updateCartBadge()
            }
        }
    }
    
    private fun updateCartBadge() {
        // This would be implemented to show the current cart item count
        // using a badge on the cart menu item
        // For example:
        // val badge = binding.bottomNavigation.getOrCreateBadge(R.id.cart)
        // badge.isVisible = cartItemCount > 0
        // badge.number = cartItemCount
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}