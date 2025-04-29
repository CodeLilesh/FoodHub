package com.example.foodorderingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onCategoryClick: (String) -> Unit
) : ListAdapter<String, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        val isSelected = position == selectedPosition
        
        holder.bind(category, isSelected)
        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {
                val previousSelected = selectedPosition
                selectedPosition = position
                
                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected)
                }
                
                notifyItemChanged(position)
                onCategoryClick(category)
            }
        }
    }

    fun selectCategory(category: String) {
        val index = currentList.indexOf(category)
        if (index != -1 && index != selectedPosition) {
            val previousSelected = selectedPosition
            selectedPosition = index
            
            if (previousSelected != -1) {
                notifyItemChanged(previousSelected)
            }
            
            notifyItemChanged(index)
        }
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String, isSelected: Boolean) {
            binding.apply {
                tvCategory.text = category
                root.isSelected = isSelected
                tvCategory.isSelected = isSelected
            }
        }
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}