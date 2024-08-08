package com.example.sketchcrew.repository

import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import com.example.sketchcrew.data.local.database.RoomDB
import com.example.sketchcrew.data.local.models.CanvasData
import com.example.sketchcrew.data.local.models.CanvasModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CanvasRepository(context: Context) {
    private val canvasDao = RoomDB.getDatabase(context).canvasDao()

    fun saveCanvas(canvas: CanvasData) {
        val entity = CanvasModel(
            id = canvas.id,
            name = canvas.name,
            desc = canvas.desc,
            serializedPaths = serializePaths(canvas.paths)
        )
        canvasDao.insertCanvas(entity)
    }

    fun loadCanvas(id: Long): CanvasData? {
        val entity = canvasDao.getCanvas(id) ?: return null
        return CanvasData(
            id = entity.id,
            name = entity.name,
            desc = entity.desc,
            paths = deserializePaths(entity.serializedPaths)
        )
    }

    fun getAllCanvases(): List<CanvasData> {
        return canvasDao.getAllCanvases().map { entity ->
            CanvasData(
                id = entity.id,
                name = entity.name,
                desc = entity.desc,
                paths = deserializePaths(entity.serializedPaths)
            )
        }
    }

    private fun serializePaths(paths: List<Pair<Path, Paint>>): String {
        // Serialize paths to JSON
        // This is a simplified example; you need a proper serialization strategy
        return Gson().toJson(paths)
    }

    private fun deserializePaths(serializedPaths: String): List<Pair<Path, Paint>> {
        // Deserialize paths from JSON
        // This is a simplified example; you need a proper deserialization strategy
        return Gson().fromJson(serializedPaths, object : TypeToken<List<Pair<Path, Paint>>>() {}.type)
    }

    fun deleteCanvas(canvas: CanvasData) {
        val entity = CanvasModel(
            id = canvas.id,
            name = canvas.name,
            desc = canvas.desc,
            serializedPaths = serializePaths(canvas.paths)
        )
        return canvasDao.deleteCanvas(entity)
    }

}