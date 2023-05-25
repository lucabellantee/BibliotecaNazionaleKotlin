import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentBookInfoBinding
import com.example.biblioteca_nazionale.model.Book
import java.io.Serializable

class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    lateinit var binding: FragmentBookInfoBinding
    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_info, container, false)
    }*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookInfoBinding.bind(view)

        val selectedBook = arguments?.getSerializable("selectedBook")
        // Utilizza il libro selezionato come desideri
        selectedBook?.let {
            //binding.textViewBookName.text = selectedBook[""]
        }
    }
}
