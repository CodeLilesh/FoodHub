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

    private var selectedPosition = 0

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
        holder.bind(category, position == selectedPosition)
        
        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {
                val oldPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onCategoryClick(category)
            }
        }
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String, isSelected: Boolean) {
            binding.tvCategoryName.text = category
            
            // Update card appearance based on selection state
            binding.root.apply {
                strokeWidth = if (isSelected) 0 else 1
                setCardBackgroundColor(
                    context.getColor(
                        if (isSelected) com.example.foodorderingapp.R.color.primary
                        else android.R.color.white
                    )
                )
            }
            
            // Update text color based on selection state
            binding.tvCategoryName.setTextColor(
                binding.root.context.getColor(
                    if (isSelected) android.R.color.white
                    else com.example.foodorderingapp.R.color.primary_text
                )
            )
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