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
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.database.RoomDB
import com.example.sketchcrew.data.local.models.PathData
import com.example.sketchcrew.utils.FileNameGen
import com.example.sketchcrew.utils.LayerManager
import com.example.sketchcrew.utils.PathIterator
import com.example.sketchcrew.utils.Truncator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class DrawingTool {
    FREEHAND, CIRCLE, SQUARE, ARROW, ERASER
}

private const val TAG = "CanvasView"

class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var scaleFactor = 1.0f
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var layerManager: LayerManager = LayerManager()
    private var currentLayer: Bitmap? = null

    init {
        init()
    }

    private var currentPath = Path()
    val paths = mutableListOf<Pair<Path, Paint>>()
    private var currentTool = DrawingTool.FREEHAND

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f

    //Variables for caching
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private var drawing = Path()

    private val curPath = Path()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    private lateinit var frame: Rect
    private var currentColor = Color.BLACK
    private var eraserMode = false

    private val undonePaths = mutableListOf<Pair<Path, Paint>>()

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.black, null)
    private var textToDraw: String? = null
    private var textX: Float = 0f
    private var textY: Float = 0f

    private fun init() {
        paintColor.apply {
            color = brushColor
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
//        layerManager = LayerManager()
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
        if (width> 0 && height> 0) {
            createNewLayer(width, height)
        }
    }

    fun setTextToDraw(text: String, x: Float, y: Float) {
        textToDraw = text
        textX = x
        textY = y
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            save()
            canvas.scale(scaleFactor, scaleFactor)

            for (i in pathList.indices) {
                paint.color = colorList[i]
                canvas.drawPath(pathList[i], paint)
                invalidate()
            }
            for ((path, paint) in paths) {
                canvas.drawPath(path, paint)
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

                    else -> {
                        myCanvas.drawPath(currentPath, paint)
                    }
                }
                invalidate()
            }

            textToDraw?.let {
                paint.color = Color.WHITE
                paint.style = Paint.Style.FILL
                canvas.drawPaint(paint)

                paint.color = Color.BLACK
                paint.textSize = 48F
                canvas.drawText(it, textX, textY, paint)
            }
            restore()
        }
        invalidate()
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
        arrowPath.moveTo(endX, endY)
        arrowPath.lineTo(
            (endX - arrowHeadLength * cos(angle - arrowHeadAngle)).toFloat(),
            (endY - arrowHeadLength * sin(angle - arrowHeadAngle)).toFloat()
        )
        arrowPath.moveTo(endX, endY)
        arrowPath.lineTo(
            (endX - arrowHeadLength * cos(angle + arrowHeadAngle)).toFloat(),
            (endY - arrowHeadLength * sin(angle + arrowHeadAngle)).toFloat()
        )
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

        arrowPath.moveTo(endX, endY)
        arrowPath.lineTo(
            (endX - arrowHeadLength * cos(angle - arrowHeadAngle)).toFloat(),
            (endY - arrowHeadLength * sin(angle - arrowHeadAngle)).toFloat()
        )
        arrowPath.moveTo(endX, endY)
        arrowPath.lineTo(
            (endX - arrowHeadLength * cos(angle + arrowHeadAngle)).toFloat(),
            (endY - arrowHeadLength * sin(angle + arrowHeadAngle)).toFloat()
        )
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
        paint.apply {
            this.color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            strokeWidth = 60f
        }
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
            this.color = color
            xfermode = null
            strokeWidth = widthStroke
        }
    }

    fun setEraserMode(isEraser: Boolean) {
        eraserMode = isEraser
        if (eraserMode) setEraser()
    }

    fun setColor(color: Int) {
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
        currentLayer = bitmap
    }

    fun switchToLayer(index: Int) {
        currentLayer = layerManager.getLayer(index)
    }

    fun removeLayer(index: Int) {
        layerManager.removeLayer(index)
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
            }

            MotionEvent.ACTION_MOVE -> {
                endX = x
                endY = y
                if (currentTool == DrawingTool.FREEHAND) {
                    currentPath.lineTo(x, y)
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
                    else -> {}
                }
                currentPath.reset()
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
    }

    fun setPath(pathData: String): Path {

        path = Path().apply {
            // Convert path data string back to Path object
            // Assume pathData is a series of coordinates in the format "x1,y1;x2,y2;..."
            val coordinates = pathData.split(";")
            coordinates.forEachIndexed { index, coordinate ->
                val points = coordinate.split(",")
                if (points.size == 2) {
                    try {
                        val x = points[0].toFloat()
                        val y = points[1].toFloat()
                        if (index == 0) {
                            moveTo(x, y)
                        } else {
                            lineTo(x, y)
                        }
                    } catch (e: NumberFormatException) {
                        // Log or handle the error gracefully
                        Log.e("CanvasView", "Invalid coordinate format: $coordinate")
                    }
                } else {
                    // Log or handle the error gracefully
                    Log.e("CanvasView", "Invalid coordinate pair: $coordinate")
                }
            }
        }
        invalidate()
        return path
    }


    fun getPathData(path: Path): String {
        val pathData = StringBuilder()

        val pathPoints = FloatArray(6) // Array to store the coordinates from the path
        val pathIterator = PathIterator(path)

        while (!pathIterator.isDone()) {
            val type = pathIterator.currentSegment(pathPoints)
            when (type) {
                PathIterator.SEG_MOVETO -> {
                    pathData.append("M${pathPoints[0]},${pathPoints[1]} ")
                }

                PathIterator.SEG_LINETO -> {
                    pathData.append("L${pathPoints[0]},${pathPoints[1]} ")
                }

                PathIterator.SEG_QUADTO -> {
                    pathData.append("Q${pathPoints[0]},${pathPoints[1]} ${pathPoints[2]},${pathPoints[3]} ")
                }

                PathIterator.SEG_CUBICTO -> {
                    pathData.append("C${pathPoints[0]},${pathPoints[1]} ${pathPoints[2]},${pathPoints[3]} ${pathPoints[4]},${pathPoints[5]} ")
                }

                PathIterator.SEG_CLOSE -> {
                    pathData.append("Z ")
                }
            }
            pathIterator.next()
        }

        return pathData.toString().trim()
    }


    fun captureBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
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

    fun loadPathData(pathData: PathData) {
        val filename = pathData.name
        val file = File("/storage/emulated/0/Android/data/com.example.sketchcrew/files/$filename")
        val fileURI: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        currentPath.reset()
//        val canvas = Canvas(currentLayer!!)
        setPath(pathData.path) // Set the path using the serialized string

        // Set paint properties
        setColor(pathData.color)
        setBrushWidth(pathData.strokeWidth)
        loadBitmapFromFile(context, fileURI)

//        canvas.drawPath(setPath(pathData.path), paint)
        invalidate() // Redraw the canvas with the new path data
    }


    fun saveCurrentPathToDatabase() {
        val pathString = getPathData(currentPath)
        val pathData = PathData(
            name = FileNameGen().generateFileNamePNG(),
            desc = Truncator(FileNameGen().generateFileNamePNG(), 10, true).textTruncate(),
            path = pathString,
            color = paintColor.color,
            strokeWidth = paintColor.strokeWidth
        )
        val db = RoomDB.getDatabase(context)
        GlobalScope.launch(Dispatchers.IO) {
            db.pathDao().insert(pathData)
        }
    }
}