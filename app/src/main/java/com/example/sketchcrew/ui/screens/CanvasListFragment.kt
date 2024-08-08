package com.example.sketchcrew.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.models.CanvasData
import com.example.sketchcrew.databinding.FragmentCanvasListBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.adapters.CanvasListAdapter
import com.example.sketchcrew.ui.screens.CanvasListFragmentDirections
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import kotlinx.coroutines.launch

private const val TAG = "CanvasListFragment"
class CanvasListFragment : Fragment() {

    private lateinit var viewModel: CanvasViewModel
    private lateinit var _binding: FragmentCanvasListBinding
    private val binding get() = _binding
    private lateinit var repository: CanvasRepository
    private lateinit var adapter: CanvasListAdapter
    private lateinit var recycle: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCanvasListBinding.inflate(inflater, container, false)
       repository = CanvasRepository(requireContext())
        viewModel = ViewModelProvider(this, CanvasViewModelFactory(repository))
            .get(CanvasViewModel::class.java)
        recycle = binding.canvasList
        recycle.layoutManager = LinearLayoutManager(requireContext())
        binding.newCanvas.setOnClickListener {
            findNavController().navigate(R.id.action_canvasListFragment_to_drawnCanvasFragment)
        }


    lifecycleScope.launch {
        viewModel.canvases.observe(viewLifecycleOwner, Observer { canvases ->
            Log.d(TAG, "onCreateView: $canvases")
            adapter = CanvasListAdapter(canvases!!, object : CanvasListAdapter.OnItemClickListener {
                override fun onCanvasClick(canvasData: CanvasData) {
                    val pathData: String = viewModel.getCanvas(canvasData.id)?.paths.toString()
                    if (pathData == null) {
                        findNavController().navigate(R.id.action_canvasListFragment_to_drawnCanvasFragment)
                    } else {
                        val action =
                            CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
                                pathData!!
                            )
                        findNavController().navigate(action)
                    }
                }

                override fun onSaveCanvas(canvasData: CanvasData) {
                    viewModel.saveCanvas(canvasData)
                }

                override fun onDeleteCanvas(canvasData: CanvasData) {
                    viewModel.deleteCanvas(canvasData)
                }

            })
            recycle.adapter = adapter
        })

//            { canvas ->
//                // Handle canvas selection (e.g., navigate to drawing screen)
//
//            }
//        })

    }



//        viewModel.canvases.observe(viewLifecycleOwner) { canvases ->
//            adapter.submitList(canvases)
//        }


        return _binding.root
    }

    companion object {

    }
}