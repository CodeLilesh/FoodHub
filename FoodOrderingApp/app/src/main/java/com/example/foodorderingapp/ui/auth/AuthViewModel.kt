package com.example.foodorderingapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.model.User
import com.example.foodorderingapp.data.repository.UserRepository
import com.example.foodorderingapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    // Login state
    private val _loginState = MutableStateFlow<NetworkResult<User>>(NetworkResult.Loading)
    val loginState: StateFlow<NetworkResult<User>> = _loginState
    
    // Register state
    private val _registerState = MutableStateFlow<NetworkResult<User>>(NetworkResult.Loading)
    val registerState: StateFlow<NetworkResult<User>> = _registerState
    
    // Form validation
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    
    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> = _nameError
    
    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError
    
    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError
    
    // Login function
    fun login(email: String, password: String) {
        // Validate input
        if (!validateLoginInput(email, password)) {
            return
        }
        
        // Update state to loading
        _loginState.value = NetworkResult.Loading
        
        viewModelScope.launch {
            val result = userRepository.loginUser(email, password)
            _loginState.value = result
        }
    }
    
    // Register function
    fun register(name: String, email: String, password: String, confirmPassword: String, phone: String) {
        // Validate input
        if (!validateRegisterInput(name, email, password, confirmPassword, phone)) {
            return
        }
        
        // Update state to loading
        _registerState.value = NetworkResult.Loading
        
        viewModelScope.launch {
            val result = userRepository.registerUser(name, email, password, phone)
            _registerState.value = result
        }
    }
    
    // Check if user is logged in
    suspend fun isLoggedIn(): Boolean {
        return userRepository.sessionManager.isLoggedIn.first()
    }
    
    // Validate login input
    private fun validateLoginInput(email: String, password: String): Boolean {
        var isValid = true
        
        // Reset errors
        _emailError.value = null
        _passwordError.value = null
        
        // Validate email
        if (email.isBlank()) {
            _emailError.value = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Please enter a valid email"
            isValid = false
        }
        
        // Validate password
        if (password.isBlank()) {
            _passwordError.value = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        }
        
        return isValid
    }
    
    // Validate register input
    private fun validateRegisterInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        phone: String
    ): Boolean {
        var isValid = true
        
        // Reset errors
        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _phoneError.value = null
        
        // Validate name
        if (name.isBlank()) {
            _nameError.value = "Name cannot be empty"
            isValid = false
        }
        
        // Validate email
        if (email.isBlank()) {
            _emailError.value = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Please enter a valid email"
            isValid = false
        }
        
        // Validate password
        if (password.isBlank()) {
            _passwordError.value = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        }
        
        // Validate confirm password
        if (confirmPassword != password) {
            _confirmPasswordError.value = "Passwords do not match"
            isValid = false
        }
        
        // Validate phone
        if (phone.isBlank()) {
            _phoneError.value = "Phone number cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            _phoneError.value = "Please enter a valid phone number"
            isValid = false
        }
        
        return isValid
    }
    
    // Reset form errors
    fun resetFormErrors() {
        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _phoneError.value = null
    }
}