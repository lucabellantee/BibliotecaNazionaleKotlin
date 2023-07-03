package com.example.biblioteca_nazionale

import com.example.biblioteca_nazionale.model.Like
import com.example.biblioteca_nazionale.model.Review
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(JUnit4::class)
class UserTest {

    lateinit var uid: String
    lateinit var email: String
    lateinit var userSettings: UserSettings
    lateinit var newUser: Users

    @Before
    fun beforeTest() {
        uid = "123456"
        email = "emailProva@gmail.com"
        userSettings = UserSettings(ArrayList(), ArrayList(), null)
        newUser = Users(uid, email, userSettings)
    }


    @Test
    fun createNewUser() {
        assertEquals(newUser.UID, uid)
        assertEquals(newUser.email, email)
        assertEquals(newUser.userSettings, userSettings)
    }


    @Test
    fun addNewComment() {
        // Aggiungo un paio di commenti uguali tra di loro, cambierà solo l'id(settato nel metodo Review)

        newUser.userSettings?.addNewComment("reviewText", "reviewTitle",
            "isbn", 7f,  "title" , "title" , "image")


        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formattedDateTime = currentDateTime.format(formatter)

        // Il commento è il primo , quindi prendo il primo

        // La data deve essere quella corrente
        assertEquals(newUser.userSettings?.commenti!!.first().date , formattedDateTime)

        // Eseguo le verifiche successive, valutando che tutte i campi siano corretti

        assertEquals(newUser.userSettings?.commenti!!.first().vote , 7f)
        assertEquals(newUser.userSettings?.commenti!!.first().isbn , "isbn")
        assertEquals(newUser.userSettings?.commenti!!.first().title , "title")
        assertEquals(newUser.userSettings?.commenti!!.first().image , "image")
        assertEquals(newUser.userSettings?.commenti!!.first().reviewText , "reviewText")
        assertEquals(newUser.userSettings?.commenti!!.first().reviewTitle , "reviewTitle")

    }



    @Test  //(expected = NoSuchElementException::class)
    fun removeCommentTest(){

        // Aggiungo un commento
        this.addComment()

        // Gli passo l'id del primo(ed unico) commento di prova
        val idComment = newUser.userSettings?.commenti!!.first().idComment

        // Siccome ho un unico commento, se lo RIMUOVO ENTRO IN ECCEZIONE:
        newUser.userSettings?.removeComment(idComment)

        assertEquals(newUser.userSettings!!.commenti.isEmpty() , true)

    }


    @Test
    fun addNewLikeTest(){
        newUser.userSettings?.addNewLike("ID_BOOK_LIKE")
        assertEquals(newUser.userSettings?.miPiace?.first().toString() , Like("ID_BOOK_LIKE").toString() )
    }

    @Test
    fun removeLikeTest(){
        addLike()
        newUser.userSettings?.deleteLike("ID_BOOK_LIKE")
        assertEquals(newUser.userSettings?.miPiace?.isEmpty() , true)
    }

    private fun addComment(){
        newUser.userSettings?.addNewComment("reviewText", "reviewTitle",
            "isbn", 7f,  "title" , "title" , "image")
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formattedDateTime = currentDateTime.format(formatter)
    }


    private fun addLike() = newUser.userSettings?.addNewLike("ID_BOOK_LIKE")

}