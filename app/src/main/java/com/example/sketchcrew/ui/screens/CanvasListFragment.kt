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


//        lifecycleScope.launch {
//            viewModel.loadCanvases()
//            viewModel.canvases.observe(viewLifecycleOwner, Observer { canvases ->
//                Log.d(TAG, "onCreateView: $canvases")
//                adapter =
//                    CanvasListAdapter(canvases, object : CanvasListAdapter.OnItemClickListener {
//                        override fun onCanvasClick(canvasData: CanvasData) {
//                            lifecycleScope.launch {
//                                val pathData = viewModel.getCanvas(canvasData.id)?.paths.toString()
//                                    .trimIndent()
////                        val gson = Gson()
////                        val type = object : TypeToken<List<PathData>>() {}.type
////                        val pathDataList: List<PathData> = gson.fromJson(pathData, type)
////                        val pathDataString = convertToPathDataString(pathDataList)
//                                if (pathData == null) {
//                                    findNavController().navigate(R.id.action_canvasListFragment_to_drawnCanvasFragment)
//                                } else {
//                                    val action =
//                                        CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
//                                            pathData.toInt()
//                                        )
//                                    findNavController().navigate(action)
//                                }
//                            }
//                        }
//
//                        override fun onSaveCanvas(canvasData: CanvasData) {
//                            lifecycleScope.launch {
//                                viewModel.saveCanvas(canvasData)
//                            }
//                        }
//
//                        override fun onDeleteCanvas(canvasData: CanvasData) {
//                            lifecycleScope.launch {
//                                viewModel.deleteCanvas(canvasData)
//                            }
//                        }
//                    })
//                recycle.adapter = adapter
//            })
//
//
////            { canvas ->
////                // Handle canvas selection (e.g., navigate to drawing screen)
////
////            }
////        })
//
//        }


//        viewModel.canvases.observe(viewLifecycleOwner) { canvases ->
//            adapter.submitList(canvases)
//        }
//        pathsAdapter = PathsAdapter(pathsList) { pathData ->
//            val bundle = Bundle().apply {
//                putString("path_data", pathData.path)
//                putInt("color", pathData.color)
//                putFloat("strokeWidth", pathData.strokeWidth)
//            }
//            findNavController().navigate(R.id.action_pathsFragment_to_drawingFragment, bundle)
//        }

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
//                val pathAdapter = PathAdapter(drawing) { draws ->
//                    draws?.let { paths ->
////                        val pathDataList: List<PathData> = Gson().fromJson(
////                            draws.paths,
////                            object : TypeToken<List<PathData>>() {}.type
////                        )
//                        val pathDataList = PairConverter().fromPaths(draws.paths)
////                        val dataPath = PairConverter().fromPathList(pathDataList)
////                        viewModel.getPath()
//                        pathDataList.forEach { pathData ->
//
//                                val pathInfo = pathData.first.toString()
//                                val colorInfo = pathData.second.color
//                                val strokeWidth = pathData.second.strokeWidth
//
//                                if (pathInfo != null) {
//                                    // Navigate and pass the arguments
//
//                                } else {
//                                    // Handle the case where path is null
//                                    // For example, log an error or show a message to the user
//                                    Log.e("CanvasListFragment", "Path data is null")
//                                }
//                        }
//                    }
//                }

            }
        }

//        viewModel.loadDrawings.observe(viewLifecycleOwner, Observer { drawing ->
//            drawing?.let {
//                val pathAdapter = PathAdapter(drawing) { draws ->
//                    val pathDataList: List<PathData> = Gson().fromJson(draws.paths, object : TypeToken<List<PathData>>() {}.type)
//                    var pathStr =  ''
//                    var colooCode = 0
//                    var widthStroke = 0f
//                    val pathsAndPaints = pathDataList.map { pathData ->
//                        val path = stringToPath(pathData.path)
//                        pathStr = path
//                        val paint = Paint().apply {
//                            color = pathData.color
//                            strokeWidth = pathData.strokeWidth
//                        }
//                        Pair(path, paint)
//                    }
//
//                    // Perform your navigation here, passing necessary data
//                    val action = CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
//                        pathData, pathData.color, pathData.strokeWidth
//                    )
//                    findNavController().navigate(action)
//                }
//                recycle.adapter = pathAdapter
//            } ?: run {
//                // Handle the case where `drawing` is null
//                println("No drawing found.")
//            }
//        })


//            viewModel.loadDrawings.observe(requireActivity(), Observer { drawing ->
//                val pathAdapter = PathAdapter(drawing) { draws ->
//                    drawing?.let {
//                        val pathDataList: List<PathData> = Gson().fromJson(draws.paths, object : TypeToken<List<PathData>>() {}.type)
//                        pathDataList.map { pathData ->
//                            val path = stringToPath(pathData.path)
//                            path.toString()
//                            val paint = Paint().apply {
//                                color = pathData.color
//                                strokeWidth = pathData.strokeWidth
//                            }
//                            pathData.color
//                            pathData.strokeWidth
//                            Pair(path, paint)
//                        }
//                    }
//                    val action =
//                        CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
//                            "", 0, 0f
//                        )
//                    findNavController().navigate(action)
//                }
//                recycle.adapter = pathAdapter
//            }
//
//            )
//            val paths = withContext(Dispatchers.IO) {
//                db.drawingDao().getAllDrawings()
//            }

//            pathAdapter = PathAdapter(paths) { pathData ->
//                // Handle path click
//                val action =
//                    CanvasListFragmentDirections.actionCanvasListFragmentToDrawnCanvasFragment(
//                        pathData.id
//                    )
//                findNavController().navigate(action)
//            }
//                val intent = Intent(this@PathListActivity, EditPathActivity::class.java).apply {
//                    putExtra("pathId", pathData.id)
//                }
//                startActivity(intent)



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