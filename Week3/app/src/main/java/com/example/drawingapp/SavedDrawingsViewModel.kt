package com.example.drawingapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.File

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



