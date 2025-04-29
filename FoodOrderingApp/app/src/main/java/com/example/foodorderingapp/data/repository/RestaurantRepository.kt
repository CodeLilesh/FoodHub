package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.api.ApiService
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.utils.NetworkResult
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Get all restaurants
    suspend fun getRestaurants(
        page: Int? = null,
        limit: Int? = null,
        category: String? = null,
        query: String? = null
    ): NetworkResult<List<Restaurant>> {
        return handleApiResponse {
            apiService.getRestaurants(page, limit, category, query)
        }
    }
    
    // Get restaurant details by ID
    suspend fun getRestaurant(restaurantId: String): NetworkResult<Restaurant> {
        return handleApiResponse {
            apiService.getRestaurant(restaurantId)
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