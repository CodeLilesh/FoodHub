package com.example.foodorderingapp.ui.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentRestaurantDetailBinding
import com.example.foodorderingapp.ui.adapters.CategoryAdapter
import com.example.foodorderingapp.ui.adapters.MenuItemAdapter
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class RestaurantDetailFragment : Fragment() {

    private var _binding: FragmentRestaurantDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantDetailViewModel by viewModels()
    private val args: RestaurantDetailFragmentArgs by navArgs()
    
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var menuItemAdapter: MenuItemAdapter
    private var cartBadge: BadgeDrawable? = null
    
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupUI()
        setupObservers()
        
        // Load restaurant data
        viewModel.loadRestaurant(args.restaurantId)
        viewModel.loadCartItemCount()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupUI() {
        // Setup category adapter
        categoryAdapter = CategoryAdapter { category ->
            viewModel.filterMenuByCategory(category)
        }
        binding.rvMenuCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        
        // Setup menu item adapter
        menuItemAdapter = MenuItemAdapter(
            onItemClick = { menuItem ->
                // Navigate to menu item detail
                findNavController().navigate(
                    RestaurantDetailFragmentDirections.actionRestaurantDetailFragmentToMenuItemDetailFragment(
                        menuItem.id,
                        args.restaurantId
                    )
                )
            },
            onAddToCartClick = { menuItem ->
                // Add to cart with confirmation
                checkCartAndAddItem(menuItem)
            }
        )
        binding.rvMenuItems.adapter = menuItemAdapter
        
        // Setup FAB to navigate to cart
        binding.fabCart.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }
    }
    
    private fun checkCartAndAddItem(menuItem: com.example.foodorderingapp.data.model.MenuItem) {
        // Check if cartItemCount > 0 and restaurant ID matches current restaurant
        // If not, ask user if they want to clear cart before adding
        if (viewModel.cartItemCount.value ?: 0 > 0 && 
            viewModel.restaurant.value?.id != args.restaurantId) {
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear Cart?")
                .setMessage("Your cart contains items from another restaurant. Would you like to clear your cart and add this item?")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Clear & Add") { _, _ ->
                    viewModel.clearCart()
                    viewModel.addToCart(menuItem)
                }
                .show()
        } else {
            // Add to cart directly
            viewModel.addToCart(menuItem)
        }
    }
    
    private fun setupObservers() {
        viewModel.restaurant.observe(viewLifecycleOwner) { restaurant ->
            binding.apply {
                collapsingToolbar.title = restaurant.name
                tvRestaurantName.text = restaurant.name
                tvRestaurantCuisine.text = restaurant.cuisine
                ratingBar.rating = restaurant.rating.toFloat()
                tvRestaurantRating.text = "${restaurant.rating} (${restaurant.ratingCount}+ ratings)"
                tvRestaurantDeliveryTime.text = "${restaurant.deliveryTime} min"
                tvDeliveryFee.text = "Delivery Fee: ${numberFormat.format(restaurant.deliveryFee)}"
                tvRestaurantAddress.text = restaurant.address
                
                // Load restaurant image
                Glide.with(requireContext())
                    .load(restaurant.imageUrl)
                    .placeholder(R.drawable.placeholder_restaurant)
                    .error(R.drawable.placeholder_restaurant)
                    .centerCrop()
                    .into(ivRestaurantHeader)
            }
        }
        
        viewModel.menuCategories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }
        
        viewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
            menuItemAdapter.submitList(menuItems)
            
            // Show/hide empty state
            binding.tvEmpty.visibility = if (menuItems.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
        
        viewModel.cartItemCount.observe(viewLifecycleOwner) { count ->
            updateCartBadge(count)
        }
        
        viewModel.addToCartSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Item added to cart", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            if (cartBadge == null) {
                cartBadge = BadgeDrawable.create(requireContext())
                cartBadge?.isVisible = true
                cartBadge?.number = count
                
                // Attach badge to FAB
                binding.fabCart.post {
                    cartBadge?.attachToBadgeDrawable(binding.fabCart)
                }
            } else {
                cartBadge?.number = count
                cartBadge?.isVisible = true
            }
        } else {
            cartBadge?.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}