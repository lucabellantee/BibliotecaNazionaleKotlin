package com.example.biblioteca_nazionale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.repository.BookRepository

class AddBookViewModel(private val bookRepository: BookRepository) : ViewModel() {

    private val _addBookResult = MutableLiveData<Resource<String>>()
    val addBookResult: LiveData<Resource<String>> = _addBookResult

    fun addBook(book: Book) {
        viewModelScope.launch {
            _addBookResult.value = bookRepository.addBook(book)
        }
    }
}
