package com.example.foodorderingapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.data.model.Restaurant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for Context to create DataStore for Cart
private val Context.cartDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "cart_preferences"
)

@Singleton
class CartRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    // Keys for the preferences
    private object PreferencesKeys {
        val CART_ITEMS = stringPreferencesKey("cart_items")
        val RESTAURANT_ID = stringPreferencesKey("restaurant_id")
        val RESTAURANT_NAME = stringPreferencesKey("restaurant_name")
    }
    
    // Get cart items as Flow
    val cartItems: Flow<List<CartItem>> = context.cartDataStore.data.map { preferences ->
        val cartItemsJson = preferences[PreferencesKeys.CART_ITEMS] ?: "[]"
        val type = object : TypeToken<List<CartItem>>() {}.type
        gson.fromJson(cartItemsJson, type)
    }
    
    // Get restaurant ID
    val restaurantId: Flow<String?> = context.cartDataStore.data.map { preferences ->
        preferences[PreferencesKeys.RESTAURANT_ID]
    }
    
    // Get restaurant name
    val restaurantName: Flow<String?> = context.cartDataStore.data.map { preferences ->
        preferences[PreferencesKeys.RESTAURANT_NAME]
    }
    
    // Add item to cart
    suspend fun addItemToCart(menuItem: MenuItem, quantity: Int, restaurantId: String, restaurantName: String, specialInstructions: String? = null) {
        val currentItems = getCurrentCartItems()
        
        // Check if the item is from the same restaurant
        val currentRestaurantId = context.cartDataStore.data.map { preferences ->
            preferences[PreferencesKeys.RESTAURANT_ID]
        }.toString()
        
        if (currentRestaurantId.isNotEmpty() && currentRestaurantId != restaurantId) {
            // Clear cart if items are from a different restaurant
            clearCart()
        }
        
        // Create new cart item
        val newItem = CartItem(
            id = UUID.randomUUID().toString(),
            menuItemId = menuItem.id,
            name = menuItem.name,
            price = menuItem.price,
            quantity = quantity,
            imageUrl = menuItem.imageUrl,
            restaurantId = restaurantId,
            restaurantName = restaurantName,
            specialInstructions = specialInstructions
        )
        
        // Check if the item already exists in the cart
        val existingItemIndex = currentItems.indexOfFirst { it.menuItemId == menuItem.id }
        
        if (existingItemIndex != -1) {
            // Update existing item quantity
            val existingItem = currentItems[existingItemIndex]
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
            currentItems[existingItemIndex] = updatedItem
        } else {
            // Add new item
            currentItems.add(newItem)
        }
        
        // Save updated cart items
        saveCartItems(currentItems, restaurantId, restaurantName)
    }
    
    // Update cart item quantity
    suspend fun updateItemQuantity(cartItem: CartItem, newQuantity: Int) {
        val currentItems = getCurrentCartItems()
        val itemIndex = currentItems.indexOfFirst { it.id == cartItem.id }
        
        if (itemIndex != -1) {
            val updatedItem = cartItem.copy(quantity = newQuantity)
            currentItems[itemIndex] = updatedItem
            
            // Save updated cart items
            saveCartItems(currentItems, cartItem.restaurantId, cartItem.restaurantName)
        }
    }
    
    // Remove item from cart
    suspend fun removeItemFromCart(cartItem: CartItem) {
        val currentItems = getCurrentCartItems()
        currentItems.removeIf { it.id == cartItem.id }
        
        if (currentItems.isEmpty()) {
            // Clear cart if no items left
            clearCart()
        } else {
            // Save updated cart items
            saveCartItems(currentItems, cartItem.restaurantId, cartItem.restaurantName)
        }
    }
    
    // Clear cart
    suspend fun clearCart() {
        context.cartDataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    // Get current cart items
    private fun getCurrentCartItems(): MutableList<CartItem> {
        val cartItemsJson = runCatching {
            val flow = context.cartDataStore.data.map { preferences ->
                preferences[PreferencesKeys.CART_ITEMS] ?: "[]"
            }
            flow.toString()
        }.getOrDefault("[]")
        
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson<List<CartItem>>(cartItemsJson, type).toMutableList()
    }
    
    // Save cart items
    private suspend fun saveCartItems(items: List<CartItem>, restaurantId: String, restaurantName: String) {
        context.cartDataStore.edit { preferences ->
            preferences[PreferencesKeys.CART_ITEMS] = gson.toJson(items)
            preferences[PreferencesKeys.RESTAURANT_ID] = restaurantId
            preferences[PreferencesKeys.RESTAURANT_NAME] = restaurantName
        }
    }
    
    // Calculate cart total
    fun calculateCartTotal(items: List<CartItem>): Double {
        return items.sumOf { it.price * it.quantity }
    }
}