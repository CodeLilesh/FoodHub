package com.example.foodorderingapp.api

import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.User
import com.google.gson.annotations.SerializedName

// Request Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

data class OrderRequest(
    val restaurantId: String,
    val items: List<OrderItemRequest>,
    val deliveryAddress: String,
    val contactPhone: String,
    val paymentMethod: String,
    val notes: String? = null
)

data class OrderItemRequest(
    val menuItemId: String,
    val quantity: Int,
    val specialInstructions: String? = null
)

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: String? = null
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

// Response Models
data class AuthResponse(
    val token: String,
    val user: User
)

data class MessageResponse(
    val message: String
)

data class ErrorResponse(
    val error: String,
    @SerializedName("status_code")
    val statusCode: Int
)