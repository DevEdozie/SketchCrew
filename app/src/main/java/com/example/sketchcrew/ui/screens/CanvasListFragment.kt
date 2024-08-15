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
import com.example.sketchcrew.data.local.database.RoomDB
import com.example.sketchcrew.data.local.models.CanvasData
import com.example.sketchcrew.data.local.models.PathData
import com.example.sketchcrew.databinding.FragmentCanvasListBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.adapters.CanvasListAdapter
import com.example.sketchcrew.ui.adapters.PathAdapter
import com.example.sketchcrew.ui.screens.CanvasListFragmentDirections
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
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
    private lateinit var pathAdapter: PathAdapter

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


//    lifecycleScope.launch {
//        viewModel.loadCanvases()
//        viewModel.canvases.observe(viewLifecycleOwner, Observer { canvases ->
//            Log.d(TAG, "onCreateView: $canvases")
//            adapter = CanvasListAdapter(canvases, object : CanvasListAdapter.OnItemClickListener {
//                override fun onCanvasClick(canvasData: CanvasData) {
//                    lifecycleScope.launch {
//                        val pathData = viewModel.getCanvas(canvasData.id)?.paths.toString().trimIndent()
////                        val gson = Gson()
////                        val type = object : TypeToken<List<PathData>>() {}.type
////                        val pathDataList: List<PathData> = gson.fromJson(pathData, type)
////                        val pathDataString = convertToPathDataString(pathDataList)
//                        if (pathData == null) {
//                            findNavController().navigate(R.id.action_canvasListFragment_to_drawnCanvasFragment)
//                        } else {
//                            val action =
//                                CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
//                                    pathData
//                                )
//                            findNavController().navigate(action)
//                        }
//                    }
//                }
//
//                override fun onSaveCanvas(canvasData: CanvasData) {
//                    lifecycleScope.launch {
//                        viewModel.saveCanvas(canvasData)
//                    }
//                }
//
//                override fun onDeleteCanvas(canvasData: CanvasData) {
//                    lifecycleScope.launch {
//                        viewModel.deleteCanvas(canvasData)
//                    }
//                }
//            })
//            recycle.adapter = adapter
//        })
//
//
////            { canvas ->
////                // Handle canvas selection (e.g., navigate to drawing screen)
////
////            }
////        })
//
//    }



//        viewModel.canvases.observe(viewLifecycleOwner) { canvases ->
//            adapter.submitList(canvases)
//        }


        return _binding.root
    }

    private fun loadPaths() {
        val db = RoomDB.getDatabase(requireContext())
        lifecycleScope.launch {
            val paths = withContext(Dispatchers.IO) {
                db.pathDao().getAllPaths()
            }
            pathAdapter = PathAdapter(paths) { pathData ->
                // Handle path click
                    val action =
                        CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
                            pathData.id
                        )
                    findNavController().navigate(action)
                }
//                val intent = Intent(this@PathListActivity, EditPathActivity::class.java).apply {
//                    putExtra("pathId", pathData.id)
//                }
//                startActivity(intent)

            recycle.adapter = pathAdapter
        }

    }

//    fun convertToPathDataString(pathDataList: List<PathData>): String {
//        return buildString {
//            pathDataList.forEachIndexed { index, pathData ->
//                // Extract x and y coordinates from mNativePaint
//                // Assuming mNativePaint encodes coordinates in a specific format, e.g., as integers or floats
//                // Here, you must replace this example with your actual logic to extract coordinates
//                val x = pathData.second.mNativePaint.toFloat() // this is a placeholder logic
//                val y = pathData.second.mNativePaint.toFloat() // this is a placeholder logic
//
//                if (index != 0) {
//                    append(";")
//                }
//                append("$x,$y")
//            }
//        }
//    }

    companion object {

    }
}

//data class PathData(val first: First, val second: Second)

data class First(val isSimplePath: Boolean)

data class Second(val mNativePaint: Long)