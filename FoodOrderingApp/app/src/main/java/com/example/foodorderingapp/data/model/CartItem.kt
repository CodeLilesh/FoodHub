package com.example.foodorderingapp.data.model

data class CartItem(
    val id: String,
    val menuItemId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val restaurantId: String,
    val restaurantName: String,
    val specialInstructions: String? = null
)