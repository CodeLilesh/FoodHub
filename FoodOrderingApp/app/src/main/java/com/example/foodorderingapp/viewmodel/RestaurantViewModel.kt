package com.example.foodorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.models.CartItem
import com.example.foodorderingapp.data.models.MenuItem
import com.example.foodorderingapp.data.models.Restaurant
import com.example.foodorderingapp.data.repository.CartRepository
import com.example.foodorderingapp.data.repository.RestaurantRepository
import com.example.foodorderingapp.utils.Result
import kotlinx.coroutines.launch

class RestaurantViewModel(application: Application) : AndroidViewModel(application) {
    
    private val restaurantRepository = RestaurantRepository(application)
    private val cartRepository = CartRepository(application)
    
    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> get() = _restaurant
    
    private val _menuItems = MutableLiveData<List<MenuItem>>()
    val menuItems: LiveData<List<MenuItem>> get() = _menuItems
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    
    private val _addToCartResult = MutableLiveData<AddToCartResult>()
    val addToCartResult: LiveData<AddToCartResult> get() = _addToCartResult
    
    fun loadRestaurant(restaurantId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = restaurantRepository.getRestaurantById(restaurantId)) {
                is Result.Success -> {
                    _restaurant.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun loadMenu(restaurantId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = restaurantRepository.getRestaurantMenu(restaurantId)) {
                is Result.Success -> {
                    _menuItems.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun loadMenuByCategory(restaurantId: Int, category: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = restaurantRepository.getRestaurantMenuByCategory(restaurantId, category)) {
                is Result.Success -> {
                    _menuItems.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun addToCart(restaurantId: Int, menuItem: MenuItem, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                // Check if we already have items from a different restaurant
                val existingRestaurantId = cartRepository.getRestaurantId()
                
                if (existingRestaurantId != null && existingRestaurantId != restaurantId) {
                    // Ask user if they want to clear cart first (this would be handled in the UI)
                    _addToCartResult.value = AddToCartResult(
                        success = false,
                        message = "You have items in your cart from a different restaurant. Adding this item will clear your current cart."
                    )
                    return@launch
                }
                
                // Add to cart
                cartRepository.addToCart(restaurantId, menuItem, quantity)
                
                _addToCartResult.value = AddToCartResult(
                    success = true,
                    cartItem = CartItem(
                        id = 0, // We don't know the exact ID, but it's not needed for the result
                        restaurantId = restaurantId,
                        menuItemId = menuItem.id,
                        name = menuItem.name,
                        description = menuItem.description,
                        imageUrl = menuItem.imageUrl,
                        pricePerItem = menuItem.price,
                        quantity = quantity,
                        isVegetarian = menuItem.isVegetarian
                    )
                )
            } catch (e: Exception) {
                _addToCartResult.value = AddToCartResult(
                    success = false,
                    message = e.message ?: "Failed to add item to cart"
                )
            }
        }
    }
}

data class AddToCartResult(
    val success: Boolean,
    val cartItem: CartItem? = null,
    val message: String? = null
)
