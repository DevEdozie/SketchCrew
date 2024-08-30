package com.example.sketchcrew.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sketchcrew.data.local.dao.CanvasDao
import com.example.sketchcrew.data.local.dao.DrawingDao
import com.example.sketchcrew.data.local.dao.PathDao
import com.example.sketchcrew.data.local.models.CanvasModel
import com.example.sketchcrew.data.local.models.Drawing
import com.example.sketchcrew.data.local.models.PairConverter
import com.example.sketchcrew.data.local.models.PathData

@Database(entities = [CanvasModel::class, PathData::class, Drawing::class], version = 9, exportSchema = false)
@TypeConverters(PairConverter::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun canvasDao(): CanvasDao
    abstract fun pathDao(): PathDao
    abstract fun drawingDao(): DrawingDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null


        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "canvas_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}