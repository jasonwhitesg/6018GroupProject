package com.example.drawingapp

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.Serializable

val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}



@Serializable
data class DrawingRequest(
    val filePath: String,
    val userUid: String,
    val userName: String
)

@Serializable
data class Drawing(
    val id: Int,
    val filePath: String,
    val userUid: String,
    val userName: String,
    val timestamp: Long
)