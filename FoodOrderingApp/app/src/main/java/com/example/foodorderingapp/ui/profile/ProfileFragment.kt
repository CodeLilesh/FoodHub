package com.example.foodorderingapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.databinding.FragmentProfileBinding
import com.example.foodorderingapp.ui.auth.LoginActivity
import com.example.foodorderingapp.viewmodel.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        
        setupClickListeners()
        observeViewModel()
        
        // Load user profile
        viewModel.getUserProfile()
    }

    private fun setupClickListeners() {
        // Save changes button
        binding.btnSaveChanges.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()
                val address = binding.etAddress.text.toString().trim()
                
                viewModel.updateUserProfile(name, email, phone, address)
            }
        }
        
        // Logout button
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        // Observe user profile
        viewModel.userProfile.observe(viewLifecycleOwner, { user ->
            user?.let {
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
                binding.etPhone.setText(it.phone ?: "")
                binding.etAddress.setText(it.address ?: "")
            }
        })
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            setInputsEnabled(!isLoading)
        })
        
        // Observe update result
        viewModel.updateResult.observe(viewLifecycleOwner, { result ->
            if (result.success) {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, result.message ?: "Failed to update profile", Toast.LENGTH_LONG).show()
            }
        })
        
        // Observe errors
        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Validate name
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.etName.error = "Name is required"
            isValid = false
        }
        
        // Validate email
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email"
            isValid = false
        }
        
        // Validate phone (if provided)
        val phone = binding.etPhone.text.toString().trim()
        if (phone.isNotEmpty() && !android.util.Patterns.PHONE.matcher(phone).matches()) {
            binding.etPhone.error = "Please enter a valid phone number"
            isValid = false
        }
        
        // Validate address
        if (binding.etAddress.text.toString().trim().isEmpty()) {
            binding.etAddress.error = "Address is required"
            isValid = false
        }
        
        return isValid
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.etName.isEnabled = enabled
        binding.etEmail.isEnabled = enabled
        binding.etPhone.isEnabled = enabled
        binding.etAddress.isEnabled = enabled
        binding.btnSaveChanges.isEnabled = enabled
        binding.btnLogout.isEnabled = enabled
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                viewModel.logout()
                navigateToLogin()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
