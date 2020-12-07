package com.kevin.cookingassistancecompanion.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kevin.cookingassistancecompanion.R
import com.kevin.cookingassistancecompanion.databinding.ItemAddBinding
import com.kevin.cookingassistancecompanion.databinding.ItemResultBinding
import com.kevin.cookingassistancecompanion.viewmodels.result.ResultActivityViewModel
import com.kevin.cookingassistancecompanion.viewmodels.result.ResultItemViewModel
import com.kevin.cookingassistancecompanion.viewmodels.result.ResultItemViewModel.Companion.ITEM_TYPE_RESULT
import com.kevin.cookingassistancecompanion.viewmodels.result.ScannedResultItemViewModel

/**
 * Adapter for the recycler view in Result activity
 */
class ResultAdapter(
    private val activityViewModel: ResultActivityViewModel
) : DiffUtilAdapter<ResultItemViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = if (viewType == ITEM_TYPE_RESULT) {
            DataBindingUtil.inflate<ItemResultBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_result,
                parent,
                false
            )
        } else {
            DataBindingUtil.inflate<ItemAddBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_add,
                parent,
                false
            )
        }

        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].itemType
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_RESULT) {
            val viewModel = data[position]
            val binding = holder.binding as ItemResultBinding
            binding.lifecycleOwner = viewModel
            binding.model = viewModel as ScannedResultItemViewModel
            binding.executePendingBindings()
        } else {
            val binding = holder.binding as ItemAddBinding
            binding.model = activityViewModel
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}