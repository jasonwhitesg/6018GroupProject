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
    fun getAllDrawings() {
        return transaction {
            DrawingsTable.selectAll().map {
                Drawing(
                    id = it[DrawingsTable.id].value,
                    filePath = it[DrawingsTable.filePath],
                    fileName = it[DrawingsTable.fileName],
                    userUid = it[DrawingsTable.userUid],
                    userName = it[DrawingsTable.userName] ?: "",
                    timestamp = it[DrawingsTable.timestamp]
                )
            }
        }
    }

    fun deleteDrawingByUserUid(userUid: String): Boolean {
        return transaction {
            // Delete the record(s) where the userUid matches the provided userUid
            val deletedCount = DrawingsTable.deleteWhere { DrawingsTable.userUid eq userUid }

            // Return true if at least one record was deleted, false otherwise
            deletedCount > 0
        }
    }


    fun getAllDrawingsOrderedByTimestamp(descending: Boolean = true): List<Drawing> {
        return transaction {
            DrawingsTable.selectAll()
                .orderBy(DrawingsTable.timestamp, if (descending) SortOrder.DESC else SortOrder.ASC)
                .map {
                    Drawing(
                        filePath = it[DrawingsTable.filePath],
                        fileName = it[DrawingsTable.filePath],
                        userUid = it[DrawingsTable.userUid],
                        userName = it[DrawingsTable.userName] ?: "", // or handle null userName differently
                        timestamp = it[DrawingsTable.timestamp]
                    )
                }
        }
    }

    fun getDrawingsSince(time: Long): List<Drawing> {
        return transaction {
            DrawingsTable.select { DrawingsTable.timestamp greaterEq time }
                .map {
                    Drawing(
                        id = it[DrawingsTable.id].value,
                        filePath = it[DrawingsTable.filePath],
                        fileName = it[DrawingsTable.filePath],
                        userUid = it[DrawingsTable.userUid],
                        userName = it[DrawingsTable.userName] ?: "",
                        timestamp = it[DrawingsTable.timestamp]
                    )
                }
        }
    }

    fun getDrawingById(id: Int): Drawing? {
        return transaction {
            DrawingsTable.select { DrawingsTable.id eq id }.singleOrNull()?.let {
                Drawing(
                    id = it[DrawingsTable.id].value,
                    filePath = it[DrawingsTable.filePath],
                    fileName = it[DrawingsTable.filePath],
                    userUid = it[DrawingsTable.userUid],
                    userName = it[DrawingsTable.userName] ?: "",
                    timestamp = it[DrawingsTable.timestamp]
                )
            }
        }
    }

    fun createDrawing(filePath: String, fileName: String, userUid: String, userName: String, timestamp: Long): Drawing {
        val id = transaction {
            DrawingsTable.insertAndGetId {
                it[DrawingsTable.filePath] = filePath
                it[DrawingsTable.fileName] = fileName
                it[DrawingsTable.userUid] = userUid
                it[DrawingsTable.userName] = userName
                it[DrawingsTable.timestamp] = timestamp
            }
        }.value
        return Drawing(id, filePath, fileName, userUid, userName, timestamp)
    }

    fun deleteDrawing(userUid: String, fileName: String): Boolean {
        return transaction {
            DrawingsTable.deleteWhere {
                (DrawingsTable.userUid eq userUid) and (DrawingsTable.fileName eq fileName)
            } > 0
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

    fun getDrawingByUidAndFileName(userUid: String, fileName: String): Drawing? {
        return transaction {
            DrawingsTable
                .select { (DrawingsTable.userUid eq userUid) and (DrawingsTable.fileName eq fileName) }
                .mapNotNull { rowToDrawing(it) }
                .singleOrNull()
        }
    }

    private fun rowToDrawing(row: ResultRow): Drawing {
        return Drawing(
            id = row[DrawingsTable.id].value,
            filePath = row[DrawingsTable.filePath],
            fileName = row[DrawingsTable.fileName],
            userUid = row[DrawingsTable.userUid],
            userName = row[DrawingsTable.userName],
            timestamp = row[DrawingsTable.timestamp]
        )
    }

}


