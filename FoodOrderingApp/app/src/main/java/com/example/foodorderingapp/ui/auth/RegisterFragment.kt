package com.example.foodorderingapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupObservers()
    }
    
    private fun setupUI() {
        // Register button click
        binding.btnRegister.setOnClickListener {
            attemptRegistration()
        }
        
        // Login redirect
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
    
    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { authState ->
            when (authState) {
                AuthViewModel.AuthState.AUTHENTICATING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                AuthViewModel.AuthState.AUTHENTICATED -> {
                    binding.progressBar.visibility = View.GONE
                    // Navigation is handled in AuthActivity
                }
                AuthViewModel.AuthState.AUTHENTICATION_FAILED -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }
                AuthViewModel.AuthState.UNAUTHENTICATED -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }
            }
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }
    
    private fun attemptRegistration() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        
        // Validate registration data
        if (viewModel.validateRegistration(name, email, password, confirmPassword, phone)) {
            // Attempt registration
            viewModel.register(name, email, password, phone)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}