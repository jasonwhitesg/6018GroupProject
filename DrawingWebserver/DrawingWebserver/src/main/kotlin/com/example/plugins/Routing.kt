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
import java.io.File
import io.ktor.http.content.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*


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
                val drawingRequest = call.receive<DrawingRequest>()
                application.log.info("Received drawingRequest: $drawingRequest")
                try {
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


            post("/upload") {
                // Initialize the multipart data reception
                val multipart = call.receiveMultipart()

                // Initialize a variable to hold the file bytes
                var fileBytes: ByteArray? = null

                // Iterate through each part of the multipart data This method is used by KTOR often
                multipart.forEachPart { part ->
                    when (part) {
                        // If the part is a FileItem (i.e., file upload), read its bytes
                        is PartData.FileItem -> {
                            try {
                                fileBytes = part.streamProvider().readBytes()
                                application.log.info("File successfully read into bytes")
                            } catch (e: Exception) {
                                application.log.error("Error reading file bytes", e)
                            }
                        }
                        else -> {
                            // Received an unsupported type of the multipart
                            application.log.info("Received an unsupported part type in multipart data: ${part::class}")
                        }
                    }
                    // Dispose of the part to free up resources
                    part.dispose()
                }

                // Check if the file bytes were successfully read
                val nonNullFileBytes = fileBytes
                if (nonNullFileBytes != null) {
                    // The folder path
                    val folderPath = "/Users/jasonwhite/Desktop/6018GroupProject/DrawingWebserver/DrawingWebserver/savedPNG"

                    // Check if folder exists or create it if it doesn't
                    val folder = File(folderPath)
                    if (!folder.exists() && !folder.mkdirs()) {
                        application.log.error("Failed to create directory: $folderPath")
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Server error: Could not create directory for file upload"))
                        return@post
                    }

                    // Generate a unique file name and define the file path help with overwriting
                    val fileName = UUID.randomUUID().toString()
                    val filePath = "$folderPath/$fileName.png"

                    // Saving the Drawing
                    try {
                        shareDrawings.saveDrawing(filePath, nonNullFileBytes)
                        application.log.info("File uploaded and saved successfully at $filePath")
                        call.respond(HttpStatusCode.OK, mapOf("message" to "File uploaded successfully"))
                    } catch (e: Exception) {
                        application.log.error("Error saving drawing at $filePath", e)
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error saving drawing"))
                    }
                } else {
                    application.log.warn("Invalid file data received in upload request")
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid file data"))
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
