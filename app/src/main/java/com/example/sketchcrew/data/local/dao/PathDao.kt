package com.example.sketchcrew.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sketchcrew.data.local.models.PathData

@Dao
interface PathDao {
    @Insert
    suspend fun insert(pathData: PathData)

    @Query("SELECT * FROM paths")
    suspend fun getAllPaths(): List<PathData>

    @Query("SELECT * FROM paths WHERE id = :id")
    suspend fun getPathById(id: Int): PathData?

    @Update
    suspend fun update(pathData: PathData)
}
