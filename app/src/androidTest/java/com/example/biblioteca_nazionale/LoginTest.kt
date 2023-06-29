package com.example.biblioteca_nazionale

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
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
import org.junit.After

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginTest {


    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun cleanup() {
        Intents.release()
    }

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)



    @Test
    fun testLogin() {

        Espresso.onView(ViewMatchers.withId(R.id.loginButtonWelcPage)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))

        Espresso.onView(ViewMatchers.withId(R.id.EditTextSearch)).perform(ViewActions.typeText("test@gmail.com"))

        // Chiudi la tastiera virtuale
        Espresso.closeSoftKeyboard()

        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("Test1234"))

        // Chiudi la tastiera virtuale di nuovo, nel caso fosse riaperta dopo aver inserito la password
        Espresso.closeSoftKeyboard()

        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click())

        // Verifica che la barra di navigazione in basso sia visualizzata
        Espresso.onView(ViewMatchers.withId(R.id.bottom_navigation)).check(matches(isDisplayed()))


    }


}