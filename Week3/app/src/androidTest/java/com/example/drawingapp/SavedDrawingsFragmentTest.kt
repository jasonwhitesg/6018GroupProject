package com.example.drawingapp
//
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentFactory
//import androidx.fragment.app.testing.FragmentScenario
//import androidx.fragment.app.testing.launchFragment
//import androidx.fragment.app.testing.launchFragmentInContainer
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.LifecycleRegistry
//import androidx.navigation.NavController
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions.click
//import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import androidx.navigation.Navigation
//import org.junit.Assert.assertEquals
//
//// Additional Import
//import com.example.drawingapp.R
//
//@RunWith(AndroidJUnit4::class)
//class SavedDrawingsFragmentTest {
//
//    private lateinit var navController: TestNavHostController
//    private lateinit var scenario: FragmentScenario<SavedDrawingsFragment>
//
//    @Before
//    fun setUp() {
//        navController = TestNavHostController(
//            ApplicationProvider.getApplicationContext()
//        )
//        navController.setGraph(R.navigation.nav_graph)
//
//        val fragmentFactory = object : FragmentFactory() {
//            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
//                return SavedDrawingsFragment().also { fragment ->
//                    fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
//                        if (viewLifecycleOwner != null) {
//                            Navigation.setViewNavController(fragment.requireView(), navController)
//                        }
//                    }
//                }
//            }
//        }
//
//        scenario = launchFragment(factory = fragmentFactory)
//
//    }
//
//
//
//
//
//
//    @Test
//    fun testNavButtonPress() {
//        onView(withContentDescription("Navigate Back")).perform(click())
//        assertEquals(R.id.drawingFragment, navController.currentDestination?.id)
//    }
//}












