package com.example.plugins

import com.example.ShareDrawings
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

fun Application.configureRouting() {
    install(Resources)

    val shareDrawings = ShareDrawings()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        route("/drawings") {
            get {
                call.respond(shareDrawings.getAllDrawings())
            }

            post {
                application.log.info("Received a POST request to /drawings")
//                val drawingRequest = call.receive<DrawingRequest>()
//                application.log.info("Received drawingRequest: $drawingRequest")
                try {
                    val post = call.receive<String>()
                    val drawingRequest = Json.decodeFromString<DrawingRequest>(post)
                    application.log.info("Received drawingRequest: $drawingRequest")
                    val drawing = shareDrawings.createDrawing(
                        filePath = drawingRequest.filePath,
                        userUid = drawingRequest.userUid,
                        userName = drawingRequest.userName,
                        timestamp = System.currentTimeMillis()
                    )
                    application.log.info("Drawing created: $drawing")
                    call.respond(HttpStatusCode.Created, drawing)
                } catch (e: Exception) {
                    application.log.error("Error processing POST request", e)
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val drawing = shareDrawings.getDrawingById(id)
                    if (drawing != null) {
                        call.respond(drawing)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                }
            }

            delete("/{id}") {
                application.log.info("Handling DELETE request for drawing")
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    if (shareDrawings.deleteDrawing(id)) {
                        call.respond(HttpStatusCode.OK, "Drawing deleted successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Drawing not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                }
            }

            get("/ordered") {
                val order = call.request.queryParameters["order"] ?: "desc"
                val drawings = shareDrawings.getAllDrawingsOrderedByTimestamp(order.toLowerCase() != "asc")
                call.respond(drawings)
            }

            get("/since/{time}") {
                val time = call.parameters["time"]?.toLongOrNull()
                if (time != null) {
                    val drawings = shareDrawings.getDrawingsSince(time)
                    call.respond(drawings)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid time format")
                }
            }
        }
    }
}

@Serializable
data class DrawingRequest(
    val filePath: String,
    val userUid: String,
    val userName: String
)
