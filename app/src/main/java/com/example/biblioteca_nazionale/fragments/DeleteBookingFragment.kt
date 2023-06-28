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
    private var _binding: FragmentDeleteBookingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDeleteBookingBinding.bind(view)

        val book = arguments?.getParcelable<MiniBook>("book")

        book?.let {
            binding.textViewBookName.text = it.bookPlace
            binding.textViewAutore.text = it.isbn
            binding.textViewDataRiconsegna.text = "${binding.textViewDataRiconsegna.text} ${it.date}"

            Glide.with(requireContext())
                .load(book.image)
                .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                .into(binding.imageViewBook)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

