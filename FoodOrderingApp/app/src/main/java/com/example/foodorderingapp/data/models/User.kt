package com.example.foodorderingapp.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val role: String,
    
    @SerializedName("created_at")
    val createdAt: Date
)
