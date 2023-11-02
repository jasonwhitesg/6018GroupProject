package com.example.drawingapp

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SharedDrawingsViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val drawingsList = MutableLiveData<List<Drawing>>()
    private val url = "http://10.0.2.2:8080"

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



        // Expose the LiveData to observe in your UI components
        fun getDrawingsLiveData(): LiveData<List<Drawing>> {
            return drawingsList
        }

        // Update the LiveData with a new List<Drawing>
        fun setDrawings(drawings: List<Drawing>) {
            drawingsList.value = drawings
        }

    // Use this function to fetch and update the list of drawings
    suspend fun updateSharedDrawingsList() {
        try {
            val downloadUrl = "$url/drawings"
            setDrawings(client.get(downloadUrl))
        } catch (e: Exception) {
            // Handle the error and return null on failure
            Log.e("ServerFile", "Error receiving file from server: ${e.message}")
            null
        }
    }

}