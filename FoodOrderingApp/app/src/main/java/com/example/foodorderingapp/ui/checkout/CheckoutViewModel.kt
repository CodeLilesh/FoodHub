package com.example.foodorderingapp.ui.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.Order
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.repository.CartRepository
import com.example.foodorderingapp.data.repository.OrderRepository
import com.example.foodorderingapp.data.repository.RestaurantRepository
import com.example.foodorderingapp.data.repository.UserRepository
import com.example.foodorderingapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val restaurantRepository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _restaurant = MutableLiveData<Restaurant?>()
    val restaurant: LiveData<Restaurant?> = _restaurant

    private val _subtotal = MutableLiveData<Double>()
    val subtotal: LiveData<Double> = _subtotal

    private val _deliveryFee = MutableLiveData<Double>()
    val deliveryFee: LiveData<Double> = _deliveryFee

    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    private val _formattedSubtotal = MutableLiveData<String>()
    val formattedSubtotal: LiveData<String> = _formattedSubtotal

    private val _formattedDeliveryFee = MutableLiveData<String>()
    val formattedDeliveryFee: LiveData<String> = _formattedDeliveryFee

    private val _formattedTotal = MutableLiveData<String>()
    val formattedTotal: LiveData<String> = _formattedTotal

    private val _itemCount = MutableLiveData<String>()
    val itemCount: LiveData<String> = _itemCount

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _orderPlaced = MutableLiveData<Order?>()
    val orderPlaced: LiveData<Order?> = _orderPlaced

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    fun loadCheckoutData(restaurantId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            // Load cart items
            cartRepository.getCartItems().collectLatest { items ->
                _cartItems.value = items
                
                // Update item count text
                val count = items.sumOf { it.quantity }
                _itemCount.value = "$count item${if (count != 1) "s" else ""}"
                
                if (items.isNotEmpty()) {
                    // Load restaurant details
                    restaurantRepository.getRestaurantById(restaurantId).collectLatest { restaurant ->
                        _restaurant.value = restaurant
                        
                        // Calculate totals
                        calculateTotals(items, restaurant)
                        
                        _loading.value = false
                    }
                } else {
                    _loading.value = false
                    _error.value = "Your cart is empty"
                }
            }
        }
    }

    private fun calculateTotals(items: List<CartItem>, restaurant: Restaurant?) {
        val subtotal = items.sumOf { it.price * it.quantity }
        _subtotal.value = subtotal
        _formattedSubtotal.value = numberFormat.format(subtotal)

        // Apply delivery fee based on restaurant
        val deliveryFee = restaurant?.deliveryFee ?: 0.0
        _deliveryFee.value = deliveryFee
        _formattedDeliveryFee.value = numberFormat.format(deliveryFee)

        // Calculate total
        val total = subtotal + deliveryFee
        _total.value = total
        _formattedTotal.value = numberFormat.format(total)
    }

    fun placeOrder(deliveryAddress: String, contactPhone: String, paymentMethod: String, notes: String?) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val restaurantId = _restaurant.value?.id ?: return@launch
            
            val result = orderRepository.createOrder(
                restaurantId = restaurantId,
                deliveryAddress = deliveryAddress,
                contactPhone = contactPhone,
                paymentMethod = paymentMethod,
                notes = notes
            )
            
            when (result) {
                is NetworkResult.Success -> {
                    _orderPlaced.value = result.data
                }
                is NetworkResult.Error -> {
                    _error.value = result.message
                }
            }
            
            _loading.value = false
        }
    }

    fun validateInputs(deliveryAddress: String, contactPhone: String): Boolean {
        if (deliveryAddress.isBlank()) {
            _error.value = "Please enter your delivery address"
            return false
        }
        
        if (contactPhone.isBlank()) {
            _error.value = "Please enter your contact phone number"
            return false
        }
        
        return true
    }
}