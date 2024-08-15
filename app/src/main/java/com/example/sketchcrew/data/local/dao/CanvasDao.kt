package com.example.sketchcrew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sketchcrew.data.local.models.CanvasModel
import kotlinx.coroutines.flow.Flow


@Dao
    interface CanvasDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertCanvas(canvas: CanvasModel)

        @Query("SELECT * FROM canvas_table WHERE id = :id")
        suspend fun getCanvas(id: Long): CanvasModel?

        @Query("SELECT * FROM canvas_table")
        suspend fun getAllCanvases(): List<CanvasModel>

        @Delete
        suspend fun deleteCanvas(canvas: CanvasModel)
    }