package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.BooksResponse
import com.example.biblioteca_nazionale.utils.GoogleBooksApiClient
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {
    private val googleBooksApiClient: GoogleBooksApiClient = GoogleBooksApiClient()

    private val _libriLiveData = MutableLiveData<BooksResponse>()

    fun searchBooks(query: String): MutableLiveData<BooksResponse> {
        viewModelScope.launch {
            try {
                val response = googleBooksApiClient.getApiService().searchBooks(query, 20)
                _libriLiveData.value = response
            } catch (e: Exception) {
                Log.d("BooksViewModel", "Error exception: " + e.message)
            }
        }

        return _libriLiveData
    }

    fun searchBooksById(query: String): MutableLiveData<Book> {

        val _LikeLiveData = MutableLiveData<Book>()

        viewModelScope.launch {
            try {
                val response = googleBooksApiClient.getApiService().getBookById(query)
                _LikeLiveData.value = response
            } catch (e: Exception) {
                Log.d("BooksViewModel", "Error exception: " + e.message)
            }
        }

        return _LikeLiveData
    }
}
