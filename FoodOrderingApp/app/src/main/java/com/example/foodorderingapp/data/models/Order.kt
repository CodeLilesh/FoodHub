package com.example.foodorderingapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodorderingapp.data.socket.OrderStatus
import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Represents an order in the system
 * Used for both API communication and local database storage
 */
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("restaurant_id")
    val restaurantId: String,
    
    @SerializedName("restaurant_name")
    val restaurantName: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("total_amount")
    val totalAmount: Double,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    
    @SerializedName("created_at")
    val createdAt: Date,
    
    @SerializedName("estimated_delivery_time")
    val estimatedDeliveryTime: Int = -1, // in minutes
    
    @SerializedName("payment_method")
    val paymentMethod: String,
    
    @SerializedName("notes")
    val notes: String? = null
) {
    /**
     * Maps the string status from API to the OrderStatus enum
     */
    fun getOrderStatus(): OrderStatus {
        return when (status.lowercase()) {
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
    
    /**
     * Checks if the order is in an active state (not delivered or cancelled)
     */
    fun isActive(): Boolean {
        val orderStatus = getOrderStatus()
        return orderStatus != OrderStatus.DELIVERED && 
               orderStatus != OrderStatus.CANCELLED
    }
}