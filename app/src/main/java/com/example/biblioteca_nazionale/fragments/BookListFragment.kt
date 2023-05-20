package com.example.biblioteca_nazionale.fragments

import BookInfoFragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.FragmentBookListBinding
import com.example.biblioteca_nazionale.repository.BookRepository

class BookListFragment : Fragment(R.layout.fragment_book_list){

    lateinit var binding: FragmentBookListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookListBinding.bind(view)

        val model: BookRepository = BookRepository()



        binding.recyclerViewBooks.layoutManager = LinearLayoutManager(requireContext())
        val bookListAdapter = BookListAdapter(model.getBooks())
        bookListAdapter.setOnBookClickListener(object : BookListAdapter.OnBookClickListener{
            override fun onBookClick(position: Int) {
                val book:Book = model.getBooks()[position]
                val bookInfoFrag = BookInfoFragment()
                val bundle = Bundle()
                bundle.putSerializable("selectedBook", book);                bookInfoFrag.arguments = bundle
                val fragmentManager: FragmentManager = parentFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentContainer, bookInfoFrag)
                fragmentTransaction.commit()
            }
        })
        binding.recyclerViewBooks.adapter = bookListAdapter
    }
}
