package com.example.biblioteca_nazionale

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
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
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.biblioteca_nazionale.activity.HomePageActivity
import org.junit.After

@RunWith(AndroidJUnit4::class)
@LargeTest
class updateInfoUserTest {


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
    fun testUpdateEmail() {

        onView(withId(R.id.loginButtonWelcPage)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))

        onView(withId(R.id.EditTextSearch)).perform(ViewActions.typeText("test@gmail.com"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.password)).perform(ViewActions.typeText("Test1234"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.loginButton)).perform(click())

        // todo luca : E' corretto questo riferimento ? Perchè sembra che faccia riferimento sia al fragment che al button
        onView(withId(R.id.profileInfoFragment)).perform(click())
       /* onView(withId(R.id.profileInfoFragment))
            .inRoot(withDecorView(withId(R.layout.home_page)))
            .perform(click())*/

        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText("test2@gmail.com"))

        onView(withId(R.id.update_button_frag)).perform(click())

        onView(withId(R.id.regButtonLayReg)).check(matches(isDisplayed()))

        onView(withId(R.id.regButtonLayReg)).perform(click())

       // onView(withText("Operazione di cambio email completata con successo!")).inRoot(ToastMatcher()).check(matches(isDisplayed()))

        //onView(withId(R.id.credentialUpdatedFragment)).check(matches(isDisplayed()))
    }


}