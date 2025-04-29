package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.api.ApiService
import com.example.foodorderingapp.data.db.dao.OrderDao
import com.example.foodorderingapp.data.db.dao.OrderItemDao
import com.example.foodorderingapp.data.models.Order
import com.example.foodorderingapp.data.models.OrderItem
import com.example.foodorderingapp.util.NetworkResult
import com.example.foodorderingapp.util.handleApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Order-related operations
 * Handles data operations between the API and local database
 */
@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {

    /**
     * Create a new order and return the order ID
     */
    suspend fun createOrder(order: Order, orderItems: List<OrderItem>): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createOrder(order, orderItems)
                val result = handleApiResponse(response)
                
                if (result is NetworkResult.Success) {
                    // Save order and items to local database
                    orderDao.insertOrder(order)
                    orderItemDao.insertOrderItems(orderItems)
                }
                
                result
            } catch (e: Exception) {
                NetworkResult.Error("Network error: ${e.message}")
            }
        }
    }

    /**
     * Get order by ID from local database or API
     */
    suspend fun getOrderById(orderId: String): Order? {
        return withContext(Dispatchers.IO) {
            // First try to get from local database
            var order = orderDao.getOrderById(orderId)
            
            // If not found locally, try to fetch from API
            if (order == null) {
                try {
                    val response = apiService.getOrderById(orderId)
                    if (response.isSuccessful && response.body() != null) {
                        order = response.body()
                        // Save to local database
                        order?.let { orderDao.insertOrder(it) }
                    }
                } catch (e: Exception) {
                    // Handle network error
                    null
                }
            }
            
            order
        }
    }

    /**
     * Get order items for a specific order
     */
    suspend fun getOrderItems(orderId: String): List<OrderItem> {
        return withContext(Dispatchers.IO) {
            // First try to get from local database
            var items = orderItemDao.getOrderItems(orderId)
            
            // If not found locally, try to fetch from API
            if (items.isEmpty()) {
                try {
                    val response = apiService.getOrderItems(orderId)
                    if (response.isSuccessful && response.body() != null) {
                        items = response.body() ?: emptyList()
                        // Save to local database
                        if (items.isNotEmpty()) {
                            orderItemDao.insertOrderItems(items)
                        }
                    }
                } catch (e: Exception) {
                    // Handle network error
                    emptyList()
                }
            }
            
            items
        }
    }

    /**
     * Get all orders for current user as a Flow
     */
    fun getUserOrders(): Flow<List<Order>> = flow {
        // Emit from database first
        val localOrders = orderDao.getAllOrders()
        emit(localOrders)
        
        // Then try to fetch fresh data from API
        try {
            val response = apiService.getUserOrders()
            if (response.isSuccessful && response.body() != null) {
                val orders = response.body() ?: emptyList()
                // Update local database
                orderDao.insertOrders(orders)
                // Emit new data (will be fetched automatically through Room Flow)
            }
        } catch (e: Exception) {
            // Error already handled by emitting local data first
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get order tracking updates from API
     */
    suspend fun refreshOrderStatus(orderId: String): NetworkResult<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getOrderById(orderId)
                val result = handleApiResponse(response)
                
                if (result is NetworkResult.Success) {
                    // Update local database
                    result.data?.let { orderDao.insertOrder(it) }
                }
                
                result
            } catch (e: Exception) {
                NetworkResult.Error("Network error: ${e.message}")
            }
        }
    }
}