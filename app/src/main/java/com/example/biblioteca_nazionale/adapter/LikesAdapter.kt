package com.example.biblioteca_nazionale.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.cache.ReviewCache
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.BooksResponse
import com.example.biblioteca_nazionale.model.Review
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LikesAdapter(var data: ArrayList<Book>) :
    RecyclerView.Adapter<LikesAdapter.BookViewHolder>() {

    private lateinit var mListner: OnBookClickListener
    private var fbModel: FirebaseViewModel = FirebaseViewModel()


    interface OnBookClickListener {
        fun onBookClick(position: Int)
    }


    fun setOnBookClickListener(listner: OnBookClickListener) {
        mListner = listner
    }

    class BookViewHolder(itemView: View, listner: OnBookClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.book_title)
        val desc: TextView = itemView.findViewById(R.id.book_description)
        val author: TextView = itemView.findViewById(R.id.book_author)
        val cover: ImageView = itemView.findViewById(R.id.imageViewCover)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val numRating: TextView = itemView.findViewById(R.id.totalRating)

        init {
            itemView.setOnClickListener {
                listner.onBookClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.searching_result, parent, false)
        return BookViewHolder(itemView, mListner)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBook = data[position]


        holder.title.text = currentBook?.info?.title ?: ""
        holder.desc.text = currentBook?.info?.description ?: "Descrizione non disponibile"
        holder.author.text = currentBook?.info?.authors.toString()

        val cacheKey = ReviewCache.getCacheKey(currentBook.id)
        val cachedResult = ReviewCache.getResult(cacheKey)

        if (cachedResult != null) {
            var totalRating = 0f
            var numReviews = 0

            if (cachedResult != null) {
                for (review in cachedResult) {
                    totalRating += review.vote
                    numReviews++
                }
            }

            val averageRating = totalRating / numReviews
            val formattedAverage =
                if (averageRating.isNaN()) "0.0" else String.format("%.2f", averageRating)
            if (numReviews > 0) {
                holder.ratingBar.rating = averageRating
                holder.numRating.text = "${formattedAverage}  (${numReviews.toString()})"
            } else {
                holder.ratingBar.rating = 0f
                holder.numRating.text = "0.0  (0)"
            }
        } else {
            fbModel.getAllCommentsByIsbn(currentBook.id).observeForever { allReviewsFuture ->

                var totalRating = 0f
                var numReviews = 0

                if (allReviewsFuture != null) {
                    for (review in allReviewsFuture) {
                        totalRating += review.vote
                        numReviews++
                    }
                }

                val averageRating = totalRating / numReviews
                val formattedAverage =
                    if (averageRating.isNaN()) "0.0" else String.format("%.2f", averageRating)
                if (numReviews > 0) {
                    holder.ratingBar.rating = averageRating
                    holder.numRating.text = "${formattedAverage}  (${numReviews.toString()})"
                } else {
                    holder.ratingBar.rating = 0f
                    holder.numRating.text = "0.0  (0)"
                }
            }

        }

        Glide.with(holder.itemView)
            .load(currentBook?.info?.imageLinks?.thumbnail.toString())
            .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
            .into(holder.cover)
    }

    override fun getItemCount(): Int {
        return (data.size ?: 0)
    }
}

