package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.adapter.MyReviewsAdapter
import com.example.biblioteca_nazionale.databinding.FragmentMyReviewsBinding
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel

class MyReviewsFragment : Fragment(R.layout.fragment_my_reviews) {

    private lateinit var binding: FragmentMyReviewsBinding
    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMyReviewsBinding.bind(view)

        fbViewModel.getUsersComments().observe(viewLifecycleOwner) { reviewsList ->
            println(reviewsList)
            if (reviewsList != null) {
                if (reviewsList.isNotEmpty()){
                    val adapter = MyReviewsAdapter(reviewsList)
                    adapter.setOnReviewClickListener(object : MyReviewsAdapter.OnReviewClickListener {
                        override fun onReviewClick(position: Int) {
                            val bundle = Bundle().apply {
                                putFloat("reviewVote", reviewsList[position].vote)
                                putParcelable("book", null)
                                putParcelable("review", reviewsList[position])
                            }

                            findNavController().navigate(
                                R.id.action_myReviewsFragment_to_writeReviewFragment, bundle
                            )
                        }
                    })
                    val layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerViewReviews.layoutManager = layoutManager
                    binding.recyclerViewReviews.adapter = adapter
                }
            }
        }
    }
}