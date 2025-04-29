package com.example.foodorderingapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.data.model.Order
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.data.model.User

@Database(
    entities = [
        User::class,
        Restaurant::class,
        MenuItem::class,
        CartItem::class,
        Order::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    
}