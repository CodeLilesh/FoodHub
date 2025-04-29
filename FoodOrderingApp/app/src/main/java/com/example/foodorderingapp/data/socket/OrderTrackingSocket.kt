package com.example.foodorderingapp.data.socket

import android.util.Log
import com.example.foodorderingapp.data.model.DriverLocation
import com.example.foodorderingapp.data.model.OrderTrackingUpdate
import com.example.foodorderingapp.util.Constants
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Socket connection states
 */
enum class SocketConnectionState {
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    ERROR
}

/**
 * Class responsible for managing WebSocket connections for real-time order tracking
 */
class OrderTrackingSocket(private val authToken: String) {
    private val TAG = "OrderTrackingSocket"
    private val gson = Gson()
    private lateinit var webSocket: WebSocket
    private var isConnected = false
    
    // Connection state flow
    private val _connectionState = MutableStateFlow(SocketConnectionState.DISCONNECTED)
    val connectionState: StateFlow<SocketConnectionState> = _connectionState.asStateFlow()
    
    // Order updates flow
    private val _orderUpdates = MutableStateFlow<OrderTrackingUpdate?>(null)
    val orderUpdates: StateFlow<OrderTrackingUpdate?> = _orderUpdates.asStateFlow()
    
    // Driver location updates flow
    private val _driverLocationUpdates = MutableStateFlow<DriverLocation?>(null)
    val driverLocationUpdates: StateFlow<DriverLocation?> = _driverLocationUpdates.asStateFlow()
    
    // Error message flow
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Connect to the WebSocket server
     */
    fun connect() {
        if (isConnected) return
        
        _connectionState.value = SocketConnectionState.CONNECTING
        
        val client = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val request = Request.Builder()
            .url(Constants.WS_BASE_URL)
            .addHeader("Authorization", "Bearer $authToken")
            .build()
            
        webSocket = client.newWebSocket(request, createWebSocketListener())
        
        Log.d(TAG, "Connecting to WebSocket server...")
    }

    /**
     * Disconnect from the WebSocket server
     */
    fun disconnect() {
        if (!isConnected) return
        
        webSocket.close(1000, "Closing connection")
        isConnected = false
        _connectionState.value = SocketConnectionState.DISCONNECTED
        
        Log.d(TAG, "Disconnected from WebSocket server")
    }

    /**
     * Subscribe to order updates for a specific order
     */
    fun subscribeToOrder(orderId: String) {
        if (!isConnected) {
            Log.e(TAG, "Cannot subscribe: Not connected to WebSocket server")
            return
        }
        
        val subscribeMessage = JSONObject().apply {
            put("event", Constants.SOCKET_EVENT_SUBSCRIBE_TO_ORDER)
            put("data", JSONObject().apply {
                put("order_id", orderId)
            })
        }
        
        webSocket.send(subscribeMessage.toString())
        Log.d(TAG, "Subscribed to order updates for order: $orderId")
    }

    /**
     * Create a WebSocket listener to handle socket events
     */
    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connection established")
                CoroutineScope(Dispatchers.Main).launch {
                    isConnected = true
                    _connectionState.value = SocketConnectionState.CONNECTED
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message received: $text")
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val jsonObject = JSONObject(text)
                        val event = jsonObject.getString("event")
                        val data = jsonObject.getJSONObject("data")

                        when (event) {
                            Constants.SOCKET_EVENT_ORDER_UPDATE -> {
                                val update = gson.fromJson(data.toString(), OrderTrackingUpdate::class.java)
                                _orderUpdates.value = update
                            }
                            Constants.SOCKET_EVENT_DRIVER_LOCATION -> {
                                val location = gson.fromJson(data.toString(), DriverLocation::class.java)
                                _driverLocationUpdates.value = location
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing message: ${e.message}")
                        _errorMessage.value = "Failed to parse server message"
                    }
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code, $reason")
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code, $reason")
                CoroutineScope(Dispatchers.Main).launch {
                    isConnected = false
                    _connectionState.value = SocketConnectionState.DISCONNECTED
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    isConnected = false
                    _connectionState.value = SocketConnectionState.ERROR
                    _errorMessage.value = t.message ?: "Connection error"
                }
            }
        }
    }
}