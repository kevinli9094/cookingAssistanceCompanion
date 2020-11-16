package com.kevin.cookingassistancecompanion.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevin.cookingassistancecompanion.adapters.ResultAdapter
import com.kevin.cookingassistancecompanion.databinding.ActivityResultBinding
import com.kevin.cookingassistancecompanion.viewmodels.ResultActivityViewModel

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ResultActivityViewModel()
        binding.model = viewModel
        setupRecyclerView(binding, viewModel)
    }

    private fun setupRecyclerView(
        binding: ActivityResultBinding,
        viewModel: ResultActivityViewModel
    ) {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ResultAdapter(viewModel)
        binding.recyclerView.adapter = adapter

        viewModel.getData().observe(this, {
            adapter.setData(it)
        })
    }
}