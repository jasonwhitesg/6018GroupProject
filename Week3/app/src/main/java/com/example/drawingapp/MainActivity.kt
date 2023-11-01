package com.example.drawingapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.drawingapp.R
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
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
            val testData = "Hello, Server!"

            val response = sendStringToServer(serverUrl, testData)
            Log.d("ServerResponse", response)
        }
    }


    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    private suspend fun sendStringToServer(serverUrl: String, data: String): String {
        return try {
            client.post<String>("$serverUrl/drawings") {
                body = data
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
