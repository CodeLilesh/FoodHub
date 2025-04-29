package com.example.foodorderingapp.data.local

import androidx.room.*
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
    
    @Query("SELECT * FROM cart_items WHERE id = :cartItemId")
    suspend fun getCartItemById(cartItemId: Int): CartItem?
    
    @Query("SELECT * FROM cart_items WHERE menuItemId = :menuItemId")
    suspend fun getCartItemByMenuItemId(menuItemId: Int): CartItem?
    
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>
    
    @Query("SELECT SUM(price * quantity) FROM cart_items")
    fun getCartTotal(): Flow<Double?>
    
    @Query("SELECT DISTINCT restaurantId FROM cart_items LIMIT 1")
    suspend fun getCartRestaurantId(): Int?
    
    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    suspend fun deleteCartItemById(cartItemId: Int)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    @Transaction
    suspend fun addOrUpdateCartItem(cartItem: CartItem): Long {
        val existingItem = getCartItemByMenuItemId(cartItem.menuItemId)
        
        return if (existingItem != null) {
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + cartItem.quantity,
                instructions = cartItem.instructions ?: existingItem.instructions
            )
            updateCartItem(updatedItem)
            existingItem.id.toLong()
        } else {
            insertCartItem(cartItem)
        }
    }
    
    @Transaction
    suspend fun validateRestaurantBeforeInsert(cartItem: CartItem): Boolean {
        val currentRestaurantId = getCartRestaurantId()
        
        // If cart is empty or item is from the same restaurant, proceed
        return currentRestaurantId == null || currentRestaurantId == cartItem.restaurantId
    }
}