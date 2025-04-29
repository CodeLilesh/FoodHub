package com.example.foodorderingapp.data.repository

import android.content.Context
import com.example.foodorderingapp.data.api.RetrofitClient
import com.example.foodorderingapp.data.api.UpdateUserRequest
import com.example.foodorderingapp.data.api.LoginRequest
import com.example.foodorderingapp.data.api.RegisterRequest
import com.example.foodorderingapp.data.local.AppDatabase
import com.example.foodorderingapp.data.local.entity.UserEntity
import com.example.foodorderingapp.data.models.User
import com.example.foodorderingapp.utils.Constants
import com.example.foodorderingapp.utils.Result
import com.example.foodorderingapp.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val context: Context) {
    
    private val apiService = RetrofitClient.apiService
    private val userDao = AppDatabase.getInstance(context).userDao()
    private val sessionManager = SessionManager(context)
    
    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email, password))
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                val token = authResponse.token
                val user = authResponse.user
                
                if (token != null && user != null) {
                    // Save user information
                    sessionManager.saveAuthToken(token)
                    sessionManager.saveUserInfo(user.id, user.name, user.email)
                    
                    // Cache user in database
                    userDao.insertUser(UserEntity.fromUser(user))
                    
                    return@withContext Result.Success(user)
                }
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Login failed"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun register(name: String, email: String, password: String, phone: String?, address: String?): Result<User> = 
        withContext(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(name, email, password, phone, address)
                val response = apiService.register(registerRequest)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val authResponse = response.body()!!
                    val token = authResponse.token
                    val user = authResponse.user
                    
                    if (token != null && user != null) {
                        // Save user information
                        sessionManager.saveAuthToken(token)
                        sessionManager.saveUserInfo(user.id, user.name, user.email)
                        
                        // Cache user in database
                        userDao.insertUser(UserEntity.fromUser(user))
                        
                        return@withContext Result.Success(user)
                    }
                }
                
                val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Registration failed"
                return@withContext Result.Error(errorMessage)
            } catch (e: Exception) {
                return@withContext Result.Error(e.message ?: "Network error occurred")
            }
        }
    
    suspend fun getUserProfile(): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Check if token exists
            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.Error("Not authenticated")
            }
            
            // First try to get from local cache
            val userId = sessionManager.getUserId()
            if (userId != Constants.DEFAULT_USER_ID) {
                val cachedUser = userDao.getUserById(userId)
                if (cachedUser != null) {
                    return@withContext Result.Success(cachedUser.toUser())
                }
            }
            
            // If not in cache, fetch from API
            val response = apiService.getUserProfile(token)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val userResponse = response.body()!!
                val user = userResponse.data
                
                if (user != null) {
                    // Cache user in database
                    userDao.insertUser(UserEntity.fromUser(user))
                    
                    return@withContext Result.Success(user)
                }
            }
            
            val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to get profile"
            return@withContext Result.Error(errorMessage)
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Network error occurred")
        }
    }
    
    suspend fun updateUserProfile(name: String, email: String, phone: String?, address: String?): Result<User> = 
        withContext(Dispatchers.IO) {
            try {
                // Check if token exists
                val token = sessionManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.Error("Not authenticated")
                }
                
                val updateRequest = UpdateUserRequest(name, email, phone, address)
                val response = apiService.updateUserProfile(token, updateRequest)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val userResponse = response.body()!!
                    val user = userResponse.data
                    
                    if (user != null) {
                        // Update session information
                        sessionManager.saveUserInfo(user.id, user.name, user.email)
                        
                        // Update cache in database
                        userDao.insertUser(UserEntity.fromUser(user))
                        
                        return@withContext Result.Success(user)
                    }
                }
                
                val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to update profile"
                return@withContext Result.Error(errorMessage)
            } catch (e: Exception) {
                return@withContext Result.Error(e.message ?: "Network error occurred")
            }
        }
    
    fun logout() {
        // Clear session
        sessionManager.clearSession()
        
        // Clear user cache
        Thread {
            AppDatabase.getInstance(context).clearAllTables()
        }.start()
    }
}
