package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentBookInfoBinding
import com.example.biblioteca_nazionale.databinding.FragmentWriteReviewBinding
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.material.appbar.MaterialToolbar

class WriteReviewFragment : Fragment(R.layout.fragment_write_review) {

    lateinit var binding: FragmentWriteReviewBinding

    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWriteReviewBinding.bind(view)

        val reviewVote = arguments?.getFloat("reviewVote")
        val book = arguments?.getParcelable<Book>("book")

        val ratingBar: RatingBar = binding.ratingBarReview

        val toolbar: MaterialToolbar = binding.toolbar


        book?.let {
            binding.textViewBookName.text = it.info?.title ?: ""
            binding.textViewAutore.text = it.info?.authors?.toString() ?: ""

            Glide.with(requireContext())
                .load(book.info.imageLinks?.thumbnail?.toString())
                .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                .into(binding.imageViewBook)

            reviewVote?.let {
                ratingBar.rating = it

                toolbar.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }

                toolbar.setOnMenuItemClickListener { item ->
                    if (item.itemId == R.id.menu_confirm) {
                        if (binding.reviewText.text.toString()
                                .isNotEmpty() && binding.reviewTitle.text.toString().isNotEmpty()
                        ) {
                            fbViewModel.addNewCommentUserSide(
                                binding.reviewText.text.toString(),
                                binding.reviewTitle.text.toString(),
                                book.id,
                                ratingBar.rating
                            )

                            Toast.makeText(requireContext(), "La recensione è andata a buon fine", Toast.LENGTH_SHORT).show()

                            val action = WriteReviewFragmentDirections.actionWriteReviewFragmentToBookInfoFragment(book)
                            findNavController().navigate(action)

                            //findNavController().popBackStack()

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Devi inserire il titolo della recensione e la recensione",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }
}