package com.example.foodorderingapp.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentOrderConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConfirmationFragment : Fragment() {

    private var _binding: FragmentOrderConfirmationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderConfirmationViewModel by viewModels()
    private val args: OrderConfirmationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupObservers()
        
        // Load order details
        viewModel.loadOrder(args.orderId)
    }
    
    private fun setupUI() {
        binding.btnBackToHome.setOnClickListener {
            // Navigate back to home screen, clearing the backstack
            findNavController().navigate(
                OrderConfirmationFragmentDirections.actionOrderConfirmationFragmentToHomeFragment()
            )
        }
        
        binding.btnTrackOrder.setOnClickListener {
            // Navigate to order tracking/detail screen
            findNavController().navigate(
                R.id.ordersFragment
            )
        }
    }
    
    private fun setupObservers() {
        viewModel.order.observe(viewLifecycleOwner) { order ->
            order?.let {
                binding.tvOrderNumber.text = "Order #${order.id}"
                binding.tvDeliveryTime.text = "${order.estimatedDeliveryTime} minutes"
                binding.tvAddress.text = order.deliveryAddress
            }
        }
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Display loading state if needed
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            // Display error if needed
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}