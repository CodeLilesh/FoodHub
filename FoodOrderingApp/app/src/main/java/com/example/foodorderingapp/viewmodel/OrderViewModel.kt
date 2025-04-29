package com.example.foodorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.data.models.Order
import com.example.foodorderingapp.data.repository.OrderRepository
import com.example.foodorderingapp.utils.Result
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = OrderRepository(application)
    
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders
    
    private val _orderDetails = MutableLiveData<Order?>()
    val orderDetails: LiveData<Order?> get() = _orderDetails
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    
    fun getUserOrders() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getUserOrders()) {
                is Result.Success -> {
                    _orders.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun getOrderDetails(orderId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getOrderById(orderId)) {
                is Result.Success -> {
                    _orderDetails.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun clearOrderDetails() {
        _orderDetails.value = null
    }
}
