package com.example.sketchcrew.data.local.models

import android.graphics.Paint
import android.graphics.Path
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PairConverter {
    @TypeConverter
    fun fromPaths(value: String): List<Pair<Path, Paint>> {
        val listType = object : TypeToken<List<Pair<Path, Paint>>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromPathList(list: List<Pair<Path, Paint>>): String {
        return Gson().toJson(list)
    }
}