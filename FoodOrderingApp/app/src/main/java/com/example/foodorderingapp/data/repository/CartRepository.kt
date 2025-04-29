package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.data.local.CartDao
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

class CartRepository(
    private val cartDao: CartDao
) {
    // Get all cart items
    fun getAllCartItems(): Flow<List<CartItem>> = cartDao.getAllCartItems()
    
    // Get cart item count
    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()
    
    // Get cart total
    fun getCartTotal(): Flow<Double?> = cartDao.getCartTotal()
    
    // Add item to cart
    suspend fun addItemToCart(menuItem: MenuItem, quantity: Int, instructions: String? = null): NetworkResult<Long> {
        return try {
            // Check if the item is from the same restaurant as other items in cart
            val cartRestaurantId = cartDao.getCartRestaurantId()
            
            if (cartRestaurantId != null && cartRestaurantId != menuItem.restaurantId) {
                return NetworkResult.Error("Items in your cart are from a different restaurant. Clear cart before adding items from another restaurant.")
            }
            
            val cartItem = CartItem(
                menuItemId = menuItem.id,
                restaurantId = menuItem.restaurantId,
                name = menuItem.name,
                price = menuItem.price,
                quantity = quantity,
                instructions = instructions,
                imageUrl = menuItem.imageUrl
            )
            
            val id = cartDao.addOrUpdateCartItem(cartItem)
            NetworkResult.Success(id)
            
        } catch (e: Exception) {
            NetworkResult.Error("Failed to add item to cart: ${e.message}")
        }
    }
    
    // Update cart item quantity
    suspend fun updateCartItemQuantity(cartItemId: Int, newQuantity: Int): NetworkResult<Unit> {
        return try {
            val cartItem = cartDao.getCartItemById(cartItemId)
            
            if (cartItem != null) {
                if (newQuantity <= 0) {
                    cartDao.deleteCartItem(cartItem)
                } else {
                    val updatedItem = cartItem.copy(quantity = newQuantity)
                    cartDao.updateCartItem(updatedItem)
                }
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Cart item not found")
            }
            
        } catch (e: Exception) {
            NetworkResult.Error("Failed to update cart item: ${e.message}")
        }
    }
    
    // Remove item from cart
    suspend fun removeCartItem(cartItemId: Int): NetworkResult<Unit> {
        return try {
            cartDao.deleteCartItemById(cartItemId)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error("Failed to remove item from cart: ${e.message}")
        }
    }
    
    // Clear cart
    suspend fun clearCart(): NetworkResult<Unit> {
        return try {
            cartDao.clearCart()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error("Failed to clear cart: ${e.message}")
        }
    }
    
    // Check if cart has items from multiple restaurants
    suspend fun validateRestaurantId(restaurantId: Int): Boolean {
        val currentRestaurantId = cartDao.getCartRestaurantId()
        return currentRestaurantId == null || currentRestaurantId == restaurantId
    }
}