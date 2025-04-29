package com.example.foodorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.models.User
import com.example.foodorderingapp.data.repository.UserRepository
import com.example.foodorderingapp.utils.Result
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userRepository = UserRepository(application)
    
    private val _loginResult = MutableLiveData<AuthResult>()
    val loginResult: LiveData<AuthResult> get() = _loginResult
    
    private val _registerResult = MutableLiveData<AuthResult>()
    val registerResult: LiveData<AuthResult> get() = _registerResult
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            when (val result = userRepository.login(email, password)) {
                is Result.Success -> {
                    _loginResult.value = AuthResult(
                        success = true,
                        user = result.data
                    )
                }
                is Result.Error -> {
                    _loginResult.value = AuthResult(
                        success = false,
                        message = result.message
                    )
                }
            }
        }
    }
    
    fun register(name: String, email: String, password: String, phone: String?, address: String?) {
        viewModelScope.launch {
            when (val result = userRepository.register(name, email, password, phone, address)) {
                is Result.Success -> {
                    _registerResult.value = AuthResult(
                        success = true,
                        user = result.data
                    )
                }
                is Result.Error -> {
                    _registerResult.value = AuthResult(
                        success = false,
                        message = result.message
                    )
                }
            }
        }
    }
}

data class AuthResult(
    val success: Boolean,
    val user: User? = null,
    val message: String? = null
)
