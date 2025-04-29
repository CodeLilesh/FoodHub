package com.example.foodorderingapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodorderingapp.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<Restaurant>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: Restaurant)
    
    @Query("SELECT * FROM restaurants ORDER BY name ASC")
    fun getAllRestaurants(): Flow<List<Restaurant>>
    
    @Query("SELECT * FROM restaurants WHERE id = :id")
    suspend fun getRestaurantById(id: String): Restaurant?
    
    @Query("SELECT * FROM restaurants WHERE category = :category ORDER BY name ASC")
    fun getRestaurantsByCategory(category: String): Flow<List<Restaurant>>
    
    @Query("SELECT * FROM restaurants WHERE name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    fun searchRestaurants(searchTerm: String): Flow<List<Restaurant>>
    
    @Query("DELETE FROM restaurants")
    suspend fun clearRestaurants()
}