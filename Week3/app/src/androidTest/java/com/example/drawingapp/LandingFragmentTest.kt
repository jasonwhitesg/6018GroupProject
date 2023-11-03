package com.example.drawingapp

import androidx.test.espresso.Espresso.onView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LandingFragmentTest {

    private var activityScenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setUp() {
        // Launch the main activity
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        // Close the activity after each test
        activityScenario?.close()
    }

    @Test
    fun testNavigationToLetsDraw() {
        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Check if the DrawingFragment is displayed
        onView(withId(R.id.drawingFragment)).check(matches(isDisplayed()))

        // Check if specific views or buttons in DrawingFragment are displayed
        onView(withId(R.id.colorPickerButton)).check(matches(isDisplayed())) // Color picker button
        onView(withId(R.id.saveButton)).check(matches(isDisplayed())) // Save drawing button
        onView(withId(R.id.loadButton)).check(matches(isDisplayed())) // Load Drawing button
        onView(withId(R.id.clearButton)).check(matches(isDisplayed())) // Clear Drawing button
        onView(withId(R.id.sizeSlider)).check(matches(isDisplayed())) // Brush Size Slider
    }

//    @Test
//    fun testNavigationToLoadDrawing() {
//        // Perform a click action on the "Load Drawing" button
//        onView(withId(R.id.button2)).perform(click())
//
//        // Check if the LoadDrawingFragment is displayed
//        //onView(withId(R.id.LoadDrawingFragment)).check(matches(isDisplayed()))
//    }

}

