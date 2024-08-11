package com.example.sketchcrew.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.example.sketchcrew.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val STROKE_WIDTH = 12f

enum class DrawingTool {
    FREEHAND, CIRCLE, SQUARE, ARROW, ERASER
}

class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        init()
    }

    private var currentPath = Path()
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var currentTool = DrawingTool.FREEHAND

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    //Variables for caching
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val drawing = Path()

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

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.white, null)

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
            strokeWidth = STROKE_WIDTH
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

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in pathList.indices) {
            paint.color = colorList[i]
            canvas.drawPath(pathList[i], paint)
        }
        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }
        if (currentTool == DrawingTool.ARROW) {
            drawArrow(canvas)
        } else if (currentTool == DrawingTool.SQUARE) {
            drawSquare(canvas)
        } else if (currentTool == DrawingTool.CIRCLE) {
            drawCircle(canvas)
        } else {
            canvas.drawPath(currentPath, paint)
        }
//        for (i in shapeType.indices) {
//            if (shapeType[i] == "rectangle") {
//                drawSquare(canvas)
//            } else if (shapeType[i] == "oval") {
//                drawCircle(canvas)
//            } else if (shapeType[i] == "arrow") {
////                drawSquare(canvas)
////                drawCircle(canvas)
//                drawArrow(canvas)
//                canvas.drawPath(path, paint)
//            }
//        }
    }

//    fun drawSquare(canvas: Canvas) {
//        val RADIUS = 100
//        val rect = Rect(
//            ((motionTouchEventX - ((0.8) * RADIUS)).toInt()),
//            ((motionTouchEventY - ((0.6) * RADIUS)).toInt()),
//            ((motionTouchEventX + ((0.8) * RADIUS)).toInt()),
//            ((motionTouchEventY + ((0.6 * RADIUS))).toInt())
//        )
//        canvas.drawRect(rect, paint)
//    }

    private fun drawSquare(canvas: Canvas) {
        val squarePath = Path()
        squarePath.moveTo(startX, startY)
        squarePath.lineTo(startX, endY)
        squarePath.lineTo(endX, endY)
        squarePath.lineTo(endX, startY)
        squarePath.close()
        canvas.drawPath(squarePath, paint)
    }

//    fun drawCircle(canvas: Canvas) {
//        canvas.drawCircle(
//            motionTouchEventX / 2.0f,
//            motionTouchEventY / 2.0f,
//            (width - 10) / 2.0f,
//            paint
//        )
//    }

    private fun drawCircle(canvas: Canvas) {
        val radius = sqrt(((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY)).toDouble()).toFloat()
        canvas.drawCircle(startX, startY, radius, paint)
    }

//    fun drawArrow(canvas: Canvas) {
//        val angle = atan2((currentY -motionTouchEventY).toDouble(), (currentX - motionTouchEventX).toDouble())
//        val curHeadLength = 30f
//        val curHeadAngle = Math.toRadians(45.0)
//
//        paint = Paint(paint)
//        canvas.drawLine(motionTouchEventX, motionTouchEventY, currentX, currentY, paint)
//
//
//        curPath.moveTo(currentX, currentY)
//        curPath.lineTo(
//            (currentX - curHeadLength * cos(angle - curHeadAngle)).toFloat(),
//            (currentY - curHeadLength * sin(angle - curHeadAngle)).toFloat()
//        )
//        curPath.moveTo(currentX, currentY)
//        curPath.lineTo(
//            (currentX - curHeadLength * cos(angle + curHeadAngle)).toFloat(),
//            (currentY - curHeadLength * sin(angle + curHeadAngle)).toFloat()
//        )
//        canvas.drawPath(curPath, paint)
//    }

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
    }

    private fun drawCirclePath(): Path {
        val circlePath = Path()
        val radius = sqrt(((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY)).toDouble()).toFloat()
        circlePath.addCircle(startX, startY, radius, Path.Direction.CW)
        return circlePath
    }

    private fun drawSquarePath(): Path {
        val squarePath = Path()
        squarePath.moveTo(startX, startY)
        squarePath.lineTo(startX, endY)
        squarePath.lineTo(endX, endY)
        squarePath.lineTo(endX, startY)
        squarePath.close()
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

    fun setEraser() {
        paint.apply {
            color = Color.WHITE
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            strokeWidth = 50f
        }
    }

    fun setBrush(color: Int) {
        paint.apply {
            this.color = color
            xfermode = null
            strokeWidth = 10f
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        motionTouchEventX = event.x
//        motionTouchEventY = event.y
//
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> touchStart()
//            MotionEvent.ACTION_MOVE -> touchMove()
//            MotionEvent.ACTION_UP -> touchUp()
//        }
//        return true
        val x = event.x
        val y = event.y

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

    private fun touchStart() {
//        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
            colorList.add(brushColor)
            pathList.add(path)
        }
        invalidate()
    }

    private fun touchUp() {
        drawing.addPath(curPath)
        paths.add(Pair(curPath, paint))
        when (currentTool) {
            DrawingTool.FREEHAND -> paths.add(Pair(Path(currentPath), Paint(paint)))
            DrawingTool.CIRCLE -> paths.add(Pair(drawCirclePath(), Paint(paint)))
            DrawingTool.SQUARE -> paths.add(Pair(drawSquarePath(), Paint(paint)))
            DrawingTool.ARROW -> paths.add(Pair(drawArrowPath(), Paint(paint)))
            else -> {}
        }
        currentPath.reset()
    }

    companion object {
        var shapeType = ArrayList<String>()
        var pathList = ArrayList<Path>()
        var colorList = ArrayList<Int>()
        var brushColor = Color.BLACK
        var path = Path()
        var paintColor = Paint()
//        var drawPath = Path()
        var paths = mutableListOf<Pair<Path, Paint>>()
    }

    fun setPath(pathData: String) {
        path = Path().apply {
            // Convert path data string back to Path object
            // Assume pathData is a series of coordinates in the format "x1,y1;x2,y2;..."
            val coordinates = pathData.split(";")
            coordinates.forEach { coordinate ->
                val (x, y) = coordinate.split(",").map { it.toFloat() }
                if (path.isEmpty) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
        }
        invalidate()
    }

    fun getPathData(): String {
        // Convert Path object to string representation
        // Note: This is a simplified version and may need adjustments for complex paths
//        return buildString {
//            path.apply {
//                val pathPoints = FloatArray(2)
//                this.computeBounds(android.graphics.RectF(), true)
//                // Iterate through path points and append to the string
//                this.rLineTo(0f, 0f)
//            }
//        }
        val pathDataBuilder = StringBuilder()

        // Temporary array to store points from the path
        val pathPoints = FloatArray(2)

        // Measure and extract the segments of the path
        val pathMeasure = PathMeasure(path, false)
        var segmentLength = pathMeasure.length
        var segmentPosition = 0f

        // Iterate over the path segments and append to the string
        while (segmentPosition < segmentLength) {
            pathMeasure.getPosTan(segmentPosition, pathPoints, null)
            pathDataBuilder.append("${pathPoints[0]},${pathPoints[1]};")
            segmentPosition += 1f // Move in small increments to sample the path points
        }

        return pathDataBuilder.toString()
    }
}