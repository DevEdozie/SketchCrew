package com.example.sketchcrew.ui.screens

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.models.CanvasModel
import com.example.sketchcrew.databinding.FragmentDrawnCanvasBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.screens.CanvasView.Companion.brushColor
import com.example.sketchcrew.ui.screens.CanvasView.Companion.paths
import com.example.sketchcrew.ui.screens.CanvasView.Companion.shapeType
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import java.util.ArrayList

private const val TAG = "DrawnCanvasFragment"
class DrawnCanvasFragment : Fragment() {

    private lateinit var _binding: FragmentDrawnCanvasBinding

    private lateinit var viewModel: CanvasViewModel
    val binding get() = _binding
    private lateinit var canvasView: CanvasView
    private val listOfButtons: ArrayList<View> = ArrayList<View>()
    var mutableListButtons = mutableListOf<View>()
    private lateinit var paint: Paint
    private lateinit var path: Path
    private lateinit var repository: CanvasRepository
    private var pathList: ArrayList<Pair<Path, Paint>> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        repository = CanvasRepository(requireContext())
        viewModel = ViewModelProvider(this, CanvasViewModelFactory(repository))
            .get(CanvasViewModel::class.java)
        _binding = FragmentDrawnCanvasBinding.inflate(inflater, container, false)

        canvasView = binding.canvasLayout.findViewById(R.id.my_canvas)


        val pen: View = binding.pen
        val arrow: View = binding.arrow
        val rect: View = binding.rectangle
        val ellipse: View = binding.ellipse
        val palette: View = binding.palette
        val eraser: View = binding.eraser
        val undo: View = binding.undo
        val redo: View = binding.redo

        mutableListButtons.add(pen)
        mutableListButtons.add(arrow)
        mutableListButtons.add(rect)
        mutableListButtons.add(ellipse)
        mutableListButtons.add(palette)
        mutableListButtons.add(eraser)
        mutableListButtons.add(undo)
        mutableListButtons.add(redo)

        shapeType.add("rectangle")
        shapeType.add("oval")
        shapeType.add("arrow")

