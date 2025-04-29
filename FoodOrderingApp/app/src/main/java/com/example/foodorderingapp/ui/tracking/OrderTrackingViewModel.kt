package com.example.foodorderingapp.ui.tracking

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.model.DriverLocation
import com.example.foodorderingapp.data.model.OrderTrackingResponse
import com.example.foodorderingapp.data.model.OrderTrackingUpdate
import com.example.foodorderingapp.data.repository.OrderRepository
import com.example.foodorderingapp.data.socket.OrderTrackingSocket
import com.example.foodorderingapp.data.socket.SocketConnectionState
import com.example.foodorderingapp.util.Constants
import com.example.foodorderingapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the OrderTrackingFragment
 */
@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val TAG = "OrderTrackingViewModel"
    
    // Get orderId from saved state handle
    private val orderId: String = checkNotNull(savedStateHandle["orderId"])
    
    // UI state flow
    private val _uiState = MutableStateFlow(OrderTrackingUIState())
    val uiState: StateFlow<OrderTrackingUIState> = _uiState.asStateFlow()
    
    // Socket instance
    private var trackingSocket: OrderTrackingSocket? = null
    
    init {
        // Fetch initial order data
        fetchOrderDetails()
        
        // Connect to socket and subscribe to updates
        connectToOrderTracking()
    }
    
    /**
     * Fetch order details from API
     */
    fun fetchOrderDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            orderRepository.getOrderTracking(orderId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        result.data?.let { 
                            processOrderResponse(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is NetworkResult.Loading -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }
    
    /**
     * Process initial order response and update UI state
     */
    private fun processOrderResponse(response: OrderTrackingResponse) {
        val orderDetails = OrderTrackingDetailsFactory.fromTrackingResponse(response)
        
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            orderDetails = orderDetails,
            driverInfo = response.driver,
            restaurantInfo = response.restaurant,
            error = null
        )
    }
    
    /**
     * Connect to WebSocket server
     */
    private fun connectToOrderTracking() {
        viewModelScope.launch {
            try {
                // Get socket from repository
                val socket = orderRepository.connectToOrderTracking()
                trackingSocket = socket
                
                // Observe connection state
                socket.connectionState.onEach { state ->
                    _uiState.value = _uiState.value.copy(connectionState = state)
                    
                    // Subscribe to order updates when connected
                    if (state == SocketConnectionState.CONNECTED) {
                        orderRepository.subscribeToOrderUpdates(orderId)
                    }
                }.launchIn(viewModelScope)
                
                // Observe order updates
                socket.orderUpdates.onEach { update ->
                    update?.let { processOrderUpdate(it) }
                }.launchIn(viewModelScope)
                
                // Observe driver location updates
                socket.driverLocationUpdates.onEach { location ->
                    processDriverLocationUpdate(location)
                }.launchIn(viewModelScope)
                
                // Observe error messages
                socket.errorMessage.onEach { errorMsg ->
                    errorMsg?.let {
                        _uiState.value = _uiState.value.copy(error = it)
                    }
                }.launchIn(viewModelScope)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error connecting to socket: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Failed to connect: ${e.message}",
                    connectionState = SocketConnectionState.ERROR
                )
            }
        }
    }
    
    /**
     * Process order update from WebSocket
     */
    private fun processOrderUpdate(update: OrderTrackingUpdate) {
        // Get current UI state
        val currentState = _uiState.value
        val currentOrderDetails = currentState.orderDetails ?: return
        
        // Create updated status history map
        val updatedStatusHistory = currentOrderDetails.statusHistory.toMutableMap()
        updatedStatusHistory[update.status] = update.timestamp
        
        // Create updated order details
        val updatedOrderDetails = currentOrderDetails.copy(
            status = update.status,
            estimatedDeliveryTime = update.estimatedDeliveryTime ?: currentOrderDetails.estimatedDeliveryTime,
            statusHistory = updatedStatusHistory
        )
        
        // Update UI state
        _uiState.value = currentState.copy(
            orderDetails = updatedOrderDetails,
            driverInfo = update.driver ?: currentState.driverInfo
        )
    }
    
    /**
     * Process driver location update from WebSocket
     */
    private fun processDriverLocationUpdate(location: DriverLocation?) {
        // If we have a driver info, update it with new location
        location?.let {
            val currentState = _uiState.value
            val currentDriverInfo = currentState.driverInfo ?: return
            
            val updatedDriverInfo = currentDriverInfo.copy(
                location = it
            )
            
            _uiState.value = currentState.copy(
                driverInfo = updatedDriverInfo
            )
        }
    }
    
    /**
     * Cancel order
     */
    fun cancelOrder() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            orderRepository.cancelOrder(orderId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        // Refresh order details after cancellation
                        fetchOrderDetails()
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is NetworkResult.Loading -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }
    
    /**
     * Format date for display
     */
    fun formatTime(date: Date?): String {
        if (date == null) return ""
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeFormat.format(date)
    }
    
    /**
     * Format estimated delivery time
     */
    fun formatEstimatedDelivery(date: Date?): String {
        if (date == null) return ""
        val now = Date()
        val diffInMillis = date.time - now.time
        
        // If less than 1 minute, show "Any moment now"
        if (diffInMillis < 60 * 1000) {
            return "Any moment now"
        }
        
        // Calculate minutes remaining
        val minutes = (diffInMillis / (1000 * 60)).toInt()
        return if (minutes > 60) {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            "$hours hr ${remainingMinutes} min"
        } else {
            "$minutes min"
        }
    }
    
    /**
     * Check if a status should be shown as active
     */
    fun isStatusActive(status: String): Boolean {
        val currentOrderStatus = _uiState.value.orderDetails?.status ?: return false
        val statusOrder = mapOf(
            Constants.ORDER_PLACED to 0,
            Constants.ORDER_CONFIRMED to 1,
            Constants.ORDER_PREPARING to 2,
            Constants.ORDER_OUT_FOR_DELIVERY to 3,
            Constants.ORDER_DELIVERED to 4,
            Constants.ORDER_CANCELLED to 5
        )
        
        val currentStatusRank = statusOrder[currentOrderStatus] ?: return false
        val checkStatusRank = statusOrder[status] ?: return false
        
        return checkStatusRank <= currentStatusRank
    }
    
    /**
     * Clean up resources when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        orderRepository.disconnectFromOrderTracking()
    }
}