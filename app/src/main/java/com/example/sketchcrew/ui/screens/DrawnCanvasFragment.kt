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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.models.CanvasModel
import com.example.sketchcrew.data.local.models.PairConverter
import com.example.sketchcrew.databinding.FragmentDrawnCanvasBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.screens.CanvasView.Companion.brushColor
import com.example.sketchcrew.ui.screens.CanvasView.Companion.path
import com.example.sketchcrew.ui.screens.CanvasView.Companion.shapeType
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import com.example.sketchcrew.utils.FileNameGen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "DrawnCanvasFragment"

class DrawnCanvasFragment : Fragment() {

    private lateinit var _binding: FragmentDrawnCanvasBinding

    private lateinit var viewModel: CanvasViewModel
    val binding get() = _binding
    private lateinit var canvasView: CanvasView
    private val listOfButtons: ArrayList<View> = ArrayList<View>()
    var mutableListButtons = mutableListOf<View>()
    private lateinit var paint: Paint

    //    private lateinit var path: Path
    private lateinit var repository: CanvasRepository
    private var pathList: ArrayList<Pair<Path, Paint>> = arrayListOf()
    var width = 1
    var height = 1


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
        val brush: View = binding.brush

        mutableListButtons.add(pen)
        mutableListButtons.add(arrow)
        mutableListButtons.add(rect)
        mutableListButtons.add(ellipse)
        mutableListButtons.add(palette)
        mutableListButtons.add(eraser)
        mutableListButtons.add(undo)
        mutableListButtons.add(redo)
        mutableListButtons.add(brush)

        shapeType.add("rectangle")
        shapeType.add("oval")
        shapeType.add("arrow")

