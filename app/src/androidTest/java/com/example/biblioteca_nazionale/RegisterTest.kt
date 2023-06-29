package com.example.biblioteca_nazionale

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.biblioteca_nazionale.activity.LoginActivity
import com.example.biblioteca_nazionale.activity.RegistrationActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class RegisterTest {


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
    fun registerTest() {

        Espresso.onView(ViewMatchers.withId(R.id.loginButtonWelcPage2)).perform(ViewActions.click())

        Intents.intended(IntentMatchers.hasComponent(RegistrationActivity::class.java.name))

        Espresso.onView(ViewMatchers.withId(R.id.emailTextViewInsert)).perform(ViewActions.typeText("topolino@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.passwordTextViewInsert)).perform(ViewActions.typeText("Topolino1234"))
        Espresso.onView(ViewMatchers.withId(R.id.passConfirmTextViewInsert)).perform(ViewActions.typeText("Topolino1234"))

        Espresso.closeSoftKeyboard()

        Espresso.onView(ViewMatchers.withId(R.id.regButtonLayReg)).perform(ViewActions.click())

    }
}