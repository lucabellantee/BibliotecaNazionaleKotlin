package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.FragmentBookListBinding
import com.example.biblioteca_nazionale.repository.BookRepository
import com.example.biblioteca_nazionale.viewmodel.BooksViewModel

class BookListFragment : Fragment(R.layout.fragment_book_list) {

    lateinit var binding: FragmentBookListBinding

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookListBinding.bind(view)

        //val model: BookRepository = BookRepository()
        //val model: BooksViewModel = ViewModelProvider(this).get(BooksViewModel::class.java)

        /*model.searchBooks("Harry potter e il prigioniero di azkaban").observe(viewLifecycleOwner, { booksResponse ->
            val adapter = BookListAdapter(booksResponse)
            binding.recyclerViewBooks.adapter = adapter
        })*/
        val model: BooksViewModel = BooksViewModel()

        binding.recyclerViewBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBooks.adapter = BookListAdapter(model.searchBooks("Harry potter e il prigioniero di azkaban"))
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookListBinding.bind(view)

        val model: BooksViewModel = BooksViewModel()

        // Imposta il layout manager
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBooks.layoutManager = layoutManager

        // Crea e imposta l'adapter
        val adapter = BookListAdapter(model.getLibriLiveData())
        binding.recyclerViewBooks.adapter = adapter

        // Esegui la ricerca dei libri
        model.searchBooks("Harry Potter e il prigioniero di Azkaban")
    }

}