package com.example.plugins

import com.example.DrawingsTable
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

import java.nio.file.Paths

fun getProjectPath(): String? {
    val currentWorkingDirectory = System.getProperty("user.dir")
    return Paths.get(currentWorkingDirectory).toAbsolutePath().toString()
}

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
                        fileName = drawingRequest.fileName,
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

            application.log.info("About to UPLOAD")

            post("/upload") {
                application.log.info("Received a request to /drawings/upload")
                // Initialize the multipart data reception
                val multipart = call.receiveMultipart()

                // Initialize a variable to hold the file bytes
                var fileBytes: ByteArray? = null
                var fileName: String? = null
                var userUid: String? = null
                var userName: String? = null

                // Iterate through each part of the multipart data
                multipart.forEachPart { part ->
                    when (part) {
                        // If the part is a FileItem (i.e., file upload), read its bytes
                        is PartData.FileItem -> {
                            try {
                                fileBytes = part.streamProvider().readBytes()
                                application.log.info("File successfully read into bytes")
                                fileName = part.originalFileName
                            } catch (e: Exception) {
                                application.log.error("Error reading file bytes", e)
                            }
                        }

                        is PartData.FormItem -> {
                            // Handle form fields
                            when (part.name) {
                                "userUid" -> {
                                    userUid = part.value
                                    application.log.info("Received userUid: $userUid")
                                }
//                                "userName" -> {
//                                    userName = part.value
//                                }
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

                // Check if the file bytes were successfully read and required information is available
                if (fileBytes != null && fileName != null && userUid != null) {
                    val folderPath = getProjectPath() + "/savedPNG"
                    application.log.info("PATH=$folderPath")

                    // Generating a unique file name and defining the file path
                    val filePath = "$folderPath/$fileName"

                    try {
                        // Saving the Drawing to Disk
                        shareDrawings.saveDrawing(filePath, fileBytes!!)

                        // Saving the Drawing Information to Database
                        val drawing = shareDrawings.createDrawing(
                            filePath = filePath,
                            fileName = fileName!!,
                            userUid = userUid!!,
                            userName = userName ?: "Unknown", // Provide a default value or handle null case as needed
                            timestamp = System.currentTimeMillis(),
                        )
                        application.log.info("Drawing information saved to database with ID: ${drawing.id}")
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "File uploaded successfully")
                        )
                    } catch (e: Exception) {
                        application.log.error("Error saving drawing", e)
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error saving drawing"))
                    }
                } else {
                    application.log.warn("Invalid file data received in upload request")
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid file data"))
                }
            }

//<<<<<<< HEAD
            // Sending File to the Android App
            get("/download/{fileName}") {
                val fileName = call.parameters["fileName"]
                if (fileName != null) {
                    val folderPath = getProjectPath() + "/savedPNG"
                    val filePath = "$folderPath/$fileName"

                    val file = File(filePath)

                    if (file.exists()) {
                        call.respondFile(file)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "File not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid file name")
                }
            }

//=======
            get("/drawing") {
                val userUid = call.request.queryParameters["userUid"]
                val fileName = call.request.queryParameters["fileName"]

                if (userUid != null && fileName != null) {
                    val drawing = shareDrawings.getDrawingByUidAndFileName(userUid, fileName)
                    if (drawing != null) {
                        call.respond(drawing)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Drawing not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing or invalid parameters")
                }
            }
//>>>>>>> jason
        }
    }
}

@Serializable
data class DrawingRequest(
    val filePath: String,
    val fileName: String,
    val userUid: String,
    val userName: String
)
