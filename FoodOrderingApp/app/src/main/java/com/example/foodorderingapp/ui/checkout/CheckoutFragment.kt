package com.example.foodorderingapp.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodorderingapp.databinding.FragmentCheckoutBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckoutViewModel by viewModels()
    private val args: CheckoutFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupUI()
        setupObservers()
        
        // Load data
        viewModel.loadCheckoutData(args.restaurantId)
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupUI() {
        binding.btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }
    
    private fun placeOrder() {
        val deliveryAddress = binding.etDeliveryAddress.text.toString().trim()
        val contactPhone = binding.etPhone.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        
        // Get selected payment method
        val paymentMethod = when (binding.rgPaymentMethod.checkedRadioButtonId) {
            binding.rbCashOnDelivery.id -> "Cash on Delivery"
            binding.rbCreditCard.id -> "Credit Card"
            else -> "Cash on Delivery" // Default
        }
        
        // Validate inputs
        if (viewModel.validateInputs(deliveryAddress, contactPhone)) {
            // Place order
            viewModel.placeOrder(deliveryAddress, contactPhone, paymentMethod, notes.ifEmpty { null })
        }
    }
    
    private fun setupObservers() {
        viewModel.restaurant.observe(viewLifecycleOwner) { restaurant ->
            binding.tvRestaurantNameValue.text = restaurant?.name
        }
        
        viewModel.itemCount.observe(viewLifecycleOwner) { count ->
            binding.tvItemCountValue.text = count
        }
        
        viewModel.formattedSubtotal.observe(viewLifecycleOwner) { subtotal ->
            binding.tvSubtotalValue.text = subtotal
        }
        
        viewModel.formattedDeliveryFee.observe(viewLifecycleOwner) { deliveryFee ->
            binding.tvDeliveryFeeValue.text = deliveryFee
        }
        
        viewModel.formattedTotal.observe(viewLifecycleOwner) { total ->
            binding.tvTotalValue.text = total
        }
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnPlaceOrder.isEnabled = !isLoading
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
        
        viewModel.orderPlaced.observe(viewLifecycleOwner) { order ->
            order?.let {
                findNavController().navigate(
                    CheckoutFragmentDirections.actionCheckoutFragmentToOrderConfirmationFragment(order.id)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}