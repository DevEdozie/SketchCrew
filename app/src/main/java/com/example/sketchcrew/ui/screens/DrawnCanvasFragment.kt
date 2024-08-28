package com.example.sketchcrew.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.models.Drawing
import com.example.sketchcrew.data.local.models.PairConverter
import com.example.sketchcrew.databinding.FragmentDrawnCanvasBinding
import com.example.sketchcrew.repository.CanvasRepository
import com.example.sketchcrew.ui.screens.CanvasView.Companion.brushColor
import com.example.sketchcrew.ui.screens.CanvasView.Companion.shapeType
import com.example.sketchcrew.ui.viewmodels.CanvasViewModel
import com.example.sketchcrew.ui.viewmodels.CanvasViewModelFactory
import com.example.sketchcrew.utils.FileNameGen
import com.example.sketchcrew.utils.Truncator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "DrawnCanvasFragment"

class DrawnCanvasFragment : Fragment() {

    private lateinit var _binding: FragmentDrawnCanvasBinding

    private lateinit var viewModel: CanvasViewModel
    val binding get() = _binding
    private lateinit var canvasView: CanvasView
    private var pathId: Int = 0
    private var pathStr: String? = null
    private var  pathColor: Int = 0
    private var pathStroke: Float = 0F
    private val listOfButtons: ArrayList<View> = ArrayList<View>()
    var mutableListButtons = mutableListOf<View>()
    private lateinit var repository: CanvasRepository
    var width = 1
    var height = 1

    // Firebase
    private lateinit var database: DatabaseReference
    // --> DO NOT TOUCH

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        repository = CanvasRepository(requireContext())
        viewModel = ViewModelProvider(this, CanvasViewModelFactory(repository))
            .get(CanvasViewModel::class.java)
        _binding = FragmentDrawnCanvasBinding.inflate(inflater, container, false)

        canvasView = binding.canvasLayout.findViewById(R.id.my_canvas)
        // database
        database = FirebaseDatabase.getInstance().getReference("drawings")
        // --> DO NOT TOUCH

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
            // FIREBASE FUNCTIONS: DO NOT TOUCH -->
            //Set up send collaboration feature
            setupSendCollaboration()
            // Set up receive collaboration feature
            setupReceiveCollaboration()
            // Set up stop collaboration feature
            setUpStopCollaboration()
            // Set up chat button to navigate to chat screen
            setUpChatButton()
            // <-- DO NOT TOUCH
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
//        val pathData = args.pathData!!.trimIndent()
        pathId = args.id

