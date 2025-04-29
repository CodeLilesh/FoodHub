package com.example.foodorderingapp.data.repository

import android.content.Context
import com.example.foodorderingapp.data.api.CreateOrderRequest
import com.example.foodorderingapp.data.api.OrderItemRequest
import com.example.foodorderingapp.data.api.RetrofitClient
import com.example.foodorderingapp.data.models.CartItem
import com.example.foodorderingapp.data.models.Order
import com.example.foodorderingapp.utils.Result
import com.example.foodorderingapp.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository(private val context: Context) {
    
    private val apiService = RetrofitClient.apiService
    private val sessionManager = SessionManager(context)
    
    suspend fun getUserOrders(): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            val response = apiService.getUserOrders(token)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val orders = response.body()?.data ?: emptyList()
                return@withContext Result.Success(orders)
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get orders"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun getOrderById(orderId: Int): Result<Order> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            val response = apiService.getOrderById(token, orderId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()?.data
                if (order != null) {
                    return@withContext Result.Success(order)
                }
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get order details"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun createOrder(
        restaurantId: Int,
        cartItems: List<CartItem>,
        deliveryAddress: String,
        paymentMethod: String,
        specialInstructions: String?
    ): Result<Order> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            // Convert cart items to order items
            val orderItems = cartItems.map { cartItem ->
                OrderItemRequest(
                    menu_item_id = cartItem.menuItemId,
                    quantity = cartItem.quantity
                )
            }
            
            val orderRequest = CreateOrderRequest(
                restaurant_id = restaurantId,
                items = orderItems,
                delivery_address = deliveryAddress,
                payment_method = paymentMethod,
                special_instructions = specialInstructions
            )
            
            val response = apiService.createOrder(token, orderRequest)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()?.data
                if (order != null) {
                    return@withContext Result.Success(order)
                }
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to create order"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
}
