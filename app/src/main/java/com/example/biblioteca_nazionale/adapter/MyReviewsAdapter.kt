package com.example.biblioteca_nazionale.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.fragments.TemporaryReview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyReviewsAdapter(private val reviews: ArrayList<TemporaryReview>) :
    RecyclerView.Adapter<MyReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_my_reviews, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)

        if (position == reviews.size-1) {
            holder.itemView.findViewById<View>(R.id.view).visibility = View.GONE
        } else {
            holder.itemView.findViewById<View>(R.id.view).visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Dichiarazioni degli elementi dell'interfaccia utente
        private val ratingReview = itemView.findViewById<RatingBar>(R.id.ratingReview2)
        private val textReviewUtente = itemView.findViewById<TextView>(R.id.textReviewUtente)
        private val textReviewDate = itemView.findViewById<TextView>(R.id.textReviewDate)
        private val textTitleReview = itemView.findViewById<TextView>(R.id.textTitleReview1)
        private val textReview = itemView.findViewById<TextView>(R.id.textReview1)

        fun bind(review: TemporaryReview) {
            // Collegamento dei dati agli elementi dell'interfaccia utente
            ratingReview.rating = review.vote
            textReviewUtente.text = "Valutazione di ${review.email}:"

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            val date: Date = inputFormat.parse(review.date)
            val outputDateString: String = outputFormat.format(date)

            textReviewDate.text = outputDateString
            textTitleReview.text = review.reviewTitle
            textReview.text = review.reviewText
        }
    }

}
