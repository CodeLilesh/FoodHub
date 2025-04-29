package com.example.foodorderingapp.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class MenuItem(
    val id: Int,
    
    @SerializedName("restaurant_id")
    val restaurantId: Int,
    
    val name: String,
    val description: String? = null,
    val price: Double,
    
    @SerializedName("image_url")
    val imageUrl: String? = null,
    
    val category: String,
    
    @SerializedName("is_vegetarian")
    val isVegetarian: Boolean = false,
    
    @SerializedName("is_available")
    val isAvailable: Boolean = true,
    
    @SerializedName("created_at")
    val createdAt: Date? = null,
    
    @SerializedName("updated_at")
    val updatedAt: Date? = null
)
