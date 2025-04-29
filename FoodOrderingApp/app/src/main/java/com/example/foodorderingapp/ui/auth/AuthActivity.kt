package com.example.foodorderingapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.foodorderingapp.databinding.ActivityAuthBinding
import com.example.foodorderingapp.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var authPagerAdapter: AuthPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Check if user is already logged in
        checkUserLoggedIn()
        
        setupViewPager()
        setupTabLayout()
    }
    
    private fun checkUserLoggedIn() {
        lifecycleScope.launch {
            if (viewModel.isLoggedIn()) {
                navigateToHome()
            }
        }
    }
    
    private fun setupViewPager() {
        authPagerAdapter = AuthPagerAdapter(this)
        binding.authViewPager.apply {
            adapter = authPagerAdapter
            isUserInputEnabled = false  // Disable swiping between tabs
            offscreenPageLimit = 1  // Keep both fragments in memory
            
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // Reset validation errors when switching tabs
                    viewModel.resetFormErrors()
                }
            })
        }
    }
    
    private fun setupTabLayout() {
        TabLayoutMediator(binding.authTabLayout, binding.authViewPager) { tab, position ->
            tab.text = if (position == 0) "Login" else "Register"
        }.attach()
    }
    
    // Navigate to login tab
    fun navigateToLogin() {
        binding.authViewPager.currentItem = 0
    }
    
    // Navigate to register tab
    fun navigateToRegister() {
        binding.authViewPager.currentItem = 1
    }
    
    // Navigate to home screen after successful login/register
    fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    // Show/hide progress bar
    fun showProgressBar(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}