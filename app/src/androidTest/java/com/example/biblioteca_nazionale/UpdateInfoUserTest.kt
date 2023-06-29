package com.example.biblioteca_nazionale

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.biblioteca_nazionale.activity.HomePageActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UpdateInfoUserTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(HomePageActivity::class.java)

    @Test
    fun updateEmailTest() {

        onView(withId(R.id.bottom_navigation)).perform(click())

        onView(withId(R.id.profileInfoFragment)).perform(click())

        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("test2@gmail.com"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.update_button_frag)).perform(click())

        //onView(withId(R.id.regButtonLayReg)).check(matches(isDisplayed()))

        //onView(withId(R.id.regButtonLayReg)).perform(click())

    }
}