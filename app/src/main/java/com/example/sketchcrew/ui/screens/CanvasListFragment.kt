package com.example.sketchcrew.ui.screens

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.database.RoomDB
import com.example.sketchcrew.data.local.models.CanvasData
import com.example.sketchcrew.data.local.models.Drawing
import com.example.sketchcrew.data.local.models.PairConverter
import com.example.sketchcrew.data.local.models.PathData
import com.example.sketchcrew.databinding.FragmentCanvasListBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.adapters.CanvasListAdapter
import com.example.sketchcrew.ui.adapters.DrawingAdapter
import com.example.sketchcrew.ui.adapters.PathAdapter
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CanvasListFragment"

class CanvasListFragment : Fragment() {

    private lateinit var viewModel: CanvasViewModel
    private lateinit var _binding: FragmentCanvasListBinding
    private val binding get() = _binding
    private lateinit var repository: CanvasRepository
    private lateinit var adapter: CanvasListAdapter
    private lateinit var recycle: RecyclerView
//    private lateinit var pathAdapter: PathAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCanvasListBinding.inflate(inflater, container, false)
        repository = CanvasRepository(requireContext())
        viewModel = ViewModelProvider(this, CanvasViewModelFactory(repository))
            .get(CanvasViewModel::class.java)
//        recycle.adapter = pathAdapter
        recycle = binding.canvasList
        recycle.layoutManager = LinearLayoutManager(requireContext())
        loadPaths()
        binding.newCanvas.setOnClickListener {
            findNavController().navigate(R.id.action_canvasListFragment_to_drawnCanvasFragment)
        }

        return _binding.root
    }

    private fun loadPaths() {
        val drawAdapter = DrawingAdapter { drawing ->
            val action =
                CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
                    drawing.id.toInt()
                )
            findNavController().navigate(action)
        }
        recycle.adapter = drawAdapter
        lifecycleScope.launch {
            viewModel.loadDrawings.observe(viewLifecycleOwner){ drawing ->
                drawAdapter.submitList(drawing)

            }
        }
    }

    private fun stringToPath(pathString: String): Path {
        val path = Path()
        val commands = pathString.split(",")
        commands.forEach {
            val args = it.substring(1).split(",").map { it.toFloat() }
            when (it[0]) {
                'M' -> path.moveTo(args[0], args[1])
                // Handle additional commands L, Q, C, etc.
            }
        }
        return path
    }
}