package com.example.foodorderingapp.data.local.dao

import androidx.room.*
import com.example.foodorderingapp.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>
    
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemsCount(): Flow<Int>
    
    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getTotalItemsCount(): Flow<Int>
    
    @Query("SELECT SUM(price_per_item * quantity) FROM cart_items")
    fun getTotalPrice(): Flow<Double?>
    
    @Query("SELECT * FROM cart_items WHERE menu_item_id = :menuItemId LIMIT 1")
    suspend fun getCartItemByMenuItemId(menuItemId: Int): CartItemEntity?
    
    @Query("SELECT * FROM cart_items WHERE id = :id LIMIT 1")
    suspend fun getCartItemById(id: Int): CartItemEntity?
    
    @Query("SELECT restaurant_id FROM cart_items LIMIT 1")
    suspend fun getRestaurantIdIfExists(): Int?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartItemEntity): Long
    
    @Update
    suspend fun update(cartItem: CartItemEntity): Int
    
    @Query("UPDATE cart_items SET quantity = :quantity WHERE menu_item_id = :menuItemId")
    suspend fun updateQuantity(menuItemId: Int, quantity: Int)
    
    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantityById(id: Int, quantity: Int)
    
    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteById(id: Int)
    
    @Delete
    suspend fun delete(cartItem: CartItemEntity)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
