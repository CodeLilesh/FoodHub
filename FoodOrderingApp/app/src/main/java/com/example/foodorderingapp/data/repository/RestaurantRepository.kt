package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.data.local.RestaurantDao
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.remote.ApiService
import com.example.foodorderingapp.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class RestaurantRepository(
    private val apiService: ApiService,
    private val restaurantDao: RestaurantDao
) {
    // Get all restaurants from local database
    fun getAllRestaurants(): Flow<List<Restaurant>> = restaurantDao.getAllRestaurants()
    
    // Get restaurant by ID from local database
    fun getRestaurantById(id: Int): Flow<Restaurant?> = restaurantDao.getRestaurantById(id)
    
    // Get restaurants by cuisine from local database
    fun getRestaurantsByCuisine(cuisine: String): Flow<List<Restaurant>> = 
        restaurantDao.getRestaurantsByCuisine(cuisine)
    
    // Get popular restaurants (rating >= 4.0) from local database
    fun getPopularRestaurants(): Flow<List<Restaurant>> = restaurantDao.getRestaurantsByRating(4.0)
    
    // Search restaurants in local database
    fun searchRestaurants(query: String): Flow<List<Restaurant>> = 
        restaurantDao.searchRestaurants(query)
    
    // Fetch all restaurants from API and update local database
    suspend fun fetchAndCacheAllRestaurants(): NetworkResult<List<Restaurant>> {
        return try {
            val response = apiService.getAllRestaurants()
            
            if (response.isSuccessful) {
                val restaurants = response.body()
                if (!restaurants.isNullOrEmpty()) {
                    restaurantDao.insertRestaurants(restaurants)
                    NetworkResult.Success(restaurants)
                } else {
                    NetworkResult.Error("No restaurants found")
                }
            } else {
                NetworkResult.Error("Failed to fetch restaurants: ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Fetch restaurant by ID from API and update local database
    suspend fun fetchAndCacheRestaurantById(id: Int): NetworkResult<Restaurant> {
        return try {
            val response = apiService.getRestaurantById(id)
            
            if (response.isSuccessful) {
                val restaurant = response.body()
                if (restaurant != null) {
                    restaurantDao.insertRestaurant(restaurant)
                    NetworkResult.Success(restaurant)
                } else {
                    NetworkResult.Error("Restaurant not found")
                }
            } else {
                NetworkResult.Error("Failed to fetch restaurant: ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Fetch restaurants by category from API and update local database
    suspend fun fetchAndCacheRestaurantsByCategory(category: String): NetworkResult<List<Restaurant>> {
        return try {
            val response = apiService.getRestaurantsByCategory(category)
            
            if (response.isSuccessful) {
                val restaurants = response.body()
                if (!restaurants.isNullOrEmpty()) {
                    restaurantDao.insertRestaurants(restaurants)
                    NetworkResult.Success(restaurants)
                } else {
                    NetworkResult.Error("No restaurants found for category: $category")
                }
            } else {
                NetworkResult.Error("Failed to fetch restaurants: ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Search restaurants via API and update local database
    suspend fun fetchAndCacheSearchResults(query: String): NetworkResult<List<Restaurant>> {
        return try {
            val response = apiService.searchRestaurants(query)
            
            if (response.isSuccessful) {
                val restaurants = response.body()
                if (!restaurants.isNullOrEmpty()) {
                    restaurantDao.insertRestaurants(restaurants)
                    NetworkResult.Success(restaurants)
                } else {
                    NetworkResult.Error("No restaurants found for: $query")
                }
            } else {
                NetworkResult.Error("Failed to search restaurants: ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Fetch menu items for a restaurant from API
    suspend fun fetchMenuItemsByRestaurant(restaurantId: Int): NetworkResult<List<MenuItem>> {
        return try {
            val response = apiService.getMenuItemsByRestaurant(restaurantId)
            
            if (response.isSuccessful) {
                val menuItems = response.body()
                if (!menuItems.isNullOrEmpty()) {
                    NetworkResult.Success(menuItems)
                } else {
                    NetworkResult.Error("No menu items found for restaurant")
                }
            } else {
                NetworkResult.Error("Failed to fetch menu items: ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Fetch menu items by category for a restaurant from API
    suspend fun fetchMenuItemsByCategory(restaurantId: Int, category: String): NetworkResult<List<MenuItem>> {
        return try {
            val response = apiService.getMenuItemsByCategory(restaurantId, category)
            
            if (response.isSuccessful) {
                val menuItems = response.body()
                if (!menuItems.isNullOrEmpty()) {
                    NetworkResult.Success(menuItems)
                } else {
                    NetworkResult.Error("No menu items found for category: $category")
                }
            } else {
                NetworkResult.Error("Failed to fetch menu items: ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
}