        listOfButtons.addAll(mutableListButtons)
        for (i in listOfButtons.indices) {
            listOfButtons[i].setOnClickListener {
                when (it) {
                    pen -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
                        binding.pen.setColorFilter(
                            resources.getColor(
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
                            resources.getColor(
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
                            resources.getColor(
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
                            resources.getColor(
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
                            resources.getColor(
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
                            resources.getColor(
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
//                        binding.eraser.setColorFilter(
//                            getResources().getColor(
//                                R.color.black,
//                                resources.newTheme()
//                            )
//                        )
                        binding.eraser.clearColorFilter()
                        binding.undo.visibility = View.VISIBLE
                        binding.paletteButtons.visibility = View.GONE
                    }

                    redo -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_selector,
                            null
                        )
//                        binding.eraser.setColorFilter(
//                            getResources().getColor(
//                                R.color.black,
//                                resources.newTheme()
//                            )
//                        )
                        binding.eraser.clearColorFilter()
                    }
                }
            }
        }
        binding.red.setOnClickListener {
            paintColor.color = Color.RED
            binding.myCanvas.setColor(paintColor.color)
        }
        binding.green.setOnClickListener {
            paintColor.color = Color.GREEN
            binding.myCanvas.setColor(paintColor.color)
        }
        binding.blue.setOnClickListener {
            paintColor.color = Color.BLUE
            binding.myCanvas.setColor(paintColor.color)
        }
        binding.black.setOnClickListener {
            paintColor.color = Color.BLACK
            binding.myCanvas.setColor(paintColor.color)
        }

        return _binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = DrawnCanvasFragmentArgs.fromBundle(requireArguments())
        val pathData = args.pathData!!.trimIndent()

        binding.canvasLayout.findViewById<CanvasView>(R.id.my_canvas).setPath(pathData)
        binding.myCanvas.createNewLayer(width = 1, height = 1)

        binding.eraser.setOnClickListener {
            binding.eraser.tooltipText = "Eraser"
            binding.myCanvas.setEraserMode(true)
            binding.myCanvas.setColor(Color.WHITE)
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

        binding.brush.setOnClickListener {
            if (binding.linearLayout2.visibility == View.VISIBLE) {
                binding.linearLayout2.visibility = View.GONE
            } else {
                if (binding.linearLayout4.visibility == View.VISIBLE){
                    binding.linearLayout4.visibility = View.GONE
                }
                binding.linearLayout2.visibility = View.VISIBLE
            }
        }

        binding.brush1.setOnClickListener {
            binding.myCanvas.setBrushWidth(12f)
        }

        binding.brush2.setOnClickListener {
            binding.myCanvas.setBrushWidth(36f)
        }
        binding.brush3.setOnClickListener {
            binding.myCanvas.setBrushWidth(64f)
        }
        binding.layer.setOnClickListener {
            if (binding.linearLayout4.visibility == View.VISIBLE) {
                binding.linearLayout4.visibility = View.GONE
            } else {
                if (binding.linearLayout2.visibility == View.VISIBLE) {
                    binding.linearLayout2.visibility = View.GONE
                }
                binding.linearLayout4.visibility = View.VISIBLE
            }
        }
        binding.text.setOnClickListener {
            showTextDialog()
        }

        binding.addLayer.setOnClickListener {
            addNewLayer()
        }

        binding.removeLayer.setOnClickListener {
            removeLayer(0)
        }

        binding.switchLayer.setOnClickListener {
            switchLayer(0)
        }

        binding.saveButton.setOnClickListener {
            showSaveCanvasDialog()
        }
    }

    fun convertToPathDataString(pathDataList: List<PathData>): String {
        return buildString {
            pathDataList.forEachIndexed { index, pathData ->
                // Extract x and y coordinates from mNativePaint
                // Assuming mNativePaint encodes coordinates in a specific format, e.g., as integers or floats
                // Here, you must replace this example with your actual logic to extract coordinates
                val x = pathData.second.mNativePaint.toFloat() // this is a placeholder logic
                val y = pathData.second.mNativePaint.toFloat() // this is a placeholder logic

                if (index != 0) {
                    append(";")
                }
                append("$x,$y")
            }
        }
    }

    private fun addNewLayer() {
        canvasView.createNewLayer(width, height)
        Toast.makeText(requireContext(), "addLayer: New Layer Added", Toast.LENGTH_LONG).show()
    }

    fun switchLayer(index: Int) {
        canvasView.switchToLayer(index)
        Toast.makeText(requireContext(), "getLayer: Layer Switched", Toast.LENGTH_LONG).show()
    }

    fun removeLayer(index: Int) {
        canvasView.removeLayer(index)
        Toast.makeText(requireContext(), "removeLayer: Layer Removed", Toast.LENGTH_LONG).show()
    }

    private fun showTextDialog() {

        val editText = EditText(requireContext())
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Enter Text")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val text = editText.text.toString()
                val x = 150f //binding.myCanvas.x
                val y = 150f //binding.myCanvas.y
                binding.myCanvas.setTextToDraw(text, x, y)
            }
            .setNegativeButton("Cancel", null).create()
        dialog.show()
    }

    private fun showSaveCanvasDialog() {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.save_dialogue, null)

        val editTextFileName = dialogView.findViewById<EditText>(R.id.editTextFileName)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val spinnerFileFormat = dialogView.findViewById<Spinner>(R.id.spinnerFileFormat)
        val buttonSave = dialogView.findViewById<Button>(R.id.buttonSave)

        // Set up the spinner with file format options
        val fileFormats = listOf("PNG", "JPEG", "SVG")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fileFormats)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFileFormat.adapter = adapter

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set up the save button
        buttonSave.setOnClickListener {
            var fileName = editTextFileName.text.toString()
            var description = editTextDescription.text.toString()
            var selectedFormat = spinnerFileFormat.selectedItem.toString()

            // Handle the save action here
            handleSave(fileName, description, selectedFormat)

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun handleSave(fileName: String, description: String, selectedFormat: String) {

            Log.d(TAG, "onCreateView: save button clicked! ${canvasView.id}")
            var filename = ""
            if (fileName.isNullOrEmpty()) {
                filename = FileNameGen().generateFileNamePNG()
            } else {
                if (selectedFormat == "PNG") {
                    filename = "${fileName}.png"
                }
                if (selectedFormat == "JPG") {
                    filename = "${fileName}.jpg"
                }
                if (selectedFormat == "SVG") {
                    filename = "${fileName}.svg"
                }
            }
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = binding.myCanvas.captureBitmap()
            val drawnBitmap = binding.myCanvas.saveBitmapToFile(
                requireContext(), bitmap,
                filename
            )
            val pathsList = binding.myCanvas.paths
            val myPath = PairConverter().fromPathList(pathsList)
            Log.d(TAG, "saveCanvas: $myPath")


            val newCanvas =
                CanvasModel(id = 0, name = filename, desc = description, myPath).toCanvasData()
            viewModel.saveCanvas(newCanvas)
            Log.d(TAG, "DrawnBitmap: $drawnBitmap")
//            Toast.makeText(requireContext(), "Drawing saved!", Toast.LENGTH_LONG).show()
        }
    }

    private fun changeColor(color: Int) {
        brushColor = color
    }

    private fun changeShapeType(shape: String) {
        shapeType[0] = shape
    }

    companion object {
        var paintColor = Paint()
    }
}

