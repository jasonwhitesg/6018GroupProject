package com.example.drawingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "Before setting the theme.")
        setTheme(R.style.Theme_Phase1)
        Log.d("MainActivity", "After setting the theme.")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.Main) {
            val serverUrl = "http://10.0.2.2:8080"
            val drawingRequest = DrawingRequest(
                filePath = "path/to/file",
                userUid = "userUid",
                userName = "userName"
            )
            val response = sendDrawingRequestToServer(serverUrl, drawingRequest)
            Log.d("ServerResponse", response)
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

    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }
}

