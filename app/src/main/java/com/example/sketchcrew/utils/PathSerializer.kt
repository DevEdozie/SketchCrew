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
        path.close()
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
