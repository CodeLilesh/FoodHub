package com.example.foodorderingapp.data.remote

import com.example.foodorderingapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip interceptor for auth endpoints
        if (originalRequest.url.toString().contains("auth/login") || 
            originalRequest.url.toString().contains("auth/register")) {
            return chain.proceed(originalRequest)
        }
        
        // Get token from SessionManager - need to use runBlocking as Interceptor doesn't support suspend functions
        val token = runBlocking { sessionManager.authToken.first() }
        
        // Proceed with original request if no token
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // Add Authorization header with token
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
            
        return chain.proceed(newRequest)
    }
}