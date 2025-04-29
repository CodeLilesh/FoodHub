package com.example.foodorderingapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.foodorderingapp.data.local.Converters
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Restaurant::class,
            parentColumns = ["id"],
            childColumns = ["restaurantId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(Converters::class)
data class Order(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("user_id")
    val userId: Int,
    
    @SerializedName("restaurant_id")
    val restaurantId: Int,
    
    @SerializedName("items")
    val items: List<OrderItem>,
    
    @SerializedName("total_amount")
    val totalAmount: Double,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    
    @SerializedName("payment_method")
    val paymentMethod: String,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String,
    
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    
    @SerializedName("special_instructions")
    val specialInstructions: String?
)

data class OrderItem(
    @SerializedName("menu_item_id")
    val menuItemId: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("special_instructions")
    val specialInstructions: String?
)