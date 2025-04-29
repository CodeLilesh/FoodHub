package com.example.foodorderingapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.models.OrderItem
import com.example.foodorderingapp.util.toCurrencyFormat

/**
 * Adapter for displaying order items in the OrderTracking screen
 */
class OrderItemAdapter : ListAdapter<OrderItem, OrderItemAdapter.OrderItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        private val itemNameText: TextView = itemView.findViewById(R.id.itemNameText)
        private val itemQuantityText: TextView = itemView.findViewById(R.id.itemQuantityText)
        private val itemSpecialInstructionsText: TextView = itemView.findViewById(R.id.itemSpecialInstructionsText)
        private val itemPriceText: TextView = itemView.findViewById(R.id.itemPriceText)

        fun bind(item: OrderItem) {
            itemNameText.text = item.name
            itemQuantityText.text = "Qty: ${item.quantity}"
            itemPriceText.text = (item.price * item.quantity).toCurrencyFormat()

            if (!item.specialInstructions.isNullOrEmpty()) {
                itemSpecialInstructionsText.visibility = View.VISIBLE
                itemSpecialInstructionsText.text = item.specialInstructions
            } else {
                itemSpecialInstructionsText.visibility = View.GONE
            }

            // Load image with Glide
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(itemImageView)
            } else {
                itemImageView.setImageResource(R.drawable.placeholder_food)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.itemId == newItem.itemId && oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }
    }
}