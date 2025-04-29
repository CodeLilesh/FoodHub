package com.example.foodorderingapp.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Order(
    val id: Int,
    
    @SerializedName("user_id")
    val userId: Int,
    
    @SerializedName("restaurant_id")
    val restaurantId: Int,
    
    @SerializedName("restaurant_name")
    val restaurantName: String,
    
    @SerializedName("total_price")
    val totalPrice: Double,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    
    @SerializedName("payment_method")
    val paymentMethod: String,
    
    val status: String,
    
    @SerializedName("special_instructions")
    val specialInstructions: String? = null,
    
    @SerializedName("created_at")
    val createdAt: Date,
    
    @SerializedName("updated_at")
    val updatedAt: Date? = null,
    
    // Additional fields
    val items: List<OrderItem> = emptyList(),
    
    @Transient
    val itemCount: Int = items.sumOf { it.quantity }
)

data class OrderItem(
    val id: Int,
    
    @SerializedName("order_id")
    val orderId: Int,
    
    @SerializedName("menu_item_id")
    val menuItemId: Int,
    
    val quantity: Int,
    
    @SerializedName("price_per_item")
    val pricePerItem: Double,
    
    // Additional fields from menu_items table
    val name: String,
    val description: String? = null,
    
    @SerializedName("image_url")
    val imageUrl: String? = null
)
