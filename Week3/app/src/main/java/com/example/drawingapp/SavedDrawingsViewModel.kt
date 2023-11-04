package com.example.drawingapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SavedDrawingsViewModel(private val repository: DrawingRepository) : ViewModel() {

    val allSavedDrawings: LiveData<List<DrawingData>> get() = repository.allSavedDrawings

    fun updateSavedStatus(drawingId: Int, isSaved: Boolean) {
        viewModelScope.launch {
            repository.updateSavedStatus(drawingId, isSaved)
        }
    }
}

class DrawingViewModelFactory(private val repository: DrawingRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedDrawingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavedDrawingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



