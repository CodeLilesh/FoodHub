package com.example.foodorderingapp.utils

import androidx.room.TypeConverter
import java.util.*

object Constants {
    // API Constants
    const val API_BASE_URL = "http://10.0.2.2:8000/api/" // For Android emulator
    
    // Default Values
    const val DEFAULT_USER_ID = -1
}

// Result class for handling success and error states
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

// Date Converter for Room
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
