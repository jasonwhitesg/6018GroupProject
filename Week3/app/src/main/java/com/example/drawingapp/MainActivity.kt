package com.example.drawingapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.drawingapp.R
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.HttpHeaders.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "Before setting the theme.")
        setTheme(R.style.Theme_Phase1)
        Log.d("MainActivity", "After setting the theme.")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.Main) {
            val serverUrl = "http://10.0.2.2:8080" // Replace with your server's address and port
            val testData = DrawingRequest("image.png", "1234", "Ricardo")

            val response = sendStringToServer(serverUrl, testData)
            Log.d("ServerResponse", response)
        }
    }


    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    private suspend fun sendStringToServer(serverUrl: String, data: DrawingRequest): String {
        val json = Gson().toJson(data)
        Log.d("Sending JSON", json)
        return try {
            client.post<String>("$serverUrl/drawings") {
                body = json
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
