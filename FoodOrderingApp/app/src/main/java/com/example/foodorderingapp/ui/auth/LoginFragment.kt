package com.example.foodorderingapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.foodorderingapp.databinding.FragmentLoginBinding
import com.example.foodorderingapp.utils.NetworkResult
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        
        setupListeners()
        observeViewModel()
    }
    
    private fun setupListeners() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            viewModel.login(email, password)
        }
        
        // Register tab navigation
        binding.tvRegisterPrompt.setOnClickListener {
            (activity as? AuthActivity)?.navigateToRegister()
        }
        
        // Forgot password click
        binding.tvForgotPassword.setOnClickListener {
            // Will implement later
            Snackbar.make(binding.root, "Forgot password functionality will be added soon", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun observeViewModel() {
        // Observe form validation errors
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.tilEmail.error = error
        }
        
        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            binding.tilPassword.error = error
        }
        
        // Observe login state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            showLoading(true)
                        }
                        is NetworkResult.Success -> {
                            showLoading(false)
                            (activity as? AuthActivity)?.navigateToHome()
                        }
                        is NetworkResult.Error -> {
                            showLoading(false)
                            Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnLogin.isEnabled = false
            (activity as? AuthActivity)?.showProgressBar(true)
        } else {
            binding.btnLogin.isEnabled = true
            (activity as? AuthActivity)?.showProgressBar(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}