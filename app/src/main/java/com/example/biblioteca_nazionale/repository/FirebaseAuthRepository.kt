import com.example.biblioteca_nazionale.interface_.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }
}