package com.example.foodorderingapp.data.api

import com.example.foodorderingapp.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth Endpoints
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>
    
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>
    
    // User Endpoints
    @GET("users/me")
    suspend fun getUserProfile(@Header("x-auth-token") token: String): Response<UserResponse>
    
    @PUT("users/me")
    suspend fun updateUserProfile(
        @Header("x-auth-token") token: String,
        @Body updateRequest: UpdateUserRequest
    ): Response<UserResponse>
    
    // Restaurant Endpoints
    @GET("restaurants")
    suspend fun getAllRestaurants(@Header("x-auth-token") token: String): Response<RestaurantsResponse>
    
    @GET("restaurants")
    suspend fun getRestaurantsByCategory(
        @Header("x-auth-token") token: String,
        @Query("category") category: String
    ): Response<RestaurantsResponse>
    
    @GET("restaurants")
    suspend fun searchRestaurants(
        @Header("x-auth-token") token: String,
        @Query("search") searchQuery: String
    ): Response<RestaurantsResponse>
    
    @GET("restaurants/{id}")
    suspend fun getRestaurantById(
        @Header("x-auth-token") token: String,
        @Path("id") restaurantId: Int
    ): Response<RestaurantResponse>
    
    // Menu Item Endpoints
    @GET("restaurants/{id}/menu")
    suspend fun getRestaurantMenu(
        @Header("x-auth-token") token: String,
        @Path("id") restaurantId: Int
    ): Response<MenuItemsResponse>
    
    @GET("restaurants/{id}/menu")
    suspend fun getRestaurantMenuByCategory(
        @Header("x-auth-token") token: String,
        @Path("id") restaurantId: Int,
        @Query("category") category: String
    ): Response<MenuItemsResponse>
    
    @GET("menu-items/{id}")
    suspend fun getMenuItemById(
        @Header("x-auth-token") token: String,
        @Path("id") menuItemId: Int
    ): Response<MenuItemResponse>
    
    // Order Endpoints
    @GET("orders")
    suspend fun getUserOrders(@Header("x-auth-token") token: String): Response<OrdersResponse>
    
    @GET("orders/{id}")
    suspend fun getOrderById(
        @Header("x-auth-token") token: String,
        @Path("id") orderId: Int
    ): Response<OrderResponse>
    
    @POST("orders")
    suspend fun createOrder(
        @Header("x-auth-token") token: String,
        @Body orderRequest: CreateOrderRequest
    ): Response<OrderResponse>
}

// Request and Response Data Classes
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val address: String? = null
)

data class UpdateUserRequest(
    val name: String,
    val email: String,
    val phone: String?,
    val address: String?
)

data class CreateOrderRequest(
    val restaurant_id: Int,
    val items: List<OrderItemRequest>,
    val delivery_address: String,
    val payment_method: String,
    val special_instructions: String? = null
)

data class OrderItemRequest(
    val menu_item_id: Int,
    val quantity: Int
)

data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val user: User? = null,
    val message: String? = null
)

data class UserResponse(
    val success: Boolean,
    val data: User? = null,
    val message: String? = null
)

data class RestaurantsResponse(
    val success: Boolean,
    val data: List<Restaurant>? = null,
    val message: String? = null
)

data class RestaurantResponse(
    val success: Boolean,
    val data: Restaurant? = null,
    val message: String? = null
)

data class MenuItemsResponse(
    val success: Boolean,
    val data: List<MenuItem>? = null,
    val message: String? = null
)

data class MenuItemResponse(
    val success: Boolean,
    val data: MenuItem? = null,
    val message: String? = null
)

data class OrdersResponse(
    val success: Boolean,
    val data: List<Order>? = null,
    val message: String? = null
)

data class OrderResponse(
    val success: Boolean,
    val data: Order? = null,
    val message: String? = null
)
