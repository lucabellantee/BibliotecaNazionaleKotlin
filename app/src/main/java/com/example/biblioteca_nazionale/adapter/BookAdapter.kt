import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.model.MiniBook

class BookAdapter(private val books: List<MiniBook>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private var onBookClickListener: OnBookClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
        holder.itemView.setOnClickListener {
            onBookClickListener?.onBookClick(position)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    fun setOnBookClickListener(listener: OnBookClickListener) {
        onBookClickListener = listener
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.bookTitleItem)
        private val imageImageView: ImageView = itemView.findViewById(R.id.bookImageItem)
        private val dateTextView: TextView = itemView.findViewById(R.id.bookDate)
        private val libraryTextView: TextView = itemView.findViewById(R.id.library)

        fun bind(book: MiniBook) {
            titleTextView.text = "Id:${book.isbn}"
            dateTextView.text = "Expiration date:${book.date}"
            libraryTextView.text = book.bookPlace
            Glide.with(itemView)
                .load(book.image)
                .apply(RequestOptions().centerCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageImageView)
        }
    }

    interface OnBookClickListener {
        fun onBookClick(position: Int)
    }
}
