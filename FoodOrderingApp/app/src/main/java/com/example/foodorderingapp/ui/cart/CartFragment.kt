package com.example.foodorderingapp.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.foodorderingapp.databinding.FragmentCartBinding
import com.example.foodorderingapp.ui.adapters.CartAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()
    
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
        
        setupUI()
        setupObservers()
    }
    
    private fun setupUI() {
        cartAdapter = CartAdapter(
            onRemoveClick = { cartItem ->
                showRemoveItemDialog(cartItem.name) {
                    viewModel.removeFromCart(cartItem)
                }
            },
            onQuantityChange = { cartItem, quantity ->
                viewModel.updateQuantity(cartItem, quantity)
            }
        )
        
        binding.rvCartItems.adapter = cartAdapter
        
        binding.btnCheckout.setOnClickListener {
            viewModel.restaurant.value?.let { restaurant ->
                findNavController().navigate(
                    CartFragmentDirections.actionCartFragmentToCheckoutFragment(restaurant.id)
                )
            }
        }
    }
    
    private fun setupObservers() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items)
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
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
        
        viewModel.cartEmpty.observe(viewLifecycleOwner) { isEmpty ->
            binding.rvCartItems.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.cardOrder.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }
    
    private fun showRemoveItemDialog(itemName: String, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Item")
            .setMessage("Are you sure you want to remove $itemName from your cart?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Remove") { _, _ ->
                onConfirm()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}