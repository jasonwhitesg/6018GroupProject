package com.example.drawingapp

import kotlinx.serialization.Serializable

@Serializable
data class DrawingRequest(
    val filePath: String,
    val userUid: String,
    val userName: String
)