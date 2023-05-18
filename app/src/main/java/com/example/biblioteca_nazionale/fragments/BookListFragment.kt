package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.FragmentBookListBinding
import com.example.biblioteca_nazionale.databinding.HomePageBinding
import com.example.biblioteca_nazionale.repository.BookRepository

class BookListFragment : Fragment(R.layout.fragment_book_list) {

    lateinit var binding: FragmentBookListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: BookRepository = BookRepository()
        val rv: RecyclerView = findViewById(R.id.recyclerViewBooks)
        rv.layoutManager = LinearLayoutManager(this)

        rv.adapter = BookListAdapter(model.getBooks().toList())

        binding = FragmentBookListBinding.inflate(layoutInflater)
    }
}