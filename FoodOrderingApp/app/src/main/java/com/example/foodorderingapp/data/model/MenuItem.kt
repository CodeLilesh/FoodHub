package com.example.foodorderingapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "menu_items",
    foreignKeys = [
        ForeignKey(
            entity = Restaurant::class,
            parentColumns = ["id"],
            childColumns = ["restaurantId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MenuItem(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val available: Boolean = true,
    @SerializedName("restaurant_id")
    val restaurantId: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)