package com.example.foodorderingapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val id: Int = 0,
    
    val menuItemId: Int,
    
    val restaurantId: Int,
    
    val name: String,
    
    val price: Double,
    
    val quantity: Int,
    
    val instructions: String? = null,
    
    val imageUrl: String? = null
) {
    val totalPrice: Double
        get() = price * quantity
}