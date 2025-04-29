package com.example.foodorderingapp.data.model

import java.util.Date

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String? = null,
    val createdAt: Date,
    val updatedAt: Date
)