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
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel

class DeleteBookingFragment : Fragment(R.layout.fragment_delete_booking) {
    private var _binding: FragmentDeleteBookingBinding? = null
    private val binding get() = _binding!!

    private val fbModel: FirebaseViewModel = FirebaseViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDeleteBookingBinding.bind(view)

        val book = arguments?.getParcelable<MiniBook>("book")

        book?.let {
            val isbn = it.isbn
            binding.textViewBookName.text = it.bookPlace
            binding.textViewAutore.text = it.isbn
            binding.textViewDataRiconsegna.text = "${binding.textViewDataRiconsegna.text} ${it.date}"

            binding.buttonCancella.setOnClickListener {
                fbModel.removeBookBooked(isbn,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Book deleted successfully!", Toast.LENGTH_SHORT).show()
                        binding.buttonCancella.isEnabled = false
                        val action = DeleteBookingFragmentDirections.actionDeleteBookingFragment2ToMyBooksFragment()
                        findNavController().navigate(action)
                    },
                    onError = {
                        Toast.makeText(requireContext(), "Problems occurred during deletion", Toast.LENGTH_SHORT).show()
                    }
                )
            }


            Glide.with(requireContext())
                .load(book.image)
                .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                .into(binding.imageViewBook)

        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

