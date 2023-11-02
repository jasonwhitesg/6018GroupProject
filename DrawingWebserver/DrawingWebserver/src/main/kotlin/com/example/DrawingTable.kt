package com.example

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

@Serializable
data class Drawing(
    val id: Int = -1,
    // val filePath: String, // Commented out if not needed
    val fileName: String,
    val userUid: String,
    val userName: String?,
    val timestamp: Long = -1,
    val imageBase64: String // Changed from ByteArray to String
)


object DrawingsTable : IntIdTable() {
    // val filePath = varchar("file_path", 255).check { it neq "" } // Commented out if not needed
    val fileName = varchar("file_name", 255).check { it neq "" }
    val userUid = varchar("user_uid", 28).check { it neq "" }
    val userName = varchar("user_name", 255).nullable()
    val timestamp = long("timestamp")
    val imageBase64 = text("image_data") // Column for Base64 image data as a string

    init {
        check {
            // filePath neq "" // Commented out if not needed
            userUid neq ""
            // Any other constraints can be added here if necessary
        }
    }
}


