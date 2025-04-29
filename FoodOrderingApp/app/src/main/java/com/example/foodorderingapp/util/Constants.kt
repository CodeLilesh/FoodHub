package com.example.foodorderingapp.util

object Constants {
    // API Base URL
    const val BASE_URL = "http://10.0.2.2:5000/api/"
    
    // WebSocket URL
    const val WS_BASE_URL = "ws://10.0.2.2:5000/ws"
    
    // Auth endpoints
    const val LOGIN_ENDPOINT = "auth/login"
    const val REGISTER_ENDPOINT = "auth/register"
    
    // Restaurant endpoints
    const val RESTAURANTS_ENDPOINT = "restaurants"
    const val RESTAURANT_MENU_ENDPOINT = "restaurants/{id}/menu"
    
    // Order endpoints
    const val ORDERS_ENDPOINT = "orders"
    const val ORDER_DETAILS_ENDPOINT = "orders/{id}"
    const val ORDER_TRACKING_ENDPOINT = "orders/{id}/track"
    
    // User endpoints
    const val USER_PROFILE_ENDPOINT = "users/profile"
    const val USER_ORDERS_ENDPOINT = "users/orders"
    
    // DataStore
    const val PREFERENCES_NAME = "food_app_preferences"
    const val AUTH_TOKEN_KEY = "auth_token"
    const val USER_ID_KEY = "user_id"
    
    // Order Status
    const val ORDER_PLACED = "PLACED"
    const val ORDER_CONFIRMED = "CONFIRMED"
    const val ORDER_PREPARING = "PREPARING"
    const val ORDER_OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY"
    const val ORDER_DELIVERED = "DELIVERED"
    const val ORDER_CANCELLED = "CANCELLED"
    
    // Socket Events
    const val SOCKET_EVENT_CONNECT = "connect"
    const val SOCKET_EVENT_DISCONNECT = "disconnect"
    const val SOCKET_EVENT_ERROR = "error"
    const val SOCKET_EVENT_ORDER_UPDATE = "order_update"
    const val SOCKET_EVENT_DRIVER_LOCATION = "driver_location"
    const val SOCKET_EVENT_SUBSCRIBE_TO_ORDER = "subscribe_to_order"
}