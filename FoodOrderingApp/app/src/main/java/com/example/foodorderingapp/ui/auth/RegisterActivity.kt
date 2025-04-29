package com.example.foodorderingapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.MainActivity
import com.example.foodorderingapp.databinding.ActivityRegisterBinding
import com.example.foodorderingapp.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // Register button click
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            if (validateInputs(name, email, password, phone, address)) {
                showProgress(true)
                authViewModel.register(name, email, password, phone, address)
            }
        }

        // Login text click
        binding.tvLogin.setOnClickListener {
            finish() // Go back to LoginActivity
        }
    }

    private fun observeViewModel() {
        // Observe register result
        authViewModel.registerResult.observe(this, { result ->
            showProgress(false)
            
            if (result.success) {
                // Registration successful, navigate to MainActivity
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // Show error message
                Toast.makeText(this, result.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validateInputs(name: String, email: String, password: String, phone: String, address: String): Boolean {
        var isValid = true

        // Validate name
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            isValid = false
        } else {
            binding.etName.error = null
        }

        // Validate email
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.etEmail.error = null
        }

        // Validate password
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.etPassword.error = null
        }

        // Validate phone (optional)
        if (phone.isNotEmpty() && !android.util.Patterns.PHONE.matcher(phone).matches()) {
            binding.etPhone.error = "Please enter a valid phone number"
            isValid = false
        } else {
            binding.etPhone.error = null
        }

        // Validate address
        if (address.isEmpty()) {
            binding.etAddress.error = "Address is required"
            isValid = false
        } else {
            binding.etAddress.error = null
        }

        return isValid
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !show
        binding.etName.isEnabled = !show
        binding.etEmail.isEnabled = !show
        binding.etPassword.isEnabled = !show
        binding.etPhone.isEnabled = !show
        binding.etAddress.isEnabled = !show
        binding.tvLogin.isEnabled = !show
    }
}
