package com.example.biblioteca_nazionale

import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.model.Review
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class MiniBookTest {

    lateinit var libriPrenotati: ArrayList<MiniBook>

    @Before
    fun beforeTest(){
        libriPrenotati = ArrayList<MiniBook>()
    }


    @Test
    fun MiniBookConstructorTest(){

        val newBook = MiniBook("isbn","bookPlace","image","29/06/2023","title")

        libriPrenotati.add(newBook)


        // Prendo il primo(ed unico) elemento dalla lista e vedo se l'oggetto è stato correttamente creato,
        // verificando il valore dei singoli attributi
        assertEquals(libriPrenotati.first().isbn , "isbn")
        assertEquals(libriPrenotati.first().bookPlace , "bookPlace")
        assertEquals(libriPrenotati.first().date , "29/06/2023")
        assertEquals(libriPrenotati.first().image , "image")
        assertEquals(libriPrenotati.first().title , "title")
    }

}