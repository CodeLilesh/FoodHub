package com.example.foodorderingapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupObservers()
    }
    
    private fun setupUI() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }
        
        // Register redirect
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        
        // Forgot password click
        binding.tvForgotPassword.setOnClickListener {
            // TODO: Implement forgot password feature
            Snackbar.make(binding.root, "Forgot password feature coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { authState ->
            when (authState) {
                AuthViewModel.AuthState.AUTHENTICATING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                AuthViewModel.AuthState.AUTHENTICATED -> {
                    binding.progressBar.visibility = View.GONE
                    // Navigation is handled in AuthActivity
                }
                AuthViewModel.AuthState.AUTHENTICATION_FAILED -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
                AuthViewModel.AuthState.UNAUTHENTICATED -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }
    
    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        // Validate input
        if (viewModel.validateLogin(email, password)) {
            // Attempt login
            viewModel.login(email, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}