        viewModel.getDrawingById(pathId.toLong()).observe(viewLifecycleOwner) { drawing ->
            if (drawing != null) {
                restoreDrawing(drawing)
            }
        }

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
            binding.myCanvas.setBrushWidth(16f)
        }

        binding.ellipse.setOnClickListener {
            binding.ellipse.tooltipText = "Circle"
            binding.myCanvas.setTool(DrawingTool.CIRCLE)
            binding.myCanvas.setBrushWidth(16f)
        }
        binding.menu.setOnClickListener {
            binding.menu.visibility =View.GONE
            binding.menuOpen.visibility = View.VISIBLE
            binding.linearLayout3.visibility = View.VISIBLE
        }

        binding.menuOpen.setOnClickListener {
            binding.menuOpen.visibility =View.GONE
            binding.menu.visibility = View.VISIBLE
            binding.linearLayout3.visibility = View.GONE
        }

        binding.pen.setOnClickListener {
            binding.pen.tooltipText = "Pen"
            binding.myCanvas.setTool(DrawingTool.FREEHAND)
            binding.myCanvas.setColor(Color.BLACK)
            binding.myCanvas.setBrushWidth(16f)
        }

        binding.pencil.setOnClickListener {
            binding.pencil.tooltipText = "Pencil"
            binding.myCanvas.setTool(DrawingTool.FREEHAND)
            binding.myCanvas.setColor(Color.parseColor("#b6b6b6"))
            binding.myCanvas.setBrushWidth(12f)
        }

        binding.brush.setOnClickListener {
            binding.brush.tooltipText = "Brushes"
            if (binding.linearLayout2.visibility == View.VISIBLE) {
                binding.linearLayout2.visibility = View.GONE
            } else {
                if (binding.linearLayout4.visibility == View.VISIBLE) {
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
            binding.layer.tooltipText = "Layer Manager"
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
            binding.text.tooltipText = "Enter Text"
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
        binding.download.setOnClickListener {
            binding.download.tooltipText = "Download File"
            val bitmap = binding.myCanvas.captureBitmap()
            val fileName: String =
                Truncator(FileNameGen().generateFileNameJPEG(), 24, false).textTruncate()
            binding.myCanvas.saveBitmapToFile(
                requireContext(), bitmap,
                "${fileName}.jpg"
            )

            Toast.makeText(requireContext(), "Image downloaded $fileName", Toast.LENGTH_LONG).show()

        }

        binding.open.setOnClickListener {
            binding.open.tooltipText = "Open File"
            openFilePicker()
        }

        binding.save.setOnClickListener {
            binding.save.tooltipText = "Save File"
            showSaveCanvasDialog()
        }
    }

    private fun restoreDrawing(drawing: Drawing) {
        Log.d(TAG, "restoreDrawing: ${drawing.pathData}")
        val pathsJson = PairConverter().fromPaths(drawing.pathData)
        Log.d(TAG, "pathsJson: ${pathsJson}")
        binding.myCanvas.paths.addAll(pathsJson)
    }

    fun loadPath(pathStr: String, pathColor: Int, pathStroke: Float) {
        val paint = Paint()
        paint.apply {
            strokeWidth = pathStroke
            color = pathColor
        }

        var pathData = PairConverter().fromPaths(pathStr)
        pathData.forEach { it ->
            binding.myCanvas.paths.add(it)
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


    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed to load the bitmap
            val filePath = "/storage/emulated/0/Android/data/com.example.sketchcrew/files/"
            loadBitmapFromExternalStorage(filePath)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
                val filePath = "/storage/emulated/0/Android/data/com.example.sketchcrew/files/"
                loadBitmapFromExternalStorage(filePath)
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBitmapFromExternalStorage(filePath: String): Bitmap? {
//        val filePath = "path/to/your/image.jpg"  // Replace with actual file path
        val bitmap = BitmapFactory.decodeFile(filePath)

        if (bitmap != null) {
            displayBitmapOnCanvas(bitmap)
        } else {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }
        return bitmap
    }

    private fun displayBitmapOnCanvas(bitmap: Bitmap) {

        // Create a mutable bitmap to draw on
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Create a canvas with the mutable bitmap
        val canvas = Canvas(mutableBitmap)

        // Draw the bitmap on the canvas (if you have more drawings, add them here)
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        // Set the bitmap to the custom view
        canvasView.setBitmap(mutableBitmap)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            val filePath = getRealPathFromURI(uri)
            if (filePath != null) {
                loadBitmapFromExternalStorage(filePath)
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var filePath: String? = null
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
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
            val fileName = editTextFileName.text.toString()
            val description = editTextDescription.text.toString()
            val selectedFormat = spinnerFileFormat.selectedItem.toString()

            // Handle the save action here
            handleSave(fileName, description, selectedFormat)
//            loadPath()

            dialog.dismiss()
        }

        dialog.show()
    }

    fun savePathToFile(pathData: String, fileName: String) {
        try {
            requireContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(pathData.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
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
            if (selectedFormat == "JPEG") {
                filename = "${fileName}.jpg"
            }
            if (selectedFormat == "SVG") {
                filename = "${fileName}.svg"
            }
        }
        val pathsList = canvasView.paths

        lifecycleScope.launch(Dispatchers.IO) {

//            val bitmap = binding.myCanvas.captureBitmap()
//            val drawnBitmap = binding.myCanvas.saveBitmapToFile(
//                requireContext(), bitmap,
//                filename
//            )
            val myPath = PairConverter().fromPathList(pathsList)
            Log.d(TAG, "saveCanvas: $myPath")
            val paths = canvasView.paths
            var serial = ""
                serial = PairConverter().fromPathList(paths)
                val serializedPaint = serial
                Log.d(TAG, "handleSave (serializedPaint): $serializedPaint")
                val drawing = Drawing(filename = filename, pathData = serial, paintData = serializedPaint)
                viewModel.saveDrawing(drawing)
//            Log.d(TAG, "DrawnBitmap: $drawnBitmap")
        }
        Toast.makeText(requireContext(), "Drawing Saved", Toast.LENGTH_LONG).show()
    }

    private fun changeColor(color: Int) {
        brushColor = color
    }

    private fun changeShapeType(shape: String) {
        shapeType[0] = shape
    }

    companion object {
        var paintColor = Paint()
        private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 101
        private val REQUEST_CODE_PICK_IMAGE = 102
    }

    private fun setupSendCollaboration() {
        binding.sendCollab.setOnClickListener {
            canvasView.saveToFirebase()
            // TEST
            canvasView.loadFromFirebase()
        }
    }

    private fun setupReceiveCollaboration() {
        binding.receiveCollab.setOnClickListener {
            canvasView.loadFromFirebase()

        }
    }

    private fun setUpStopCollaboration() {
        binding.endCollab.setOnClickListener {
            canvasView.stopCollaboration()
        }
    }

    private fun setUpChatButton() {
        binding.chatBtn.setOnClickListener {
            val bottomSheet = ChatFragment()
            bottomSheet.show(parentFragmentManager, "ChatFragment")
        }
    }


    // <------: END

}





