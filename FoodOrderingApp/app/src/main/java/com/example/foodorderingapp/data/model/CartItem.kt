package com.example.foodorderingapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = MenuItem::class,
            parentColumns = ["id"],
            childColumns = ["menuItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @SerializedName("menu_item_id")
    val menuItemId: String,
    
    val quantity: Int,
    
    // These fields are denormalized from MenuItem for quick access
    val name: String,
    val price: Double,
    
    @SerializedName("image_url")
    val imageUrl: String,
    
    @SerializedName("restaurant_id")
    val restaurantId: String,
    
    // Special instructions for this item
    val notes: String? = null
) {
    val totalPrice: Double
        get() = price * quantity
}