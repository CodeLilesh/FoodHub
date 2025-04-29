package com.example.foodorderingapp.data.local

import androidx.room.*
import com.example.foodorderingapp.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(menuItem: MenuItem)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(menuItems: List<MenuItem>)
    
    @Update
    suspend fun updateMenuItem(menuItem: MenuItem)
    
    @Delete
    suspend fun deleteMenuItem(menuItem: MenuItem)
    
    @Query("SELECT * FROM menu_items WHERE id = :menuItemId")
    fun getMenuItemById(menuItemId: Int): Flow<MenuItem?>
    
    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId")
    fun getMenuItemsByRestaurant(restaurantId: Int): Flow<List<MenuItem>>
    
    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId AND category = :category")
    fun getMenuItemsByCategory(restaurantId: Int, category: String): Flow<List<MenuItem>>
    
    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId AND is_vegetarian = 1")
    fun getVegetarianItems(restaurantId: Int): Flow<List<MenuItem>>
    
    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId AND name LIKE '%' || :searchTerm || '%'")
    fun searchMenuItems(restaurantId: Int, searchTerm: String): Flow<List<MenuItem>>
    
    @Query("SELECT DISTINCT category FROM menu_items WHERE restaurantId = :restaurantId")
    fun getCategoriesForRestaurant(restaurantId: Int): Flow<List<String>>
    
    @Query("DELETE FROM menu_items WHERE restaurantId = :restaurantId")
    suspend fun clearMenuItemsForRestaurant(restaurantId: Int)
    
    @Query("DELETE FROM menu_items")
    suspend fun clearMenuItems()
}