package com.example.foodorderingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.model.Order
import com.example.foodorderingapp.databinding.ItemOrderBinding
import com.example.foodorderingapp.utils.Constants
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = "Order #${order.id}"
                tvRestaurantName.text = order.restaurantName
                tvOrderDate.text = dateFormat.format(order.createdAt)
                tvOrderTotal.text = numberFormat.format(order.total)
                
                // Set status text and color
                tvOrderStatus.text = getFormattedStatus(order.status)
                tvOrderStatus.setTextColor(getStatusColor(order.status))
                
                // Set status background
                statusView.setBackgroundColor(getStatusColor(order.status))
                
                // Set click listener
                root.setOnClickListener {
                    onOrderClick(order)
                }
            }
        }
        
        private fun getFormattedStatus(status: String): String {
            return status.replaceFirstChar { it.uppercase() }
        }
        
        private fun getStatusColor(status: String): Int {
            val colorResId = when (status) {
                Constants.ORDER_STATUS_PENDING -> R.color.warning
                Constants.ORDER_STATUS_PREPARING -> R.color.info
                Constants.ORDER_STATUS_READY -> R.color.primary
                Constants.ORDER_STATUS_DELIVERED -> R.color.success
                Constants.ORDER_STATUS_CANCELLED -> R.color.error
                else -> R.color.secondary_text
            }
            
            return ContextCompat.getColor(binding.root.context, colorResId)
        }
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}