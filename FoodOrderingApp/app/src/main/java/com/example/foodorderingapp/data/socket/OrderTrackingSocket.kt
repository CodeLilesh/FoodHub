package com.example.foodorderingapp.data.socket

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.*
import okio.ByteString

/**
 * This class manages the WebSocket connection for real-time order tracking
 */
@Singleton
class OrderTrackingSocket @Inject constructor() {
    
    private val TAG = "OrderTrackingSocket"
    
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Tracking states for order status
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _orderStatus = MutableStateFlow<OrderStatusUpdate?>(null)
    val orderStatus: StateFlow<OrderStatusUpdate?> = _orderStatus.asStateFlow()
    
    /**
     * Connect to WebSocket server for specific order tracking
     */
    fun connectToOrderTracking(orderId: String) {
        if (webSocket != null) {
            disconnectFromTracking()
        }
        
        _connectionState.value = ConnectionState.CONNECTING
        
        val request = Request.Builder()
            .url("${BASE_WS_URL}/ws/orders/$orderId/track")
            .build()
            
        webSocket = client.newWebSocket(request, createWebSocketListener())
        
        // Send authentication message if needed
        // webSocket?.send("{ \"type\": \"auth\", \"token\": \"$authToken\" }")
    }
    
    /**
     * Disconnect from WebSocket
     */
    fun disconnectFromTracking() {
        webSocket?.close(1000, "User closing connection")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
        _orderStatus.value = null
    }
    
    /**
     * WebSocket event listener
     */
    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connection opened")
                _connectionState.value = ConnectionState.CONNECTED
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "WebSocket message received: $text")
                try {
                    val json = JSONObject(text)
                    when (json.optString("type")) {
                        "status_update" -> {
                            val status = json.optString("status")
                            val estimatedTime = json.optInt("estimatedMinutes", -1)
                            val message = json.optString("message", "")
                            
                            _orderStatus.value = OrderStatusUpdate(
                                status = mapToOrderStatus(status),
                                estimatedDeliveryTimeMinutes = estimatedTime,
                                message = message
                            )
                        }
                        "error" -> {
                            Log.e(TAG, "WebSocket error: ${json.optString("message")}")
                            _connectionState.value = ConnectionState.ERROR
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing WebSocket message", e)
                }
            }
            
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "WebSocket binary message received")
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code / $reason")
                webSocket.close(1000, null)
                _connectionState.value = ConnectionState.DISCONNECTED
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code / $reason")
                _connectionState.value = ConnectionState.DISCONNECTED
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure", t)
                _connectionState.value = ConnectionState.ERROR
            }
        }
    }
    
    /**
     * Map server status string to OrderStatus enum
     */
    private fun mapToOrderStatus(statusString: String): OrderStatus {
        return when (statusString.lowercase()) {
            "received" -> OrderStatus.RECEIVED
            "preparing" -> OrderStatus.PREPARING
            "ready" -> OrderStatus.READY
            "picked_up" -> OrderStatus.PICKED_UP
            "in_transit" -> OrderStatus.IN_TRANSIT
            "delivered" -> OrderStatus.DELIVERED
            "cancelled" -> OrderStatus.CANCELLED
            else -> OrderStatus.UNKNOWN
        }
    }
    
    companion object {
        // Replace with your actual WebSocket URL
        private const val BASE_WS_URL = "ws://your-api-domain.com" 
    }
}

/**
 * Represents the connection state of the WebSocket
 */
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

/**
 * Represents the status of an order
 */
enum class OrderStatus {
    UNKNOWN,
    RECEIVED,
    PREPARING,
    READY,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}

/**
 * Data class for order status updates
 */
data class OrderStatusUpdate(
    val status: OrderStatus,
    val estimatedDeliveryTimeMinutes: Int = -1,
    val message: String = ""
)