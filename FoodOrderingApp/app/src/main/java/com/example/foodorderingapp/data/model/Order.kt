package com.example.foodorderingapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.foodorderingapp.data.local.Converters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "orders")
@TypeConverters(Converters::class)
data class Order(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("restaurant_id")
    val restaurantId: String,
    
    @SerializedName("restaurant_name")
    val restaurantName: String,
    
    val items: List<OrderItem>,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    
    @SerializedName("contact_phone")
    val contactPhone: String,
    
    val status: String,  // PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    
    @SerializedName("total_amount")
    val totalAmount: Double,
    
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    
    @SerializedName("payment_method")
    val paymentMethod: String,
    
    @SerializedName("payment_status")
    val paymentStatus: String,  // PENDING, COMPLETED, FAILED
    
    val notes: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class OrderItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val notes: String? = null
)

data class OrderRequest(
    @SerializedName("restaurant_id")
    val restaurantId: String,
    val items: List<OrderItemRequest>,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("contact_phone")
    val contactPhone: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    val notes: String? = null
)

data class OrderItemRequest(
    @SerializedName("menu_item_id")
    val menuItemId: String,
    val quantity: Int,
    val notes: String? = null
)