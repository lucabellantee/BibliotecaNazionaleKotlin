package com.example.biblioteca_nazionale.adapter

import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
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
import com.example.biblioteca_nazionale.model.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyReviewsAdapter(private val reviews: ArrayList<Review>) :
    RecyclerView.Adapter<MyReviewsAdapter.ReviewViewHolder>() {


    private lateinit var reviewListner: MyReviewsAdapter.OnReviewClickListener

    interface OnReviewClickListener {
        fun onReviewClick(position: Int)
    }

    fun setOnReviewClickListener(listner: MyReviewsAdapter.OnReviewClickListener) {
        reviewListner = listner
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_my_review, parent, false)
        return ReviewViewHolder(view, reviewListner)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)

        if (position == reviews.size - 1) {
            holder.itemView.findViewById<View>(R.id.view).visibility = View.GONE
        } else {
            holder.itemView.findViewById<View>(R.id.view).visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class ReviewViewHolder(itemView: View, listner: OnReviewClickListener) :
        RecyclerView.ViewHolder(itemView) {
        // Dichiarazioni degli elementi dell'interfaccia utente
        private val ratingReview = itemView.findViewById<RatingBar>(R.id.ratingReview2)
        private val textReviewDate = itemView.findViewById<TextView>(R.id.textReviewDate)
        private val textTitleReview = itemView.findViewById<TextView>(R.id.textTitleReview1)
        private val textReview = itemView.findViewById<TextView>(R.id.textReview1)
        private val bookCover = itemView.findViewById<ImageView>(R.id.imageViewCover)
        private val changeReview = itemView.findViewById<TextView>(R.id.textChangeReview)
        private val textChangeReview = itemView.findViewById<TextView>(R.id.textChangeReview)

        //private var isExpanded = false

        private var isExpanded = false


        init {
            changeReview.setOnClickListener {
                listner.onReviewClick(adapterPosition)
            }

            textChangeReview.setOnClickListener {
                isExpanded = !isExpanded
                updateTextReviewVisibility()
                updateTextChangeReviewText()
            }
        }



            fun bind(review: Review) {
                // Collegamento dei dati agli elementi dell'interfaccia utente
                ratingReview.rating = review.vote

                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                val date: Date = inputFormat.parse(review.date)
                val outputDateString: String = outputFormat.format(date)

                textReviewDate.text = "${review.title}   ${outputDateString}"
                textTitleReview.text = review.reviewTitle
                textReview.text = review.reviewText

                Glide.with(bookCover)
                    .load(review.image)
                    .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                    .into(bookCover)

                textReview.post {
                    if (textReview.lineCount < 5 && textTitleReview.lineCount < 2) {
                        textChangeReview.visibility = View.GONE
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
                        textChangeReview.visibility = View.VISIBLE
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
            textChangeReview.text = context.getString(textResId)
        }
    }
}
