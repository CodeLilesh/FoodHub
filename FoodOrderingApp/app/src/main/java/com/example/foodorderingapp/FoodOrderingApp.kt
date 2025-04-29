package com.example.foodorderingapp

import android.app.Application
import androidx.room.Room
import com.example.foodorderingapp.data.local.AppDatabase
import com.example.foodorderingapp.data.remote.ApiService
import com.example.foodorderingapp.data.remote.AuthInterceptor
import com.example.foodorderingapp.data.repository.CartRepository
import com.example.foodorderingapp.data.repository.RestaurantRepository
import com.example.foodorderingapp.data.repository.UserRepository
import com.example.foodorderingapp.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FoodOrderingApp : Application() {

    companion object {
        lateinit var instance: FoodOrderingApp
            private set
    }

    // Database
    lateinit var database: AppDatabase
        private set

    // API Service
    lateinit var apiService: ApiService
        private set

    // Repositories
    lateinit var userRepository: UserRepository
        private set
    lateinit var restaurantRepository: RestaurantRepository
        private set
    lateinit var cartRepository: CartRepository
        private set

    // Session Manager
    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Session Manager
        sessionManager = SessionManager(this)

        // Initialize Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "food_ordering_db"
        ).fallbackToDestructiveMigration().build()

        // Initialize API Service
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor(sessionManager)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Initialize Repositories
        userRepository = UserRepository(apiService, database.userDao(), sessionManager)
        restaurantRepository = RestaurantRepository(apiService, database.restaurantDao())
        cartRepository = CartRepository(database.cartDao())
    }
}