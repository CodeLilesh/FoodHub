package com.example.foodorderingapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.foodorderingapp.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem): Long
    
    @Update
    suspend fun updateCartItem(cartItem: CartItem)
    
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)
    
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>
    
    @Query("SELECT * FROM cart_items WHERE id = :id")
    suspend fun getCartItemById(id: Long): CartItem?
    
    @Query("SELECT * FROM cart_items WHERE menuItemId = :menuItemId")
    suspend fun getCartItemByMenuItemId(menuItemId: String): CartItem?
    
    @Query("SELECT * FROM cart_items WHERE restaurantId = :restaurantId")
    fun getCartItemsByRestaurant(restaurantId: String): Flow<List<CartItem>>
    
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>
    
    @Query("SELECT SUM(price * quantity) FROM cart_items")
    fun getCartTotalPrice(): Flow<Double?>
    
    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateCartItemQuantity(id: Long, quantity: Int)
    
    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: Long)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    @Query("DELETE FROM cart_items WHERE restaurantId = :restaurantId")
    suspend fun clearCartByRestaurant(restaurantId: String)
    
    @Query("SELECT DISTINCT restaurantId FROM cart_items LIMIT 1")
    suspend fun getCurrentRestaurantId(): String?
}