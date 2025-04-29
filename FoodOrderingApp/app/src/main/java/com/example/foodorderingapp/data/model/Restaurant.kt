package com.example.foodorderingapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("cuisine_type")
    val cuisineType: String,
    
    @SerializedName("image_url")
    val imageUrl: String?,
    
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    
    @SerializedName("min_order_amount")
    val minOrderAmount: Double,
    
    @SerializedName("delivery_time")
    val deliveryTime: String,
    
    @SerializedName("opening_hours")
    val openingHours: String,
    
    @SerializedName("rating")
    val rating: Double,
    
    @SerializedName("created_at")
    val createdAt: String
)