package com.example.drawingapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.core.app.ActivityScenario

@LargeTest
@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @Test
    fun splashScreenTest() {
        // Launch the activity
        val activityScenario = ActivityScenario.launch(SplashActivity::class.java)

        // Check if the splash screen's logo is displayed.
        onView(withId(R.id.logo)).check(matches(isDisplayed()))

        // Giving things time to get set up
        Thread.sleep(2200)

        // Check if the lets draw button is displayed in the LandingFragment.
        onView(withId(R.id.button))
            .check(matches(isDisplayed()))
            .check(matches(withText("LETS DRAW")))

        // Close the activity
        activityScenario.close()
    }
}



