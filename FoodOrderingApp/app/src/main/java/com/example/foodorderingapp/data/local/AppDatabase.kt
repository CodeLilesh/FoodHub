package com.example.foodorderingapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodorderingapp.data.local.dao.CartDao
import com.example.foodorderingapp.data.local.dao.UserDao
import com.example.foodorderingapp.data.local.entity.CartItemEntity
import com.example.foodorderingapp.data.local.entity.UserEntity
import com.example.foodorderingapp.utils.DateConverter

@Database(
    entities = [CartItemEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun cartDao(): CartDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_ordering_app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
