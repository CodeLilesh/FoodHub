package com.example.foodorderingapp.data.local

import androidx.room.TypeConverter
import com.example.foodorderingapp.data.model.OrderItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters for Room database
 * Handles conversions between complex data types and primitive types that Room can store
 */
class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromOrderItemList(value: List<OrderItem>?): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toOrderItemList(value: String): List<OrderItem> {
        val listType = object : TypeToken<List<OrderItem>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}