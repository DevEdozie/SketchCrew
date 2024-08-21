package com.example.sketchcrew.ui.screens

import Sketch
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sketchcrew.MainActivity
import com.example.sketchcrew.R
import com.example.sketchcrew.ui.adapters.SketchAdapter
import com.example.sketchcrew.databinding.ActivitySketchesBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.adapters.CanvasListAdapter
import com.example.sketchcrew.ui.adapters.DrawingAdapter
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import kotlinx.coroutines.launch

class SketchesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySketchesBinding
    private lateinit var sketchAdapter: SketchAdapter
    private lateinit var repository: CanvasRepository
    private lateinit var recycle: RecyclerView
    private lateinit var viewModel: CanvasViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySketchesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = CanvasRepository(this)
        viewModel = ViewModelProvider(this, CanvasViewModelFactory(repository))
            .get(CanvasViewModel::class.java)
//        recycle.adapter = pathAdapter
        recycle = binding.selfServiceRecyclerView
        recycle.layoutManager = LinearLayoutManager(this)

//        sketchAdapter = SketchAdapter { sketch ->
//            deleteSketch(sketch)
//        }
//        binding.selfServiceRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.selfServiceRecyclerView.adapter = sketchAdapter

        // Load data into RecyclerView
        loadData()

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadData() {
        sketchAdapter = SketchAdapter { drawing ->
            startActivity(Intent(this, MainActivity::class.java))
            val action =
                SketchesActivityDirections.actionGlobalDrawnCanvasFragment(
                    drawing.id.toInt()
                )

            findNavController(R.id.nav_host_fragment_sketch).navigate(action)

        }
        recycle.adapter = sketchAdapter

        viewModel.loadDrawings.observe(this){ drawing ->
            sketchAdapter.submitList(drawing)

        }

    }

    private fun deleteSketch(sketch: Sketch) {

    }
}
