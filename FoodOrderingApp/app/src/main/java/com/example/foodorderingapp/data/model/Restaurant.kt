package com.example.foodorderingapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val phone: String,
    val category: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val rating: Double = 0.0,
    @SerializedName("delivery_fee")
    val deliveryFee: Double = 0.0,
    @SerializedName("delivery_time")
    val deliveryTime: String = "30-45 min",
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)