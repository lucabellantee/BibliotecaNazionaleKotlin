package com.example.biblioteca_nazionale

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.biblioteca_nazionale.activity.HomePageActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class LogoutTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<HomePageActivity> = ActivityScenarioRule(HomePageActivity::class.java)



    @Test
    fun logoutTest() {

        onView(withId(R.id.bottom_navigation)).perform(click())

        onView(withId(R.id.profileInfoFragment)).perform(click())

        onView(withId(R.id.scrollViewInfoProfile)).perform(swipeUp())

        onView(withId(R.id.logoutButton)).perform(click())

        onView(withText("Confirm Logout"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(android.R.id.button1))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

    }


}