package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.FragmentBookListBinding
import com.example.biblioteca_nazionale.viewmodel.BooksViewModel
import com.google.firebase.auth.FirebaseAuth

class BookListFragment : Fragment(R.layout.fragment_book_list) {

    private lateinit var binding: FragmentBookListBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val model: BooksViewModel = BooksViewModel()

    private var lastQuery: String = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookListBinding.bind(view)

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

        // Dichiarazione della variabile per memorizzare l'ultima query

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val trimmedQuery = query.trim() // Rimuovi gli spazi vuoti dalla nuova query
                val trimmedLastQuery = lastQuery.trim() // Rimuovi gli spazi vuoti dall'ultima query

                if (!trimmedQuery.equals(trimmedLastQuery, ignoreCase = true)) { // Confronto tra la nuova query e l'ultima query senza spazi vuoti
                    lastQuery = query // Aggiornamento dell'ultima query
                    performBookSearch(query) // Esegui la ricerca dei libri
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val trimmedText = newText.trim() // Rimuovi gli spazi vuoti dal nuovo testo
                val trimmedLastQuery = lastQuery.trim() // Rimuovi gli spazi vuoti dall'ultima query

                if (!trimmedText.equals(trimmedLastQuery, ignoreCase = true)) { // Confronto tra il nuovo testo e l'ultima query senza spazi vuoti
                    lastQuery = newText // Aggiornamento dell'ultima query
                    performBookSearch(newText) // Esegui la ricerca dei libri
                }
                return true
            }
        })


        Log.d("yolxzxzoddd", binding.searchView.hasFocus().toString())

    }

    private fun performBookSearch(query: String) {
        model.searchBooks(query)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBooks.layoutManager = layoutManager
        model.getLibriLiveData().observe(viewLifecycleOwner) { libriList ->
            println(libriList)
            val adapter = BookListAdapter(libriList)
            adapter.setOnBookClickListener(object : BookListAdapter.OnBookClickListener {
                override fun onBookClick(position: Int) {
                    val libro = libriList.items[position]
                    if (libro != null) {
                        val action = BookListFragmentDirections.actionBookListFragmentToBookInfoFragment(libro)
                        findNavController().navigate(action)
                    } else {
                        // Gestisci il caso in cui il libro è nullo
                    }
                }
            })
            binding.recyclerViewBooks.adapter = adapter
        }
    }
}
