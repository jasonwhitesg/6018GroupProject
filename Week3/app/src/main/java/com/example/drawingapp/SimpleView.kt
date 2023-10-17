package com.example.drawingapp

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
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> get() = _bitmap

    // Add fields to store color and slider value
    private var selectedColor: Int = Color.BLACK
    private var selectedSliderValue: Int = 10

    // Functions to get the stored values
    fun getSelectedColor(): Int {
        return selectedColor
    }

    fun getSelectedSliderValue(): Int {
        return selectedSliderValue
    }

    // Functions to update the stored values
    fun updateSelectedColor(color: Int) {
        selectedColor = color
    }

    fun updateSelectedSliderValue(value: Int) {
        selectedSliderValue = value
    }

    // Function to update the bitmap
    fun updateBitmap(newBitmap: Bitmap) {
        _bitmap.value = newBitmap
    }
}