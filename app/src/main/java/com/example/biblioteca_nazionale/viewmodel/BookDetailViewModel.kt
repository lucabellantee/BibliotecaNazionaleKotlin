package com.example.biblioteca_nazionale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.repository.BookRepository
import kotlinx.coroutines.launch

class BookDetailViewModel(private val bookRepository: BookRepository) : ViewModel() {

    /*private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> = _book

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _book.value = bookRepository.getBookById(bookId)
        }
    }*/
}
