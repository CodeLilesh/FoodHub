package com.example.foodorderingapp.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Restaurant(
    val id: Int,
    val name: String,
    val description: String? = null,
    val address: String,
    val phone: String,
    
    @SerializedName("image_url")
    val imageUrl: String? = null,
    
    @SerializedName("opening_hours")
    val openingHours: String? = null,
    
    val category: String,
    
    @SerializedName("created_at")
    val createdAt: Date? = null,
    
    @SerializedName("updated_at")
    val updatedAt: Date? = null
)
