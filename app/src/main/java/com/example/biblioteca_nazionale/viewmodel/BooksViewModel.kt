package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.model.BooksResponse
import com.example.biblioteca_nazionale.utils.GoogleBooksApiClient
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {
    private val googleBooksApiClient: GoogleBooksApiClient = GoogleBooksApiClient()

    private val _libriLiveData = MutableLiveData<BooksResponse>()

    fun getLibriLiveData(): LiveData<BooksResponse> {
        return _libriLiveData
    }

    fun searchBooks(query: String) {
        viewModelScope.launch{
            try {
                val response = googleBooksApiClient.getApiService().searchBooks(query)
                _libriLiveData.value = response
                println()
            } catch (e: Exception) {
                Log.d("BooksViewModel", "Error exception: " + e.message)
            }
        }
    }
}
