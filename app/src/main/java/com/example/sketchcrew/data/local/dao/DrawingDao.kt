package com.example.sketchcrew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sketchcrew.data.local.models.Drawing
import com.example.sketchcrew.data.local.models.PathData
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrawing(drawing: Drawing)

    @Query("SELECT * FROM drawings WHERE filename = :filename")
    suspend fun getDrawing(filename: String): Drawing?

    @Query("SELECT * FROM drawings")
    fun getAllDrawings(): Flow<List<Drawing>>

    @Query("SELECT * FROM drawings WHERE id = :id")
    fun getDrawingById(id: Long): LiveData<Drawing>
}
