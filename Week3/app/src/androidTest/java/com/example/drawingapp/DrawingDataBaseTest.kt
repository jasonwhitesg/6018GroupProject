package com.example.drawingapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class DrawingDataBaseTest {
    private lateinit var dao: DrawingDAO
    private lateinit var db: DrawingDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DrawingDatabase::class.java).build()
        dao = db.drawingDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveDrawing() = runBlocking {
        // Create a test DrawingData object
        val timestamp = Date()
        val savedFile = "test_drawing.png"
        val drawingData = DrawingData(timestamp = timestamp, savedFile = savedFile)

        // Insert the drawing into the database
        dao.addSavedFile(drawingData)

        // Retrieve the latest saved drawing from the database
        val latestDrawing = dao.latestDrawing().firstOrNull()

        // Verify that the retrieved drawing has the same timestamp and savedFile
        assertThat(latestDrawing?.timestamp, equalTo(timestamp))
        assertThat(latestDrawing?.savedFile, equalTo(savedFile))
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveMultipleDrawings() = runBlocking {
        // Create multiple test DrawingData objects
        val drawing1 = DrawingData(timestamp = Date(), savedFile = "drawing1.png")
        val drawing2 = DrawingData(timestamp = Date(), savedFile = "drawing2.png")
        val drawing3 = DrawingData(timestamp = Date(), savedFile = "drawing3.png")

        // Insert the drawings into the database
        dao.addSavedFile(drawing1)
        dao.addSavedFile(drawing2)
        dao.addSavedFile(drawing3)

        // Retrieve all saved drawings from the database
        val allDrawings = dao.allDrawings().firstOrNull()

        // Verify that the number of retrieved drawings matches the number of inserted drawings
        assertThat(allDrawings?.size, equalTo(3))
    }

    @Test
    @Throws(Exception::class)
    fun retrieveLatestDrawingWithoutInserting() = runBlocking {
        // Try to retrieve the latest saved drawing without inserting any
        val latestDrawing = dao.latestDrawing().firstOrNull()

        // Verify that the result is null because no drawings have been inserted
        assertThat(latestDrawing, equalTo(null))
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveSomeDrawings() = runBlocking {
        // Create 10 test DrawingData objects
        val drawings = mutableListOf<DrawingData>()
        for (i in 1..10) {
            drawings.add(DrawingData(timestamp = Date(), savedFile = "drawing$i.png"))
        }

        // Insert the drawings into the database
        drawings.forEach { dao.addSavedFile(it) }

        // Retrieve all saved drawings from the database
        val allDrawings = dao.allDrawings().firstOrNull()

        // Verify that the number of retrieved drawings matches the number of inserted drawings (10)
        assertThat(allDrawings?.size, equalTo(10))
    }

    @Test
    @Throws(Exception::class)
    fun retrieveAllDrawingsWithoutInserting() = runBlocking {
        // Try to retrieve all saved drawings without inserting any
        val allDrawings = dao.allDrawings().firstOrNull()

        // Verify that the result is an empty list because no drawings have been inserted
        assertThat(allDrawings?.isEmpty(), equalTo(true))
    }

    @Test
    @Throws(Exception::class)
    fun retrieveAllDrawings() = runBlocking {
        // Create multiple test DrawingData objects
        val drawing1 = DrawingData(timestamp = Date(), savedFile = "drawing1.png")
        val drawing2 = DrawingData(timestamp = Date(), savedFile = "drawing2.png")
        val drawing3 = DrawingData(timestamp = Date(), savedFile = "drawing3.png")

        // Insert the drawings into the database
        dao.addSavedFile(drawing1)
        dao.addSavedFile(drawing2)
        dao.addSavedFile(drawing3)

        // Retrieve all saved drawings from the database
        val allDrawings = dao.allDrawings().firstOrNull()

        // Verify that the number of retrieved drawings matches the number of inserted drawings
        assertThat(allDrawings?.size, equalTo(3))
    }

}
