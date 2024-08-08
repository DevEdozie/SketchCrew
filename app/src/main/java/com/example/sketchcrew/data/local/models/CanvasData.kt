package com.example.sketchcrew.data.local.models

import android.graphics.Paint
import android.graphics.Path
import androidx.databinding.adapters.Converters

data class CanvasData(
    val id: Long,
    val name: String,
    val desc: String,
    val paths: List<Pair<Path, Paint>> = listOf()
)

