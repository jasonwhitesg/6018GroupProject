package com.example.drawingapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SavedDrawingsViewModel(private val repository: DrawingRepository) : ViewModel() {

    val latestSavedDrawing: LiveData<DrawingData> get() = repository.latestSavedDrawing
    val allSavedDrawings: LiveData<List<DrawingData>> get() = repository.allSavedDrawings

    fun saveDrawing(savedFilePath: String) {
        repository.saveDrawing(savedFilePath)
    }
}

class DrawingViewModelFactory(private val repository: DrawingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedDrawingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavedDrawingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

