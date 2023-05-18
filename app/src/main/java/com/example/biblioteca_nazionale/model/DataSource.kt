
package com.example.recyclersample.data

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.biblioteca_nazionale.model.Book

/* Handles operations on BooksLiveData and holds details about it. */
class DataSource(resources: Resources) {
    private val initialBookList = bookList(resources)
    private val BooksLiveData = MutableLiveData(initialBookList)

    /* Adds Book to liveData and posts value. */
    fun addBook(book: Book) {
        val currentList = BooksLiveData.value
        if (currentList == null) {
            BooksLiveData.postValue(listOf(book))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, book)
            BooksLiveData.postValue(updatedList)
        }
    }

    /* Removes Book from liveData and posts value. */
    fun removeBook(book: Book) {
        val currentList = BooksLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(book)
            BooksLiveData.postValue(updatedList)
        }
    }

    /* Returns Book given an ID. */
    fun getBookForId(id: String): Book? {
        BooksLiveData.value?.let { books ->
            return books.firstOrNull{ it.isbn == id}
        }
        return null
    }

    fun getBookList(): LiveData<List<Book>> {
        return BooksLiveData
    }

    /* Returns a random Book asset for Books that are added. */
    /*fun getRandomBookImageAsset(): Int? {
        val randomNumber = (initialBookList.indices).random()
        return initialBookList[randomNumber].image
    }*/

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}