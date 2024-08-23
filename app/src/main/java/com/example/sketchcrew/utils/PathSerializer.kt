package com.example.sketchcrew.utils

import android.graphics.Paint
import android.graphics.Path
import android.graphics.*
import android.graphics.PathMeasure
import com.google.gson.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type

class PathSerializer : JsonSerializer<Path>, JsonDeserializer<Path> {
    override fun serialize(src: Path, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        // Convert the Path to a JSONArray
        val paths = JSONArray()
        val pathIterator = PathIteratorFirebase(src)

        for (point in pathIterator) {
            val pointObject = JSONObject()
            pointObject.put("x", point.x)
            pointObject.put("y", point.y)
            paths.put(pointObject)
        }

        return JsonParser.parseString(paths.toString())

//        val pathCommands = JSONArray()
//        val pathIterator = PathIterator(src)
//
//        while (pathIterator.hasNext()) {
//            val command = pathIterator.next()
//            val commandObject = JSONObject()
//
//            when (command.type) {
//                PathCommandType.MOVE_TO -> {
//                    commandObject.put("type", "moveTo")
//                    commandObject.put("x", command.x)
//                    commandObject.put("y", command.y)
//                }
//                PathCommandType.LINE_TO -> {
//                    commandObject.put("type", "lineTo")
//                    commandObject.put("x", command.x)
//                    commandObject.put("y", command.y)
//                }
//                PathCommandType.QUAD_TO -> {
//                    commandObject.put("type", "quadTo")
//                    commandObject.put("controlX", command.controlX)
//                    commandObject.put("controlY", command.controlY)
//                    commandObject.put("endX", command.endX)
//                    commandObject.put("endY", command.endY)
//                }
//                PathCommandType.CUBIC_TO -> {
//                    commandObject.put("type", "cubicTo")
//                    commandObject.put("controlX1", command.controlX1)
//                    commandObject.put("controlY1", command.controlY1)
//                    commandObject.put("controlX2", command.controlX2)
//                    commandObject.put("controlY2", command.controlY2)
//                    commandObject.put("endX", command.endX)
//                    commandObject.put("endY", command.endY)
//                }
//                PathCommandType.ARC_TO -> {
//                    commandObject.put("type", "arcTo")
//                    commandObject.put("left", command.left)
//                    commandObject.put("top", command.top)
//                    commandObject.put("right", command.right)
//                    commandObject.put("bottom", command.bottom)
//                    commandObject.put("startAngle", command.startAngle)
//                    commandObject.put("sweepAngle", command.sweepAngle)
//                }
//                PathCommandType.CLOSE -> {
//                    commandObject.put("type", "close")
//                }
//            }
//
//            pathCommands.put(commandObject)
//        }
//
//        return JsonParser.parseString(pathCommands.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Path {
        // Convert the JSON back to a Path object
        val path = Path()
        val jsonArray = JSONArray(json.toString())

        for (i in 0 until jsonArray.length()) {
            val pointObject = jsonArray.getJSONObject(i)
            val x = pointObject.getDouble("x").toFloat()
            val y = pointObject.getDouble("y").toFloat()

            // Move to the first point or draw line to subsequent points
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        return path
    }
}

class PaintSerializer : JsonSerializer<Paint>, JsonDeserializer<Paint> {
    override fun serialize(src: Paint, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("color", src.color)
        jsonObject.addProperty("style", src.style.name)
        jsonObject.addProperty("strokeWidth", src.strokeWidth)
        // Add more properties of Paint as needed
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Paint {
        val paint = Paint()
        val jsonObject = json.asJsonObject
        paint.color = jsonObject.get("color").asInt
        paint.style = Paint.Style.valueOf(jsonObject.get("style").asString)
        paint.strokeWidth = jsonObject.get("strokeWidth").asFloat
        // Add more properties of Paint as needed
        return paint
    }
}

//data class PathCommand(
//    val type: PathCommandType,
//    val x: Float = 0f,
//    val y: Float = 0f,
//    val controlX: Float = 0f,
//    val controlY: Float = 0f,
//    val endX: Float = 0f,
//    val endY: Float = 0f,
//    val controlX1: Float = 0f,
//    val controlY1: Float = 0f,
//    val controlX2: Float = 0f,
//    val controlY2: Float = 0f,
//    val left: Float = 0f,
//    val top: Float = 0f,
//    val right: Float = 0f,
//    val bottom: Float = 0f,
//    val startAngle: Float = 0f,
//    val sweepAngle: Float = 0f
//)
//
//enum class PathCommandType {
//    MOVE_TO, LINE_TO, QUAD_TO, CUBIC_TO, ARC_TO, CLOSE
//}
//
//class PathIterator(private val path: Path) : Iterator<PathCommand> {
//    private val pathMeasure = PathMeasure(path, false)
//    private val pathData = mutableListOf<PathCommand>()
//    private var currentIndex = 0
//
//    init {
//        extractPathCommands()
//    }
//
//    override fun hasNext(): Boolean = currentIndex < pathData.size
//
//    override fun next(): PathCommand {
//        if (!hasNext()) throw NoSuchElementException()
//        return pathData[currentIndex++]
//    }
//
//    private fun extractPathCommands() {
////        val pathMeasure = PathMeasure(path, false)
//        val length = pathMeasure.length
//        val stepSize = 1f
//        var distance = 0f
//
//        val pos = FloatArray(2)
//        val tan = FloatArray(2)
//
//        while (distance < length) {
//            pathMeasure.getPosTan(distance, pos, tan)
//            // Assuming that you have a mechanism to detect the current command type
//            // This part of code must be customized to handle each command type appropriately
//            val command = detectPathCommandType(path, pos[0], pos[1])
//            pathData.add(command)
//            distance += stepSize
//        }
//    }
//
//    private fun detectPathCommandType(path: Path, x: Float, y: Float): PathCommand {
//        // This method should detect the command type at the given coordinates
//        // Here you should implement logic to determine the command type
//        // For the sake of demonstration, we return a dummy PathCommand of MOVE_TO type
////        return PathCommand(PathCommandType.MOVE_TO, x, y)
//
////        PathMeasure(path, false)
////        val pathBounds = RectF()
////        path.computeBounds(pathBounds, true)
////        val pathIterator = PathIterator(path)
////
////        while (pathIterator.hasNext()) {
////            val command = pathIterator.next()
////
////            when (command.type) {
////                PathCommandType.MOVE_TO -> {
////                    if (command.x == x && command.y == y) {
////                        return PathCommand(PathCommandType.MOVE_TO, x, y)
////                    }
////                }
////                PathCommandType.LINE_TO -> {
////                    if (command.x == x && command.y == y) {
////                        return PathCommand(PathCommandType.LINE_TO, x, y)
////                    }
////                }
////                PathCommandType.QUAD_TO -> {
////                    // Quadratic Bezier Curve requires additional information
////                    // Assuming the `command` object contains this information
////                    if (command.x == x && command.y == y) {
////                        return PathCommand(
////                            PathCommandType.QUAD_TO,
////                            command.controlX,
////                            command.controlY,
////                            x,
////                            y
////                        )
////                    }
////                }
////                PathCommandType.CUBIC_TO -> {
////                    // Cubic Bezier Curve requires additional information
////                    // Assuming the `command` object contains this information
////                    if (command.x == x && command.y == y) {
////                        return PathCommand(
////                            PathCommandType.CUBIC_TO,
////                            command.controlX1,
////                            command.controlY1,
////                            command.controlX2,
////                            command.controlY2,
////                            x,
////                            y
////                        )
////                    }
////                }
////                PathCommandType.ARC_TO -> {
////                    // ArcTo requires additional information
////                    // Assuming the `command` object contains this information
////                    if (command.x == x && command.y == y) {
////                        return PathCommand(
////                            PathCommandType.ARC_TO,
////                            command.left,
////                            command.top,
////                            command.right,
////                            command.bottom,
////                            command.startAngle,
////                            command.sweepAngle
////                        )
////                    }
////                }
////                PathCommandType.CLOSE -> {
////                    if (command.x == x && command.y == y) {
////                        return PathCommand(PathCommandType.CLOSE)
////                    }
////                }
////            }
////        }
////
////        // If no command is found that matches the given coordinates
////        throw IllegalArgumentException("No matching path command found for the coordinates ($x, $y)")
//
//        val pathCommands = mutableListOf<PathCommand>()
//        val length = pathMeasure.length
//        val stepSize = 1f
//        var distance = 0f
//
//        val pos = FloatArray(2)
//        val tan = FloatArray(2)
//        var lastPos = FloatArray(2)
//        var previousCommandType: PathCommandType? = null
//
//        while (distance < length) {
//            pathMeasure.getPosTan(distance, pos, tan)
//            val currentX = pos[0]
//            val currentY = pos[1]
//
//            if (previousCommandType == null) {
//                // The starting point is always a MOVE_TO
//                pathCommands.add(PathCommand(PathCommandType.MOVE_TO, currentX, currentY))
//                lastPos = pos.copyOf()
//                previousCommandType = PathCommandType.MOVE_TO
//            } else {
//                // Calculate the distance from the last position
//                val dist = Math.sqrt(
//                    Math.pow((currentX - lastPos[0]).toDouble(), 2.0) +
//                            Math.pow((currentY - lastPos[1]).toDouble(), 2.0)
//                ).toFloat()
//
//                // Determine the command type based on distance and position changes
//                when (previousCommandType) {
//                    PathCommandType.MOVE_TO -> {
//                        // Detect lineTo command
//                        pathCommands.add(PathCommand(PathCommandType.LINE_TO, currentX, currentY))
//                        lastPos = pos.copyOf()
//                        previousCommandType = PathCommandType.LINE_TO
//                    }
//                    PathCommandType.LINE_TO -> {
//                        // Detect quadTo command
//                        // Example criteria for quadTo, adjust as needed
//                        if (dist > 10f) { // Example threshold, adjust based on requirements
//                            pathCommands.add(PathCommand(PathCommandType.QUAD_TO, lastPos[0], lastPos[1], currentX, currentY))
//                            lastPos = pos.copyOf()
//                            previousCommandType = PathCommandType.QUAD_TO
//                        } else {
//                            pathCommands.add(PathCommand(PathCommandType.LINE_TO, currentX, currentY))
//                            lastPos = pos.copyOf()
//                            previousCommandType = PathCommandType.LINE_TO
//                        }
//                    }
//                    PathCommandType.QUAD_TO -> {
//                        // Detect cubicTo command
//                        // Example criteria for cubicTo, adjust as needed
//                        if (dist > 20f) { // Example threshold, adjust based on requirements
//                            pathCommands.add(PathCommand(PathCommandType.CUBIC_TO, lastPos[0], lastPos[1], currentX, currentY, currentX, currentY))
//                            lastPos = pos.copyOf()
//                            previousCommandType = PathCommandType.CUBIC_TO
//                        } else {
//                            pathCommands.add(PathCommand(PathCommandType.QUAD_TO, lastPos[0], lastPos[1], currentX, currentY))
//                            lastPos = pos.copyOf()
//                            previousCommandType = PathCommandType.QUAD_TO
//                        }
//                    }
//                    PathCommandType.CUBIC_TO -> {
//                        // Detect arcTo command
//                        // Example criteria for arcTo, adjust as needed
//                        if (dist > 30f) { // Example threshold, adjust based on requirements
//                            pathCommands.add(PathCommand(PathCommandType.ARC_TO, lastPos[0], lastPos[1], currentX, currentY, 0f, 0f))
//                            lastPos = pos.copyOf()
//                            previousCommandType = PathCommandType.ARC_TO
//                        } else {
//                            pathCommands.add(PathCommand(PathCommandType.CUBIC_TO, lastPos[0], lastPos[1], currentX, currentY, currentX, currentY))
//                            lastPos = pos.copyOf()
//                            previousCommandType = PathCommandType.CUBIC_TO
//                        }
//                    }
//                    PathCommandType.ARC_TO -> {
//                        // End of the path or handle other cases
//                        pathCommands.add(PathCommand(PathCommandType.CLOSE))
//                    }
//                    else -> {
//                        // Default to close path if none of the above types match
//                        pathCommands.add(PathCommand(PathCommandType.CLOSE))
//                    }
//                }
//            }
//
//            distance += stepSize
//        }
//
//        // Ensure we return a default or close command if the path ends
//        if (previousCommandType != null) {
//            pathCommands.add(PathCommand(PathCommandType.CLOSE))
//        }
//
//        return pathCommands.firstOrNull { it.x == x && it.y == y }
//            ?: PathCommand(PathCommandType.MOVE_TO, x, y)
//    }
//}
