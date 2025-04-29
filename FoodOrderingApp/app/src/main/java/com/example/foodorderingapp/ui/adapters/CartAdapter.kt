package com.example.foodorderingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.model.CartItem
import com.example.foodorderingapp.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val onRemoveClick: (CartItem) -> Unit,
    private val onQuantityChange: (CartItem, Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                tvItemName.text = cartItem.name
                tvItemPrice.text = numberFormat.format(cartItem.price * cartItem.quantity)
                tvQuantity.text = cartItem.quantity.toString()

                // Load food image
                Glide.with(ivItemImage.context)
                    .load(cartItem.imageUrl)
                    .placeholder(R.drawable.placeholder_restaurant)
                    .error(R.drawable.placeholder_restaurant)
                    .centerCrop()
                    .into(ivItemImage)

                // Setup remove button
                btnRemove.setOnClickListener {
                    onRemoveClick(cartItem)
                }

                // Setup quantity controls
                btnDecrease.setOnClickListener {
                    if (cartItem.quantity > 1) {
                        onQuantityChange(cartItem, cartItem.quantity - 1)
                    } else {
                        onRemoveClick(cartItem)
                    }
                }

                btnIncrease.setOnClickListener {
                    onQuantityChange(cartItem, cartItem.quantity + 1)
                }
            }
        }
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}