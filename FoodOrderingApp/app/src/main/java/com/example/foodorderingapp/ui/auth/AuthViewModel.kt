package com.example.foodorderingapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.repository.UserRepository
import com.example.foodorderingapp.utils.NetworkResult
import com.example.foodorderingapp.utils.validateEmail
import com.example.foodorderingapp.utils.validatePassword
import com.example.foodorderingapp.utils.validatePhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Login state
    private val _loginState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Success(Unit))
    val loginState: StateFlow<NetworkResult<Unit>> = _loginState.asStateFlow()

    // Register state
    private val _registerState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Success(Unit))
    val registerState: StateFlow<NetworkResult<Unit>> = _registerState.asStateFlow()
    
    // Form validation errors
    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> = _nameError
    
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    
    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError
    
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    
    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError
    
    // Login function
    fun login(email: String, password: String) {
        // Reset validation errors
        resetFormErrors()
        
        // Validate form
        var isValid = true
        
        if (email.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!validateEmail(email)) {
            _emailError.value = "Invalid email format"
            isValid = false
        }
        
        if (password.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        } else if (!validatePassword(password)) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        }
        
        if (!isValid) return
        
        // Proceed with login
        _loginState.value = NetworkResult.Loading()
        
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            _loginState.value = result
        }
    }
    
    // Register function
    fun register(name: String, email: String, password: String, confirmPassword: String, phone: String) {
        // Reset validation errors
        resetFormErrors()
        
        // Validate form
        var isValid = true
        
        if (name.isBlank()) {
            _nameError.value = "Name is required"
            isValid = false
        }
        
        if (email.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!validateEmail(email)) {
            _emailError.value = "Invalid email format"
            isValid = false
        }
        
        if (phone.isBlank()) {
            _phoneError.value = "Phone number is required"
            isValid = false
        } else if (!validatePhone(phone)) {
            _phoneError.value = "Invalid phone number format"
            isValid = false
        }
        
        if (password.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        } else if (!validatePassword(password)) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        }
        
        if (confirmPassword.isBlank()) {
            _confirmPasswordError.value = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            _confirmPasswordError.value = "Passwords do not match"
            isValid = false
        }
        
        if (!isValid) return
        
        // Proceed with registration
        _registerState.value = NetworkResult.Loading()
        
        viewModelScope.launch {
            val result = userRepository.register(name, email, password, phone)
            _registerState.value = result
        }
    }
    
    // Check if user is logged in
    suspend fun isLoggedIn(): Boolean {
        return userRepository.isLoggedIn()
    }
    
    // Reset form validation errors
    fun resetFormErrors() {
        _nameError.value = null
        _emailError.value = null
        _phoneError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
    }
}