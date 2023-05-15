package com.example.biblioteca_nazionale.data

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore

class BookRepository {
    private val database = Firebase.firestore
    private val booksCollection = database.collection("books")

    fun addBook(book: Book, callback: (Boolean) -> Unit) {
        booksCollection
            .add(book)
            .addOnSuccessListener {
                book.id = it.id
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding book", e)
                callback(false)
            }
    }

    fun getBooks(callback: (List<Book>) -> Unit) {
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
