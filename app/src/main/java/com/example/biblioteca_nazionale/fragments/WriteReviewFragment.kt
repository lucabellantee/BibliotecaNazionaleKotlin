package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentBookInfoBinding
import com.example.biblioteca_nazionale.databinding.FragmentWriteReviewBinding
import com.example.biblioteca_nazionale.model.Book

class WriteReviewFragment : Fragment(R.layout.fragment_write_review){

    lateinit var binding: FragmentWriteReviewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWriteReviewBinding.bind(view)

        val reviewVote = arguments?.getFloat("reviewVote")
        val book = arguments?.getParcelable<Book>("book")

        val ratingBar: RatingBar = binding.ratingBarReview

        book?.let {
            binding.textViewBookName.text = it.info?.title ?: ""
            binding.textViewAutore.text = it.info?.authors?.toString() ?: ""

            Glide.with(requireContext())
                .load(book.info.imageLinks?.thumbnail.toString())
                .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                .into(binding.imageViewBook)

            reviewVote?.let {
                ratingBar.rating=it
            }
        }
    }
}