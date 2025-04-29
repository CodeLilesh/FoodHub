package com.example.foodorderingapp.data.repository

import android.content.Context
import com.example.foodorderingapp.data.local.AppDatabase
import com.example.foodorderingapp.data.local.entity.CartItemEntity
import com.example.foodorderingapp.data.models.CartItem
import com.example.foodorderingapp.data.models.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepository(context: Context) {
    
    private val cartDao = AppDatabase.getInstance(context).cartDao()
    
    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { it.toCartItem() }
        }
    }
    
    fun getCartItemsCount(): Flow<Int> {
        return cartDao.getCartItemsCount()
    }
    
    suspend fun addToCart(restaurantId: Int, menuItem: MenuItem, quantity: Int = 1): Long = withContext(Dispatchers.IO) {
        // Check if restaurant ID matches existing cart items
        val existingRestaurantId = cartDao.getRestaurantIdIfExists()
        
        if (existingRestaurantId != null && existingRestaurantId != restaurantId) {
            // Clear cart if adding items from a different restaurant
            cartDao.clearCart()
        }
        
        // Check if item already exists in cart
        val existingItem = cartDao.getCartItemByMenuItemId(menuItem.id)
        
        if (existingItem != null) {
            // Update quantity of existing item
            val updatedQuantity = existingItem.quantity + quantity
            cartDao.updateQuantity(existingItem.menuItemId, updatedQuantity)
            return@withContext existingItem.id.toLong()
        } else {
            // Add new item to cart
            val cartItemEntity = CartItemEntity(
                id = 0, // Room will auto-generate ID
                restaurantId = restaurantId,
                menuItemId = menuItem.id,
                name = menuItem.name,
                description = menuItem.description,
                imageUrl = menuItem.imageUrl,
                pricePerItem = menuItem.price,
                quantity = quantity,
                isVegetarian = menuItem.isVegetarian
            )
            
            return@withContext cartDao.insert(cartItemEntity)
        }
    }
    
    suspend fun updateCartItemQuantity(cartItemId: Int, newQuantity: Int) = withContext(Dispatchers.IO) {
        if (newQuantity <= 0) {
            // Remove item if quantity is 0 or negative
            cartDao.deleteById(cartItemId)
        } else {
            // Update quantity
            cartDao.updateQuantityById(cartItemId, newQuantity)
        }
    }
    
    suspend fun removeFromCart(cartItemId: Int) = withContext(Dispatchers.IO) {
        cartDao.deleteById(cartItemId)
    }
    
    suspend fun clearCart() = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }
    
    suspend fun getRestaurantId(): Int? = withContext(Dispatchers.IO) {
        return@withContext cartDao.getRestaurantIdIfExists()
    }
}
