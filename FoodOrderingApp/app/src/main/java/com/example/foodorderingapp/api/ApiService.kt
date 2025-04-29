package com.example.foodorderingapp.api

import com.example.foodorderingapp.data.model.OrderTrackingResponse
import com.example.foodorderingapp.util.Constants
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface for network requests
 */
interface ApiService {
    /**
     * User login endpoint
     */
    @POST(Constants.LOGIN_ENDPOINT)
    suspend fun login(
        @Body loginRequest: HashMap<String, String>
    ): Response<LoginResponse>

    /**
     * User registration endpoint
     */
    @POST(Constants.REGISTER_ENDPOINT)
    suspend fun register(
        @Body registerRequest: HashMap<String, String>
    ): Response<RegisterResponse>

    /**
     * Get list of restaurants endpoint
     */
    @GET(Constants.RESTAURANTS_ENDPOINT)
    suspend fun getRestaurants(
        @Header("Authorization") token: String
    ): Response<List<Restaurant>>

    /**
     * Get restaurant menu items endpoint
     */
    @GET(Constants.RESTAURANT_MENU_ENDPOINT)
    suspend fun getRestaurantMenu(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: String
    ): Response<List<MenuItem>>

    /**
     * Place a new order endpoint
     */
    @POST(Constants.ORDERS_ENDPOINT)
    suspend fun placeOrder(
        @Header("Authorization") token: String,
        @Body orderRequest: PlaceOrderRequest
    ): Response<Order>

    /**
     * Get order details endpoint
     */
    @GET(Constants.ORDER_DETAILS_ENDPOINT)
    suspend fun getOrderDetails(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): Response<Order>

    /**
     * Get order tracking details endpoint
     */
    @GET(Constants.ORDER_TRACKING_ENDPOINT)
    suspend fun getOrderTracking(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): Response<OrderTrackingResponse>

    /**
     * Get user orders history endpoint
     */
    @GET(Constants.USER_ORDERS_ENDPOINT)
    suspend fun getUserOrders(
        @Header("Authorization") token: String
    ): Response<List<Order>>

    /**
     * Get user profile endpoint
     */
    @GET(Constants.USER_PROFILE_ENDPOINT)
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<User>

    /**
     * Cancel an order endpoint
     */
    @POST("${Constants.ORDERS_ENDPOINT}/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): Response<Void>
}

// Response models (these would be defined in their own files in a real project)
data class LoginResponse(
    val token: String,
    val userId: String,
    val name: String,
    val email: String
)

data class RegisterResponse(
    val token: String,
    val userId: String,
    val name: String,
    val email: String
)

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val image: String,
    val rating: Float,
    val address: String,
    val phone: String
)

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: String,
    val restaurantId: String,
    val isAvailable: Boolean
)

data class PlaceOrderRequest(
    val restaurantId: String,
    val items: List<OrderItemRequest>,
    val totalPrice: Double,
    val address: String,
    val paymentMethod: String
)

data class OrderItemRequest(
    val menuItemId: String,
    val quantity: Int,
    val price: Double
)

data class Order(
    val id: String,
    val userId: String,
    val restaurantId: String,
    val items: List<com.example.foodorderingapp.data.model.OrderItem>,
    val totalPrice: Double,
    val status: String,
    val address: String,
    val paymentMethod: String,
    val createdAt: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?
)