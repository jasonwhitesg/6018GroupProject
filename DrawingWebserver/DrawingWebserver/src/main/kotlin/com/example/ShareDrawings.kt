package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.io.File
import java.io.IOException

class ShareDrawings {

    fun getAllDrawings(): List<Drawing> {
        return transaction {
            DrawingsTable.selectAll().map {
                Drawing(
                    id = it[DrawingsTable.id].value,
//                    filePath = it[DrawingsTable.filePath],
                    fileName = it[DrawingsTable.fileName],
                    userUid = it[DrawingsTable.userUid],
                    userName = it[DrawingsTable.userName] ?: "",
                    timestamp = it[DrawingsTable.timestamp],
                    imageBase64 = it[DrawingsTable.imageBase64]
                )
            }
        }
    }

    fun getAllDrawingsOrderedByTimestamp(descending: Boolean = true): List<Drawing> {
        return transaction {
            DrawingsTable.selectAll()
                .orderBy(DrawingsTable.timestamp, if (descending) SortOrder.DESC else SortOrder.ASC)
                .map {
                    Drawing(
                        id = it[DrawingsTable.id].value,
//                        filePath = it[DrawingsTable.filePath],
                        fileName = it[DrawingsTable.fileName],
                        userUid = it[DrawingsTable.userUid],
                        userName = it[DrawingsTable.userName] ?: "",
                        imageBase64 = it[DrawingsTable.imageBase64],
                        timestamp = it[DrawingsTable.timestamp]
                    )
                }
        }
    }


    fun getDrawingsByUserUidSince(userUid: String, time: Long): List<Drawing> {
        return transaction {
            DrawingsTable.select {
                (DrawingsTable.timestamp greaterEq time) and (DrawingsTable.userUid eq userUid)
            }
                .map { row ->
                    Drawing(
                        id = row[DrawingsTable.id].value,
//                        filePath = row[DrawingsTable.filePath],
                        fileName = row[DrawingsTable.fileName],
                        userUid = row[DrawingsTable.userUid],
                        userName = row[DrawingsTable.userName] ?: "",
                        imageBase64 = row[DrawingsTable.imageBase64], // Assuming you have this field for image bytes
                        timestamp = row[DrawingsTable.timestamp]
                    )
                }
        }
    }

    fun getDrawingById(id: Int): Drawing? {
        return transaction {
            DrawingsTable.select { DrawingsTable.id eq id }.singleOrNull()?.let { row ->
                Drawing(
                    id = row[DrawingsTable.id].value,
//                    filePath = row[DrawingsTable.filePath],
                    fileName = row[DrawingsTable.fileName],
                    userUid = row[DrawingsTable.userUid],
                    userName = row[DrawingsTable.userName] ?: "",
                    imageBase64 = row[DrawingsTable.imageBase64], // Assuming imageData is a ByteArray column
                    timestamp = row[DrawingsTable.timestamp]
                )
            }
        }
    }

    fun createDrawing(fileName: String, userUid: String, userName: String?, timestamp: Long, imageBase64: String): Drawing {
        val id = transaction {
            DrawingsTable.insertAndGetId { statement ->
                statement[DrawingsTable.fileName] = fileName
                statement[DrawingsTable.userUid] = userUid
                statement[DrawingsTable.userName] = userName
                statement[DrawingsTable.timestamp] = timestamp
                statement[DrawingsTable.imageBase64] = imageBase64
            }
        }.value
        return Drawing(id, fileName, userUid, userName.orEmpty(), timestamp, imageBase64)
    }


    fun deleteDrawingsByUserUid(userUid: String): Int {
        return transaction {
            DrawingsTable.deleteWhere { DrawingsTable.userUid eq userUid }
        }
    }


    /**
     * Saves a drawing file to the specified path.
     *
     * @param filePath the path where the file should be saved.
     * @param fileBytes the byte array representing the file content.
     * @throws IOException if an I/O error occurs.
     */
    @Throws(IOException::class)
    fun saveDrawing(filePath: String, fileBytes: ByteArray) {
        val file = File(filePath)
        file.writeBytes(fileBytes)
    }

    fun getDrawingsByUserUid(userUid: String): List<Drawing> {
        return transaction {
            DrawingsTable
                .select { DrawingsTable.userUid eq userUid }
                .map { rowToDrawing(it) }
        }
    }

    private fun rowToDrawing(row: ResultRow): Drawing {
        return Drawing(
            id = row[DrawingsTable.id].value,
//            filePath = row[DrawingsTable.filePath],
            fileName = row[DrawingsTable.fileName],
            userUid = row[DrawingsTable.userUid],
            userName = row[DrawingsTable.userName],
            timestamp = row[DrawingsTable.timestamp],
            imageBase64 = row[DrawingsTable.imageBase64] // Make sure you have this column defined properly in your table
        )
    }
}


