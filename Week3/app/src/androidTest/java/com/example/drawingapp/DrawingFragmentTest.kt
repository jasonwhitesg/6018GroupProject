package com.example.drawingapp

import android.graphics.Bitmap
import android.view.View
import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matcher
import org.junit.Assert

@RunWith(AndroidJUnit4::class)
@MediumTest
class DrawingFragmentTest {

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
    fun testButtonClickForSaveDrawing1() {
        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Drawing lines
        onView(withId(R.id.customView)).perform(swipeDown())
        onView(withId(R.id.customView)).perform(swipeRight())

        // Perform a click action on the "Save Drawing" button
        onView(withId(R.id.button4)).perform(click())

        // Check if the dialog is displayed
        onView(withText("Name Your Drawing")).check(matches(isDisplayed()))

        // Type text into the EditText in the dialog
        onView(isAssignableFrom(EditText::class.java)).perform(typeTextInEditText("DrawingTest1"))

        // Click the "Save" button in the dialog
        onView(withText("Save")).perform(click())

    }

    @Test
    fun testButtonClickForSaveDrawing2() {
        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Drawing line
        onView(withId(R.id.customView)).perform(swipeDown())

        // Perform a click action on the "Save Drawing" button
        onView(withId(R.id.button4)).perform(click())

        // Check if the dialog is displayed
        onView(withText("Name Your Drawing")).check(matches(isDisplayed()))

        // Type text into the EditText in the dialog
        onView(isAssignableFrom(EditText::class.java)).perform(typeTextInEditText("DrawingTest2"))


        // Click the "Save" button in the dialog
        onView(withText("Cancel")).perform(click())

    }

    @Test
    fun testButtonClickForLoadDrawing() {
        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Drawing line
        onView(withId(R.id.customView)).perform(swipeRight())

        // Perform a click action on the "Save Drawing" button
        onView(withId(R.id.button4)).perform(click())

        // Check if the dialog is displayed
        onView(withText("Name Your Drawing")).check(matches(isDisplayed()))

        // Type text into the EditText in the dialog
        onView(isAssignableFrom(EditText::class.java)).perform(typeTextInEditText("DrawingTest1"))

        // Click the "Save" button in the dialog
        onView(withText("Save")).perform(click())

        // Perform a click action on the "Load Drawing" button
        onView(withId(R.id.button6)).perform(click())

    }

    @Test
    fun testButtonClickForClearDrawing() {
        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Drawing lines
        onView(withId(R.id.customView)).perform(swipeDown())
        onView(withId(R.id.customView)).perform(swipeRight())

        // Perform a click action on the "Clear Drawing" button
        onView(withId(R.id.button7)).perform(click())

        // Check if the drawing canvas is cleared
        onView(withId(R.id.customView)).check { view, _ ->
            val customView = view as CustomView
            val bitmap = customView.getCurrentBitmap()

            // Assert that the bitmap is cleared (empty)
            Assert.assertTrue(bitmap.sameAs(Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)))
        }
    }

    @Test
    fun testButtonClickForPickColor() {
        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Perform a click action on the "Pick Color" button
        onView(withId(R.id.button3)).perform(click())

        // Check if specific color picker views or elements are displayed
        onView(withId(R.id.colorPickerContainer)).check(matches(isDisplayed())) // Color picker view
        onView(withId(R.id.imageView)).check(matches(isDisplayed()))   // ImageView

        // Perform a swipe action to select a new color
        onView(withId(R.id.colorPickerContainer)).perform(swipeDown())
        onView(withId(R.id.colorPickerContainer)).perform(swipeLeft())
        onView(withId(R.id.colorPickerContainer)).perform(swipeUp())
        onView(withId(R.id.colorPickerContainer)).perform(swipeRight())

    }

    @Test
    fun testForSizeSlider() {
        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Check if the size slider is displayed
        onView(withId(R.id.sizeSlider)).check(matches(isDisplayed()))

        // Check if the size slider is enabled
        onView(withId(R.id.sizeSlider)).check(matches(isEnabled()))
    }

    @Test
    fun testDrawWithPen() {
        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Drawing lines
        onView(withId(R.id.customView)).perform(swipeDown())
        onView(withId(R.id.customView)).perform(swipeRight())

    }


    private fun typeTextInEditText(text: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(EditText::class.java)
            }

            override fun getDescription(): String {
                return "Type text into EditText"
            }

            override fun perform(uiController: UiController, view: View) {
                val editText = view as EditText
                editText.setText(text)
            }
        }
    }
}

