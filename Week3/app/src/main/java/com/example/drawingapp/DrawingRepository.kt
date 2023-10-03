package com.example.drawingapp

import android.util.Log
import java.io.IOException
import com.google.gson.JsonParseException
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date


//class DrawingRepository(private val scope: CoroutineScope, private val dao: DrawingDAO) {

//    val latestDrawing = dao.latestDrawings().asLiveData()
//
//    val allDrawings = dao.allDrawings().asLiveData()
//
//    fun fetchFile() {
//        scope.launch {
//            try {
//                // Save the fetched joke to the database
//                dao.addJoke(DrawingData(Date(), jokeText))
//            } catch (exception: IOException) {
//                Log.e("DrawingRepo", "Network Error: ${exception.localizedMessage}")
//            } catch (exception: JsonParseException) {
//                Log.e("DrawingRepo", "JSON Parsing Error: ${exception.localizedMessage}")
//            } catch (exception: Exception) {
//                Log.e("DrawingRepo", "Unexpected Error: ${exception.localizedMessage}")
//            }
//        }
//    }


//}