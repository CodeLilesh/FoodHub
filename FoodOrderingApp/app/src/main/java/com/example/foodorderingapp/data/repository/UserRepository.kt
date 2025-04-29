package com.example.foodorderingapp.data.repository

import com.example.foodorderingapp.data.local.UserDao
import com.example.foodorderingapp.data.model.LoginRequest
import com.example.foodorderingapp.data.model.RegisterRequest
import com.example.foodorderingapp.data.model.User
import com.example.foodorderingapp.data.remote.ApiService
import com.example.foodorderingapp.utils.NetworkResult
import com.example.foodorderingapp.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    
    val currentUser: Flow<User?> = userDao.getCurrentUser()
    
    suspend fun register(name: String, email: String, password: String, phone: String): NetworkResult<Unit> {
        return try {
            val request = RegisterRequest(name, email, password, phone)
            val response = apiService.register(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save user to database
                userDao.insertUser(authResponse.user)
                // Save auth data to preferences
                sessionManager.saveAuthData(
                    authResponse.token,
                    authResponse.user.id,
                    authResponse.user.name,
                    authResponse.user.email
                )
                NetworkResult.Success(Unit)
            } else {
                Timber.e("Registration failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Registration failed. Please try again.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Registration error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
    
    suspend fun login(email: String, password: String): NetworkResult<Unit> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save user to database
                userDao.insertUser(authResponse.user)
                // Save auth data to preferences
                sessionManager.saveAuthData(
                    authResponse.token,
                    authResponse.user.id,
                    authResponse.user.name,
                    authResponse.user.email
                )
                NetworkResult.Success(Unit)
            } else {
                Timber.e("Login failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Invalid email or password.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Login error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
    
    suspend fun logout(): NetworkResult<Unit> {
        return try {
            val response = apiService.logout()
            
            // Clear local data even if API call fails
            sessionManager.clearAuthData()
            userDao.clearUsers()
            
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Logout error")
            
            // Still clear local data
            sessionManager.clearAuthData()
            userDao.clearUsers()
            
            NetworkResult.Success(Unit)
        }
    }
    
    suspend fun refreshUserData(): NetworkResult<User> {
        return try {
            val response = apiService.getCurrentUser()
            
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userDao.insertUser(user)
                NetworkResult.Success(user)
            } else {
                Timber.e("Get user data failed: ${response.errorBody()?.string()}")
                NetworkResult.Error("Failed to get user data.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Get user data error")
            NetworkResult.Error("Could not connect to server. Please check your internet connection.")
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        val token = sessionManager.authToken.firstOrNull()
        return !token.isNullOrEmpty()
    }
}