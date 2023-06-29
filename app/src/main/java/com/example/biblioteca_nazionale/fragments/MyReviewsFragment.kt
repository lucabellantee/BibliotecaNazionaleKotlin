package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentMyReviewsBinding

class MyReviewsFragment : Fragment(R.layout.fragment_my_reviews) {

    private lateinit var binding: FragmentMyReviewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMyReviewsBinding.bind(view)


    }
}