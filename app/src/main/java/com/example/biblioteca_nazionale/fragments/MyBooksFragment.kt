package com.example.biblioteca_nazionale.fragments

import BookAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel

class MyBooksFragment : Fragment(R.layout.fragment_my_books) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val firebaseViewModel: FirebaseViewModel = ViewModelProvider(requireActivity()).get(FirebaseViewModel::class.java)
        val currentUser = firebaseViewModel.getCurrentUser(firebaseViewModel.firebase.getCurrentUid().toString()).get()
        val libriPrenotati: ArrayList<MiniBook>? = currentUser.userSettings?.libriPrenotati
        Log.d("SONO QUI1", currentUser.email+ " "+ currentUser.userSettings!!.libriPrenotati!!.get(0).bookPlace)
        /*val libriPrenotati: ArrayList<MiniBook>? =
            firebaseViewModel.getCurrentUser(firebaseViewModel.getUidLoggedUser()).get().userSettings?.libriPrenotati*/

        val bookList: List<MiniBook> = libriPrenotati ?: listOf()

        Log.d("SONO QUI3", "MIAO")
        val view = inflater.inflate(R.layout.fragment_my_books, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        Log.d("LISTAA", bookList.listIterator().toString())
        adapter = BookAdapter(bookList)
        Log.d("LISTAA1", bookList.listIterator().toString())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        Log.d("SONO QUI4", "MIAO")

        return view
    }
}
