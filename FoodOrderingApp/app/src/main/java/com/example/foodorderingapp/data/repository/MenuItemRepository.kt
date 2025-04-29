package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.api.ApiService
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.utils.NetworkResult
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuItemRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Get menu items for a restaurant
    suspend fun getMenuItems(
        restaurantId: String,
        category: String? = null,
        query: String? = null
    ): NetworkResult<List<MenuItem>> {
        return handleApiResponse {
            apiService.getMenuItems(restaurantId, category, query)
        }
    }
    
    // Get menu item details by ID
    suspend fun getMenuItem(menuItemId: String): NetworkResult<MenuItem> {
        return handleApiResponse {
            apiService.getMenuItem(menuItemId)
        }
    }
    
    // Handle API response
    private suspend fun <T> handleApiResponse(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            
            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkResult.success(it)
                } ?: NetworkResult.error("Response body is null")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                NetworkResult.error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.error("Network error: ${e.message}")
        }
    }
}