package com.example.sketchcrew.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sketchcrew.R
import com.example.sketchcrew.databinding.FragmentCanvasListBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.adapters.CanvasListAdapter
import com.example.sketchcrew.ui.adapters.DrawingAdapter
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CanvasListFragment"

class CanvasListFragment : Fragment() {

    private lateinit var viewModel: CanvasViewModel
    private lateinit var _binding: FragmentCanvasListBinding
    private val binding get() = _binding
    private lateinit var repository: CanvasRepository
    private lateinit var adapter: CanvasListAdapter
    private lateinit var recycle: RecyclerView
    private lateinit var  emptyView: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCanvasListBinding.inflate(inflater, container, false)
        repository = CanvasRepository(requireContext())
        viewModel = ViewModelProvider(this, CanvasViewModelFactory(repository))
            .get(CanvasViewModel::class.java)
        recycle = binding.canvasList
        emptyView = binding.emptyView
        recycle.layoutManager = LinearLayoutManager(requireContext())
        loadPaths()
        binding.newCanvas.setOnClickListener {
            findNavController().navigate(R.id.action_canvasListFragment_to_drawnCanvasFragment)
        }

        binding.ibBack.setOnClickListener {
            startActivity(Intent(requireContext(), HomeActivity::class.java))
        }
        return _binding.root
    }

    private fun loadPaths() {
        val drawAdapter = DrawingAdapter({ drawing ->
            val action =
                CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
                    drawing.id.toInt()
                )
            findNavController().navigate(action)
        },
            { drawing ->
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteDrawing(drawing.id)
                }
            }
        )
        recycle.adapter = drawAdapter
        lifecycleScope.launch {
            viewModel.loadDrawings.observe(viewLifecycleOwner) { drawing ->
                if (drawing.isEmpty()) {
                    recycle.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                } else {
                    recycle.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                    drawAdapter.submitList(drawing)
                }

            }
        }
    }
}