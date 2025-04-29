package com.example.foodorderingapp.data.remote

import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.data.model.Order
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth Endpoints
    @POST("auth/register")
    suspend fun register(@Body userData: Map<String, String>): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body loginData: Map<String, String>): Response<AuthResponse>
    
    @GET("auth/user")
    suspend fun getCurrentUser(): Response<User>
    
    // Restaurant Endpoints
    @GET("restaurants")
    suspend fun getAllRestaurants(): Response<List<Restaurant>>
    
    @GET("restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: Int): Response<Restaurant>
    
    @GET("restaurants/category/{category}")
    suspend fun getRestaurantsByCategory(@Path("category") category: String): Response<List<Restaurant>>
    
    @GET("restaurants/search")
    suspend fun searchRestaurants(@Query("term") searchTerm: String): Response<List<Restaurant>>
    
    // Menu Items Endpoints
    @GET("menu-items/restaurant/{restaurantId}")
    suspend fun getMenuItemsByRestaurant(@Path("restaurantId") restaurantId: Int): Response<List<MenuItem>>
    
    @GET("menu-items/{id}")
    suspend fun getMenuItemById(@Path("id") id: Int): Response<MenuItem>
    
    @GET("menu-items/restaurant/{restaurantId}/category/{category}")
    suspend fun getMenuItemsByCategory(
        @Path("restaurantId") restaurantId: Int,
        @Path("category") category: String
    ): Response<List<MenuItem>>
    
    // Order Endpoints
    @POST("orders")
    suspend fun createOrder(@Body orderData: Map<String, Any>): Response<Order>
    
    @GET("orders/user")
    suspend fun getUserOrders(): Response<List<Order>>
    
    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<Order>
    
    // User Profile Endpoints
    @PUT("users/profile")
    suspend fun updateUserProfile(@Body userData: Map<String, String>): Response<User>
}

data class AuthResponse(
    val success: Boolean,
    val token: String,
    val user: User
)