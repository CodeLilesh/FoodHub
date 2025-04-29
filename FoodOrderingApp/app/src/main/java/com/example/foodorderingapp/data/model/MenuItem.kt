package com.example.foodorderingapp.data.model

data class MenuItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val isAvailable: Boolean,
    val isPopular: Boolean,
    val calories: Int? = null,
    val preparationTime: Int? = null // in minutes
)