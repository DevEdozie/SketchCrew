package com.example.sketchcrew.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "canvas_table")
data class CanvasModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val desc: String,
    val serializedPaths: String
) {
    fun toCanvasData(): CanvasData {
        val paths = PairConverter().fromPaths(this.serializedPaths)
        return CanvasData(
            id = this.id,
            name = this.name,
            desc = this.desc,
            paths = paths
        )
    }
}
