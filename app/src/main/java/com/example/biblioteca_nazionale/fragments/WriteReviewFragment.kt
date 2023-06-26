package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.Book

class WriteReviewFragment : Fragment(R.layout.fragment_write_review){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reviewVote = arguments?.getFloat("reviewVote")
        val book = arguments?.getParcelable<Book>("book")
    }
}