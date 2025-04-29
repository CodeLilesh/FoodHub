package com.example.foodorderingapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentHomeBinding
import com.example.foodorderingapp.ui.adapters.CategoryAdapter
import com.example.foodorderingapp.ui.adapters.RestaurantAdapter
import com.example.foodorderingapp.ui.adapters.RestaurantFeaturedAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var featuredRestaurantAdapter: RestaurantFeaturedAdapter
    private lateinit var restaurantAdapter: RestaurantAdapter

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
        
        setupUI()
        setupObservers()
        setupRefreshLayout()
    }
    
    private fun setupUI() {
        // Setup category adapter
        categoryAdapter = CategoryAdapter { category ->
            viewModel.filterByCategory(category)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        
        // Setup featured restaurant adapter
        featuredRestaurantAdapter = RestaurantFeaturedAdapter { restaurant ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToRestaurantDetailFragment(restaurant.id)
            )
        }
        binding.rvFeaturedRestaurants.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = featuredRestaurantAdapter
        }
        
        // Setup all restaurants adapter
        restaurantAdapter = RestaurantAdapter { restaurant ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToRestaurantDetailFragment(restaurant.id)
            )
        }
        binding.rvAllRestaurants.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = restaurantAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }
        
        viewModel.featuredRestaurants.observe(viewLifecycleOwner) { restaurants ->
            featuredRestaurantAdapter.submitList(restaurants)
            binding.tvFeaturedTitle.visibility = if (restaurants.isNotEmpty()) View.VISIBLE else View.GONE
            binding.rvFeaturedRestaurants.visibility = if (restaurants.isNotEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.restaurants.observe(viewLifecycleOwner) { restaurants ->
            restaurantAdapter.submitList(restaurants)
            
            // Show/hide empty state
            binding.tvEmpty.visibility = if (restaurants.isEmpty()) View.VISIBLE else View.GONE
            binding.tvAllRestaurantsTitle.visibility = if (restaurants.isNotEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRestaurants()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}