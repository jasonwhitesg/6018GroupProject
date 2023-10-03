package com.example.drawingapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.drawingapp.R


class MainActivity : AppCompatActivity() {
    private val viewModel: SimpleView by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "Before setting the theme.")
        setTheme(R.style.Theme_Phase1)
        Log.d("MainActivity", "After setting the theme.")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
