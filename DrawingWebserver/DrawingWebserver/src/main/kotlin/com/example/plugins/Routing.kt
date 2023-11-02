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
//                        filePath = drawingRequest.filePath,
                        fileName = drawingRequest.fileName,
                        userUid = drawingRequest.userUid,
                        userName = drawingRequest.userName,
                        imageBase64 = drawingRequest.imageBase64,
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

            delete("/{userUid}") {
                application.log.info("Handling DELETE request for drawings by userUid")
                val userUid = call.parameters["userUid"]
                if (!userUid.isNullOrBlank()) { // Check that it's not null or blank
                    val deletedCount = shareDrawings.deleteDrawingsByUserUid(userUid)
                    if (deletedCount > 0) { // Check if any rows were deleted
                        call.respond(HttpStatusCode.OK, "Drawings deleted successfully for userUid: $userUid")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "No drawings found for the specified userUid")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid userUid format or missing userUid")
                }
            }



            get("/ordered") {
                val order = call.request.queryParameters["order"] ?: "desc"
                val drawings = shareDrawings.getAllDrawingsOrderedByTimestamp(order.toLowerCase() != "asc")
                call.respond(drawings)
            }

            application.log.info("About to UPLOAD")

            post("/upload") {
                application.log.info("Received a request to /drawings/upload")

                try {

//                    // Read the raw request body text
//                    application.log.info("BEFORE TEXT")
//                    val requestBodyText = call.receiveText()
//                    application.log.info("After TEXT")
//
//                    // Log the raw request body text
//                    application.log.info("Raw request body: $requestBodyText")
//                    // Receive the JSON payload into an object of DrawingRequest
                    application.log.info("BEFORE TEXT")
                    val drawingRequest = call.receive<DrawingRequest>()
                    application.log.info("After TEXT")

                    // Generating a unique file name could be a good practice to prevent overwriting
                    val timestamp = System.currentTimeMillis()

                    // Saving the Drawing to Disk (implement the saveDrawing method as needed)
                    // Create the Drawing record in the database
                    val drawing = shareDrawings.createDrawing(
                        fileName = drawingRequest.fileName,
                        userUid = drawingRequest.userUid,
                        userName = drawingRequest.userName,
                        timestamp = timestamp,
                        imageBase64 = drawingRequest.imageBase64 // Passing the base64 string directly
                    )

                    application.log.info("Drawing information saved to database with ID: ${drawing.id}")
                    call.respond(HttpStatusCode.OK, mapOf("message" to "File uploaded successfully", "drawingId" to drawing.id))
                } catch (e: Exception) {
                    application.log.error("Error processing upload", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error processing upload"))
                }
            }

            get("/drawing/{userUid}") {
                val userUid = call.parameters["userUid"]

                if (userUid != null) {
                    val drawings = shareDrawings.getDrawingsByUserUid(userUid)
                    if (drawings.isNotEmpty()) {
                        call.respond(drawings)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "No drawings found for userUid: $userUid")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing userUid parameter")
                }
            }
        }
    }
}

@Serializable
data class DrawingRequest(
    val fileName: String,
    val userUid: String,
    val userName: String,
    val imageBase64: String
)

