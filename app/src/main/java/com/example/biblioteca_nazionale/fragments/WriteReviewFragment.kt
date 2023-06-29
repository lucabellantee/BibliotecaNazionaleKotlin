package com.example.biblioteca_nazionale.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentWriteReviewBinding
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.Review
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

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

        binding.buttonEliminaRecensione.visibility = View.GONE

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
                binding.reviewTitle.setText(it.reviewTitle)
                binding.reviewText.setText(it.reviewText)

                binding.buttonEliminaRecensione.visibility = View.VISIBLE

                binding.buttonEliminaRecensione.setOnClickListener {
                    fbViewModel.removeCommentUserSide(review.idComment).thenAccept {

                        val action =
                            WriteReviewFragmentDirections.actionWriteReviewFragmentToBookInfoFragment(
                                book
                            )
                        findNavController().navigate(action)

                        Toast.makeText(
                            requireContext(),
                            "La recensione è stata eliminata",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            reviewVote?.let {
                ratingBar.rating = it
                manageToolbarFromInfoFragment(book, review)
            }
        } else {

            if (review != null) {
                binding.textViewBookName.text = review.title ?: ""

                binding.textViewAutore.visibility = View.GONE

                Glide.with(requireContext())
                    .load(review.image)
                    .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                    .into(binding.imageViewBook)

                binding.buttonEliminaRecensione.visibility = View.VISIBLE

                binding.buttonEliminaRecensione.setOnClickListener {

                    fbViewModel.removeCommentUserSide(review.idComment).thenAccept {

                        val action =
                            WriteReviewFragmentDirections.actionWriteReviewFragmentToMyReviewsFragment()
                        findNavController().navigate(action)

                        Toast.makeText(
                            requireContext(),
                            "La recensione è stata eliminata",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                review?.let {
                    println(review)
                    binding.reviewTitle.setText(it.reviewTitle)
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
                        val action =
                            WriteReviewFragmentDirections.actionWriteReviewFragmentToBookInfoFragment(book)
                        findNavController().navigate(action)
                    } else {
                        val action =
                            WriteReviewFragmentDirections.actionWriteReviewFragmentToBookInfoFragment(
                                book
                            )
                        updateReview(review, action)
                    }
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
                    println(review)

                    val action =
                        WriteReviewFragmentDirections.actionWriteReviewFragmentToMyReviewsFragment()

                    updateReview(review, action)

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

    private fun updateReview(review: Review, action: NavDirections) {
        fbViewModel.getCurrentUser().thenAccept { currentUser ->
            val commentToUpdate = currentUser.userSettings?.commenti?.find { it.idComment == review.idComment }

            if (commentToUpdate != null) {
                commentToUpdate.reviewText = binding.reviewText.text.toString()
                commentToUpdate.reviewTitle = binding.reviewTitle.text.toString()
                commentToUpdate.isbn = review.isbn
                commentToUpdate.vote = ratingBar.rating
                commentToUpdate.title = review.title
                commentToUpdate.image = review.image

                fbViewModel.firebase.updateBookPrenoted(currentUser).thenAccept {
                    Toast.makeText(requireContext(), "Review updated", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(action)
                }.exceptionally { throwable ->
                    Toast.makeText(requireContext(), "Problems occurred during update", Toast.LENGTH_SHORT).show()
                    null
                }
            } else {
                Toast.makeText(requireContext(), "Comment not found", Toast.LENGTH_SHORT).show()
            }
        }.exceptionally { throwable ->
            Toast.makeText(requireContext(), "Error occurred while getting user information", Toast.LENGTH_SHORT).show()
            null
        }
    }
}