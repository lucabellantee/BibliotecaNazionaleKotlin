package com.example.biblioteca_nazionale.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentWriteReviewBinding
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.Review
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.material.appbar.MaterialToolbar

class WriteReviewFragment : Fragment(R.layout.fragment_write_review) {

    private lateinit var binding: FragmentWriteReviewBinding

    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()

    private lateinit var ratingBar: RatingBar


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWriteReviewBinding.bind(view)

        val reviewVote = arguments?.getFloat("reviewVote")
        val book = arguments?.getParcelable<Book>("book")
        val review = arguments?.getParcelable<Review>("review")

        ratingBar = binding.ratingBarReview

        binding.reviewTitle.requestFocus()


        binding.reviewTitle.postDelayed({
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }, 1)

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating == 0.0f) {
                ratingBar.rating = 1.0f // Imposta il rating a 1 se l'utente ha selezionato 0
            }
        }

        if (book != null) {
            binding.textViewBookName.text = book.info?.title ?: ""
            binding.textViewAutore.text = book.info?.authors?.toString() ?: ""

            Glide.with(requireContext())
                .load(book.info.imageLinks?.thumbnail?.toString())
                .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                .into(binding.imageViewBook)

            review?.let {
                binding.reviewTitle.setText(it.reviewText)
                binding.reviewText.setText(it.reviewText)
            }

            reviewVote?.let {
                ratingBar.rating = it
                manageToolbarFromInfoFragment(book, review)
            }
        } else {

            if (review != null) {
                binding.textViewBookName.text = review.title ?: ""

                binding.textViewAutore.visibility=View.GONE

                Glide.with(requireContext())
                    .load(review.image)
                    .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                    .into(binding.imageViewBook)

                review?.let {
                    binding.reviewTitle.setText(it.reviewText)
                    binding.reviewText.setText(it.reviewText)
                }

                reviewVote?.let {
                    ratingBar.rating = it
                }

                manageToolbarFromMyReviews(review)
            }

        }
    }

    private fun manageToolbarFromInfoFragment(book: Book, review: Review?) {
        val toolbar: MaterialToolbar = binding.toolbar

        toolbar.setNavigationOnClickListener {
            val action =
                WriteReviewFragmentDirections.actionWriteReviewFragmentToBookInfoFragment(book)
            findNavController().navigate(action)
        }

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_confirm) {
                if (binding.reviewText.text.toString()
                        .isNotEmpty() && binding.reviewTitle.text.toString().isNotEmpty()
                ) {
                    if (review == null) {
                        book.info.title?.let {
                            fbViewModel.addNewCommentUserSide(
                                binding.reviewText.text.toString(),
                                binding.reviewTitle.text.toString(),
                                book.id,
                                ratingBar.rating,
                                null,
                                it,
                                book.info.imageLinks?.thumbnail.toString()
                            )
                        }
                    } else {
                        updateReview(review)
                    }

                    Toast.makeText(
                        requireContext(),
                        "La recensione è andata a buon fine",
                        Toast.LENGTH_SHORT
                    ).show()

                    val action =
                        WriteReviewFragmentDirections.actionWriteReviewFragmentToBookInfoFragment(
                            book
                        )
                    findNavController().navigate(action)
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

    private fun manageToolbarFromMyReviews(review: Review) {
        val toolbar: MaterialToolbar = binding.toolbar

        toolbar.setNavigationOnClickListener {
            val action =
                WriteReviewFragmentDirections.actionWriteReviewFragmentToMyReviewsFragment()
            findNavController().navigate(action)
        }

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_confirm) {
                if (binding.reviewText.text.toString()
                        .isNotEmpty() && binding.reviewTitle.text.toString().isNotEmpty()
                ) {

                    updateReview(review)

                    Toast.makeText(
                        requireContext(),
                        "La recensione è andata a buon fine",
                        Toast.LENGTH_SHORT
                    ).show()

                    val action =
                        WriteReviewFragmentDirections.actionWriteReviewFragmentToMyReviewsFragment()
                    findNavController().navigate(action)
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

    private fun updateReview(review: Review) {
        fbViewModel.removeCommentUserSide(review.idComment)

        fbViewModel.addNewCommentUserSide(
            binding.reviewText.text.toString(),
            binding.reviewTitle.text.toString(),
            review.isbn,
            ratingBar.rating,
            review.idComment,
            review.title,
            review.image
        )
    }
}