package com.example.biblioteca_nazionale.repository

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.biblioteca_nazionale.interface_.ApiService
import com.example.biblioteca_nazionale.model.Book
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder

class BookRepository {
    /*private val database = Firebase.firestore
    private val booksCollection = database.collection("books")

    fun addBook(book: Book, callback: (Boolean) -> Unit) {

            booksCollection
                .add(book)
                .addOnSuccessListener {
                    book.isbn = it.id
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding book", e)
                    callback(false)
                }
    }*/

    /*fun getBooks(callback: (List<Book>) -> Unit) {
        booksCollection
            .get()
            .addOnSuccessListener { result ->
                val books = mutableListOf<Book>()
                for (document in result) {
                    val book = document.toObject(Book::class.java)
                    book.id = document.id
                    books.add(book)
                }
                callback(books)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting books", e)
                callback(emptyList())
            }
    }*/

    fun getBooks() : List<Book>{
        val book1 = Book(734687682, "Harry potter", "J.K.Rowling", "", "Questo è il primo libro sulla pietra filosofale", "", "", "", "")
        val book2 = Book(734564568, "Harry potter 2", "J.K.Rowling", "", "Quì si parla della camera dei segreti", "", "", "", "")
        val book3 = Book(739347682, "Harry potter 3", "J.K.Rowling", "", "Questo parla del prigioniero di Azkaban, cioè il padrino di Harry", "", "", "", "")
        return listOf<Book>(book1, book2, book3)
            }


        companion object {
        private const val TAG = "BookRepository"
    }

    /*suspend fun uploadBook(book: Book) {
        val db = FirebaseFirestore.getInstance()
        db.collection("books")
            .document(book.id)
            .set(book, SetOptions.merge())
            .await()
    }

    suspend fun getBookById(bookId: String): Book? {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("books").document(bookId)
        val docSnapshot = docRef.get().await()
        return docSnapshot.toObject(Book::class.java)
    }*/
}
