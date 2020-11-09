package com.kevin.cookingassistancecompanion.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kevin.cookingassistancecompanion.R
import com.kevin.cookingassistancecompanion.databinding.ItemResultBinding
import com.kevin.cookingassistancecompanion.viewmodels.ResultActivityViewModel
import com.kevin.cookingassistancecompanion.viewmodels.ResultItemViewModel


class ResultAdapter(private val viewModel: ResultActivityViewModel) :
    RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    private lateinit var data: List<ResultItemViewModel>


    // todo add diffUtil later
    fun setData(list: List<ResultItemViewModel>) {
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemResultBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_result,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(var itemRowBinding: ItemResultBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {

        fun bind(model: ResultItemViewModel) {
            itemRowBinding.model = model
            itemRowBinding.executePendingBindings()
        }

    }
}