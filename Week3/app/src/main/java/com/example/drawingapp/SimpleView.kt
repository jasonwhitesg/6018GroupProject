package com.example.drawingapp

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SimpleView : ViewModel() {
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> get() = _bitmap

    // Add fields to store color and slider value
    private var selectedColor: Int = Color.BLACK
    private var selectedSliderValue: Int = 10

    private val _ballSize = MutableLiveData<Float>(20f)
    val ballSize: LiveData<Float> = _ballSize

    private val _ballColor = MutableLiveData<Int>(Color.BLACK)
    val ballColor: LiveData<Int> = _ballColor

    private val _ballX = MutableLiveData<Float>(400f)
    val ballX: LiveData<Float> get() = _ballX

    private val _ballY = MutableLiveData<Float>(400f)
    val ballY: LiveData<Float> get() = _ballY


    fun updateBallPosition(x: Float, y: Float) {
        _ballX.value = x
        _ballY.value = y
//        Log.d("ViewModel", "Ball position updated: x = $x, y = $y")
    }

    fun getBallPosition(): Pair<Float?, Float?> {
        val x = _ballX.value
        val y = _ballY.value
        Log.d("ViewModel", "Getting ball position: x = $x, y = $y")
        return Pair(x, y)
    }


    fun updateBallSize(size: Float) {
        _ballSize.value = size
    }

    fun updateBallColor(color: Int) {
        _ballColor.value = color
    }

    fun getBallColor(): Int {
        return selectedColor
    }

    fun getBallSize(): Int {
        return selectedSliderValue
    }

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