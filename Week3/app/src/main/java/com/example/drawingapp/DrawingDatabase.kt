package com.example.drawingapp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow


@Database(entities= [DrawingData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DrawingDatabase : RoomDatabase(){
    abstract fun drawingDao(): DrawingDAO

    companion object {

        @Volatile
        private var INSTANCE: DrawingDatabase? = null

        fun getDatabase(context: Context): DrawingDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrawingDatabase::class.java,
                    "Drawing_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

@Dao
interface DrawingDAO {
//this is a random comment
    @Insert
    suspend fun addSavedFile(filePath: DrawingData)

    @Query("SELECT * FROM drawingTable ORDER BY timestamp DESC LIMIT 1")
    fun latestDrawing(): Flow<DrawingData>

    @Query("SELECT * FROM drawingTable ORDER BY timestamp DESC")
    fun allDrawings(): Flow<List<DrawingData>>
}


