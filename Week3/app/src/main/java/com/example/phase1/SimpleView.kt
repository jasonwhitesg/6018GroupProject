package com.example.phase1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SimpleView : ViewModel() {

    data class Circle(val x: Float, val y: Float, val color: Int)

    private val _circlesLiveData = MutableLiveData<List<Circle>>(mutableListOf())

    val circlesLiveData: LiveData<List<Circle>>
        get() = _circlesLiveData

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> get() = _bitmap

    fun randomColor(): Int {
        return Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }

    fun addCircle(x: Float, y: Float) {
        val color = randomColor()
        val newCircle = Circle(x, y, color)
        val currentCircles = _circlesLiveData.value?.toMutableList() ?: mutableListOf()
        currentCircles.add(newCircle)
        _circlesLiveData.value = currentCircles  // Update LiveData
    }

    fun saveCircles(context: Context) {
        val sharedPreferences = context.getSharedPreferences("circle_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(_circlesLiveData.value)  // <- Changed this line
        editor.putString("circle_list", json)
        editor.apply()
    }

    fun loadCircles(context: Context) {
        val sharedPreferences = context.getSharedPreferences("circle_data", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("circle_list", null)
        val type = object : TypeToken<List<Circle>>() {}.type
        val circles = gson.fromJson(json, type) as? List<Circle> ?: emptyList()

        _circlesLiveData.value = circles
    }

    fun clearCircles(context: Context) {
        val sharedPreferences = context.getSharedPreferences("circle_data", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        _circlesLiveData.value = emptyList()
    }

    fun updateBitmap(newBitmap: Bitmap) {
        _bitmap.value = newBitmap
    }


}