package com.example.foodorderingapp.data.local

import androidx.room.TypeConverter
import com.example.foodorderingapp.data.model.OrderItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()
    
    // Convert Date to Long timestamp and vice versa
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    // Convert List<OrderItem> to String and vice versa
    @TypeConverter
    fun fromOrderItemList(value: List<OrderItem>?): String {
        return gson.toJson(value ?: emptyList<OrderItem>())
    }
    
    @TypeConverter
    fun toOrderItemList(value: String): List<OrderItem> {
        val listType = object : TypeToken<List<OrderItem>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    // Convert List<String> to String and vice versa
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}