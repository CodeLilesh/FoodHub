package com.example.foodorderingapp.di

import android.content.Context
import androidx.room.Room
import com.example.foodorderingapp.BuildConfig
import com.example.foodorderingapp.data.local.AppDatabase
import com.example.foodorderingapp.data.remote.ApiService
import com.example.foodorderingapp.data.remote.AuthInterceptor
import com.example.foodorderingapp.data.repository.CartRepository
import com.example.foodorderingapp.data.repository.OrderRepository
import com.example.foodorderingapp.data.repository.RestaurantRepository
import com.example.foodorderingapp.data.repository.UserRepository
import com.example.foodorderingapp.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "food_ordering_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor {
        return AuthInterceptor(sessionManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        appDatabase: AppDatabase,
        sessionManager: SessionManager
    ): UserRepository {
        return UserRepository(apiService, appDatabase.userDao(), sessionManager)
    }

    @Provides
    @Singleton
    fun provideRestaurantRepository(
        apiService: ApiService,
        appDatabase: AppDatabase
    ): RestaurantRepository {
        return RestaurantRepository(apiService, appDatabase.restaurantDao())
    }

    @Provides
    @Singleton
    fun provideCartRepository(appDatabase: AppDatabase): CartRepository {
        return CartRepository(appDatabase.cartDao())
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        apiService: ApiService,
        appDatabase: AppDatabase
    ): OrderRepository {
        return OrderRepository(apiService, appDatabase.orderDao(), appDatabase.cartDao())
    }
}