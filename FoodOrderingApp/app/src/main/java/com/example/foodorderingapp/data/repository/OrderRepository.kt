package com.example.foodorderingapp.data.repository

import android.util.Log
import com.example.foodorderingapp.api.ApiService
import com.example.foodorderingapp.data.model.OrderTrackingResponse
import com.example.foodorderingapp.data.model.OrderTrackingUpdate
import com.example.foodorderingapp.data.socket.OrderTrackingSocket
import com.example.foodorderingapp.data.socket.SocketConnectionState
import com.example.foodorderingapp.util.Constants
import com.example.foodorderingapp.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class for handling order-related operations
 */
@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    private val TAG = "OrderRepository"
    
    // Cached tracking socket instance
    private var orderTrackingSocket: OrderTrackingSocket? = null
    
    /**
     * Get order tracking data from API
     */
    suspend fun getOrderTracking(orderId: String): Flow<NetworkResult<OrderTrackingResponse>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            val token = sessionManager.getAuthToken() ?: throw Exception("Authentication required")
            val response = apiService.getOrderTracking("Bearer $token", orderId)
            
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                } ?: emit(NetworkResult.Error("Empty response body"))
            } else {
                emit(NetworkResult.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getOrderTracking: ${e.message}")
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }
    
    /**
     * Connect to the WebSocket for real-time order tracking
     */
    fun connectToOrderTracking(): OrderTrackingSocket {
        // Return existing instance if already connected
        orderTrackingSocket?.let {
            if (it.connectionState.value == SocketConnectionState.CONNECTED) {
                return it
            }
        }
        
        // Create new socket instance
        val token = sessionManager.getAuthToken() ?: throw IllegalStateException("Authentication required")
        val socket = OrderTrackingSocket(token)
        orderTrackingSocket = socket
        socket.connect()
        
        return socket
    }
    
    /**
     * Subscribe to order updates for a specific order
     */
    fun subscribeToOrderUpdates(orderId: String) {
        val socket = getOrCreateSocket()
        if (socket.connectionState.value == SocketConnectionState.CONNECTED) {
            socket.subscribeToOrder(orderId)
        } else {
            Log.e(TAG, "Cannot subscribe: Socket not connected")
        }
    }
    
    /**
     * Disconnect from WebSocket
     */
    fun disconnectFromOrderTracking() {
        orderTrackingSocket?.disconnect()
    }
    
    /**
     * Cancel an order
     */
    suspend fun cancelOrder(orderId: String): Flow<NetworkResult<Boolean>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            val token = sessionManager.getAuthToken() ?: throw Exception("Authentication required")
            val response = apiService.cancelOrder("Bearer $token", orderId)
            
            if (response.isSuccessful) {
                emit(NetworkResult.Success(true))
            } else {
                emit(NetworkResult.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "cancelOrder: ${e.message}")
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }
    
    /**
     * Get or create a WebSocket instance
     */
    private fun getOrCreateSocket(): OrderTrackingSocket {
        return orderTrackingSocket ?: connectToOrderTracking()
    }
}