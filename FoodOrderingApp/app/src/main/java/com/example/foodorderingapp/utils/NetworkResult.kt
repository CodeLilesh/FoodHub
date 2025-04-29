package com.example.foodorderingapp.utils

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String = ""
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
}