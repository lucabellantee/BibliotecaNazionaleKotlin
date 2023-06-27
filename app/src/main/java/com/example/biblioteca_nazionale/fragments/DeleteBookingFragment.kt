package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentDeleteBookingBinding
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.MiniBook

class DeleteBookingFragment : Fragment(R.layout.fragment_delete_booking) {

    lateinit var binding: FragmentDeleteBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val book = arguments?.getParcelable<MiniBook>("book")

        book?.let {
            binding.textViewBookName.text = it.isbn

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