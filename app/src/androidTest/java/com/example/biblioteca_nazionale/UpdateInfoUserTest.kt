package com.example.biblioteca_nazionale

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.biblioteca_nazionale.activity.LoginActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.biblioteca_nazionale.activity.HomePageActivity
import org.junit.After

@RunWith(AndroidJUnit4::class)
@LargeTest
class UpdateInfoUserTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<HomePageActivity> = ActivityScenarioRule(HomePageActivity::class.java)



    @Test
    fun updateEmailTest() {

        onView(withId(R.id.bottom_navigation)).perform(click())
        /* onView(withId(R.id.profileInfoFragment))
             .inRoot(withDecorView(withId(R.layout.home_page)))
             .perform(click())*/

        onView(withId(R.id.profileInfoFragment)).perform(click())

        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("test2@gmail.com"))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.editTextTextPassword)).perform(typeText("Test1234"))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.editTextTextPassword2)).perform(typeText("Test1234"))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.update_button_frag)).perform(click()).check(matches(isDisplayed()))

    }


}