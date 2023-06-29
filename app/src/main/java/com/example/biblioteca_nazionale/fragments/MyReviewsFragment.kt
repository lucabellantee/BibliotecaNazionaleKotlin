package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentMyReviewsBinding
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel

class MyReviewsFragment : Fragment(R.layout.fragment_my_reviews) {

    private lateinit var binding: FragmentMyReviewsBinding
    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMyReviewsBinding.bind(view)

        fbViewModel.getUsersComments().observe(viewLifecycleOwner) { usersList ->

        }
    }
}