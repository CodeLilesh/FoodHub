package com.example.foodorderingapp.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Represents an individual item within an order
 */
@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0,
    
    @SerializedName("order_id")
    val orderId: String,
    
    @SerializedName("menu_item_id")
    val menuItemId: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("special_instructions")
    val specialInstructions: String? = null,
    
    @SerializedName("image_url")
    val imageUrl: String? = null
) {
    /**
     * Calculate the total price for this order item (price * quantity)
     */
    fun getTotalPrice(): Double = price * quantity
}