package com.example.foodorderingapp.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Model class to represent an order tracking update response from the server
 */
data class OrderTrackingUpdate(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: Date,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: Date?,
    @SerializedName("driver") val driver: DriverInfo?
)

/**
 * Model class to represent driver information
 */
data class DriverInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("location") val location: DriverLocation?
)

/**
 * Model class to represent driver location coordinates
 */
data class DriverLocation(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

/**
 * Model class to represent order status history
 */
data class OrderStatusHistory(
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: Date
)

/**
 * Model class to represent a complete order tracking response
 */
data class OrderTrackingResponse(
    @SerializedName("id") val id: String,
    @SerializedName("restaurant") val restaurant: RestaurantInfo,
    @SerializedName("items") val items: List<OrderItem>,
    @SerializedName("status") val status: String,
    @SerializedName("total") val total: Double,
    @SerializedName("address") val address: String,
    @SerializedName("driver") val driver: DriverInfo?,
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: Date?,
    @SerializedName("status_history") val statusHistory: List<OrderStatusHistory>
)

/**
 * Simplified restaurant info for order tracking
 */
data class RestaurantInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("image") val image: String?
)

/**
 * Model class to represent an order item with quantity
 */
data class OrderItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int
)