import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.Review

class ReviewsAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(review: Review) {
            // Popolate gli elementi dell'item_review.xml con i dati della recensione
            // Utilizzate itemView.findViewById per ottenere le reference agli elementi
            // E assegnate i valori corrispondenti ai campi della recensione
            // Esempio: val textViewTitle = itemView.findViewById<TextView>(R.id.textTitleReview1)
            //         textViewTitle.text = review.reviewTitle
        }
    }
}
