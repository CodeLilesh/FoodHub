package com.example.foodorderingapp.utils

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
    
    companion object {
        fun <T> loading(): NetworkResult<T> = Loading
        fun <T> success(data: T): NetworkResult<T> = Success(data)
        fun <T> error(message: String): NetworkResult<T> = Error(message)
    }
    
    // Check if result is successful
    fun isSuccess(): Boolean = this is Success
    
    // Check if result is error
    fun isError(): Boolean = this is Error
    
    // Check if result is loading
    fun isLoading(): Boolean = this is Loading
    
    // Get data from success or null
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    // Get error message or null
    fun errorOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }
    
    // Transform the success data
    fun <R> map(transform: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message)
            is Loading -> Loading
        }
    }
    
    // Execute different functions based on result type
    inline fun fold(
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit,
        onLoading: () -> Unit = {}
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(message)
            is Loading -> onLoading()
        }
    }
}