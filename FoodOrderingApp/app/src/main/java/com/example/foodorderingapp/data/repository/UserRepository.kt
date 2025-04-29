package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.data.local.UserDao
import com.example.foodorderingapp.data.model.User
import com.example.foodorderingapp.data.remote.ApiService
import com.example.foodorderingapp.utils.NetworkResult
import com.example.foodorderingapp.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.IOException

class UserRepository(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    // Get current user from local database
    fun getCurrentUser(userId: Int): Flow<User?> = userDao.getUserById(userId)
    
    // Register user
    suspend fun registerUser(name: String, email: String, password: String, phone: String): NetworkResult<User> {
        return try {
            val userData = mapOf(
                "name" to name,
                "email" to email,
                "password" to password,
                "phone" to phone
            )
            
            val response = apiService.register(userData)
            handleAuthResponse(response)
            
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Login user
    suspend fun loginUser(email: String, password: String): NetworkResult<User> {
        return try {
            val loginData = mapOf(
                "email" to email,
                "password" to password
            )
            
            val response = apiService.login(loginData)
            handleAuthResponse(response)
            
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Handle auth response
    private suspend fun handleAuthResponse(response: Response<com.example.foodorderingapp.data.remote.AuthResponse>): NetworkResult<User> {
        if (response.isSuccessful) {
            val authResponse = response.body()
            if (authResponse != null) {
                // Save auth token
                sessionManager.saveAuthToken(authResponse.token)
                
                // Save user details
                sessionManager.saveUserDetails(
                    authResponse.user.id.toString(),
                    authResponse.user.name,
                    authResponse.user.email
                )
                
                // Save user to local database
                userDao.insertUser(authResponse.user)
                
                return NetworkResult.Success(authResponse.user)
            }
        }
        return NetworkResult.Error("Authentication failed: ${response.message()}")
    }
    
    // Logout user
    suspend fun logoutUser() {
        userDao.clearUsers()
        sessionManager.clearSession()
    }
    
    // Update user profile
    suspend fun updateUserProfile(userData: Map<String, String>): NetworkResult<User> {
        return try {
            val response = apiService.updateUserProfile(userData)
            
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    // Update user in local database
                    userDao.insertUser(user)
                    
                    // Update user session details
                    sessionManager.saveUserDetails(
                        user.id.toString(),
                        user.name,
                        user.email
                    )
                    
                    return NetworkResult.Success(user)
                }
            }
            NetworkResult.Error("Update failed: ${response.message()}")
            
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
    
    // Fetch current user from API and update local database
    suspend fun refreshCurrentUser(): NetworkResult<User> {
        return try {
            val response = apiService.getCurrentUser()
            
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    userDao.insertUser(user)
                    return NetworkResult.Success(user)
                }
            }
            NetworkResult.Error("Failed to refresh user: ${response.message()}")
            
        } catch (e: IOException) {
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }
}