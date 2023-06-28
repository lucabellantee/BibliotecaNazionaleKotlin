import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R

class NotificationAdapter(private val context: Context, private val notificationList: List<Pair<String, String>>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        println(notificationList)
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.bookTitleItem)
        private val textTextView: TextView = itemView.findViewById(R.id.bookDate)
        private val logoImageView: ImageView = itemView.findViewById(R.id.notificationsImageItem)

        fun bind(notification: Pair<String, String>) {
            val title = notification.first
            val text = notification.second

            titleTextView.text = title
            textTextView.text = text
            logoImageView.setImageResource(R.drawable.logo_welcome) // Imposta l'immagine del logo
        }
    }
}
