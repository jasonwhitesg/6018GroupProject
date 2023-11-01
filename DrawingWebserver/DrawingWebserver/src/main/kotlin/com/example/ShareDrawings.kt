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

class ShareDrawings {

    fun getAllDrawings(): List<Drawing> {
        return transaction {
            DrawingsTable.selectAll().map {
                Drawing(
                    id = it[DrawingsTable.id].value,
                    filePath = it[DrawingsTable.filePath],
                    userUid = it[DrawingsTable.userUid],
                    userName = it[DrawingsTable.userName] ?: "",
                    timestamp = it[DrawingsTable.timestamp]
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
                        filePath = it[DrawingsTable.filePath],
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
                    userUid = it[DrawingsTable.userUid],
                    userName = it[DrawingsTable.userName] ?: "",
                    timestamp = it[DrawingsTable.timestamp]
                )
            }
        }
    }

    fun createDrawing(filePath: String, userUid: String, userName: String, timestamp: Long): Drawing {
        val id = transaction {
            DrawingsTable.insertAndGetId {
                it[DrawingsTable.filePath] = filePath
                it[DrawingsTable.userUid] = userUid
                it[DrawingsTable.userName] = userName
                it[DrawingsTable.timestamp] = timestamp
            }
        }.value
        return Drawing(id, filePath, userUid, userName, timestamp)
    }

    fun deleteDrawing(id: Int): Boolean {
        return transaction {
            DrawingsTable.deleteWhere { DrawingsTable.id eq id } > 0
        }
    }
}

