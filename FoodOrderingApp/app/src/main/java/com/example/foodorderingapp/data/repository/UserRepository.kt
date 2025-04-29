package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.api.ApiService
import com.example.foodorderingapp.api.LoginResponse
import com.example.foodorderingapp.api.RegisterResponse
import com.example.foodorderingapp.data.local.SessionManager
import com.example.foodorderingapp.data.model.User
import com.example.foodorderingapp.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    // Get user authentication state
    val isLoggedIn: Flow<Boolean> = sessionManager.isLoggedIn
    
    // Get user name
    val userName: Flow<String?> = sessionManager.userName
    
    // Get user email
    val userEmail: Flow<String?> = sessionManager.userEmail
    
    // Get user ID
    val userId: Flow<String?> = sessionManager.userId
    
    // Login user
    suspend fun login(email: String, password: String): NetworkResult<User> {
        return handleAuthResponse {
            apiService.login(email, password)
        }
    }
    
    // Register user
    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String? = null
    ): NetworkResult<User> {
        return handleAuthResponse {
            apiService.register(name, email, password, phone, address)
        }
    }
    
    // Logout user
    suspend fun logout() {
        try {
            apiService.logout()
        } finally {
            // Clear session data regardless of API result
            sessionManager.clearSession()
        }
    }
    
    // Get user profile
    suspend fun getUserProfile(): NetworkResult<User> {
        return handleApiResponse {
            apiService.getUserProfile()
        }
    }
    
    // Update user profile
    suspend fun updateUserProfile(
        name: String,
        phone: String,
        address: String? = null
    ): NetworkResult<User> {
        return handleApiResponse {
            apiService.updateUserProfile(name, phone, address)
        }
    }
    
    // Handle authentication API response (login/register)
    private suspend fun <T> handleAuthResponse(apiCall: suspend () -> Response<T>): NetworkResult<User> {
        return try {
            val response = apiCall()
            
            if (response.isSuccessful) {
                response.body()?.let {
                    when (it) {
                        is LoginResponse -> {
                            sessionManager.saveAuthToken(it.token)
                            sessionManager.saveUserDetails(
                                userId = it.user.id,
                                email = it.user.email,
                                name = it.user.name
                            )
                            NetworkResult.success(it.user)
                        }
                        is RegisterResponse -> {
                            sessionManager.saveAuthToken(it.token)
                            sessionManager.saveUserDetails(
                                userId = it.user.id,
                                email = it.user.email,
                                name = it.user.name
                            )
                            NetworkResult.success(it.user)
                        }
                        else -> NetworkResult.error("Unexpected response type")
                    }
                } ?: NetworkResult.error("Response body is null")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                NetworkResult.error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.error("Network error: ${e.message}")
        }
    }
    
    // Handle API response
    private suspend fun <T> handleApiResponse(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            
            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkResult.success(it)
                } ?: NetworkResult.error("Response body is null")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                NetworkResult.error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.error("Network error: ${e.message}")
        }
    }
}