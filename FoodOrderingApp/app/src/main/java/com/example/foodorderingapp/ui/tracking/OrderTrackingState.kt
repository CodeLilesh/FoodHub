package com.example.foodorderingapp.ui.tracking

import com.example.foodorderingapp.data.model.DriverInfo
import com.example.foodorderingapp.data.model.OrderItem
import com.example.foodorderingapp.data.model.OrderStatusHistory
import com.example.foodorderingapp.data.model.RestaurantInfo
import com.example.foodorderingapp.data.socket.SocketConnectionState
import java.util.Date

/**
 * Represents the UI state for the OrderTrackingFragment
 */
data class OrderTrackingUIState(
    val isLoading: Boolean = false,
    val orderDetails: OrderTrackingDetails? = null,
    val driverInfo: DriverInfo? = null,
    val restaurantInfo: RestaurantInfo? = null,
    val connectionState: SocketConnectionState = SocketConnectionState.DISCONNECTED,
    val error: String? = null
)

/**
 * Represents the detailed information about an order
 */
data class OrderTrackingDetails(
    val orderId: String,
    val status: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val address: String,
    val createdAt: Date,
    val estimatedDeliveryTime: Date?,
    val statusHistory: Map<String, Date?> // maps status to timestamp
) {
    // Helper properties for UI
    val hasDriverAssigned: Boolean
        get() = status == "OUT_FOR_DELIVERY" || status == "DELIVERED"

    val canBeCancelled: Boolean
        get() = status != "DELIVERED" && status != "CANCELLED"

    // Get formatted timestamp for a specific status
    fun getStatusTimestamp(status: String): String? {
        return statusHistory[status]?.toString()
    }
}

/**
 * Factory to convert from API/WebSocket model to UI model
 */
object OrderTrackingDetailsFactory {
    fun fromTrackingResponse(
        response: com.example.foodorderingapp.data.model.OrderTrackingResponse
    ): OrderTrackingDetails {
        // Create status history map from status history list
        val statusHistoryMap = response.statusHistory.associate { 
            it.status to it.timestamp 
        }
        
        return OrderTrackingDetails(
            orderId = response.id,
            status = response.status,
            items = response.items,
            totalPrice = response.total,
            address = response.address,
            createdAt = response.createdAt,
            estimatedDeliveryTime = response.estimatedDeliveryTime,
            statusHistory = statusHistoryMap
        )
    }
}