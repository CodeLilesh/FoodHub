package com.example.foodorderingapp.ui.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.model.Order
import com.example.foodorderingapp.data.repository.OrderRepository
import com.example.foodorderingapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderConfirmationViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _order = MutableLiveData<Order?>()
    val order: LiveData<Order?> = _order

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            when (val result = orderRepository.getOrderById(orderId)) {
                is NetworkResult.Success -> {
                    _order.value = result.data
                }
                is NetworkResult.Error -> {
                    _error.value = result.message
                }
            }
            
            _loading.value = false
        }
    }
}