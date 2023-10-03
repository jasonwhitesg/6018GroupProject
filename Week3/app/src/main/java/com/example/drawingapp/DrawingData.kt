package com.example.drawingapp


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}


@Entity(tableName="DrawingTable")
data class DrawingData(
    var timestamp: Date,
    var filePath: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}



