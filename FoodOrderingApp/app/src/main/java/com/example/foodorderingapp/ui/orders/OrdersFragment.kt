package com.example.foodorderingapp.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.models.Order
import com.example.foodorderingapp.databinding.FragmentOrdersBinding
import com.example.foodorderingapp.databinding.ItemOrderBinding
import com.example.foodorderingapp.viewmodel.OrderViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: OrderViewModel
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
        
        // Load orders
        loadOrders()
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter(::onOrderClick)
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ordersAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadOrders()
        }
    }

    private fun observeViewModel() {
        // Observe orders
        viewModel.orders.observe(viewLifecycleOwner, { orders ->
            ordersAdapter.submitList(orders)
            binding.emptyOrdersLayout.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
        })
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
        
        // Observe order details
        viewModel.orderDetails.observe(viewLifecycleOwner, { order ->
            order?.let {
                showOrderDetailsDialog(it)
            }
        })
        
        // Observe errors
        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun loadOrders() {
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.getUserOrders()
    }

    private fun onOrderClick(order: Order) {
        viewModel.getOrderDetails(order.id)
    }

    private fun showOrderDetailsDialog(order: Order) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        val orderDate = dateFormat.format(order.createdAt)
        
        val items = order.items.joinToString("\n") { item ->
            "${item.quantity}x ${item.name} - $${String.format("%.2f", item.pricePerItem * item.quantity)}"
        }
        
        val message = """
            |Order #${order.id}
            |Date: $orderDate
            |Restaurant: ${order.restaurantName}
            |Status: ${order.status.capitalize()}
            |
            |Items:
            |$items
            |
            |Delivery Address:
            |${order.deliveryAddress}
            |
            |Payment Method: ${order.paymentMethod}
            |
            |Total: $${String.format("%.2f", order.totalPrice)}
        """.trimMargin()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Order Details")
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class OrdersAdapter(private val onOrderClick: (Order) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<Order, OrdersAdapter.OrderViewHolder>(
        OrderDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    onOrderClick(getItem(position))
                }
            }
        }

        fun bind(order: Order) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            
            binding.tvOrderId.text = "Order #${order.id}"
            binding.tvRestaurantName.text = order.restaurantName
            binding.tvOrderDate.text = dateFormat.format(order.createdAt)
            binding.tvOrderTime.text = timeFormat.format(order.createdAt)
            binding.tvTotalPrice.text = "$${String.format("%.2f", order.totalPrice)}"
            binding.tvItemCount.text = "${order.itemCount} items"
            
            // Set status and color
            binding.tvStatus.text = order.status.capitalize()
            val statusColor = when (order.status) {
                "pending" -> R.color.colorPending
                "confirmed" -> R.color.colorConfirmed
                "preparing" -> R.color.colorPreparing
                "ready" -> R.color.colorReady
                "delivered" -> R.color.colorDelivered
                "cancelled" -> R.color.colorCancelled
                else -> R.color.colorPending
            }
            binding.tvStatus.setTextColor(binding.root.context.getColor(statusColor))
            
            // Show appropriate icon based on status
            val statusIcon = when (order.status) {
                "pending" -> R.drawable.ic_pending
                "confirmed" -> R.drawable.ic_confirmed
                "preparing" -> R.drawable.ic_preparing
                "ready" -> R.drawable.ic_ready
                "delivered" -> R.drawable.ic_delivered
                "cancelled" -> R.drawable.ic_cancelled
                else -> R.drawable.ic_pending
            }
            binding.ivStatusIcon.setImageResource(statusIcon)
        }
    }

    private class OrderDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
