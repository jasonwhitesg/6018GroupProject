package com.example

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Drawing(
    val id: Int = -1,
    val filePath: String,
    val fileName: String,
    val userUid: String,
    val userName: String?,
    val timestamp: Long = -1
)


object DrawingsTable : IntIdTable() {
    val filePath = varchar("filePath", 255).check { it neq "" }
    val fileName = varchar("fileName", 255).check { it neq "" }
    val userUid = varchar("userUid", 28).check { it neq "" }
    val userName = varchar("userName", 255).nullable()
    val timestamp = long("timestamp")

    init { // Making filePath, so they can't be null and timestamp can't be zero
        check {
            filePath neq ""
            userUid neq ""
        }
    }
}
