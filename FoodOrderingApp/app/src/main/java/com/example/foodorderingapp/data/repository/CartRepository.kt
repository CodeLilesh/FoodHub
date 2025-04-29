package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.data.local.CartDao
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    
    // Get all cart items
    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
    }
    
    // Get cart items for a specific restaurant
    fun getCartItemsByRestaurant(restaurantId: String): Flow<List<CartItem>> {
        return cartDao.getCartItemsByRestaurant(restaurantId)
    }
    
    // Get cart item count
    fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCount()
    }
    
    // Get cart total price
    fun getCartTotalPrice(): Flow<Double?> {
        return cartDao.getCartTotalPrice()
    }
    
    // Get current restaurant ID in cart
    suspend fun getCurrentRestaurantId(): String? {
        return cartDao.getCurrentRestaurantId()
    }
    
    // Add item to cart
    suspend fun addToCart(menuItem: MenuItem, quantity: Int, notes: String? = null): NetworkResult<Long> {
        return try {
            // Check if there's already a different restaurant in the cart
            val currentRestaurantId = cartDao.getCurrentRestaurantId()
            if (currentRestaurantId != null && currentRestaurantId != menuItem.restaurantId) {
                return NetworkResult.Error("Your cart contains items from a different restaurant. Clear your cart to add items from this restaurant.")
            }
            
            // Check if the item is already in the cart
            val existingItem = cartDao.getCartItemByMenuItemId(menuItem.id)
            
            if (existingItem != null) {
                // Update existing item
                val updatedQuantity = existingItem.quantity + quantity
                cartDao.updateCartItemQuantity(existingItem.id, updatedQuantity)
                NetworkResult.Success(existingItem.id)
            } else {
                // Create new cart item
                val cartItem = CartItem(
                    menuItemId = menuItem.id,
                    quantity = quantity,
                    name = menuItem.name,
                    price = menuItem.price,
                    imageUrl = menuItem.imageUrl,
                    restaurantId = menuItem.restaurantId,
                    notes = notes
                )
                val insertedId = cartDao.insertCartItem(cartItem)
                NetworkResult.Success(insertedId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Add to cart error")
            NetworkResult.Error("Failed to add item to cart. Please try again.")
        }
    }
    
    // Update cart item quantity
    suspend fun updateCartItemQuantity(cartItemId: Long, quantity: Int): NetworkResult<Unit> {
        return try {
            if (quantity <= 0) {
                cartDao.deleteCartItemById(cartItemId)
            } else {
                cartDao.updateCartItemQuantity(cartItemId, quantity)
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Update cart item quantity error")
            NetworkResult.Error("Failed to update cart. Please try again.")
        }
    }
    
    // Remove item from cart
    suspend fun removeFromCart(cartItemId: Long): NetworkResult<Unit> {
        return try {
            cartDao.deleteCartItemById(cartItemId)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Remove from cart error")
            NetworkResult.Error("Failed to remove item from cart. Please try again.")
        }
    }
    
    // Clear cart
    suspend fun clearCart(): NetworkResult<Unit> {
        return try {
            cartDao.clearCart()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Clear cart error")
            NetworkResult.Error("Failed to clear cart. Please try again.")
        }
    }
    
    // Clear cart for specific restaurant
    suspend fun clearCartByRestaurant(restaurantId: String): NetworkResult<Unit> {
        return try {
            cartDao.clearCartByRestaurant(restaurantId)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Clear cart by restaurant error")
            NetworkResult.Error("Failed to clear cart. Please try again.")
        }
    }
}