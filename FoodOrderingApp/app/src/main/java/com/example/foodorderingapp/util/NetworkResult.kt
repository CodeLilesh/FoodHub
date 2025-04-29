package com.example.foodorderingapp.util

/**
 * A generic class that holds a value with its loading status
 * @param <T>
 */
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T>(data: T? = null) : NetworkResult<T>(data)
}