package com.example.foodorderingapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodorderingapp.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(menuItems: List<MenuItem>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(menuItem: MenuItem)
    
    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId ORDER BY name ASC")
    fun getMenuByRestaurant(restaurantId: String): Flow<List<MenuItem>>
    
    @Query("SELECT * FROM menu_items WHERE id = :id")
    suspend fun getMenuItemById(id: String): MenuItem?
    
    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId AND category = :category ORDER BY name ASC")
    fun getMenuByCategory(restaurantId: String, category: String): Flow<List<MenuItem>>
    
    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId AND name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    fun searchMenuItems(restaurantId: String, searchTerm: String): Flow<List<MenuItem>>
    
    @Query("DELETE FROM menu_items WHERE restaurantId = :restaurantId")
    suspend fun clearRestaurantMenu(restaurantId: String)
    
    @Query("DELETE FROM menu_items")
    suspend fun clearAllMenuItems()
}