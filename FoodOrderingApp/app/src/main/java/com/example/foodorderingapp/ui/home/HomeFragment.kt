package com.example.foodorderingapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.data.models.Restaurant
import com.example.foodorderingapp.databinding.FragmentHomeBinding
import com.example.foodorderingapp.ui.restaurant.RestaurantActivity
import com.example.foodorderingapp.viewmodel.HomeViewModel

class HomeFragment : Fragment(), RestaurantAdapter.RestaurantClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        
        setupRecyclerViews()
        setupSearchView()
        setupSwipeRefresh()
        observeViewModel()
        
        // Initial data load
        loadRestaurants()
    }

    private fun setupRecyclerViews() {
        // Setup Restaurants RecyclerView
        restaurantAdapter = RestaurantAdapter(this)
        binding.rvRestaurants.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = restaurantAdapter
        }

        // Setup Categories RecyclerView
        categoryAdapter = CategoryAdapter { category ->
            viewModel.getRestaurantsByCategory(category)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // Set default categories
        val categories = listOf(
            "All", "Italian", "Chinese", "Indian", "Mexican", 
            "Japanese", "American", "Fast Food", "Pizza", "Burger",
            "Vegetarian", "Dessert", "Cafe"
        )
        categoryAdapter.submitList(categories)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchRestaurants(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Only search when user stops typing for better UX
                return false
            }
        })

        // Clear search and show all restaurants when X is clicked
        binding.searchView.setOnCloseListener {
            loadRestaurants()
            false
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadRestaurants()
        }
    }

    private fun observeViewModel() {
        // Observe restaurants list
        viewModel.restaurants.observe(viewLifecycleOwner, { restaurants ->
            restaurantAdapter.submitList(restaurants)
            binding.tvNoRestaurants.visibility = if (restaurants.isEmpty()) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
        })

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner, { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun loadRestaurants() {
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.getAllRestaurants()
    }

    override fun onRestaurantClick(restaurant: Restaurant) {
        // Navigate to restaurant detail screen
        val intent = Intent(context, RestaurantActivity::class.java).apply {
            putExtra("restaurant_id", restaurant.id)
            putExtra("restaurant_name", restaurant.name)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class RestaurantAdapter(private val listener: RestaurantClickListener) :
    androidx.recyclerview.widget.ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(
        RestaurantDiffCallback()
    ) {

    interface RestaurantClickListener {
        fun onRestaurantClick(restaurant: Restaurant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = com.example.foodorderingapp.databinding.ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RestaurantViewHolder(private val binding: com.example.foodorderingapp.databinding.ItemRestaurantBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    listener.onRestaurantClick(getItem(position))
                }
            }
        }

        fun bind(restaurant: Restaurant) {
            binding.tvRestaurantName.text = restaurant.name
            binding.tvRestaurantCategory.text = restaurant.category
            binding.tvRestaurantAddress.text = restaurant.address
            
            // Load image with Coil library
            if (!restaurant.imageUrl.isNullOrEmpty()) {
                coil.load(binding.ivRestaurant, restaurant.imageUrl) {
                    crossfade(true)
                    placeholder(com.example.foodorderingapp.R.drawable.placeholder_restaurant)
                    error(com.example.foodorderingapp.R.drawable.placeholder_restaurant)
                }
            } else {
                binding.ivRestaurant.setImageResource(com.example.foodorderingapp.R.drawable.placeholder_restaurant)
            }
        }
    }

    private class RestaurantDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem == newItem
        }
    }
}

class CategoryAdapter(private val onCategorySelected: (String) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<String, CategoryAdapter.CategoryViewHolder>(
        CategoryDiffCallback()
    ) {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class CategoryViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private val textView: android.widget.TextView = itemView.findViewById(android.R.id.text1)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    val oldSelectedPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(oldSelectedPosition)
                    notifyItemChanged(selectedPosition)
                    onCategorySelected(getItem(position))
                }
            }
        }

        fun bind(category: String, isSelected: Boolean) {
            textView.text = category
            if (isSelected) {
                textView.setTextColor(android.graphics.Color.WHITE)
                itemView.setBackgroundResource(com.example.foodorderingapp.R.color.colorPrimary)
            } else {
                textView.setTextColor(android.graphics.Color.BLACK)
                itemView.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    private class CategoryDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
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
