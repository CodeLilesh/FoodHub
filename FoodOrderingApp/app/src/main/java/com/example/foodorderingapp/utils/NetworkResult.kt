package com.example.foodorderingapp.utils

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
    
    companion object {
        fun <T> success(data: T): NetworkResult<T> = Success(data)
        fun error(message: String, code: Int? = null): NetworkResult<Nothing> = Error(message, code)
        fun <T> loading(): NetworkResult<T> = Loading
    }
}

// Extension functions to handle NetworkResult
fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> this
        is NetworkResult.Loading -> this
    }
}

fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) {
        action(data)
    }
    return this
}

fun <T> NetworkResult<T>.onError(action: (String, Int?) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) {
        action(message, code)
    }
    return this
}

fun <T> NetworkResult<T>.onLoading(action: () -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Loading) {
        action()
    }
    return this
}