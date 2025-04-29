package com.example.foodorderingapp.data.model

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val cuisine: String,
    val address: String,
    val imageUrl: String,
    val rating: Double,
    val ratingCount: Int,
    val deliveryTime: Int, // in minutes
    val deliveryFee: Double,
    val minOrderAmount: Double,
    val isOpen: Boolean,
    val categories: List<String>
)