        listOfButtons.addAll(mutableListButtons)
        for (i in listOfButtons.indices) {
            listOfButtons[i].setOnClickListener {
//                binding.paletteButtons.visibility = View.GONE
                when (it) {
                    pen -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.pen.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.paletteButtons.visibility = View.GONE
                    }
                    arrow -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.arrow.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.pen.clearColorFilter()
                        changeShapeType("arrow")
                        binding.paletteButtons.visibility = View.GONE
                    }
                    rect -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.rectangle.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.arrow.clearColorFilter()
                        changeShapeType("rectangle")
                        binding.paletteButtons.visibility = View.GONE
                    }
                    ellipse -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.ellipse.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.rectangle.clearColorFilter()
                        changeShapeType("oval")
                        binding.paletteButtons.visibility = View.GONE
                    }
                    palette -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.palette.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.ellipse.clearColorFilter()
                        if (binding.paletteButtons.visibility == View.VISIBLE) {
                            binding.paletteButtons.visibility = View.GONE
                        } else {
                            binding.paletteButtons.visibility = View.VISIBLE
                        }
                    }
                    eraser -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.eraser.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.palette.clearColorFilter()
                        binding.paletteButtons.visibility = View.GONE
                    }

                    undo -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.eraser.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.ellipse.clearColorFilter()
                        binding.undo.visibility = View.VISIBLE
                        binding.paletteButtons.visibility = View.GONE
                    }

                    redo -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.eraser.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.redo.clearColorFilter()
                    }

                }

            }
        }
        binding.red.setOnClickListener {
            paintColor.color = Color.RED
//            changeColor(paintColor.color)
            binding.myCanvas.setColor(paintColor.color)
        }
        binding.green.setOnClickListener {
            paintColor.color = Color.GREEN
//            changeColor(paintColor.color)
            binding.myCanvas.setColor(paintColor.color)
        }
        binding.blue.setOnClickListener {
            paintColor.color = Color.BLUE
//            changeColor(paintColor.color)
            binding.myCanvas.setColor(paintColor.color)
        }
        binding.black.setOnClickListener {
            paintColor.color = Color.BLACK
//            changeColor(paintColor.color)
            binding.myCanvas.setColor(paintColor.color)
        }

        return _binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = DrawnCanvasFragmentArgs.fromBundle(requireArguments())
        val pathData = args.pathData
        binding.canvasLayout.findViewById<CanvasView>(R.id.my_canvas).setPath(pathData!!)

        binding.saveButton.setOnClickListener {
            Log.d(TAG, "onCreateView: save button clicked! ${canvasView.id}")
//            if (canvasView.getPathData().isNullOrEmpty()){
////                paths = deserializePaths(canvasView.getPathData())
//                Toast.makeText(context, "No Data added $paths", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }

            try {
                SaveCanvasDialog(requireContext()) { canvasSave ->
                    Log.d(TAG, "saveCanvas: $canvasSave")
                    val paths = canvasView.getPathData()
//                    Log.d(TAG, "saveCanvas: $paths")
                    val newCanvas =
                        CanvasModel(id = 0, name = "name1", desc = "", paths).toCanvasData()
                    viewModel.saveCanvas(newCanvas)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in saveCanvas: ${e.message}", e)
            }

        }

        binding.eraser.setOnClickListener {
            binding.eraser.tooltipText = "Eraser"
            binding.myCanvas.setEraserMode(true)
            binding.myCanvas.setBrush(Color.WHITE)
        }

        binding.undo.setOnClickListener {
            binding.undo.tooltipText = "Undo"
            binding.myCanvas.undo()
        }

        binding.redo.setOnClickListener {
            binding.undo.tooltipText = "Undo"
            binding.myCanvas.redo()
        }
        binding.arrow.setOnClickListener {
            binding.arrow.tooltipText = "Arrow"
            binding.myCanvas.setTool(DrawingTool.ARROW)
        }

        binding.rectangle.setOnClickListener {
            binding.arrow.tooltipText = "Square"
            binding.myCanvas.setTool(DrawingTool.SQUARE)
        }

        binding.ellipse.setOnClickListener {
            binding.myCanvas.setTool(DrawingTool.CIRCLE)
        }

        binding.pen.setOnClickListener {
            binding.myCanvas.setTool(DrawingTool.FREEHAND)

        }
    }
//    fun saveCanvas(canv: CanvasView) {
//
////            val paths = canvasView.getPathData()
//    }

//    private fun deserializePaths(serializedPaths: String): MutableList<Pair<Path, Paint>> {
//        // Deserialize paths from JSON
//        // This is a simplified example; you need a proper deserialization strategy
//        return Gson().fromJson(serializedPaths, object : TypeToken<List<Pair<Path, Paint>>>() {}.type)
//    }


//    private fun performClick(view: View, color: Int) {
//        view.setOnClickListener {
//            paintColor.color = color
//            changeColor(paintColor.color)
//        }
//    }

    private fun changeColor(color: Int) {
        brushColor = color
        path = Path()
    }

    private fun changeShapeType(shape: String) {
        shapeType[0] = shape
    }

    companion object {
        var path = Path()
        var paintColor = Paint()
    }

    inner class SaveCanvasDialog(
        context: Context,
        private val onSave: (String) -> Unit
    ) : Dialog(context) {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.save_canvas_dialog)

            val canvasNameEditText: EditText = findViewById(R.id.canvasNameEditText)
            val saveButton: Button = findViewById(R.id.saveButton)
            val cancelButton: Button = findViewById(R.id.cancelButton)

            canvasNameEditText.doAfterTextChanged {
                saveButton.isEnabled = it.toString().trim().isNotEmpty()
            }

            saveButton.setOnClickListener {
                val canvasName = canvasNameEditText.text.toString().trim()
                if (canvasName.isNotEmpty()) {
                    onSave(canvasName)
                    dismiss()
                }
            }

            cancelButton.setOnClickListener {
                dismiss()
            }
        }
    }
}