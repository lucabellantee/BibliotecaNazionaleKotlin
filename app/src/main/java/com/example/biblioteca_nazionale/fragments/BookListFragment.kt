package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.viewmodel.BookListViewModel
import com.example.biblioteca_nazionale.databinding.FragmentBookListBinding


class BookListFragment : Fragment() {
    private lateinit var binding: FragmentBookListBinding
    private lateinit var viewModel: BookListViewModel
    private lateinit var adapter: BookListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(BookListViewModel::class.java)
        adapter = BookListAdapter()

        binding.recyclerViewBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBooks.adapter = adapter

        viewModel.getBooks().observe(viewLifecycleOwner, { books -> adapter.submitList(books)
        })
    }
}

