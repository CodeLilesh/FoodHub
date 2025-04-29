package com.example.foodorderingapp.data.local

import androidx.room.*
import com.example.foodorderingapp.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: Restaurant)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<Restaurant>)
    
    @Update
    suspend fun updateRestaurant(restaurant: Restaurant)
    
    @Delete
    suspend fun deleteRestaurant(restaurant: Restaurant)
    
    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): Flow<List<Restaurant>>
    
    @Query("SELECT * FROM restaurants WHERE id = :restaurantId")
    fun getRestaurantById(restaurantId: Int): Flow<Restaurant?>
    
    @Query("SELECT * FROM restaurants WHERE cuisine_type = :cuisineType")
    fun getRestaurantsByCuisine(cuisineType: String): Flow<List<Restaurant>>
    
    @Query("SELECT * FROM restaurants WHERE rating >= :minRating")
    fun getRestaurantsByRating(minRating: Double): Flow<List<Restaurant>>
    
    @Query("SELECT * FROM restaurants WHERE name LIKE '%' || :searchTerm || '%'")
    fun searchRestaurants(searchTerm: String): Flow<List<Restaurant>>
    
    @Query("DELETE FROM restaurants")
    suspend fun clearRestaurants()
}