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
import com.example.foodorderingapp.databinding.FragmentRegisterBinding
import com.example.foodorderingapp.utils.NetworkResult
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        
        setupListeners()
        observeViewModel()
    }
    
    private fun setupListeners() {
        // Register button click
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            
            viewModel.register(name, email, password, confirmPassword, phone)
        }
        
        // Login tab navigation
        binding.tvLoginPrompt.setOnClickListener {
            (activity as? AuthActivity)?.navigateToLogin()
        }
    }
    
    private fun observeViewModel() {
        // Observe form validation errors
        viewModel.nameError.observe(viewLifecycleOwner) { error ->
            binding.tilName.error = error
        }
        
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.tilEmail.error = error
        }
        
        viewModel.phoneError.observe(viewLifecycleOwner) { error ->
            binding.tilPhone.error = error
        }
        
        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            binding.tilPassword.error = error
        }
        
        viewModel.confirmPasswordError.observe(viewLifecycleOwner) { error ->
            binding.tilConfirmPassword.error = error
        }
        
        // Observe register state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            showLoading(true)
                        }
                        is NetworkResult.Success -> {
                            showLoading(false)
                            Snackbar.make(binding.root, "Registration successful!", Snackbar.LENGTH_LONG).show()
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
            binding.btnRegister.isEnabled = false
            (activity as? AuthActivity)?.showProgressBar(true)
        } else {
            binding.btnRegister.isEnabled = true
            (activity as? AuthActivity)?.showProgressBar(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}