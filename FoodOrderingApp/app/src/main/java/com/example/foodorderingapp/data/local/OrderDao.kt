package com.example.foodorderingapp.data.local

import androidx.room.*
import com.example.foodorderingapp.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<Order>)
    
    @Update
    suspend fun updateOrder(order: Order)
    
    @Delete
    suspend fun deleteOrder(order: Order)
    
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: Int): Flow<Order?>
    
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY created_at DESC")
    fun getOrdersByUser(userId: Int): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE userId = :userId AND status = :status ORDER BY created_at DESC")
    fun getOrdersByStatus(userId: Int, status: String): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE restaurantId = :restaurantId ORDER BY created_at DESC")
    fun getOrdersByRestaurant(restaurantId: Int): Flow<List<Order>>
    
    @Query("DELETE FROM orders WHERE userId = :userId")
    suspend fun clearOrdersForUser(userId: Int)
    
    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}