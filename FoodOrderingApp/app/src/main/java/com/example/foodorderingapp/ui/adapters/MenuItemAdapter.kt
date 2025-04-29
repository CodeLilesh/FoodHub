package com.example.foodorderingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.model.MenuItem
import com.example.foodorderingapp.databinding.ItemMenuBinding
import java.text.NumberFormat
import java.util.Locale

class MenuItemAdapter(
    private val onItemClick: (MenuItem) -> Unit,
    private val onAddToCartClick: (MenuItem) -> Unit
) : ListAdapter<MenuItem, MenuItemAdapter.MenuItemViewHolder>(MenuItemDiffCallback()) {

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = ItemMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = getItem(position)
        holder.bind(menuItem)
    }

    inner class MenuItemViewHolder(
        private val binding: ItemMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem) {
            binding.apply {
                tvItemName.text = menuItem.name
                tvItemDescription.text = menuItem.description
                tvItemPrice.text = numberFormat.format(menuItem.price)
                
                // Load food image
                Glide.with(ivFoodImage.context)
                    .load(menuItem.imageUrl)
                    .placeholder(R.drawable.placeholder_restaurant)
                    .error(R.drawable.placeholder_restaurant)
                    .centerCrop()
                    .into(ivFoodImage)
                
                // Set click listeners
                root.setOnClickListener {
                    onItemClick(menuItem)
                }
                
                btnAddToCart.setOnClickListener {
                    onAddToCartClick(menuItem)
                }
            }
        }
    }
}

class MenuItemDiffCallback : DiffUtil.ItemCallback<MenuItem>() {
    override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
        return oldItem == newItem
    }
}