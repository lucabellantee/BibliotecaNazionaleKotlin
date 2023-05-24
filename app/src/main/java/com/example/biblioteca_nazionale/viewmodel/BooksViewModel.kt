package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.model.BooksResponse
import com.example.biblioteca_nazionale.utils.GoogleBooksApiClient
import kotlinx.coroutines.async

class BooksViewModel : ViewModel() {
    private val googleBooksApiClient: GoogleBooksApiClient = GoogleBooksApiClient()

    //private val _libriLiveData = MutableLiveData<List<Book>>()
    private val _libriLiveData = MutableLiveData<BooksResponse>()

    fun getLibriLiveData(): LiveData<BooksResponse> {
        return _libriLiveData
    }

    fun searchBooks(query: String): LiveData<BooksResponse> {
        viewModelScope.async {
            try {
                val response = googleBooksApiClient.getApiService().searchBooks(query)
                //Log.d("BooksViewModel-response", "Valore: ${response}")
                //Log.d("BooksViewModel-response-size", "Valore: ${response.items[1]}")

                    //val booksResponse = response.get(0)
                    //val books = booksResponse.items ?: emptyList()

                    _libriLiveData.value = response
                    /*Log.d("BooksViewModel", "Valore: ${response.items.get(0).info.title}")
                    Log.d("BooksViewModel", "Valore: ${response.items.get(1).info.title}")
                    Log.d("BooksViewModel", "Valore: ${_libriLiveData.value!!.items[0]}")
                    Log.d("BooksViewModel", "Valore: ${_libriLiveData.value!!.items[1]}")
                    Log.d("BooksViewModel", "LiveData: ${_libriLiveData.value}")*/

                    /*if (books.isNotEmpty()) {
                        for(book in books){
                            title = book.title
                            description = book.description
                            authors = book.authors.toString()
                        }
                    }*/
            } catch (e: Exception) {
                Log.d("BooksViewModel", "Error exception: "+e.message)
            }
        }
        //Log.d("BooksViewModelReturn", "Nervo: ${_libriLiveData.value?.items?.get(0)}")
        return _libriLiveData
    }
}
