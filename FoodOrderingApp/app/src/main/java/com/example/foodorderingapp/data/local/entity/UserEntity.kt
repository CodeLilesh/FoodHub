package com.example.foodorderingapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodorderingapp.data.models.User
import java.util.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "phone")
    val phone: String? = null,
    
    @ColumnInfo(name = "address")
    val address: String? = null,
    
    @ColumnInfo(name = "role")
    val role: String,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date
) {
    fun toUser(): User {
        return User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            address = address,
            role = role,
            createdAt = createdAt
        )
    }
    
    companion object {
        fun fromUser(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                phone = user.phone,
                address = user.address,
                role = user.role,
                createdAt = user.createdAt
            )
        }
    }
}
