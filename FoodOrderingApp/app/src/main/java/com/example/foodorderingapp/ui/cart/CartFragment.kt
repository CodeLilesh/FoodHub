package com.example.foodorderingapp.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.data.models.CartItem
import com.example.foodorderingapp.databinding.FragmentCartBinding
import com.example.foodorderingapp.databinding.ItemCartBinding
import com.example.foodorderingapp.viewmodel.CartViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CartFragment : Fragment(), CartAdapter.CartItemClickListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        // Load cart items
        viewModel.refreshCartItems()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(this)
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun setupClickListeners() {
        // Checkout button
        binding.btnCheckout.setOnClickListener {
            if (binding.etDeliveryAddress.text.toString().trim().isEmpty()) {
                binding.etDeliveryAddress.error = "Delivery address is required"
                return@setOnClickListener
            }
            
            showPaymentMethodDialog()
        }
    }

    private fun showPaymentMethodDialog() {
        val paymentMethods = arrayOf("Cash on Delivery", "Credit Card", "PayPal")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Payment Method")
            .setItems(paymentMethods) { _, which ->
                val paymentMethod = paymentMethods[which]
                val deliveryAddress = binding.etDeliveryAddress.text.toString().trim()
                val specialInstructions = binding.etSpecialInstructions.text.toString().trim()
                
                viewModel.placeOrder(deliveryAddress, paymentMethod, specialInstructions)
            }
            .show()
    }

    private fun observeViewModel() {
        // Observe cart items
        viewModel.cartItems.observe(viewLifecycleOwner, { items ->
            cartAdapter.submitList(items)
            updateCartSummary(items)
            
            // Show/hide empty cart message
            binding.emptyCartLayout.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            binding.cartContentLayout.visibility = if (items.isNotEmpty()) View.VISIBLE else View.GONE
        })
        
        // Observe order placement result
        viewModel.orderPlacementResult.observe(viewLifecycleOwner, { result ->
            if (result.success) {
                // Order placed successfully
                Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_LONG).show()
                
                // Clear inputs
                binding.etDeliveryAddress.text?.clear()
                binding.etSpecialInstructions.text?.clear()
                
                // Reset cart UI
                viewModel.refreshCartItems()
            } else {
                // Order placement failed
                Toast.makeText(context, result.message ?: "Failed to place order", Toast.LENGTH_LONG).show()
            }
        })
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCheckout.isEnabled = !isLoading
        })
        
        // Observe errors
        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateCartSummary(items: List<CartItem>) {
        var subtotal = 0.0
        var itemCount = 0
        
        for (item in items) {
            subtotal += item.pricePerItem * item.quantity
            itemCount += item.quantity
        }
        
        // Apply delivery fee if cart is not empty
        val deliveryFee = if (items.isNotEmpty()) 2.99 else 0.0
        val total = subtotal + deliveryFee
        
        binding.tvSubtotal.text = "$${String.format("%.2f", subtotal)}"
        binding.tvDeliveryFee.text = "$${String.format("%.2f", deliveryFee)}"
        binding.tvTotal.text = "$${String.format("%.2f", total)}"
        binding.tvItemCount.text = "($itemCount items)"
    }

    override fun onQuantityChanged(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            // Show confirmation dialog for removal
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove this item from your cart?")
                .setPositiveButton("Remove") { _, _ ->
                    viewModel.removeFromCart(cartItem.id)
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            // Update quantity
            viewModel.updateCartItemQuantity(cartItem.id, newQuantity)
        }
    }

    override fun onRemoveClick(cartItem: CartItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Item")
            .setMessage("Are you sure you want to remove this item from your cart?")
            .setPositiveButton("Remove") { _, _ ->
                viewModel.removeFromCart(cartItem.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CartAdapter(private val listener: CartItemClickListener) :
    androidx.recyclerview.widget.ListAdapter<CartItem, CartAdapter.CartViewHolder>(
        CartItemDiffCallback()
    ) {

    interface CartItemClickListener {
        fun onQuantityChanged(cartItem: CartItem, newQuantity: Int)
        fun onRemoveClick(cartItem: CartItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.tvItemName.text = cartItem.name
            binding.tvItemPrice.text = "$${String.format("%.2f", cartItem.pricePerItem)}"
            binding.tvQuantity.text = cartItem.quantity.toString()
            binding.tvTotalPrice.text = "$${String.format("%.2f", cartItem.pricePerItem * cartItem.quantity)}"
            
            // Load item image with Coil
            if (!cartItem.imageUrl.isNullOrEmpty()) {
                coil.load(binding.ivItem, cartItem.imageUrl) {
                    crossfade(true)
                    placeholder(com.example.foodorderingapp.R.drawable.placeholder_food)
                    error(com.example.foodorderingapp.R.drawable.placeholder_food)
                }
            } else {
                binding.ivItem.setImageResource(com.example.foodorderingapp.R.drawable.placeholder_food)
            }

            // Set click listeners for quantity controls
            binding.btnDecrement.setOnClickListener {
                val currentQuantity = cartItem.quantity
                if (currentQuantity > 0) {
                    listener.onQuantityChanged(cartItem, currentQuantity - 1)
                }
            }

            binding.btnIncrement.setOnClickListener {
                val currentQuantity = cartItem.quantity
                listener.onQuantityChanged(cartItem, currentQuantity + 1)
            }

            binding.btnRemove.setOnClickListener {
                listener.onRemoveClick(cartItem)
            }
        }
    }

    private class CartItemDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}

// Extension function for Coil image loading
fun coil.load(imageView: android.widget.ImageView, url: String, builder: coil.request.ImageRequest.Builder.() -> Unit = {}) {
    val request = coil.request.ImageRequest.Builder(imageView.context)
        .data(url)
        .target(imageView)
        .apply(builder)
        .build()
    coil.ImageLoader(imageView.context).enqueue(request)
}
