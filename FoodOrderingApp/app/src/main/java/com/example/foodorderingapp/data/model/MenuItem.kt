package com.example.foodorderingapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "menu_items",
    foreignKeys = [
        ForeignKey(
            entity = Restaurant::class,
            parentColumns = ["id"],
            childColumns = ["restaurantId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MenuItem(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("restaurant_id")
    val restaurantId: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("image_url")
    val imageUrl: String?,
    
    @SerializedName("is_vegetarian")
    val isVegetarian: Boolean,
    
    @SerializedName("is_available")
    val isAvailable: Boolean,
    
    @SerializedName("created_at")
    val createdAt: String
)