package com.kevin.cookingassistancecompanion.activities

import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kevin.cookingassistancecompanion.adapters.ResultAdapter
import com.kevin.cookingassistancecompanion.adapters.ViewHolder
import com.kevin.cookingassistancecompanion.databinding.ActivityResultBinding
import com.kevin.cookingassistancecompanion.databinding.ItemResultBinding
import com.kevin.cookingassistancecompanion.viewmodels.result.ResultActivityViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ResultActivity : AppCompatActivity() {
    companion object {
        const val ITEM_DELETE_DELAY_MS = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResultBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this).get(ResultActivityViewModel::class.java)
        binding.model = viewModel
        setupRecyclerView(binding, viewModel)
    }

    private fun setupRecyclerView(
        binding: ActivityResultBinding,
        viewModel: ResultActivityViewModel
    ) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ResultAdapter(viewModel)
        binding.recyclerView.adapter = adapter

        adapter.setDataSource(viewModel.getData(), this)

        viewModel.scrollPositionObservable.observe(this) { position ->
            binding.recyclerView.smoothScrollToPosition(position)
        }

        val swipeToDeleteCallback = object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (viewHolder.adapterPosition < adapter.itemCount - 1) {
                    makeFlag(ACTION_STATE_IDLE, RIGHT) or makeFlag(ACTION_STATE_SWIPE, RIGHT)
                } else {
                    0
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                this@ResultActivity.lifecycleScope.launch {
                    delay(ITEM_DELETE_DELAY_MS)
                    val model = ((viewHolder as ViewHolder).binding as ItemResultBinding).model!!
                    viewModel.remove(model)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (viewHolder is ViewHolder) {
                    val itemViewBinding = viewHolder.binding
                    if (itemViewBinding is ItemResultBinding) {
                        val itemViewModel = itemViewBinding.model!!
                        val alphaValue = 1 - dX / viewHolder.itemView.width
                        itemViewModel.foregroundTranslationXObservable.value = dX
                        itemViewModel.foregroundAlphaObservable.value = alphaValue
                    }
                    return
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }
}