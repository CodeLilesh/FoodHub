package com.example.foodorderingapp.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.socket.ConnectionState
import com.example.foodorderingapp.databinding.FragmentOrderTrackingBinding
import com.example.foodorderingapp.ui.adapters.OrderItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment for tracking order status in real-time using WebSockets
 */
@AndroidEntryPoint
class OrderTrackingFragment : Fragment() {

    private var _binding: FragmentOrderTrackingBinding? = null
    private val binding get() = _binding!!

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
        setupClickListeners()
        observeState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter()
        binding.orderItemsRecyclerView.apply {
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.retryButton.setOnClickListener {
            viewModel.reconnectTracking()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trackingState.collectLatest { state ->
                    updateLoadingState(state.isLoading)
                    updateConnectionStatus(state.connectionState)
                    updateErrorState(state.hasError, state.error)
                    
                    if (!state.isLoading && state.order != null) {
                        updateOrderInfo(state)
                        updateOrderStatus(state)
                        updateOrderItems(state)
                    }
                }
            }
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.orderInfoCard.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.orderTimelineCard.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.orderItemsCard.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun updateConnectionStatus(connectionState: ConnectionState) {
        val (text, color, icon) = when (connectionState) {
            ConnectionState.CONNECTED -> Triple(
                "Connected - Live Updates",
                ContextCompat.getColor(requireContext(), R.color.green),
                R.drawable.ic_connected
            )
            ConnectionState.CONNECTING -> Triple(
                "Connecting...",
                ContextCompat.getColor(requireContext(), R.color.orange),
                R.drawable.ic_connecting
            )
            ConnectionState.DISCONNECTED -> Triple(
                "Disconnected",
                ContextCompat.getColor(requireContext(), R.color.gray),
                R.drawable.ic_disconnected
            )
            ConnectionState.ERROR -> Triple(
                "Connection Error",
                ContextCompat.getColor(requireContext(), R.color.red),
                R.drawable.ic_error
            )
        }

        binding.connectionStatusText.text = text
        binding.connectionStatusText.setTextColor(color)
        binding.connectionStatusIcon.setImageResource(icon)
        binding.connectionStatusIcon.setColorFilter(color)
    }

    private fun updateErrorState(hasError: Boolean, errorMessage: String?) {
        binding.errorCard.visibility = if (hasError) View.VISIBLE else View.GONE
        binding.errorMessageText.text = errorMessage ?: "Unknown error occurred"
    }

    private fun updateOrderInfo(state: OrderTrackingState) {
        state.order?.let { order ->
            binding.restaurantNameText.text = order.restaurantName
            binding.orderIdText.text = "#${order.id}"
            binding.orderStatusText.text = order.status.replaceFirstChar { it.uppercase() }
            binding.estimatedTimeText.text = state.estimatedTimeText
        }
    }

    private fun updateOrderStatus(state: OrderTrackingState) {
        // Reset all icons to inactive state
        resetAllStatusIcons()
        
        // Highlight completed phases
        when (state.currentPhase) {
            TrackingPhase.ORDER_PLACED -> {
                setPhaseActive(binding.orderPlacedIcon, binding.orderPlacedLayout)
            }
            TrackingPhase.PREPARING -> {
                setPhaseActive(binding.orderPlacedIcon, binding.orderPlacedLayout)
                setPhaseActive(binding.preparingIcon, binding.preparingLayout)
            }
            TrackingPhase.READY -> {
                setPhaseActive(binding.orderPlacedIcon, binding.orderPlacedLayout)
                setPhaseActive(binding.preparingIcon, binding.preparingLayout)
                setPhaseActive(binding.readyIcon, binding.readyLayout)
            }
            TrackingPhase.ON_THE_WAY -> {
                setPhaseActive(binding.orderPlacedIcon, binding.orderPlacedLayout)
                setPhaseActive(binding.preparingIcon, binding.preparingLayout)
                setPhaseActive(binding.readyIcon, binding.readyLayout)
                setPhaseActive(binding.onTheWayIcon, binding.onTheWayLayout)
            }
            TrackingPhase.DELIVERED -> {
                setPhaseActive(binding.orderPlacedIcon, binding.orderPlacedLayout)
                setPhaseActive(binding.preparingIcon, binding.preparingLayout)
                setPhaseActive(binding.readyIcon, binding.readyLayout)
                setPhaseActive(binding.onTheWayIcon, binding.onTheWayLayout)
                setPhaseActive(binding.deliveredIcon, binding.deliveredLayout)
            }
            TrackingPhase.CANCELLED -> {
                binding.orderStatusText.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
            }
            TrackingPhase.ERROR -> {
                // Error state is handled separately
            }
        }
    }

    private fun resetAllStatusIcons() {
        val grayColor = ContextCompat.getColor(requireContext(), R.color.light_gray)
        
        binding.orderPlacedIcon.setImageResource(R.drawable.ic_circle)
        binding.orderPlacedIcon.setColorFilter(grayColor)
        
        binding.preparingIcon.setImageResource(R.drawable.ic_circle)
        binding.preparingIcon.setColorFilter(grayColor)
        
        binding.readyIcon.setImageResource(R.drawable.ic_circle)
        binding.readyIcon.setColorFilter(grayColor)
        
        binding.onTheWayIcon.setImageResource(R.drawable.ic_circle)
        binding.onTheWayIcon.setColorFilter(grayColor)
        
        binding.deliveredIcon.setImageResource(R.drawable.ic_circle)
        binding.deliveredIcon.setColorFilter(grayColor)
    }

    private fun setPhaseActive(icon: View, layout: View) {
        val greenColor = ContextCompat.getColor(requireContext(), R.color.green)
        
        if (icon is android.widget.ImageView) {
            icon.setImageResource(R.drawable.ic_check_circle)
            icon.setColorFilter(greenColor)
        }
        
        // Also set the text color to active
        val textView = (layout as? ViewGroup)?.getChildAt(1) as? android.widget.TextView
        textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun updateOrderItems(state: OrderTrackingState) {
        orderItemAdapter.submitList(state.orderItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}