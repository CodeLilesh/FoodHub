package com.example.foodorderingapp.util

import org.json.JSONObject
import retrofit2.Response

/**
 * Sealed class to handle API responses with success, error, and loading states
 */
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val data: T? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}

/**
 * Extension function to handle API responses and convert them to NetworkResult
 */
fun <T> handleApiResponse(response: Response<T>): NetworkResult<T> {
    return if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.Error("Empty response body")
        }
    } else {
        val errorBody = response.errorBody()
        val errorMessage = if (errorBody != null) {
            try {
                val jsonError = JSONObject(errorBody.string())
                jsonError.optString("message", "Unknown error")
            } catch (e: Exception) {
                "Error parsing error response: ${e.message}"
            }
        } else {
            "Unknown error: ${response.code()}"
        }
        NetworkResult.Error(errorMessage)
    }
}