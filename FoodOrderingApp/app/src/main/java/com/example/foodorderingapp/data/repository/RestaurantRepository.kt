package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.data.local.RestaurantDao
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.remote.ApiService
import com.example.foodorderingapp.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    private val apiService: ApiService,
    private val restaurantDao: RestaurantDao
) {
    
    // Get all restaurants from local database
    fun getRestaurants(): Flow<List<Restaurant>> {
        return restaurantDao.getAllRestaurants()
    }
    
    // Get restaurants by category from local database
    fun getRestaurantsByCategory(category: String): Flow<List<Restaurant>> {
        return restaurantDao.getRestaurantsByCategory(category)
    }
    
    // Search restaurants by term from local database
    fun searchRestaurants(term: String): Flow<List<Restaurant>> {
        return restaurantDao.searchRestaurants(term)
    }
    
    // Fetch all restaurants from API and update local database
    suspend fun refreshRestaurants(): NetworkResult<List<Restaurant>> {
        return try {
            val response = apiService.getRestaurants()
            
            if (response.isSuccessful && response.body() != null) {
                val restaurants = response.body()!!
                restaurantDao.insertRestaurants(restaurants)
                NetworkResult.Success(restaurants)
            } else {
                Timber.e("Fetch restaurants failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Failed to load restaurants.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Fetch restaurants error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
    
    // Fetch restaurants by category from API and update local database
    suspend fun refreshRestaurantsByCategory(category: String): NetworkResult<List<Restaurant>> {
        return try {
            val response = apiService.getRestaurantsByCategory(category)
            
            if (response.isSuccessful && response.body() != null) {
                val restaurants = response.body()!!
                restaurantDao.insertRestaurants(restaurants)
                NetworkResult.Success(restaurants)
            } else {
                Timber.e("Fetch restaurants by category failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Failed to load restaurants.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Fetch restaurants by category error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
    
    // Fetch a single restaurant by ID
    suspend fun getRestaurantById(id: String): NetworkResult<Restaurant> {
        // First try to get from local database
        val localRestaurant = restaurantDao.getRestaurantById(id)
        if (localRestaurant != null) {
            return NetworkResult.Success(localRestaurant)
        }
        
        // If not in database, fetch from API
        return try {
            val response = apiService.getRestaurantById(id)
            
            if (response.isSuccessful && response.body() != null) {
                val restaurant = response.body()!!
                restaurantDao.insertRestaurant(restaurant)
                NetworkResult.Success(restaurant)
            } else {
                Timber.e("Fetch restaurant by ID failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Failed to load restaurant details.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Fetch restaurant by ID error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
    
    // Search restaurants from API
    suspend fun searchRestaurantsFromApi(term: String): NetworkResult<List<Restaurant>> {
        return try {
            val response = apiService.searchRestaurants(term)
            
            if (response.isSuccessful && response.body() != null) {
                val restaurants = response.body()!!
                restaurantDao.insertRestaurants(restaurants)
                NetworkResult.Success(restaurants)
            } else {
                Timber.e("Search restaurants failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Failed to search restaurants.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Search restaurants error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
}