package com.example.sketchcrew.data.local.models

import android.graphics.Paint
import android.graphics.Path
import android.graphics.*
import androidx.room.TypeConverter
import com.example.sketchcrew.utils.PaintSerializer
import com.example.sketchcrew.utils.PathSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PairConverter {

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Path::class.java, PathSerializer())
        .registerTypeAdapter(Paint::class.java, PaintSerializer())
        .registerTypeAdapter(Pair::class.java, CustomPairDeserializer())
        .create()

    @TypeConverter
    fun fromPaths(value: String): List<Pair<Path, Paint>> {
        val listType = object : TypeToken<List<Pair<Path, Paint>>>() {}.type
        return gson.fromJson<List<Pair<Path, Paint>>>(value, listType)
    }

    @TypeConverter
    fun fromPathList(list: List<Pair<Path, Paint>>): String {
        return gson.toJson(list)
    }
}

class CustomPairDeserializer : JsonDeserializer<Pair<Path, Paint>> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Pair<Path, Paint> {
        val jsonObject = json.asJsonObject

        // Deserialize the "first" part (list of points)
        val path = Path()
        val pointsArray = jsonObject.getAsJsonArray("first")
        if (pointsArray.size() > 0) {
            val firstPoint = pointsArray[0].asJsonObject
            path.moveTo(firstPoint.get("x").asFloat, firstPoint.get("y").asFloat)
            for (i in 1 until pointsArray.size()) {
                val point = pointsArray[i].asJsonObject
                path.lineTo(point.get("x").asFloat, point.get("y").asFloat)
            }
        }

        // Deserialize the "second" part (Paint object)
        val paintJson = jsonObject.getAsJsonObject("second")
        val paint = context.deserialize<Paint>(paintJson, Paint::class.java)

        return Pair(path, paint)
    }
}




