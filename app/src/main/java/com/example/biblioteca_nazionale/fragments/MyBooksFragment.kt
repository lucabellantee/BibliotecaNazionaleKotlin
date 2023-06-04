package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookAdapter
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.ImageLinks
import com.example.biblioteca_nazionale.model.InfoBook

class MyBooksFragment : Fragment() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    val listaStringa = listOf("stringa1", "stringa2")
    val imagelinkss = ImageLinks("thumbnail", "")
    val info1 = InfoBook("titolo primo libro", listaStringa,"","","", imagelinkss)
    val info2 = InfoBook("titolo secondo libro", listaStringa,"","","", imagelinkss)
    val libro1 = Book("759389304",info1)
    val libro2 = Book("749204830284",info2)
    val list = listOf(libro1, libro2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_books, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        val adapter = BookAdapter(list) // Quì ci va la lista dei libri dell'utente, acquisibile dal database

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // o LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) se desideri un layout orizzontale

        return view
        //return inflater.inflate(R.layout.fragment_my_books, container, false)
    }


}