package com.example.foodorderingapp.ui.restaurant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.repository.CartRepository
import com.example.foodorderingapp.data.repository.MenuItemRepository
import com.example.foodorderingapp.data.repository.RestaurantRepository
import com.example.foodorderingapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val menuItemRepository: MenuItemRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> = _restaurant

    private val _menuItems = MutableLiveData<List<MenuItem>>()
    val menuItems: LiveData<List<MenuItem>> = _menuItems

    private val _menuCategories = MutableLiveData<List<String>>()
    val menuCategories: LiveData<List<String>> = _menuCategories

    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> = _cartItemCount

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _addToCartSuccess = MutableLiveData<Boolean>()
    val addToCartSuccess: LiveData<Boolean> = _addToCartSuccess

    fun loadRestaurant(restaurantId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            // Get restaurant from local database
            restaurantRepository.getRestaurantById(restaurantId).collectLatest { restaurant ->
                restaurant?.let {
                    _restaurant.value = it
                }

                // Refresh from network
                val result = restaurantRepository.refreshRestaurantById(restaurantId)
                when (result) {
                    is NetworkResult.Error -> {
                        if (restaurant == null) {
                            // Only show error if we couldn't load from database
                            _error.value = result.message
                        }
                    }
                    is NetworkResult.Success -> {
                        // The flow will update automatically
                    }
                }

                // Load menu items for this restaurant regardless of restaurant result
                loadMenuItems(restaurantId)
            }
        }
    }

    private fun loadMenuItems(restaurantId: String) {
        viewModelScope.launch {
            // Get menu items from local database
            menuItemRepository.getMenuByRestaurant(restaurantId).collectLatest { menuItems ->
                _menuItems.value = menuItems
                
                // Extract unique categories from menu items
                val categories = menuItems.map { it.category }.distinct()
                _menuCategories.value = listOf("All") + categories
                
                // Refresh from network
                val result = menuItemRepository.refreshMenu(restaurantId)
                when (result) {
                    is NetworkResult.Error -> {
                        if (menuItems.isEmpty()) {
                            // Only show error if we couldn't load from database
                            _error.value = result.message
                        }
                    }
                    is NetworkResult.Success -> {
                        // The flow will update automatically
                    }
                }
                
                _loading.value = false
            }
        }
    }

    fun filterMenuByCategory(category: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val restaurantId = _restaurant.value?.id ?: return@launch
            
            if (category == "All") {
                loadMenuItems(restaurantId)
                return@launch
            }
            
            // Get menu items by category from local database
            menuItemRepository.getMenuByCategory(restaurantId, category).collectLatest { menuItems ->
                _menuItems.value = menuItems
                
                // Refresh from network
                val result = menuItemRepository.refreshMenuByCategory(restaurantId, category)
                when (result) {
                    is NetworkResult.Error -> {
                        if (menuItems.isEmpty()) {
                            // Only show error if we couldn't load from database
                            _error.value = result.message
                        }
                    }
                    is NetworkResult.Success -> {
                        // The flow will update automatically
                    }
                }
                
                _loading.value = false
            }
        }
    }

    fun searchMenu(query: String) {
        viewModelScope.launch {
            if (query.length < 2) return@launch
            
            _loading.value = true
            _error.value = null
            
            val restaurantId = _restaurant.value?.id ?: return@launch
            
            // Get search results from local database
            menuItemRepository.searchMenuItems(restaurantId, query).collectLatest { menuItems ->
                _menuItems.value = menuItems
                
                // Refresh from network
                val result = menuItemRepository.searchMenuItemsFromApi(restaurantId, query)
                when (result) {
                    is NetworkResult.Error -> {
                        if (menuItems.isEmpty()) {
                            // Only show error if we couldn't load from database
                            _error.value = result.message
                        }
                    }
                    is NetworkResult.Success -> {
                        // The flow will update automatically
                    }
                }
                
                _loading.value = false
            }
        }
    }

    fun addToCart(menuItem: MenuItem) {
        viewModelScope.launch {
            val restaurantId = _restaurant.value?.id ?: return@launch
            
            val cartItem = CartItem(
                id = menuItem.id,
                restaurantId = restaurantId,
                menuItemId = menuItem.id,
                name = menuItem.name,
                price = menuItem.price,
                quantity = 1,
                imageUrl = menuItem.imageUrl
            )
            
            cartRepository.addToCart(cartItem)
            _addToCartSuccess.value = true
            loadCartItemCount()
        }
    }

    fun loadCartItemCount() {
        viewModelScope.launch {
            cartRepository.getCartItemCount().collectLatest { count ->
                _cartItemCount.value = count
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}