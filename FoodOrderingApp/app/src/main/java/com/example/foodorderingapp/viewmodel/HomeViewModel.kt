package com.example.foodorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.models.Restaurant
import com.example.foodorderingapp.data.repository.RestaurantRepository
import com.example.foodorderingapp.utils.Result
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = RestaurantRepository(application)
    
    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> get() = _restaurants
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    
    fun getAllRestaurants() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getAllRestaurants()) {
                is Result.Success -> {
                    _restaurants.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun getRestaurantsByCategory(category: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getRestaurantsByCategory(category)) {
                is Result.Success -> {
                    _restaurants.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun searchRestaurants(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.searchRestaurants(query)) {
                is Result.Success -> {
                    _restaurants.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
}
