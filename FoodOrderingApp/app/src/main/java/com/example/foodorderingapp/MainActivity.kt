package com.example.foodorderingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.databinding.ActivityMainBinding
import com.example.foodorderingapp.ui.cart.CartFragment
import com.example.foodorderingapp.ui.home.HomeFragment
import com.example.foodorderingapp.ui.orders.OrdersFragment
import com.example.foodorderingapp.ui.profile.ProfileFragment
import com.example.foodorderingapp.utils.SessionManager
import com.example.foodorderingapp.viewmodel.CartViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        setupBottomNavigation()

        // Set Home as the default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Observe cart items count for badge
        cartViewModel.getCartItemsCount().observe(this, { count ->
            if (count > 0) {
                binding.bottomNavigation.getOrCreateBadge(R.id.navigation_cart).apply {
                    number = count
                    isVisible = true
                }
            } else {
                binding.bottomNavigation.removeBadge(R.id.navigation_cart)
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_cart -> {
                    loadFragment(CartFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_orders -> {
                    loadFragment(OrdersFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Method to update cart badge from other components
    fun updateCartBadge() {
        cartViewModel.refreshCartItems()
    }
}
