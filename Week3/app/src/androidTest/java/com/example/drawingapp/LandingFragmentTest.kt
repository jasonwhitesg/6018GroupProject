package com.example.drawingapp

import androidx.test.espresso.Espresso.onView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.drawingapp.R
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
@MediumTest
class LandingFragmentTest {

    @Test
    fun testNavigationToLetsDraw() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java)

        // Perform a click action on the button
        onView(withId(R.id.button)).perform(click())

        // Check that the LetsDraw fragment is displayed
        onView(withId(R.id.fragmentContainerView))
            .check(matches(hasDescendant(withId(R.id.customView))))
    }
}

