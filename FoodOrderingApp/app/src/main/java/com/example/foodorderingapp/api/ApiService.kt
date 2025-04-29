package com.example.foodorderingapp.api

import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.data.model.Order
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>
    
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("address") address: String?
    ): Response<RegisterResponse>
    
    @POST("auth/logout")
    suspend fun logout(): Response<LogoutResponse>
    
    // User
    @GET("users/profile")
    suspend fun getUserProfile(): Response<User>
    
    @FormUrlEncoded
    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("address") address: String?
    ): Response<User>
    
    // Restaurants
    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("category") category: String? = null,
        @Query("query") searchQuery: String? = null
    ): Response<List<Restaurant>>
    
    @GET("restaurants/{id}")
    suspend fun getRestaurant(
        @Path("id") restaurantId: String
    ): Response<Restaurant>
    
    // Menu Items
    @GET("menuItems")
    suspend fun getMenuItems(
        @Query("restaurantId") restaurantId: String,
        @Query("category") category: String? = null,
        @Query("query") searchQuery: String? = null
    ): Response<List<MenuItem>>
    
    @GET("menuItems/{id}")
    suspend fun getMenuItem(
        @Path("id") menuItemId: String
    ): Response<MenuItem>
    
    // Orders
    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>
    
    @GET("orders/{id}")
    suspend fun getOrder(
        @Path("id") orderId: String
    ): Response<Order>
    
    @POST("orders")
    suspend fun createOrder(
        @Body orderRequest: CreateOrderRequest
    ): Response<Order>
}

// Response and Request data classes
data class LoginResponse(
    val token: String,
    val user: User
)

data class RegisterResponse(
    val token: String,
    val user: User
)

data class LogoutResponse(
    val message: String
)

data class CreateOrderRequest(
    val restaurantId: String,
    val items: List<OrderItemRequest>,
    val deliveryAddress: String,
    val contactPhone: String,
    val paymentMethod: String,
    val notes: String?
)

data class OrderItemRequest(
    val menuItemId: String,
    val quantity: Int,
    val specialInstructions: String?
)