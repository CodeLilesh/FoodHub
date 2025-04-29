package com.example.foodorderingapp.data.repository

import android.content.Context
import com.example.foodorderingapp.data.api.RetrofitClient
import com.example.foodorderingapp.data.models.MenuItem
import com.example.foodorderingapp.data.models.Restaurant
import com.example.foodorderingapp.utils.Result
import com.example.foodorderingapp.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestaurantRepository(private val context: Context) {
    
    private val apiService = RetrofitClient.apiService
    private val sessionManager = SessionManager(context)
    
    suspend fun getAllRestaurants(): Result<List<Restaurant>> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            val response = apiService.getAllRestaurants(token)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurants = response.body()?.data ?: emptyList()
                return@withContext Result.Success(restaurants)
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get restaurants"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun getRestaurantsByCategory(category: String): Result<List<Restaurant>> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            // If category is "All", get all restaurants
            if (category.equals("All", ignoreCase = true)) {
                return@withContext getAllRestaurants()
            }
            
            val response = apiService.getRestaurantsByCategory(token, category)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurants = response.body()?.data ?: emptyList()
                return@withContext Result.Success(restaurants)
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get restaurants by category"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun searchRestaurants(query: String): Result<List<Restaurant>> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            val response = apiService.searchRestaurants(token, query)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurants = response.body()?.data ?: emptyList()
                return@withContext Result.Success(restaurants)
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to search restaurants"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun getRestaurantById(restaurantId: Int): Result<Restaurant> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            val response = apiService.getRestaurantById(token, restaurantId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurant = response.body()?.data
                if (restaurant != null) {
                    return@withContext Result.Success(restaurant)
                }
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get restaurant"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun getRestaurantMenu(restaurantId: Int): Result<List<MenuItem>> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            val response = apiService.getRestaurantMenu(token, restaurantId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val menuItems = response.body()?.data ?: emptyList()
                return@withContext Result.Success(menuItems)
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get menu"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun getRestaurantMenuByCategory(restaurantId: Int, category: String): Result<List<MenuItem>> = 
        withContext(Dispatchers.IO) {
            try {
                // Check if token exists
                val token = sessionManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.Error("Not authenticated")
                }
                
                val response = apiService.getRestaurantMenuByCategory(token, restaurantId, category)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val menuItems = response.body()?.data ?: emptyList()
                    return@withContext Result.Success(menuItems)
                }
                
                val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get menu by category"
                return@withContext Result.Error(errorMessage)
            } catch (e: Exception) {
                return@withContext Result.Error(e.message ?: "Network error occurred")
            }
        }
    
    suspend fun getMenuItemById(menuItemId: Int): Result<MenuItem> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            val response = apiService.getMenuItemById(token, menuItemId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val menuItem = response.body()?.data
                if (menuItem != null) {
                    return@withContext Result.Success(menuItem)
                }
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get menu item"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
}
