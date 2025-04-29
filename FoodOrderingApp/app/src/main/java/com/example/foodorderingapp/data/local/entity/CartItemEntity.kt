package com.example.foodorderingapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodorderingapp.data.models.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "restaurant_id")
    val restaurantId: Int,
    
    @ColumnInfo(name = "menu_item_id")
    val menuItemId: Int,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,
    
    @ColumnInfo(name = "price_per_item")
    val pricePerItem: Double,
    
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    
    @ColumnInfo(name = "is_vegetarian")
    val isVegetarian: Boolean = false
) {
    fun toCartItem(): CartItem {
        return CartItem(
            id = id,
            restaurantId = restaurantId,
            menuItemId = menuItemId,
            name = name,
            description = description,
            imageUrl = imageUrl,
            pricePerItem = pricePerItem,
            quantity = quantity,
            isVegetarian = isVegetarian
        )
    }
    
    companion object {
        fun fromCartItem(cartItem: CartItem): CartItemEntity {
            return CartItemEntity(
                id = cartItem.id,
                restaurantId = cartItem.restaurantId,
                menuItemId = cartItem.menuItemId,
                name = cartItem.name,
                description = cartItem.description,
                imageUrl = cartItem.imageUrl,
                pricePerItem = cartItem.pricePerItem,
                quantity = cartItem.quantity,
                isVegetarian = cartItem.isVegetarian
            )
        }
    }
}
