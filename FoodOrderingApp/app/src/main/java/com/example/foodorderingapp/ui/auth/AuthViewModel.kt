package com.example.foodorderingapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.model.User
import com.example.foodorderingapp.data.repository.UserRepository
import com.example.foodorderingapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    enum class AuthState {
        UNAUTHENTICATED,
        AUTHENTICATING,
        AUTHENTICATED,
        AUTHENTICATION_FAILED
    }

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        // Initialize state
        _authState.value = if (isLoggedIn()) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
        
        // Load user if already logged in
        if (isLoggedIn()) {
            loadUser()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.AUTHENTICATING
            _errorMessage.value = null

            when (val result = userRepository.login(email, password)) {
                is NetworkResult.Success -> {
                    _user.value = result.data
                    _authState.value = AuthState.AUTHENTICATED
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    _authState.value = AuthState.AUTHENTICATION_FAILED
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.AUTHENTICATING
            _errorMessage.value = null

            when (val result = userRepository.register(name, email, password, phone)) {
                is NetworkResult.Success -> {
                    _user.value = result.data
                    _authState.value = AuthState.AUTHENTICATED
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    _authState.value = AuthState.AUTHENTICATION_FAILED
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _user.value = null
            _authState.value = AuthState.UNAUTHENTICATED
        }
    }

    fun isLoggedIn(): Boolean {
        return userRepository.isLoggedIn()
    }

    private fun loadUser() {
        viewModelScope.launch {
            when (val result = userRepository.getCurrentUser()) {
                is NetworkResult.Success -> {
                    _user.value = result.data
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    // If can't get current user, logout
                    logout()
                }
            }
        }
    }

    fun validateRegistration(name: String, email: String, password: String, confirmPassword: String, phone: String): Boolean {
        if (name.isBlank()) {
            _errorMessage.value = "Name cannot be empty"
            return false
        }
        
        if (email.isBlank()) {
            _errorMessage.value = "Email cannot be empty"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Invalid email format"
            return false
        }
        
        if (password.isBlank()) {
            _errorMessage.value = "Password cannot be empty"
            return false
        }
        
        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return false
        }
        
        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match"
            return false
        }
        
        if (phone.isBlank()) {
            _errorMessage.value = "Phone number cannot be empty"
            return false
        }
        
        return true
    }

    fun validateLogin(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _errorMessage.value = "Email cannot be empty"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Invalid email format"
            return false
        }
        
        if (password.isBlank()) {
            _errorMessage.value = "Password cannot be empty"
            return false
        }
        
        return true
    }
}