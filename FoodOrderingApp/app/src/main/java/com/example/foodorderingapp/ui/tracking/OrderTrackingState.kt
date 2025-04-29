package com.example.foodorderingapp.ui.tracking

import com.example.foodorderingapp.data.models.Order
import com.example.foodorderingapp.data.models.OrderItem
import com.example.foodorderingapp.data.socket.ConnectionState
import com.example.foodorderingapp.data.socket.OrderStatus

/**
 * State class for the Order Tracking screen
 * Handles different UI states: loading, error, and connected with live updates
 */
data class OrderTrackingState(
    val isLoading: Boolean = true,
    val order: Order? = null,
    val orderItems: List<OrderItem> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val orderStatus: OrderStatus = OrderStatus.UNKNOWN,
    val estimatedDeliveryTimeMinutes: Int = -1,
    val statusMessage: String = "",
    val error: String? = null
) {
    /**
     * Returns the current phase of the order in the delivery process
     * Used to highlight the current step in the tracking UI
     */
    val currentPhase: TrackingPhase
        get() = when (orderStatus) {
            OrderStatus.RECEIVED -> TrackingPhase.ORDER_PLACED
            OrderStatus.PREPARING -> TrackingPhase.PREPARING
            OrderStatus.READY -> TrackingPhase.READY
            OrderStatus.PICKED_UP, OrderStatus.IN_TRANSIT -> TrackingPhase.ON_THE_WAY
            OrderStatus.DELIVERED -> TrackingPhase.DELIVERED
            OrderStatus.CANCELLED -> TrackingPhase.CANCELLED
            OrderStatus.UNKNOWN -> if (error != null) TrackingPhase.ERROR else TrackingPhase.ORDER_PLACED
        }
    
    /**
     * Returns true if the app is currently connected to the WebSocket
     */
    val isConnected: Boolean
        get() = connectionState == ConnectionState.CONNECTED
    
    /**
     * Returns a formatted estimated delivery time string
     */
    val estimatedTimeText: String
        get() = if (estimatedDeliveryTimeMinutes > 0) {
            "$estimatedDeliveryTimeMinutes minutes"
        } else {
            "Calculating..."
        }
    
    /**
     * Returns true if there was an error connecting to the WebSocket or fetching the order
     */
    val hasError: Boolean
        get() = error != null || connectionState == ConnectionState.ERROR
}

/**
 * Enum representing the different phases of order tracking
 * Used to control the UI state of the tracking screen
 */
enum class TrackingPhase {
    ORDER_PLACED,
    PREPARING,
    READY,
    ON_THE_WAY,
    DELIVERED,
    CANCELLED,
    ERROR
}