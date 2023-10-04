package com.example.drawingapp

import android.util.Log
import java.io.IOException
import com.google.gson.JsonParseException
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date



class DrawingRepository(private val scope: CoroutineScope, private val dao: DrawingDAO) {

    val latestSavedDrawing = dao.latestDrawing().asLiveData()
    val allSavedDrawings = dao.allDrawings().asLiveData()

    fun saveDrawing(savedFilePath: String) {
        scope.launch {
            try {
                Log.d("DrawingRepo", "Attempting to save: $savedFilePath")

                // Save the drawing file path to the database
                dao.addSavedFile(DrawingData(timestamp = Date(), savedFile = savedFilePath))

                Log.d("DrawingRepo", "Successfully saved: $savedFilePath")
            } catch (exception: IOException) {
                Log.e("DrawingRepo", "Network Error: ${exception.localizedMessage}")
            } catch (exception: JsonParseException) {
                Log.e("DrawingRepo", "JSON Parsing Error: ${exception.localizedMessage}")
            } catch (exception: Exception) {
                Log.e("DrawingRepo", "Unexpected Error: ${exception.localizedMessage}")
            }
        }
    }
}
