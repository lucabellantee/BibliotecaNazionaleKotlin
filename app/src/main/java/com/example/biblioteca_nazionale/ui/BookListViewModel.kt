package com.example.biblioteca_nazionale.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.data.Book
import com.example.biblioteca_nazionale.data.BookRepository

class BookListViewModel(private val bookRepository: BookRepository) : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    fun loadBooks() {
        viewModelScope.launch {
            _books.value = bookRepository.getBooks()
        }
    }
}
