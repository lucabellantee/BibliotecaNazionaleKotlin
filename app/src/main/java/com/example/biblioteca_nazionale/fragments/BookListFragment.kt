package com.example.biblioteca_nazionale.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.viewmodel.BookListViewModel
import com.example.biblioteca_nazionale.databinding.FragmentBookListBinding
import com.example.recyclersample.BookList.BookListAdapter


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

        val rv: RecyclerView = view.findViewById(R.id.recyclerViewBooks)
        rv.layoutManager = LinearLayoutManager(this.context)

        viewModel.books.observe(viewLifecycleOwner, Observer { books ->
            adapter.submitList(books)
        })
    }


}

