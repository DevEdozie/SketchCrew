package com.example.sketchcrew.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawings")
data class Drawing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filename: String,
    val description: String,
    val authId: String,
    val pathData: String,
    val paintData: String
)

