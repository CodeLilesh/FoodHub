package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.data.local.MenuItemDao
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.data.remote.ApiService
import com.example.foodorderingapp.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class MenuItemRepository @Inject constructor(
    private val apiService: ApiService,
    private val menuItemDao: MenuItemDao
) {
    
    // Get menu for a restaurant from local database
    fun getMenuByRestaurant(restaurantId: String): Flow<List<MenuItem>> {
        return menuItemDao.getMenuByRestaurant(restaurantId)
    }
    
    // Get menu by category for a restaurant from local database
    fun getMenuByCategory(restaurantId: String, category: String): Flow<List<MenuItem>> {
        return menuItemDao.getMenuByCategory(restaurantId, category)
    }
    
    // Search menu items for a restaurant from local database
    fun searchMenuItems(restaurantId: String, searchTerm: String): Flow<List<MenuItem>> {
        return menuItemDao.searchMenuItems(restaurantId, searchTerm)
    }
    
    // Fetch menu for a restaurant from API and update local database
    suspend fun refreshMenu(restaurantId: String): NetworkResult<List<MenuItem>> {
        return try {
            val response = apiService.getMenuByRestaurant(restaurantId)
            
            if (response.isSuccessful && response.body() != null) {
                val menuItems = response.body()!!
                menuItemDao.insertMenuItems(menuItems)
                NetworkResult.Success(menuItems)
            } else {
                Timber.e("Fetch menu failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Failed to load menu.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Fetch menu error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
    
    // Fetch menu by category for a restaurant from API and update local database
    suspend fun refreshMenuByCategory(restaurantId: String, category: String): NetworkResult<List<MenuItem>> {
        return try {
            val response = apiService.getMenuByCategory(restaurantId, category)
            
            if (response.isSuccessful && response.body() != null) {
                val menuItems = response.body()!!
                menuItemDao.insertMenuItems(menuItems)
                NetworkResult.Success(menuItems)
            } else {
                Timber.e("Fetch menu by category failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Failed to load menu items.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Fetch menu by category error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
    
    // Fetch a single menu item by ID
    suspend fun getMenuItemById(id: String): NetworkResult<MenuItem> {
        // First try to get from local database
        val localMenuItem = menuItemDao.getMenuItemById(id)
        if (localMenuItem != null) {
            return NetworkResult.Success(localMenuItem)
        }
        
        // We don't have a specific API endpoint for a single menu item,
        // so we'd have to implement that or handle it differently in a real app.
        return NetworkResult.Error("Menu item not found.")
    }
    
    // Search menu items for a restaurant from API
    suspend fun searchMenuItemsFromApi(restaurantId: String, searchTerm: String): NetworkResult<List<MenuItem>> {
        return try {
            val response = apiService.searchMenuItems(restaurantId, searchTerm)
            
            if (response.isSuccessful && response.body() != null) {
                val menuItems = response.body()!!
                menuItemDao.insertMenuItems(menuItems)
                NetworkResult.Success(menuItems)
            } else {
                Timber.e("Search menu items failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Failed to search menu items.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Search menu items error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
}