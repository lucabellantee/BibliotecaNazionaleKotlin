package com.example.biblioteca_nazionale.adapter

import android.text.TextUtils
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

class ReviewsAdapter(private val reviews: ArrayList<TemporaryReview>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)

        val viewSeparator = holder.itemView.findViewById<View>(R.id.view)
        viewSeparator.visibility = if (position == reviews.size - 1) View.GONE else View.VISIBLE
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ratingReview = itemView.findViewById<RatingBar>(R.id.ratingReview2)
        private val textReviewUtente = itemView.findViewById<TextView>(R.id.textReviewUtente)
        private val textReviewDate = itemView.findViewById<TextView>(R.id.textReviewDate)
        private val textTitleReview = itemView.findViewById<TextView>(R.id.textTitleReview1)
        private val textReview = itemView.findViewById<TextView>(R.id.textReview1)
        private val readMoreText = itemView.findViewById<TextView>(R.id.textChangeReview)

        private var isExpanded = false

        init {
            readMoreText.setOnClickListener {
                isExpanded = !isExpanded
                updateTextReviewVisibility()
                updateTextChangeReviewText()
            }
        }

        fun bind(review: TemporaryReview) {
            ratingReview.rating = review.vote
            textReviewUtente.text = "Valutation of ${review.email}:"

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            val date: Date = inputFormat.parse(review.date)
            val outputDateString: String = outputFormat.format(date)

            textReviewDate.text = outputDateString
            textTitleReview.text = review.reviewTitle
            textReview.text = review.reviewText

            println(textReview.lineCount)

            textReview.post {
                if (textReview.lineCount < 5 && textTitleReview.lineCount < 2) {
                    readMoreText.visibility = View.GONE
                } else {
                    println(textReview.lineCount)
                    if (textReview.lineCount > 5) {
                        textReview.maxLines = 5
                        textReview.ellipsize = TextUtils.TruncateAt.END
                    }
                    if (textTitleReview.lineCount > 2) {
                        textTitleReview.maxLines = 2
                        textTitleReview.ellipsize = TextUtils.TruncateAt.END
                    }
                    readMoreText.visibility = View.VISIBLE
                }

                updateTextReviewVisibility()
                updateTextChangeReviewText()
            }
        }

        private fun updateTextReviewVisibility() {
            if (isExpanded) {
                textReview.maxLines = Integer.MAX_VALUE
                textTitleReview.maxLines = Integer.MAX_VALUE
                textReview.ellipsize = null
                textTitleReview.ellipsize = null
            } else {
                if (textReview.lineCount > 5) {
                    textReview.maxLines = 5
                    textReview.ellipsize = TextUtils.TruncateAt.END
                }
                if (textTitleReview.lineCount > 2) {
                    textTitleReview.maxLines = 2
                    textTitleReview.ellipsize = TextUtils.TruncateAt.END
                }
            }
        }

        private fun updateTextChangeReviewText() {
            val context = itemView.context
            val textResId = if (isExpanded) R.string.read_less else R.string.read_more
            readMoreText.text = context.getString(textResId)
        }
    }
}
