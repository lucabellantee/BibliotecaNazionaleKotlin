package com.example.biblioteca_nazionale

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.biblioteca_nazionale.activity.HomePageActivity
import com.example.biblioteca_nazionale.fragments.BookListFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


// todo luca: non funzionante o si aggiusta o si elimina

@RunWith(AndroidJUnit4::class)
@LargeTest
class BookTest {

    @get:Rule
    val scenario: ActivityScenarioRule<HomePageActivity> =
        ActivityScenarioRule(HomePageActivity::class.java)

   // lateinit var fragmentScenario: FragmentScenario<BookListFragment>




    @Test
    fun searchBookTest() {

        Espresso.onView(withId(R.id.toolbar))
            .perform(click())
            //.check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.searchView))
            .perform(ViewActions.typeText("1984"))

        //Thread.sleep(1000)

        Espresso.onView(withId(R.id.recyclerViewBooks))
            .perform(swipeUp()) // Scorri verso l'alto per assicurarti che il primo elemento sia visibile
        Espresso.onView(withText("1984"))
            .perform(click())

    }

}