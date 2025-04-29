package com.example.foodorderingapp.ui.tracking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.model.OrderItem
import com.example.foodorderingapp.data.socket.SocketConnectionState
import com.example.foodorderingapp.databinding.FragmentOrderTrackingBinding
import com.example.foodorderingapp.ui.adapters.OrderItemAdapter
import com.example.foodorderingapp.util.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Fragment for tracking a specific order with real-time updates
 */
@AndroidEntryPoint
class OrderTrackingFragment : Fragment() {
    private var _binding: FragmentOrderTrackingBinding? = null
    private val binding get() = _binding!!
    
    private val args: OrderTrackingFragmentArgs by navArgs()
    private val viewModel: OrderTrackingViewModel by viewModels()
    
    private lateinit var orderItemAdapter: OrderItemAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeUiState()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter()
        binding.rvOrderItems.apply {
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupListeners() {
        // Call restaurant button
        binding.btnCallRestaurant.setOnClickListener {
            viewModel.uiState.value.restaurantInfo?.phone?.let { phone ->
                dialPhoneNumber(phone)
            }
        }
        
        // Call driver button
        binding.btnCallDriver.setOnClickListener {
            viewModel.uiState.value.driverInfo?.phone?.let { phone ->
                dialPhoneNumber(phone)
            }
        }
        
        // Cancel order button
        binding.btnCancelOrder.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }
    
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateLoadingState(state.isLoading)
                    updateConnectionState(state.connectionState)
                    
                    state.orderDetails?.let { details ->
                        updateOrderDetails(details)
                    }
                    
                    state.driverInfo?.let { driver ->
                        updateDriverInfo(driver)
                    }
                    
                    state.error?.let { error ->
                        showError(error)
                    }
                }
            }
        }
    }
    
    private fun updateLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvError.visibility = View.GONE
    }
    
    private fun updateConnectionState(state: SocketConnectionState) {
        when (state) {
            SocketConnectionState.CONNECTED -> {
                // Connected state, no UI changes needed
            }
            SocketConnectionState.CONNECTING -> {
                // Show loading state for initial connection
            }
            SocketConnectionState.DISCONNECTED -> {
                // Show reconnecting message or subtle indicator
                Toast.makeText(requireContext(), "Connection lost. Reconnecting...", Toast.LENGTH_SHORT).show()
            }
            SocketConnectionState.ERROR -> {
                // Show error state
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
    
    private fun updateOrderDetails(details: OrderTrackingDetails) {
        // Order ID
        binding.tvOrderId.text = getString(R.string.order_id, details.orderId)
        
        // Estimated delivery time
        val estimatedDelivery = viewModel.formatEstimatedDelivery(details.estimatedDeliveryTime)
        binding.tvDeliveryTime.text = estimatedDelivery
        
        // Order status indicators
        updateStatusIndicators(details)
        
        // Order items
        orderItemAdapter.submitList(details.items)
        
        // Total price
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        binding.tvOrderTotal.text = formatter.format(details.totalPrice)
        
        // Delivery address
        binding.tvDeliveryAddress.text = details.address
        
        // Cancel button visibility
        binding.btnCancelOrder.isEnabled = details.canBeCancelled
    }
    
    private fun updateStatusIndicators(details: OrderTrackingDetails) {
        // Order placed
        updateStatusIndicator(
            Constants.ORDER_PLACED,
            binding.statusOrderPlaced,
            binding.tvOrderPlacedStatus,
            binding.tvOrderPlacedTime,
            details
        )
        
        // Order confirmed
        updateStatusIndicator(
            Constants.ORDER_CONFIRMED,
            binding.statusOrderConfirmed,
            binding.tvOrderConfirmedStatus,
            binding.tvOrderConfirmedTime,
            details
        )
        
        // Order preparing
        updateStatusIndicator(
            Constants.ORDER_PREPARING,
            binding.statusOrderPreparing,
            binding.tvOrderPreparingStatus,
            binding.tvOrderPreparingTime,
            details
        )
        
        // Order out for delivery
        updateStatusIndicator(
            Constants.ORDER_OUT_FOR_DELIVERY,
            binding.statusOrderOutForDelivery,
            binding.tvOrderOutForDeliveryStatus,
            binding.tvOrderOutForDeliveryTime,
            details
        )
        
        // Order delivered
        updateStatusIndicator(
            Constants.ORDER_DELIVERED,
            binding.statusOrderDelivered,
            binding.tvOrderDeliveredStatus,
            binding.tvOrderDeliveredTime,
            details
        )
    }
    
    private fun updateStatusIndicator(
        status: String,
        statusView: View,
        statusTextView: android.widget.TextView,
        timeTextView: android.widget.TextView,
        details: OrderTrackingDetails
    ) {
        val isActive = viewModel.isStatusActive(status)
        val timestamp = details.statusHistory[status]
        
        // Update status indicator
        statusView.setBackgroundResource(
            if (isActive) R.drawable.bg_status_active else R.drawable.bg_status_inactive
        )
        
        // Update status text style
        statusTextView.setTypeface(statusTextView.typeface, if (isActive) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        
        // Update timestamp
        timeTextView.text = if (timestamp != null) {
            viewModel.formatTime(timestamp)
        } else {
            ""
        }
    }
    
    private fun updateDriverInfo(driver: com.example.foodorderingapp.data.model.DriverInfo) {
        binding.layoutDeliveryPerson.visibility = View.VISIBLE
        binding.tvDriverName.text = driver.name
        binding.btnCallDriver.visibility = View.VISIBLE
    }
    
    private fun showError(error: String) {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = error
        
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
    
    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Cannot make calls from this device", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showCancelConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel Order")
            .setMessage("Are you sure you want to cancel this order? This action cannot be undone.")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                viewModel.cancelOrder()
            }
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}