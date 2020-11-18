package com.kevin.cookingassistancecompanion.adapters

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

abstract class DiffUtilAdapter<T> : RecyclerView.Adapter<ViewHolder>() {

    private var newData: List<T> = emptyList()
    protected var data: List<T> = emptyList()

    fun setDataSource(dataSource: LiveData<List<T>>, lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            dataSource.observe(lifecycleOwner) {
                newData = it
                val diffResult = DiffUtil.calculateDiff(diffUtilCallback, true)
                diffResult.dispatchUpdatesTo(this@DiffUtilAdapter)
                data = it
            }
        }
    }

    open fun getOldListSize(): Int {
        return data.size
    }

    open fun getNewListSize(): Int {
        return newData.size
    }

    open fun areItemsTheSame(
        oldData: List<T>,
        oldItemPosition: Int,
        newData: List<T>,
        newItemPosition: Int
    ): Boolean {
        return oldData[oldItemPosition] === newData[newItemPosition]
    }

    open fun areContentsTheSame(
        oldData: List<T>,
        oldItemPosition: Int,
        newData: List<T>,
        newItemPosition: Int
    ): Boolean {
        return false
    }

    private val diffUtilCallback = object : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return this@DiffUtilAdapter.getOldListSize()
        }

        override fun getNewListSize(): Int {
            return this@DiffUtilAdapter.getNewListSize()
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return this@DiffUtilAdapter.areItemsTheSame(
                data,
                oldItemPosition,
                newData,
                newItemPosition
            )
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return this@DiffUtilAdapter.areContentsTheSame(
                data,
                oldItemPosition,
                newData,
                newItemPosition
            )
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return Unit
        }
    }
}