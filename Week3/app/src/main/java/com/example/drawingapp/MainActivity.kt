package com.example.drawingapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File
import io.ktor.client.statement.*


class MainActivity : AppCompatActivity() {

    private val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
    private val coroutineScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        coroutineScope.launch(Dispatchers.Main) {
//            val serverUrl = "http://10.0.2.2:8080"
//
//            // Perform multiple POST requests
//            val drawingRequest1 = DrawingRequest("path/to/file1", "userUid1", "userName1")
//            sendDrawingRequestToServer(serverUrl, drawingRequest1)
//
//            val drawingRequest2 = DrawingRequest("path/to/file2", "userUid2", "userName2")
//            sendDrawingRequestToServer(serverUrl, drawingRequest2)
//
//            // Fetch and log all drawings
//            val allDrawings = fetchAllDrawings(serverUrl)
//            Log.d("ServerResponse", "All Drawings: $allDrawings")
//
//            // Fetch and log drawing by ID (assuming ID is 1 for example)
//            val drawingById = fetchDrawingById(serverUrl, 1)
//            Log.d("ServerResponse", "Drawing by ID: $drawingById")
//            val root = applicationContext.filesDir.path
//            val filePath = "$root/jason.png"
//            // Assume you have a File instance
//            val file = File(filePath)
//
//            // Send the file to the server
//            val response = sendFileToServer(serverUrl, file)
//            Log.d("ServerResponse", "Response: $response")
//
//            // Received file from Server
//            val downloadedFile = receiveFileFromServer(serverUrl, "fromServer.png")
//        }
    }

    private suspend fun sendFileToServer(serverUrl: String, file: File): String {
        return try {
            val response: HttpResponse = client.post("$serverUrl/drawings/upload") {
                body = MultiPartFormDataContent(
                    formData {
                        append("file", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                            append(HttpHeaders.ContentType, "application/octet-stream") // or the correct content type
                        })
                        // append other fields if needed
                    }
                )
            }

            val status = response.status
            Log.d("FileUpload", "Response status: $status")
            if (status == HttpStatusCode.OK) {
                response.readText()
            } else {
                "Failed to upload file: $status"
            }
        } catch (e: Exception) {
            Log.e("FileUpload", "Exception during file upload", e)
            "Error: ${e.localizedMessage}"
        }
    }


    private suspend fun fetchAllDrawings(serverUrl: String): List<Drawing>? {
        return try {
            client.get("$serverUrl/drawings")
        } catch (e: Exception) {
            Log.e("ServerResponse", "Error fetching all drawings: ${e.message}")
            null
        }
    }

    private suspend fun fetchDrawingById(serverUrl: String, id: Int): Drawing? {
        return try {
            client.get("$serverUrl/drawings/$id")
        } catch (e: Exception) {
            Log.e("ServerResponse", "Error fetching drawing by ID: ${e.message}")
            null
        }
    }

    private suspend fun sendDrawingRequestToServer(
        serverUrl: String,
        drawingRequest: DrawingRequest
    ): String {
        return try {
            client.post<String>("$serverUrl/drawings") {
                contentType(ContentType.Application.Json)
                body = drawingRequest
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private suspend fun receiveFileFromServer(serverUrl: String, fileName: String): ByteArray? {
        return try {
            val downloadUrl = "$serverUrl/drawings/download/$fileName"
            val responseBytes = client.get<ByteArray>(downloadUrl)

            return responseBytes

        } catch (e: Exception) {
            Log.e("ServerFile", "Error receiving file from server: ${e.message}")
            null // Return null on failure
        }
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        coroutineScope.cancel()
//        client.close()
//    }
}
