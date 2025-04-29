package com.example.foodorderingapp.ui.restaurant

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.MainActivity
import com.example.foodorderingapp.R
import com.example.foodorderingapp.data.models.MenuItem
import com.example.foodorderingapp.databinding.ActivityRestaurantBinding
import com.example.foodorderingapp.viewmodel.RestaurantViewModel
import com.google.android.material.snackbar.Snackbar

class RestaurantActivity : AppCompatActivity(), MenuItemAdapter.MenuItemClickListener {

    private lateinit var binding: ActivityRestaurantBinding
    private lateinit var viewModel: RestaurantViewModel
    private lateinit var menuAdapter: MenuItemAdapter
    private var restaurantId: Int = 0
    private var restaurantName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get restaurant ID from intent
        restaurantId = intent.getIntExtra("restaurant_id", 0)
        restaurantName = intent.getStringExtra("restaurant_name") ?: "Restaurant"

        if (restaurantId == 0) {
            Toast.makeText(this, "Invalid restaurant ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)
        
        setupToolbar()
        setupRecyclerView()
        setupCategoryFilter()
        observeViewModel()
        
        // Load restaurant details and menu
        viewModel.loadRestaurant(restaurantId)
        viewModel.loadMenu(restaurantId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = restaurantName
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuItemAdapter(this)
        binding.rvMenu.apply {
            layoutManager = LinearLayoutManager(this@RestaurantActivity)
            adapter = menuAdapter
        }
    }

    private fun setupCategoryFilter() {
        binding.chipGroupCategories.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipAll -> viewModel.loadMenu(restaurantId)
                R.id.chipAppetizers -> viewModel.loadMenuByCategory(restaurantId, "Appetizer")
                R.id.chipMain -> viewModel.loadMenuByCategory(restaurantId, "Main")
                R.id.chipDesserts -> viewModel.loadMenuByCategory(restaurantId, "Dessert")
                R.id.chipBeverages -> viewModel.loadMenuByCategory(restaurantId, "Beverage")
            }
        }
    }

    private fun observeViewModel() {
        // Observe restaurant data
        viewModel.restaurant.observe(this, { restaurant ->
            if (restaurant != null) {
                binding.tvRestaurantName.text = restaurant.name
                binding.tvRestaurantCategory.text = restaurant.category
                binding.tvRestaurantAddress.text = restaurant.address
                binding.tvRestaurantHours.text = restaurant.openingHours ?: "No hours available"
                
                // Load restaurant image with Coil
                if (!restaurant.imageUrl.isNullOrEmpty()) {
                    coil.load(binding.ivRestaurant, restaurant.imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.placeholder_restaurant)
                        error(R.drawable.placeholder_restaurant)
                    }
                }
            }
        })

        // Observe menu items
        viewModel.menuItems.observe(this, { menuItems ->
            menuAdapter.submitList(menuItems)
            binding.tvNoMenuItems.visibility = if (menuItems.isEmpty()) View.VISIBLE else View.GONE
            binding.shimmerLayout.visibility = View.GONE
        })

        // Observe loading state
        viewModel.isLoading.observe(this, { isLoading ->
            if (isLoading) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
            }
        })

        // Observe add to cart result
        viewModel.addToCartResult.observe(this, { result ->
            if (result.success) {
                Snackbar.make(binding.root, "Added to cart", Snackbar.LENGTH_SHORT)
                    .setAction("View Cart") {
                        finish()
                        // Update main activity to show cart tab
                        (this.parent as? MainActivity)?.let { mainActivity ->
                            mainActivity.updateCartBadge()
                            mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_cart
                        }
                    }
                    .show()
            } else {
                Toast.makeText(this, result.message ?: "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        })

        // Observe error messages
        viewModel.errorMessage.observe(this, { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onMenuItemClick(menuItem: MenuItem) {
        // Do nothing on single click, let the user see the details
    }

    override fun onAddToCartClick(menuItem: MenuItem) {
        viewModel.addToCart(restaurantId, menuItem)
    }
}

class MenuItemAdapter(private val listener: MenuItemClickListener) :
    androidx.recyclerview.widget.ListAdapter<MenuItem, MenuItemAdapter.MenuItemViewHolder>(
        MenuItemDiffCallback()
    ) {

    interface MenuItemClickListener {
        fun onMenuItemClick(menuItem: MenuItem)
        fun onAddToCartClick(menuItem: MenuItem)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = com.example.foodorderingapp.databinding.ItemMenuBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MenuItemViewHolder(private val binding: com.example.foodorderingapp.databinding.ItemMenuBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    listener.onMenuItemClick(getItem(position))
                }
            }
            
            binding.btnAddToCart.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    listener.onAddToCartClick(getItem(position))
                }
            }
        }

        fun bind(menuItem: MenuItem) {
            binding.tvMenuItemName.text = menuItem.name
            binding.tvMenuItemDescription.text = menuItem.description ?: "No description available"
            binding.tvMenuItemPrice.text = "$${String.format("%.2f", menuItem.price)}"
            
            // Show vegetarian badge if applicable
            binding.tvVegetarian.visibility = if (menuItem.isVegetarian) View.VISIBLE else View.GONE
            
            // Disable add to cart button if item is not available
            binding.btnAddToCart.isEnabled = menuItem.isAvailable
            binding.btnAddToCart.text = if (menuItem.isAvailable) "Add to Cart" else "Not Available"
            
            // Load menu item image with Coil
            if (!menuItem.imageUrl.isNullOrEmpty()) {
                coil.load(binding.ivMenuItem, menuItem.imageUrl) {
                    crossfade(true)
                    placeholder(com.example.foodorderingapp.R.drawable.placeholder_food)
                    error(com.example.foodorderingapp.R.drawable.placeholder_food)
                }
            } else {
                binding.ivMenuItem.setImageResource(com.example.foodorderingapp.R.drawable.placeholder_food)
            }
        }
    }

    private class MenuItemDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<MenuItem>() {
        override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
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
