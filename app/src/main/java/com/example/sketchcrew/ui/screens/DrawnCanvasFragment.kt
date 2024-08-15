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
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.models.CanvasModel
import com.example.sketchcrew.databinding.FragmentDrawnCanvasBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.screens.CanvasView.Companion.brushColor
import com.example.sketchcrew.ui.screens.CanvasView.Companion.shapeType
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import com.example.sketchcrew.utils.FileNameGen

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

//                binding.paletteButtons.visibility = View.GONE

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

                        binding.eraser.setColorFilter(
                            getResources().getColor(
                                R.color.black,
                                resources.newTheme()
                            )
                        )
                        binding.ellipse.clearColorFilter()

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
        val pathData = args.pathData
        binding.canvasLayout.findViewById<CanvasView>(R.id.my_canvas).setPath(pathData!!)


        binding.saveButton.setOnClickListener {
            val paths = canvasView.getPathData()
            val newCanvas1 =
                CanvasModel(id = 0, name = "name2", desc = "", paths).toCanvasData()
            viewModel.saveCanvas(newCanvas1)
            Log.d(TAG, "onCreateView: save button clicked! ${canvasView.id}")
//            if (canvasView.getPathData().isNullOrEmpty()){
////                paths = deserializePaths(canvasView.getPathData())
//                Toast.makeText(context, "No Data added $paths", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
        binding.myCanvas.createNewLayer(width = 1, height = 1)

//        addNewLayer()
//        switchLayer(0)
//        removeLayer(0)

//        binding.saveButton.setOnClickListener {
//            Log.d(TAG, "onCreateView: save button clicked! ${canvasView.id}")
//            try {
//                SaveCanvasDialog(requireContext()) { canvasSave ->
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Error in saveCanvas: ${e.message}", e)
//            }
//        }

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
        filename = if (fileName.isNullOrEmpty()) {
            FileNameGen().generateFileNamePNG()
        } else ({
            if (selectedFormat == "PNG") {
                "${fileName}.png"
            }
            if (selectedFormat == "JPG") {
                "${fileName}.jpg"
            }
            if (selectedFormat == "SVG") {
                "${fileName}.svg"
            }
        }).toString()

        val bitmap = canvasView.captureBitmap()
        val drawnBitmap = canvasView.saveBitmapToFile(
            requireContext(), bitmap,
            filename
        )
        val paths = canvasView.getPathData(path)
        Log.d(TAG, "saveCanvas: $paths")
        val newCanvas =
            CanvasModel(id = 0, name = filename, desc = description, paths).toCanvasData()
        viewModel.saveCanvas(newCanvas)
        Log.d(TAG, "DrawnBitmap: $drawnBitmap")
        Toast.makeText(requireContext(), "Drawing saved!", Toast.LENGTH_LONG).show()

    }

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

    class SaveCanvasDialog(
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