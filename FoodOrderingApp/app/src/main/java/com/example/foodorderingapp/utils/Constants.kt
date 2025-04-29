package com.example.foodorderingapp.utils

object Constants {
    // API Base URL
    const val BASE_URL = "http://10.0.2.2:5000/api/"  // For Android Emulator
    // const val BASE_URL = "http://localhost:5000/api/"  // For Physical Device
    
    // WebSocket Base URL
    const val WS_BASE_URL = "ws://10.0.2.2:5000/ws/"  // For Android Emulator
    // const val WS_BASE_URL = "ws://localhost:5000/ws/"  // For Physical Device

    // API Endpoints
    const val LOGIN_URL = "auth/login"
    const val REGISTER_URL = "auth/register"
    const val LOGOUT_URL = "auth/logout"
    const val USER_URL = "users/profile"
    const val RESTAURANTS_URL = "restaurants"
    const val MENU_ITEMS_URL = "menuItems"
    const val ORDERS_URL = "orders"
    
    // SharedPreferences
    const val PREFERENCES_NAME = "food_app_preferences"
    const val AUTH_TOKEN_KEY = "auth_token"
    const val USER_ID_KEY = "user_id"
    const val USER_EMAIL_KEY = "user_email"
    const val USER_NAME_KEY = "user_name"
    
    // Order Status
    const val ORDER_STATUS_PENDING = "pending"
    const val ORDER_STATUS_PREPARING = "preparing"
    const val ORDER_STATUS_READY = "ready"
    const val ORDER_STATUS_DELIVERED = "delivered"
    const val ORDER_STATUS_CANCELLED = "cancelled"
    
    // Payment Methods
    const val PAYMENT_METHOD_CASH = "cash"
    const val PAYMENT_METHOD_CARD = "card"
    
    // Intent Keys
    const val RESTAURANT_ID_KEY = "restaurant_id"
    const val ORDER_ID_KEY = "order_id"
    
    // Notification Channels
    const val ORDER_NOTIFICATION_CHANNEL_ID = "order_notification_channel"
    const val ORDER_NOTIFICATION_CHANNEL_NAME = "Order Status Updates"
    
    // Request Codes
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    const val PAYMENT_REQUEST_CODE = 1002
    
    // Result Codes
    const val RESULT_ORDER_PLACED = 2001
    
    // Default Values
    const val DEFAULT_PAGE_SIZE = 10
    const val MIN_SEARCH_LENGTH = 3
}