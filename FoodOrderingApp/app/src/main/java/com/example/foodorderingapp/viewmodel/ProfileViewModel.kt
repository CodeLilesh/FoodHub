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

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = UserRepository(application)
    
    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _updateResult = MutableLiveData<UpdateProfileResult>()
    val updateResult: LiveData<UpdateProfileResult> get() = _updateResult
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    
    fun getUserProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getUserProfile()) {
                is Result.Success -> {
                    _userProfile.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun updateUserProfile(name: String, email: String, phone: String?, address: String?) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.updateUserProfile(name, email, phone, address)) {
                is Result.Success -> {
                    _userProfile.value = result.data
                    _updateResult.value = UpdateProfileResult(
                        success = true
                    )
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _updateResult.value = UpdateProfileResult(
                        success = false,
                        message = result.message
                    )
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun logout() {
        repository.logout()
        _userProfile.value = null
    }
}

data class UpdateProfileResult(
    val success: Boolean,
    val message: String? = null
)
