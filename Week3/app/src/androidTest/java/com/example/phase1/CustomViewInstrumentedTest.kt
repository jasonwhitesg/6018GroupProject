package com.example.phase1

import android.graphics.Bitmap
import android.os.Handler
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule



@RunWith(AndroidJUnit4::class)
@LargeTest
class CustomViewInstrumentedTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @Test
    fun testDrawing() {
        val scenario: FragmentScenario<DrawingFragment> = launchFragmentInContainer()

        val idlingResource = ElapsedTimeIdlingResource(3000)
        IdlingRegistry.getInstance().register(idlingResource)

        onView(withId(R.id.customView)).perform(swipeRight())

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testOnTouchEvent() {
        val scenario: FragmentScenario<DrawingFragment> = launchFragmentInContainer()

        scenario.onFragment { fragment ->
            fragment.activity?.runOnUiThread {
                onView(withId(R.id.customView)).perform(swipeRight())

                onView(withId(R.id.customView)).check { view, _ ->
                    val customViewInViewCheck = view as CustomView
                    val beforeBitmap = customViewInViewCheck.getCurrentBitmap().copy(Bitmap.Config.ARGB_8888, false)

                    onView(withId(R.id.customView)).perform(swipeRight())

                    val afterBitmap = customViewInViewCheck.getCurrentBitmap()
                    assertNotEquals(beforeBitmap, afterBitmap)
                }
            }
        }
    }

}

class ElapsedTimeIdlingResource(private val waitingTime: Long) : IdlingResource {
    private val startTime: Long = System.currentTimeMillis()
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = this.javaClass.name

    override fun isIdleNow(): Boolean {
        val elapsed = System.currentTimeMillis() - startTime
        val idle = elapsed >= waitingTime
        if (idle) resourceCallback?.onTransitionToIdle()
        return idle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = resourceCallback
    }
}

class HandlerIdlingResource(handler: Handler) : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = this.javaClass.name

    override fun isIdleNow(): Boolean {
        resourceCallback?.onTransitionToIdle()
        return true
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = resourceCallback
    }
}




