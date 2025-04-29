package com.example.foodorderingapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
    
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: String): Order?
    
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUserId(userId: String): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE restaurantId = :restaurantId ORDER BY createdAt DESC")
    fun getOrdersByRestaurantId(restaurantId: String): Flow<List<Order>>
    
    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: String, status: String)
    
    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}