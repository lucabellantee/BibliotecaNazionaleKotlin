package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentReviewsBinding

class ReviewsFragment : Fragment(R.layout.fragment_reviews) {

    lateinit var binding: FragmentReviewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding = FragmentReviewsBinding.bind(view)
    }


}