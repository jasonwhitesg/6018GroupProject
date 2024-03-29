package com.example.drawingapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.File

class SharedDrawingsViewModel : ViewModel() {

    private val drawingsList = MutableLiveData<List<Drawing>>()
    private val url = "http://10.0.2.2:8080"

    suspend fun sendFileToServer(
        file: File,
        userUid: String
    ): String {

        return try {

            val response: HttpResponse = client.post("$url/drawings/upload") {
                body = MultiPartFormDataContent(
                    formData {
                        append("file", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                        })
//                        append("name", fileName)
                        append("userUid", userUid,)
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

    suspend fun requestDrawing(fileName: String): ByteArray? {
        return try {
            return client.get<ByteArray>("$url/drawings/download/$fileName")
        } catch (e: Exception) {
            Log.e("ServerFile", "Error receiving file from server: ${e.message}")
            null // Return null on failure
        }
    }

    // Expose the LiveData to observe in your UI components
    fun getDrawingsLiveData(): LiveData<List<Drawing>> {
        return drawingsList
    }


    // Update the LiveData with a new List<Drawing>
    private fun setDrawingsLiveData(drawings: List<Drawing>) {
        drawingsList.value = drawings
    }

    // Use this function to fetch and update the list of drawings
    suspend fun updateSharedDrawingsList() {
        try {
            val downloadUrl = "$url/drawings"

            val resultList: List<Drawing>? = client.get(downloadUrl)
            if (resultList != null) {
                for(drawing: Drawing in resultList){
                    Log.d("DrawingFoundOnServer",drawing.toString())
                }
            }
            setDrawingsLiveData(client.get(downloadUrl))

        } catch (e: Exception) {
            // Handle the error and return null on failure
            Log.e("ServerFile", "Error receiving file from server: ${e.message}")
        }
    }


}
class SharedDrawingViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedDrawingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedDrawingsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}