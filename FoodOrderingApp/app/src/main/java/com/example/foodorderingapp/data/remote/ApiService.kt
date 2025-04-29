package com.example.foodorderingapp.data.remote

import com.example.foodorderingapp.data.model.LoginRequest
import com.example.foodorderingapp.data.model.RegisterRequest
import com.example.foodorderingapp.data.model.User
import com.example.foodorderingapp.data.model.AuthResponse
import com.example.foodorderingapp.data.model.OrderRequest
import com.example.foodorderingapp.data.model.Order
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.model.MenuItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    
    // Auth endpoints
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>
    
    @GET("api/auth/user")
    suspend fun getCurrentUser(): Response<User>
    
    // Restaurant endpoints
    @GET("api/restaurants")
    suspend fun getRestaurants(): Response<List<Restaurant>>
    
    @GET("api/restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: String): Response<Restaurant>
    
    @GET("api/restaurants/category/{category}")
    suspend fun getRestaurantsByCategory(@Path("category") category: String): Response<List<Restaurant>>
    
    @GET("api/restaurants/search")
    suspend fun searchRestaurants(@Query("term") searchTerm: String): Response<List<Restaurant>>
    
    // Menu Item endpoints
    @GET("api/restaurants/{restaurantId}/menu")
    suspend fun getMenuByRestaurant(@Path("restaurantId") restaurantId: String): Response<List<MenuItem>>
    
    @GET("api/restaurants/{restaurantId}/menu/category/{category}")
    suspend fun getMenuByCategory(
        @Path("restaurantId") restaurantId: String,
        @Path("category") category: String
    ): Response<List<MenuItem>>
    
    @GET("api/menu/search")
    suspend fun searchMenuItems(
        @Path("restaurantId") restaurantId: String,
        @Query("term") searchTerm: String
    ): Response<List<MenuItem>>
    
    // Order endpoints
    @POST("api/orders")
    suspend fun createOrder(@Body orderRequest: OrderRequest): Response<Order>
    
    @GET("api/orders/user")
    suspend fun getUserOrders(): Response<List<Order>>
    
    @GET("api/orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<Order>
    
    @PUT("api/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body status: Map<String, String>
    ): Response<Order>
}