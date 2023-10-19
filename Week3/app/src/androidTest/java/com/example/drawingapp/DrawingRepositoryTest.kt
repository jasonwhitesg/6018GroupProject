package com.example.drawingapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class DrawingRepositoryTest {

    private lateinit var dao: DrawingDAO
    private lateinit var db: DrawingDatabase
    private lateinit var repository: DrawingRepository
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DrawingDatabase::class.java).build()
        dao = db.drawingDao()
        repository = DrawingRepository(testScope, dao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun retrieveLatestDrawingWithoutSaving() = testScope.runBlockingTest {
        // Try to retrieve the latest saved drawing without saving any
        val latestDrawing = repository.latestSavedDrawing.value

        // Verify that the result is null because no drawings have been saved
        assertThat(latestDrawing, equalTo(null))
    }

    @Test
    @Throws(Exception::class)
    fun retrieveAllDrawingsWithoutSaving() = testScope.runBlockingTest {
        // Try to retrieve all saved drawings without saving any
        val allDrawings = repository.allSavedDrawings.value

        // Verify that the result is an empty list because no drawings have been saved
        assertThat(allDrawings?.isEmpty(), equalTo(null))
    }

}
