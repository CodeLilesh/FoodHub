package com.example.foodorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.models.CartItem
import com.example.foodorderingapp.data.models.Order
import com.example.foodorderingapp.data.repository.CartRepository
import com.example.foodorderingapp.data.repository.OrderRepository
import com.example.foodorderingapp.utils.Result
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    
    private val cartRepository = CartRepository(application)
    private val orderRepository = OrderRepository(application)
    
    // Cart items
    val cartItems = cartRepository.getCartItems().asLiveData()
    
    // Cart items count
    private val _cartItemsCount = cartRepository.getCartItemsCount().map { it ?: 0 }
    val cartItemsCount = _cartItemsCount.asLiveData()
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    
    private val _orderPlacementResult = MutableLiveData<OrderPlacementResult>()
    val orderPlacementResult: LiveData<OrderPlacementResult> get() = _orderPlacementResult
    
    fun refreshCartItems() {
        // This method is intentionally left empty as Room's Flow will automatically
        // update the LiveData when the database changes
    }
    
    fun updateCartItemQuantity(cartItemId: Int, newQuantity: Int) {
        viewModelScope.launch {
            try {
                cartRepository.updateCartItemQuantity(cartItemId, newQuantity)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to update quantity"
            }
        }
    }
    
    fun removeFromCart(cartItemId: Int) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(cartItemId)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to remove item from cart"
            }
        }
    }
    
    fun placeOrder(
        deliveryAddress: String,
        paymentMethod: String,
        specialInstructions: String?
    ) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val items = cartItems.value
                
                if (items.isNullOrEmpty()) {
                    _orderPlacementResult.value = OrderPlacementResult(
                        success = false,
                        message = "Your cart is empty"
                    )
                    _isLoading.value = false
                    return@launch
                }
                
                // Get restaurant ID from the first cart item
                val restaurantId = items.first().restaurantId
                
                // Create order
                when (val result = orderRepository.createOrder(
                    restaurantId = restaurantId,
                    cartItems = items,
                    deliveryAddress = deliveryAddress,
                    paymentMethod = paymentMethod,
                    specialInstructions = specialInstructions
                )) {
                    is Result.Success -> {
                        // Clear cart after successful order
                        cartRepository.clearCart()
                        
                        _orderPlacementResult.value = OrderPlacementResult(
                            success = true,
                            order = result.data
                        )
                    }
                    is Result.Error -> {
                        _orderPlacementResult.value = OrderPlacementResult(
                            success = false,
                            message = result.message
                        )
                    }
                }
                
                _isLoading.value = false
                
            } catch (e: Exception) {
                _orderPlacementResult.value = OrderPlacementResult(
                    success = false,
                    message = e.message ?: "Failed to place order"
                )
                _isLoading.value = false
            }
        }
    }
    
    fun getCartItemsCount(): LiveData<Int> {
        return cartItemsCount
    }
}

data class OrderPlacementResult(
    val success: Boolean,
    val order: Order? = null,
    val message: String? = null
)
