package com.example.foodorderingapp.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.repository.CartRepository
import com.example.foodorderingapp.data.repository.RestaurantRepository
import com.example.foodorderingapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val restaurantRepository: RestaurantRepository
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

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cartEmpty = MutableLiveData<Boolean>()
    val cartEmpty: LiveData<Boolean> = _cartEmpty

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            cartRepository.getCartItems().collectLatest { items ->
                _cartItems.value = items
                _cartEmpty.value = items.isEmpty()

                if (items.isNotEmpty()) {
                    val restaurantId = items.first().restaurantId

                    // Load restaurant details
                    restaurantRepository.getRestaurantById(restaurantId).collectLatest { restaurant ->
                        _restaurant.value = restaurant
                        
                        // Calculate totals
                        calculateTotals(items, restaurant)
                    }
                } else {
                    // Reset values if cart is empty
                    _restaurant.value = null
                    _subtotal.value = 0.0
                    _deliveryFee.value = 0.0
                    _total.value = 0.0
                    _formattedSubtotal.value = numberFormat.format(0.0)
                    _formattedDeliveryFee.value = numberFormat.format(0.0)
                    _formattedTotal.value = numberFormat.format(0.0)
                }

                _loading.value = false
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

    fun updateQuantity(cartItem: CartItem, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) {
                // Remove item if quantity is 0 or less
                removeFromCart(cartItem)
            } else {
                cartRepository.updateCartItemQuantity(cartItem.id, quantity)
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(cartItem.id)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}