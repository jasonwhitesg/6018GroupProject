package com.example.drawingapp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class KtorStuff {


    suspend fun main() {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get("https://ktor.io/")
    }
}