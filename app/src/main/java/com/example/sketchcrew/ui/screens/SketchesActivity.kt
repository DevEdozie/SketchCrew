package com.example.sketchcrew.ui.screens

import Sketch
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sketchcrew.R
import com.example.sketchcrew.SketchAdapter
import com.example.sketchcrew.databinding.ActivitySketchesBinding

class SketchesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySketchesBinding
    private lateinit var sketchAdapter: SketchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySketchesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sketchAdapter = SketchAdapter { sketch ->
            deleteSketch(sketch)
        }
        binding.selfServiceRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.selfServiceRecyclerView.adapter = sketchAdapter

        // Load data into RecyclerView
        loadData()

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadData() {

    }

    private fun deleteSketch(sketch: Sketch) {

    }
}
