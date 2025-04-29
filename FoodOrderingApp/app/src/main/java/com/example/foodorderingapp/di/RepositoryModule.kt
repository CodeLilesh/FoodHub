package com.example.foodorderingapp.di

import android.content.Context
import com.example.foodorderingapp.api.ApiService
import com.example.foodorderingapp.data.repository.OrderRepository
import com.example.foodorderingapp.data.repository.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger-Hilt module for providing repository dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides a SessionManager instance for managing user sessions
     */
    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    /**
     * Provides an OrderRepository instance for order-related operations
     */
    @Singleton
    @Provides
    fun provideOrderRepository(
        apiService: ApiService,
        sessionManager: SessionManager
    ): OrderRepository {
        return OrderRepository(apiService, sessionManager)
    }
}