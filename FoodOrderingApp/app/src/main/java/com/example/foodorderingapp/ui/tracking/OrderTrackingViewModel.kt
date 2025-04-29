package com.example.foodorderingapp.ui.tracking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.models.Order
import com.example.foodorderingapp.data.models.OrderItem
import com.example.foodorderingapp.data.repository.OrderRepository
import com.example.foodorderingapp.data.socket.OrderStatusUpdate
import com.example.foodorderingapp.data.socket.OrderTrackingSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Order Tracking screen
 * Manages the WebSocket connection and updates the UI state based on socket events
 */
@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val orderTrackingSocket: OrderTrackingSocket,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get orderId from navigation arguments
    private val orderId: String = checkNotNull(savedStateHandle["orderId"])

    // UI state management
    private val _trackingState = MutableStateFlow(OrderTrackingState())
    val trackingState: StateFlow<OrderTrackingState> = _trackingState.asStateFlow()

    init {
        // Load order details and establish WebSocket connection
        viewModelScope.launch {
            loadOrderDetails()
            observeWebSocketConnection()
            observeOrderStatusUpdates()
            connectToOrderTracking()
        }
    }

    /**
     * Fetch the order details from the repository
     */
    private suspend fun loadOrderDetails() {
        try {
            _trackingState.update { it.copy(isLoading = true) }
            
            // Retrieve order details and items
            val order = orderRepository.getOrderById(orderId)
            val orderItems = orderRepository.getOrderItems(orderId)
            
            if (order != null) {
                _trackingState.update { 
                    it.copy(
                        isLoading = false,
                        order = order,
                        orderItems = orderItems,
                        orderStatus = order.getOrderStatus(),
                        estimatedDeliveryTimeMinutes = order.estimatedDeliveryTime
                    )
                }
            } else {
                _trackingState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Order not found"
                    )
                }
            }
        } catch (e: Exception) {
            _trackingState.update { 
                it.copy(
                    isLoading = false, 
                    error = "Error loading order: ${e.message}"
                ) 
            }
        }
    }

    /**
     * Connect to the WebSocket for real-time order tracking
     */
    private fun connectToOrderTracking() {
        orderTrackingSocket.connectToOrderTracking(orderId)
    }

    /**
     * Observe WebSocket connection state changes
     */
    private fun observeWebSocketConnection() {
        viewModelScope.launch {
            orderTrackingSocket.connectionState.collect { connectionState ->
                _trackingState.update { it.copy(connectionState = connectionState) }
            }
        }
    }

    /**
     * Observe order status updates from the WebSocket
     */
    private fun observeOrderStatusUpdates() {
        viewModelScope.launch {
            orderTrackingSocket.orderStatus.collect { statusUpdate ->
                statusUpdate?.let { update ->
                    _trackingState.update {
                        it.copy(
                            orderStatus = update.status,
                            estimatedDeliveryTimeMinutes = update.estimatedDeliveryTimeMinutes,
                            statusMessage = update.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Refresh the order data from the server
     */
    fun refreshOrderDetails() {
        viewModelScope.launch {
            loadOrderDetails()
        }
    }

    /**
     * Manually reconnect to the WebSocket
     * Used when the connection is lost or when there's an error
     */
    fun reconnectTracking() {
        connectToOrderTracking()
    }

    /**
     * Clean up resources when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        orderTrackingSocket.disconnectFromTracking()
    }
}