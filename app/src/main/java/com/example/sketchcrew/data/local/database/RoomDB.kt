package com.example.sketchcrew.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sketchcrew.data.local.dao.CanvasDao
import com.example.sketchcrew.data.local.models.CanvasModel

@Database(entities = [CanvasModel::class], version = 1, exportSchema = false)
abstract class RoomDB : RoomDatabase() {
    abstract fun canvasDao(): CanvasDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "canvas_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}