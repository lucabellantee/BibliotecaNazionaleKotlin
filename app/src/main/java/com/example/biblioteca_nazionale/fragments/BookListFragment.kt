package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.FragmentBookListBinding
import com.example.biblioteca_nazionale.interface_.GoogleBooksApiService
import com.example.biblioteca_nazionale.viewmodel.BooksViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class BookListFragment : Fragment(R.layout.fragment_book_list) {

    private lateinit var binding: FragmentBookListBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val model: BooksViewModel = BooksViewModel()

    private var lastQuery: String = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookListBinding.bind(view)

        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener{
            val action = BookListFragmentDirections.actionBookListFragmentToMyLikesFragment()
            findNavController().navigate(action)
        }

        binding.textViewPrincipale.visibility=View.VISIBLE
        binding.progressBar.visibility = View.GONE

        var focusSearchView = arguments?.getBoolean("focusSearchView") ?: false
        if (focusSearchView) {
            binding.searchView.postDelayed({
                binding.searchView.clearFocus()
                binding.searchView.requestFocus()

                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }, 1)
        }

        binding.searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if(binding.layoutPrincipale.visibility==View.VISIBLE){
                binding.layoutPrincipale.visibility=View.GONE
            }
        }




        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                val trimmedQuery = query.trim()
                val trimmedLastQuery = lastQuery.trim()

                if (!trimmedQuery.equals(trimmedLastQuery, ignoreCase = true)) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewBooks.visibility=View.GONE
                    lastQuery = query
                    performBookSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                val trimmedText = newText.trim()
                val trimmedLastQuery = lastQuery.trim()

                if (!trimmedText.equals(trimmedLastQuery, ignoreCase = true)) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewBooks.visibility=View.GONE
                    lastQuery = newText
                    performBookSearch(newText)
                }
                return true
            }
        })

    }

    private fun performBookSearch(query: String) {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBooks.layoutManager = layoutManager
        model.searchBooks(query).observe(viewLifecycleOwner) { libriList ->
            println(libriList)
            val adapter = BookListAdapter(libriList)
            adapter.setOnBookClickListener(object : BookListAdapter.OnBookClickListener {
                override fun onBookClick(position: Int) {
                    val libro = libriList.items[position]
                    if (libro != null) {
                        val action = BookListFragmentDirections.actionBookListFragmentToBookInfoFragment(libro)
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(requireContext(), "Book not found", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            binding.recyclerViewBooks.adapter = adapter
            binding.progressBar.visibility = View.GONE
            binding.recyclerViewBooks.visibility=View.VISIBLE
        }
    }
}
