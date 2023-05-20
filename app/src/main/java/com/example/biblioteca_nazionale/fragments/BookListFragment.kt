package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.activity.HomePageActivity
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.FragmentBookListBinding
import com.example.biblioteca_nazionale.databinding.HomePageBinding
import com.example.biblioteca_nazionale.model.Book
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
                val bookInfoFrag = BookInfoFragment()
                val fragmentManager: FragmentManager = parentFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentContainer, bookInfoFrag) //Quì serve la recyclerView
                fragmentTransaction.commit()
            }
        })
        binding.recyclerViewBooks.adapter = bookListAdapter
    }
}
