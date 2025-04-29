package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.data.local.CartDao
import com.example.foodorderingapp.data.local.OrderDao
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.Order
import com.example.foodorderingapp.data.remote.ApiService
import com.example.foodorderingapp.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.IOException

class OrderRepository(
    private val apiService: ApiService,
    private val orderDao: OrderDao,
    private val cartDao: CartDao
) {
    // Get user orders from local database
    fun getUserOrders(userId: Int): Flow<List<Order>> = orderDao.getOrdersByUser(userId)
    
    // Get order by ID from local database
    fun getOrderById(orderId: Int): Flow<Order?> = orderDao.getOrderById(orderId)
    
    // Get orders by status from local database
    fun getOrdersByStatus(userId: Int, status: String): Flow<List<Order>> = 
        orderDao.getOrdersByStatus(userId, status)
    
    // Place a new order
    suspend fun placeOrder(
        userId: Int,
        restaurantId: Int,
        deliveryAddress: String,
        paymentMethod: String,
        specialInstructions: String? = null
    ): NetworkResult<Order> {
        return try {
            // Get cart items
            val cartItems = cartDao.getAllCartItems().first()
            
            if (cartItems.isEmpty()) {
                return NetworkResult.Error("Your cart is empty")
            }
            
            // Calculate total amount
            val subtotal = cartItems.sumOf { it.price * it.quantity }
            val deliveryFee = 5.0 // Fixed delivery fee or fetch from restaurant
            val totalAmount = subtotal + deliveryFee
            
            // Create order data
            val orderItems = cartItems.map { cartItem ->
                mapOf(
                    "menu_item_id" to cartItem.menuItemId,
                    "name" to cartItem.name,
                    "price" to cartItem.price,
                    "quantity" to cartItem.quantity,
                    "special_instructions" to cartItem.instructions
                )
            }
            
            val orderData = mapOf(
                "user_id" to userId,
                "restaurant_id" to restaurantId,
                "items" to orderItems,
                "total_amount" to totalAmount,
                "delivery_address" to deliveryAddress,
                "payment_method" to paymentMethod,
                "delivery_fee" to deliveryFee,
                "special_instructions" to specialInstructions
            )
            
            // Place order via API
            val response = apiService.createOrder(orderData as Map<String, Any>)
            
            if (response.isSuccessful) {
                val order = response.body()
                if (order != null) {
                    // Save order to local database
                    orderDao.insertOrder(order)
                    
                    // Clear cart
                    cartDao.clearCart()
                    
                    return NetworkResult.Success(order)
                }
            }
            
            NetworkResult.Error("Failed to place order: ${response.message()}")
            
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Fetch user orders from API and update local database
    suspend fun fetchAndCacheUserOrders(userId: Int): NetworkResult<List<Order>> {
        return try {
            val response = apiService.getUserOrders()
            
            if (response.isSuccessful) {
                val orders = response.body()
                if (!orders.isNullOrEmpty()) {
                    orderDao.insertOrders(orders)
                    NetworkResult.Success(orders)
                } else {
                    NetworkResult.Error("No orders found")
                }
            } else {
                NetworkResult.Error("Failed to fetch orders: ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Fetch order details from API and update local database
    suspend fun fetchAndCacheOrderById(orderId: Int): NetworkResult<Order> {
        return try {
            val response = apiService.getOrderById(orderId)
            
            if (response.isSuccessful) {
                val order = response.body()
                if (order != null) {
                    orderDao.insertOrder(order)
                    NetworkResult.Success(order)
                } else {
                    NetworkResult.Error("Order not found")
                }
            } else {
                NetworkResult.Error("Failed to fetch order: ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
}