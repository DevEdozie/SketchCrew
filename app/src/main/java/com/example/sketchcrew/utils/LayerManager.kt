package com.example.sketchcrew.utils

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast

private const val TAG = "LayerManager"
open class LayerManager {
    private val layers = mutableListOf<Bitmap>()

    fun addLayer(bitmap: Bitmap) {
        layers.add(bitmap)
        Log.d(TAG, "addLayer: New Layer Added")
    }

    fun getLayer(index: Int): Bitmap? {
        Log.d(TAG, "getLayer: Layer Switched")
        return if (index in layers.indices) layers[index] else null
    }

    fun removeLayer(index: Int) {
        if (index in layers.indices) {
            Log.d(TAG, "removeLayer: Layer Removed")
            layers.removeAt(index)
        }
    }

    fun getAllLayers(): List<Bitmap> {
        return layers
    }
}