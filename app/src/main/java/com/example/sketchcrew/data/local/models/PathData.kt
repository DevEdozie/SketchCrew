package com.example.sketchcrew.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paths")
data class PathData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val desc: String,
    val path: String,
    val color: Int,
    val strokeWidth: Float
)

