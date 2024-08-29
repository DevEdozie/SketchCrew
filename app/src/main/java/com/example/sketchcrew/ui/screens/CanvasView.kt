package com.example.sketchcrew.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.sketchcrew.R
import com.example.sketchcrew.utils.LayerManager
import com.example.sketchcrew.utils.PathIteratorFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class DrawingTool {
    FREEHAND, CIRCLE, SQUARE, ARROW, ERASER
}

class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var scaleFactor = 1.0f
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var layerManager: LayerManager = LayerManager()
    private var currentLayer: Bitmap? = null
    private var layerArray = mutableListOf<Bitmap>()

    // Firebase Variables :-> DO NOT TOUCH
    private lateinit var database: DatabaseReference
    private var valueEventListener: ValueEventListener? = null
    private var isShared = false // Variable to check if code is being shared or not
    // < --

    init {
        init()
    }

    private var currentPath = Path()
    val paths = mutableListOf<Pair<Path, Paint>>()
    var currentTool = DrawingTool.FREEHAND
    val mpaths = mutableListOf<Path>()

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f

    //Variables for caching
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private lateinit var staticLayout: StaticLayout

    private var currentColor = Color.BLACK
    private var eraserMode = false

    private val undonePaths = mutableListOf<Pair<Path, Paint>>()

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.black, null)
    private var textToDraw: String? = null
    private var textX: Float = 0f
    private var textY: Float = 0f
    private var bitmap: Bitmap? = null

    private fun init() {
        paintColor.apply {
            color = Color.BLACK
            // Smooths out edges of what is drawn without affecting shape.
            isAntiAlias = true
            // Dithering affects how colors with higher-precision than the device are down-sampled.
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 16f
        }
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.1f, 10.0f)
            invalidate()
            return true
        }

    }

    private var paint = paintColor

    fun setTool(tool: DrawingTool) {
        currentTool = tool
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
        layerArray.add(extraBitmap)
//        if (width > 0 && height > 0) {
//            createNewLayer(width, height)
//        }
    }

    fun setTextToDraw(text: String, x: Float, y: Float) {
        textToDraw = text
        val textPaint: TextPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = 16 * resources.displayMetrics.density
        textPaint.color = 0xFF000000.toInt()

        val width = textPaint.measureText(textToDraw).toFloat()
        staticLayout = StaticLayout(
            text, textPaint,
            width.toInt(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0F, false
        )
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            save()
            canvas.scale(scaleFactor, scaleFactor)

            for (i in pathList.indices) {
                if (eraserMode) {
                    // Ensure eraser settings are applied when eraserMode is on
//                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
//                    paint.color = Color.TRANSPARENT
                    setEraser()
                } else {
                    paint.xfermode = null
                    paint.color = colorList[i]
                }

                canvas.drawPath(pathList[i], paint)
                invalidate()
            }
            for ((path, paint) in paths) {
                canvas.drawPath(path, paint)
                invalidate()
            }
            currentLayer?.let {
                val myCanvas = Canvas(it)
                when (currentTool) {
                    DrawingTool.ARROW -> {
                        drawArrow(myCanvas)
                    }

                    DrawingTool.SQUARE -> {
                        drawSquare(myCanvas)
                    }

                    DrawingTool.CIRCLE -> {
                        drawCircle(myCanvas)
                    }

                    DrawingTool.ERASER -> {
                        paint.apply {
                            color = Color.WHITE
                        }
                        setEraserMode(true)
                    }

                    else -> {
                        myCanvas.drawPath(currentPath, paint)
                    }
                }
                invalidate()
            }

            bitmap?.let {
                canvas.drawBitmap(it, 0f, 0f, null)
            }

            textToDraw?.let {
                staticLayout.draw(canvas)
            }
            restore()
            invalidate()
        }
    }

    private fun drawSquare(canvas: Canvas) {
        val squarePath = Path()
        squarePath.moveTo(startX, startY)
        squarePath.lineTo(startX, endY)
        squarePath.lineTo(endX, endY)
        squarePath.lineTo(endX, startY)
        squarePath.close()
        canvas.drawPath(squarePath, paint)
        invalidate()
    }

    private fun drawCircle(canvas: Canvas) {
        val radius =
            sqrt(((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY)).toDouble()).toFloat()
        canvas.drawCircle(startX, startY, radius, paint)
        invalidate()
    }

    private fun drawArrow(canvas: Canvas) {
        val angle = atan2((endY - startY).toDouble(), (endX - startX).toDouble())
        val arrowHeadLength = 30f
        val arrowHeadAngle = Math.toRadians(45.0)

        val linePaint = Paint(paint)
        canvas.drawLine(startX, startY, endX, endY, linePaint)

        val arrowPath = Path()

        arrowPath.lineTo(endX, endY)

        // Calculate the first point of the arrowhead
        val p1X = (endX - arrowHeadLength * cos(angle - arrowHeadAngle)).toFloat()
        val p1Y = (endY - arrowHeadLength * sin(angle - arrowHeadAngle)).toFloat()

        // Calculate the second point of the arrowhead
        val p2X = (endX - arrowHeadLength * cos(angle + arrowHeadAngle)).toFloat()
        val p2Y = (endY - arrowHeadLength * sin(angle + arrowHeadAngle)).toFloat()

        // Draw the first side of the arrowhead
        arrowPath.moveTo(p1X, p1Y)

        // Draw the second side of the arrowhead from the first point
        arrowPath.lineTo(p2X, p2Y)

        // Optionally, close the path to make it a filled arrowhead
        arrowPath.lineTo(endX, endY)

        canvas.drawPath(arrowPath, linePaint)
        invalidate()
    }

    private fun drawCirclePath(): Path {
        val circlePath = Path()
        val radius =
            sqrt(((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY)).toDouble()).toFloat()
        circlePath.addCircle(startX, startY, radius, Path.Direction.CW)
        paths.add(Pair(circlePath, paint))
        return circlePath
    }

    private fun drawSquarePath(): Path {
        val squarePath = Path()
        squarePath.moveTo(startX, startY)
        squarePath.lineTo(startX, endY)
        squarePath.lineTo(endX, endY)
        squarePath.lineTo(endX, startY)
        squarePath.close()
        paths.add(Pair(squarePath, paint))
        return squarePath
    }

    private fun drawArrowPath(): Path {
        val arrowPath = Path()
        arrowPath.moveTo(startX, startY)
        arrowPath.lineTo(endX, endY)

        val angle = atan2((endY - startY).toDouble(), (endX - startX).toDouble())
        val arrowHeadLength = 30f
        val arrowHeadAngle = Math.toRadians(45.0)

        // Calculate the points for the arrowhead
        val p1X = (endX - arrowHeadLength * cos(angle - arrowHeadAngle)).toFloat()
        val p1Y = (endY - arrowHeadLength * sin(angle - arrowHeadAngle)).toFloat()

        val p2X = (endX - arrowHeadLength * cos(angle + arrowHeadAngle)).toFloat()
        val p2Y = (endY - arrowHeadLength * sin(angle + arrowHeadAngle)).toFloat()

        // Draw the arrowhead as a part of the same path
        arrowPath.lineTo(p1X, p1Y)
        arrowPath.lineTo(endX, endY) // Move back to the end point of the arrow
        arrowPath.lineTo(p2X, p2Y)


        arrowPath.lineTo(p1X, p1Y)
        arrowPath.lineTo(endX, endY)
        arrowPath.lineTo(p2X, p2Y)

        paths.add(Pair(arrowPath, paint))
        return arrowPath
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            undonePaths.add(paths.removeAt(paths.size - 1))
            invalidate()
        }
    }

    fun redo() {
        if (undonePaths.isNotEmpty()) {
            paths.add(undonePaths.removeAt(undonePaths.size - 1))
            invalidate()
        }
    }

    private fun setEraser() {
        paintColor.apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            strokeWidth = 80f
            color = Color.TRANSPARENT
            Log.d("CanvasView", "Eraser Mode Activated")
        }

        brushColor = Color.TRANSPARENT
        currentColor = Color.TRANSPARENT
    }

    private fun setBrush(color: Int) {
        paint.apply {
            this.color = color
            xfermode = null
            strokeWidth = 60f
        }
    }

    fun setBrushWidth(widthStroke: Float) {
        paint.apply {
            color = color
            xfermode = null
            strokeWidth = widthStroke
        }
    }

    fun setEraserMode(isEraser: Boolean) {
        eraserMode = isEraser
        Log.d("CanvasView", "Eraser Mode Set: $eraserMode")
        if (eraserMode) {
            setEraser()
//            paint.color = Color.WHITE
        } else {
            // Reset paint settings when eraser mode is turned off
            paint.xfermode = null
            paint.strokeWidth = 10f
            // Set the paint color back to the drawing color
            paint.color = currentColor
            brushColor = Color.BLACK
        }
    }

    fun setColor(color: Int) {
        paint.apply {
            this.color = color
        }
        currentColor = color
        setBrush(color)
    }

    fun clearCanvas() {
        paths.clear()
        invalidate()
    }

    fun createNewLayer(width: Int, height: Int) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layerManager.addLayer(bitmap)
        invalidate()
    }

    fun switchToLayer(index: Int): Bitmap? {
        currentLayer = layerManager.getLayer(index)
        Canvas(currentLayer!!)  //Check this line ie usage is incorrect
        return currentLayer!!
    }

    fun removeLayer(index: Int) {
        layerManager.removeLayer(index)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        scaleGestureDetector.onTouchEvent(event)

        if (scaleGestureDetector.isInProgress) {
            return true
        }

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                startX = x
                startY = y
                currentPath.moveTo(x, y)
                mpaths.add(currentPath)
            }

            MotionEvent.ACTION_MOVE -> {
                endX = x
                endY = y
                if (currentTool == DrawingTool.FREEHAND) {
                    currentPath.lineTo(x, y)
                    mpaths.add(currentPath)
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                endX = x
                endY = y
                when (currentTool) {
                    DrawingTool.FREEHAND -> paths.add(Pair(Path(currentPath), Paint(paint)))
                    DrawingTool.CIRCLE -> paths.add(Pair(drawCirclePath(), Paint(paint)))
                    DrawingTool.SQUARE -> paths.add(Pair(drawSquarePath(), Paint(paint)))
                    DrawingTool.ARROW -> paths.add(Pair(drawArrowPath(), Paint(paint)))
                    DrawingTool.ERASER -> {
                        setEraser()
                        paths.add(Pair(Path(currentPath), Paint(paint)))
                    }

                    else -> {
                        currentPath.moveTo(x, y)
                    }
                }
                mpaths.add(currentPath)
                currentPath.reset()
                if (isShared) {
                    saveToFirebase() // Update the current state
                    // TEST
//                    loadFromFirebase()
                }

            }
        }
        return true
    }

    companion object {
        var shapeType = ArrayList<String>()
        var pathList = ArrayList<Path>()
        var colorList = ArrayList<Int>()
        var brushColor = Color.BLACK
        var path = Path()
        var paintColor = Paint()
        lateinit var firebaseAuth: FirebaseAuth
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        invalidate()  // Redraw the view
    }


    fun captureBitmap(): Bitmap {
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(currentLayer!!)
        draw(canvas)
        return currentLayer!!
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String): Uri? {
        var uri: Uri? = null
        try {
            val file = File(context.getExternalFilesDir(null), filename)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri
    }

    fun loadBitmapFromFile(context: Context, fileUri: Uri): Bitmap? {
        return try {
            val fileDescriptor =
                context.contentResolver.openFileDescriptor(fileUri, "r")?.fileDescriptor
            BitmapFactory.decodeFileDescriptor(fileDescriptor)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    // My Firebase functions: -> DO NOT TOUCH PLEASE

    // Serialization of Paths and Paints
    fun saveToJson(): JSONArray {
        return serializePathsAndPaints(paths)
    }

    // Deserialization of Paths and Paints
    fun loadFromJson(jsonArray: JSONArray) {
        paths.clear()
        paths.addAll(deserializePathsAndPaints(jsonArray))
        invalidate()
    }

    fun serializePathsAndPaints(paths: List<Pair<Path, Paint>>): JSONArray {
        val jsonArray = JSONArray()
        for ((path, paint) in paths) {
            val pathPoints = serializePath(path)
            val paintData = serializePaint(paint)

            val pathObject = JSONObject()
            pathObject.put("pathPoints", pathPoints)
            pathObject.put("paint", paintData)

            jsonArray.put(pathObject)
        }
        return jsonArray
    }

    fun serializePath(path: Path): JSONArray {
        val pathPoints = JSONArray()
        val pathIterator = PathIteratorFirebase(path)

        for (point in pathIterator) {
            val pointObject = JSONObject()
            pointObject.put("x", point.x)
            pointObject.put("y", point.y)
            pathPoints.put(pointObject)
        }

        return pathPoints
    }

    private fun serializePaint(paint: Paint): JSONObject {
        val paintObject = JSONObject()
        paintObject.put("color", paint.color)
        paintObject.put("strokeWidth", paint.strokeWidth)
        paintObject.put("style", paint.style.name)
        return paintObject
    }

    private fun deserializePathsAndPaints(jsonArray: JSONArray): List<Pair<Path, Paint>> {
        val paths = mutableListOf<Pair<Path, Paint>>()

        for (i in 0 until jsonArray.length()) {
            val pathObject = jsonArray.getJSONObject(i)

            val path = deserializePath(pathObject.getJSONArray("pathPoints"))
            val paint = deserializePaint(pathObject.getJSONObject("paint"))

            paths.add(Pair(path, paint))
        }

        return paths
    }

    private fun deserializePath(jsonArray: JSONArray): Path {
        val path = Path()

        for (i in 0 until jsonArray.length()) {
            val pointObject = jsonArray.getJSONObject(i)
            val x = pointObject.getDouble("x").toFloat()
            val y = pointObject.getDouble("y").toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        return path
    }

    private fun deserializePaint(jsonObject: JSONObject): Paint {
        val paint = Paint()
        paint.color = jsonObject.getInt("color")
        paint.strokeWidth = jsonObject.getDouble("strokeWidth").toFloat()
        paint.style = Paint.Style.valueOf(jsonObject.getString("style"))
        return paint
    }

    // Firebase Integration

    fun saveToFirebase() {
        database = FirebaseDatabase.getInstance().getReference("drawings")
        val jsonArray = saveToJson()
        database.child("canvasData").setValue(jsonArray.toString())
        isShared = true
        Toast.makeText(
            context,
            "Data Updated...",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun loadFromFirebase() {
        database = FirebaseDatabase.getInstance().getReference("drawings")
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jsonArray = JSONArray(snapshot.value.toString())
                if (jsonArray != null) {
                    loadFromJson(jsonArray)
                    // Test
//                    saveToFirebase()
                    // Optionally, notify the user or refresh the UI
                    Toast.makeText(
                        context,
                        "Canvas data loaded...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(
                    context,
                    "Error loading data",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        database.child("canvasData").addValueEventListener(valueEventListener as ValueEventListener)
        isShared = true
    }

    fun stopCollaboration() {
        database = FirebaseDatabase.getInstance().getReference("drawings")
        // Remove the data from the database
//        database.child("canvasData").removeValue().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // Data deleted successfully, you can perform further actions here
//                Toast.makeText(
//                    context,
//                    "...",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                // Handle any errors
//                Toast.makeText(
//                    context,
//                    "Error",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
        // Detach the listener to stop receiving updates
        valueEventListener?.let {
            database.child("canvasData").removeEventListener(it)
        }
        isShared = false
        Toast.makeText(
            context,
            "Collaboration ended...",
            Toast.LENGTH_SHORT
        ).show()
    }


//    fun getUniqueId(): String {
//        database = FirebaseDatabase.getInstance().getReference("drawings")
//        drawingId = database.push().key!!
//        return drawingId
//    }

    // FIREBASE -> END
}