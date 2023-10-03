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


    fun updateBitmap(newBitmap: Bitmap) {
        _bitmap.value = newBitmap
    }
}