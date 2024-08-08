package com.example.sketchcrew.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sketchcrew.data.local.models.CanvasModel


    @Dao
    interface CanvasDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertCanvas(canvas: CanvasModel)

        @Query("SELECT * FROM canvas_table WHERE id = :id")
        fun getCanvas(id: Long): CanvasModel?

        @Query("SELECT * FROM canvas_table")
        fun getAllCanvases(): List<CanvasModel>

        @Delete
        fun deleteCanvas(canvas: CanvasModel)
    }