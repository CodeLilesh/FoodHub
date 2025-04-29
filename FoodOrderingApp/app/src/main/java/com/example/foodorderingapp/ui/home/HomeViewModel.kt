package com.example.foodorderingapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.repository.RestaurantRepository
import com.example.foodorderingapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> = _restaurants

    private val _featuredRestaurants = MutableLiveData<List<Restaurant>>()
    val featuredRestaurants: LiveData<List<Restaurant>> = _featuredRestaurants

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadRestaurants()
        loadCategories()
    }

    fun loadRestaurants() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            // First, get from local database and update UI
            restaurantRepository.getAllRestaurants().collectLatest { localRestaurants ->
                if (localRestaurants.isNotEmpty()) {
                    _restaurants.value = localRestaurants
                    _featuredRestaurants.value = localRestaurants.filter { it.featured }
                }

                // Then, refresh from network
                when (val result = restaurantRepository.refreshRestaurants()) {
                    is NetworkResult.Success -> {
                        // Note: We don't need to update UI here since the Flow from Room
                        // will emit new values when the database is updated
                    }
                    is NetworkResult.Error -> {
                        if (localRestaurants.isEmpty()) {
                            // Only show error if we couldn't load from database
                            _error.value = result.message
                        }
                    }
                }
                _loading.value = false
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            // This would normally come from the API, but for now we'll use a static list
            _categories.value = listOf(
                "All", "Pizza", "Burger", "Sushi", "Italian", "Chinese", "Mexican", "Indian"
            )
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            if (category.equals("All", ignoreCase = true)) {
                loadRestaurants()
                return@launch
            }

            // First try from database
            restaurantRepository.getRestaurantsByCategory(category).collectLatest { localRestaurants ->
                if (localRestaurants.isNotEmpty()) {
                    _restaurants.value = localRestaurants
                }

                // Then refresh from network
                when (val result = restaurantRepository.refreshRestaurantsByCategory(category)) {
                    is NetworkResult.Success -> {
                        // Flow will update automatically
                    }
                    is NetworkResult.Error -> {
                        if (localRestaurants.isEmpty()) {
                            _error.value = result.message
                        }
                    }
                }
                _loading.value = false
            }
        }
    }

    fun searchRestaurants(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            // First try from database
            restaurantRepository.searchRestaurants(query).collectLatest { localRestaurants ->
                if (localRestaurants.isNotEmpty()) {
                    _restaurants.value = localRestaurants
                }

                // Then refresh from network
                when (val result = restaurantRepository.searchRestaurantsFromApi(query)) {
                    is NetworkResult.Success -> {
                        // Flow will update automatically
                    }
                    is NetworkResult.Error -> {
                        if (localRestaurants.isEmpty()) {
                            _error.value = result.message
                        }
                    }
                }
                _loading.value = false
            }
        }
    }
}