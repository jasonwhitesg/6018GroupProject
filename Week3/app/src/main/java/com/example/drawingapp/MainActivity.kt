package com.example.drawingapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
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
import kotlinx.serialization.json.Json
import java.io.FileOutputStream
import java.io.IOException
import java.util.Base64
import kotlinx.serialization.encodeToString


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

        coroutineScope.launch(Dispatchers.Main) {
            val serverUrl = "http://10.0.2.2:8080"

            // Perform multiple POST requests
            val drawingRequest1 = DrawingRequest("path/to/file1", "userUid1", "userName1", "Testing BASE64")
            sendDrawingRequestToServer(serverUrl, drawingRequest1)

            val drawingRequest2 = DrawingRequest("path/to/file2", "userUid2", "userName2", "Testing BASE64")
            sendDrawingRequestToServer(serverUrl, drawingRequest2)

            // Fetch and log all drawings
            val allDrawings = fetchAllDrawings(serverUrl)
            Log.d("ServerResponse", "All Drawings: $allDrawings")

            // Fetch and log drawing by ID (assuming ID is 1 for example)
            val drawingById = fetchDrawingById(serverUrl, 1)
            Log.d("ServerResponse", "Drawing by ID: $drawingById")

            val root = applicationContext.filesDir.path
            val filePath = "$root/jason.png"

            val userName = "userName"
            val userUid = "userUid"
            val fileName = "filename"
            // Assume you have a File instance
            val file = File(filePath)

            // Send the file to the server
            val response = sendFileToServer(serverUrl, file,  userUid, userName, fileName)
            Log.d("ServerResponse", "Response: $response")
        }
    }

    private suspend fun sendFileToServer(serverUrl: String, file: File, userUid: String, userName: String, fileName: String): String {
        // Read file bytes and encode to Base64
        val base64Encoded = Base64.getEncoder().encodeToString(fileBytes)

// Check if the base64Encoded string ends with the padding character
// and if not, add padding manually.
        val paddingNeeded = 4 - (base64Encoded.length % 4)
        val correctlyPadded = base64Encoded + "=".repeat(paddingNeeded % 4)

// Now correctlyPadded is guaranteed to be correctly padded.
// Note: This should not be necessary when using the getEncoder().encodeToString() method from Java's Base64 class.


        // Create a DrawingRequest object with user details and image
        val drawingRequest = DrawingRequest(
            fileName = fileName,
            userUid = userUid,
            userName = userName,
            imageBase64 = base64Encoded
        )

        // Serialize DrawingRequest to JSON
        val jsonBody = Json.encodeToString(drawingRequest)

        // Log the JSON payload being sent
        println("JSON payload being sent: $jsonBody")

        // Send POST request with JSON body
        return try {
            val response: HttpResponse = client.post("$serverUrl/drawings/upload") {
                contentType(ContentType.Application.Json)
                body = jsonBody
            }

            // Handle the response
            if (response.status == HttpStatusCode.OK) {
                response.readText()
            } else {
                "Failed to upload file: ${response.status.description}"
            }
        } catch (e: Exception) {
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

    fun saveByteArrayToPNGFile(byteArray: ByteArray, filename: String, context: Context): String? {
        return try {
            // Create a file in the internal storage
            val file = File(context.filesDir, filename)

            // Write the byte array to the file
            FileOutputStream(file).use { it.write(byteArray) }

            // Return the absolute path of the saved file
            file.absolutePath
        } catch (e: IOException) {
            // Log the exception if there's an error
            Log.e("FileSaveError", "Error saving byte array to PNG file", e)
            null
        }
    }

    private suspend fun fetchDrawingFromServer(serverUrl: String, userUid: String, fileName: String): ByteArray? {
        return try {
            client.get("$serverUrl/drawing") {
                url {
                    parameters.append("userUid", userUid)
                    parameters.append("fileName", fileName)
                }
            }
        } catch (e: Exception) {
            Log.e("ServerResponse", "Error fetching drawing: ${e.message}")
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        client.close()
    }
}
