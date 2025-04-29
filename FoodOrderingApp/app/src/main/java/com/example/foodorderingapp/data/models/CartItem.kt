package com.example.foodorderingapp.data.models

data class CartItem(
    val id: Int,
    val restaurantId: Int,
    val menuItemId: Int,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val pricePerItem: Double,
    var quantity: Int,
    val isVegetarian: Boolean = false
)
