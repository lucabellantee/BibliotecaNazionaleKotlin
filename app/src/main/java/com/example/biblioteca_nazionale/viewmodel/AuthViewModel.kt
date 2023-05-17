import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.interface_.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.registerUser(email, password)
            // Gestisci il risultato qui (ad esempio, emetti un LiveData, un StateFlow, ecc.)
        }
    }
}