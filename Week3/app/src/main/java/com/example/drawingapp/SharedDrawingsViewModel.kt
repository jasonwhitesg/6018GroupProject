package com.example.drawingapp

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import java.io.File

class SharedDrawingsViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val latestSavedDrawing: LiveData<DrawingData>? get() = null
//    val allSavedDrawings: List<Drawing>? get() = viewModelScope.launch(getAllDrawings("10.0.2.2"))

    fun saveDrawing(savedFilePath: String) {
    }

    suspend fun sendFileToServer(
        serverUrl: String,
        file: File,
        fileName: String,
        userId: String
    ): String {
        return try {
            val response: HttpResponse = client.post("$serverUrl/drawings/upload") {
                body = MultiPartFormDataContent(
                    formData {
                        append("file", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                        })
                        append("name", fileName)
                        append("user", userId)
                    }
                )
            }

            if (response.status == HttpStatusCode.OK) {
                response.readText()
            } else {
                "Failed to upload file: ${response.status}"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }


    class MyViewModel : ViewModel() {
        private val drawingsList = MutableLiveData<List<Drawing>?>()

        fun getDrawingsLiveData(): LiveData<List<Drawing>> {
            return drawingsList
        }

        // Use this function to fetch and update the list of drawings
        fun fetchDrawingsFromServer(serverUrl: String) {
            viewModelScope.launch {
                try {
                    val downloadedDrawings = getAllDrawings(serverUrl)
                    if (downloadedDrawings != null) {
                        drawingsList.value = downloadedDrawings
                    }
                } catch (e: Exception) {
                    // Handle the error, e.g., show an error message or log it
                }
            }
        }

        private suspend fun getAllDrawings(serverUrl: String): List<Drawing>? {
            return try {
                val downloadUrl = "$serverUrl/drawings"
                client.get<List<Drawing>>(downloadUrl)
            } catch (e: Exception) {
                // Handle the error and return null on failure
                Log.e("ServerFile", "Error receiving file from server: ${e.message}")
                null
            }
        }
    }

}