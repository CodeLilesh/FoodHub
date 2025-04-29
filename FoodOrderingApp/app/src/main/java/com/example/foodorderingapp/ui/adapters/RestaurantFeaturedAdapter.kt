package com.example.foodorderingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.model.Restaurant
import com.example.foodorderingapp.databinding.ItemRestaurantFeaturedBinding

class RestaurantFeaturedAdapter(
    private val onRestaurantClick: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantFeaturedAdapter.FeaturedRestaurantViewHolder>(FeaturedRestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedRestaurantViewHolder {
        val binding = ItemRestaurantFeaturedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeaturedRestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeaturedRestaurantViewHolder, position: Int) {
        val restaurant = getItem(position)
        holder.bind(restaurant)
        
        holder.itemView.setOnClickListener {
            onRestaurantClick(restaurant)
        }
    }

    inner class FeaturedRestaurantViewHolder(
        private val binding: ItemRestaurantFeaturedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(restaurant: Restaurant) {
            binding.apply {
                tvRestaurantName.text = restaurant.name
                tvRestaurantCategory.text = restaurant.cuisine
                ratingBar.rating = restaurant.rating.toFloat()
                
                // Load restaurant image
                Glide.with(ivRestaurantImage.context)
                    .load(restaurant.imageUrl)
                    .placeholder(R.drawable.placeholder_restaurant)
                    .error(R.drawable.placeholder_restaurant)
                    .centerCrop()
                    .into(ivRestaurantImage)
            }
        }
    }
}

class FeaturedRestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }
}