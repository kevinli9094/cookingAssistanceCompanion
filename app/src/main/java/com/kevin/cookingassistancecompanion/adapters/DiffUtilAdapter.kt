package com.kevin.cookingassistancecompanion.adapters

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class DiffUtilAdapter<T> : RecyclerView.Adapter<ViewHolder>() {

    companion object{
        const val TAG = "DiffUtilAdapter"
    }

    private var newData: List<T> = emptyList()
    protected var data: MutableList<T> = mutableListOf()

    private var channel: Channel<List<T>>? = null
    fun setDataSource(dataSource: LiveData<List<T>>, lifecycleOwner: LifecycleOwner) {
        if(channel!= null){
            Log.w(TAG, "Data source should not be set twice")
            return
        }
        channel = Channel()
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            channel!!.consumeEach {
                newData = it
                val diffResult = DiffUtil.calculateDiff(diffUtilCallback, false)
                withContext(Dispatchers.Main){
                    data = it.toMutableList()
                    diffResult.dispatchUpdatesTo(this@DiffUtilAdapter)
                }
            }
        }
        dataSource.observe(lifecycleOwner) {
            lifecycleOwner.lifecycleScope.launch {
                channel!!.send(it)
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