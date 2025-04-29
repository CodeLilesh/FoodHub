package com.example.foodorderingapp.data.model

import java.util.Date

data class Order(
    val id: String,
    val userId: String,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<OrderItem>,
    val status: String,
    val total: Double,
    val subtotal: Double,
    val deliveryFee: Double,
    val deliveryAddress: String,
    val contactPhone: String,
    val paymentMethod: String,
    val notes: String?,
    val estimatedDeliveryTime: Int, // in minutes
    val createdAt: Date,
    val updatedAt: Date
)

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val specialInstructions: String?
)