package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.firebase.FirebaseDB
import com.example.biblioteca_nazionale.model.BookFirebase
import com.example.biblioteca_nazionale.model.Like
import com.example.biblioteca_nazionale.model.UserSettings
import com.example.biblioteca_nazionale.model.Users
import com.google.firebase.firestore.DocumentSnapshot
import java.util.concurrent.CompletableFuture
import com.example.biblioteca_nazionale.model.MiniBook
import com.example.biblioteca_nazionale.model.Review
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar


class FirebaseViewModel : ViewModel() {


    val auth = FirebaseAuth.getInstance()

    val firebase = FirebaseDB()

    private fun convertHashMapToMiniBook(hashMap: HashMap<*, *>): MiniBook {
        val isbn = hashMap["isbn"] as? String ?: ""
        val bookPlace = hashMap["bookPlace"] as? String ?: ""
        val image = hashMap["image"] as? String ?: ""
        val date = hashMap["date"] as? String ?: ""
        val title = hashMap["title"] as? String ?: ""


        return MiniBook(isbn, bookPlace, image, date, title)
    }

    private fun convertHashMapToReview(hashMap: HashMap<*, *>): Review {
        val idComment = hashMap["idComment"] as? String ?: ""
        val reviewText = hashMap["reviewText"] as? String ?: ""
        val reviewTitle = hashMap["reviewTitle"] as? String ?: ""
        val isbn = hashMap["isbn"] as? String ?: ""

        val vote = hashMap["vote"].toString().toFloat()
        val date = hashMap["date"] as? String ?: ""
        val title = hashMap["title"] as? String ?: ""
        val image = hashMap["image"] as? String ?: ""


        return Review(idComment, reviewText, reviewTitle, isbn, vote, date, title, image)
    }

    private fun convertHashMapToLike(hashMap: HashMap<*, *>): Like {
        val idBook = hashMap["bookId"] as? String ?: ""

        return Like(idBook)
    }


    fun getUserInfo(uid: String): MutableLiveData<DocumentSnapshot> {
        return firebase.getAllUserInfoFromUid(uid)
    }

    fun getAllDocument(): MutableLiveData<ArrayList<DocumentSnapshot>> {
        return firebase.getAllUserFromDB()
    }

    fun getEmailLoggedUser(): String = firebase.getCurrentEmail().toString()

    fun getUidLoggedUser(): String = firebase.getCurrentUid().toString()

    fun getCurrentUser(): CompletableFuture<Users> {
        val futureResult = CompletableFuture<Users>()

        this.getUserInfo(getUidLoggedUser()).observeForever { documentSnapshot ->
            val data = documentSnapshot
            println(documentSnapshot)
            val impostazioniData = data?.get("userSettings") as? HashMap<*, *>
            val libriPrenotatiData =
                impostazioniData?.get("libriPrenotati") as? ArrayList<HashMap<*, *>>
            val commentiData = impostazioniData?.get("commenti") as? ArrayList<HashMap<*, *>>
            val miPiaceData = impostazioniData?.get("miPiace") as? ArrayList<HashMap<*, *>>
            val uid = data?.get("uid") as? String
            val email = data?.get("email") as? String

            if (uid != null && email != null) {
                val libriPrenotati =
                    libriPrenotatiData?.map { convertHashMapToMiniBook(it) } as ArrayList<MiniBook>?
                val commenti =
                    commentiData?.map { convertHashMapToReview(it) } as ArrayList<Review>?

                val miPiace =
                    miPiaceData?.map { convertHashMapToLike(it) } as ArrayList<Like>?

                val userSettings = commenti?.let { UserSettings(libriPrenotati, it, miPiace) }
                val users = Users(uid, email, userSettings)
                futureResult.complete(users)
            } else {
                futureResult.completeExceptionally(Exception("Missing or null data"))
            }
        }
        return futureResult
    }


    fun getAllUser(): LiveData<ArrayList<Users>> {
        val allUserLiveData = MutableLiveData<ArrayList<Users>>()

        this.getAllDocument().observeForever { allDocument ->
            val allUser = ArrayList<Users>()
            for (document in allDocument) {
                val impostazioniData = document?.get("userSettings") as? HashMap<*, *>
                val libriPrenotatiData =
                    impostazioniData?.get("libriPrenotati") as? ArrayList<HashMap<*, *>>
                val commentiData = impostazioniData?.get("commenti") as? ArrayList<HashMap<*, *>>
                val miPiaceData = impostazioniData?.get("miPiace") as? ArrayList<HashMap<*, *>>
                val uid = document?.get("uid") as? String
                val email = document?.get("email") as? String

                val libriPrenotati =
                    libriPrenotatiData?.map { convertHashMapToMiniBook(it) } as ArrayList<MiniBook>?
                val commenti =
                    commentiData?.map { convertHashMapToReview(it) } as ArrayList<Review>?

                val miPiace =
                    miPiaceData?.map { convertHashMapToLike(it) } as ArrayList<Like>?

                val userSettings = commenti?.let { UserSettings(libriPrenotati, it, miPiace) }
                val tmpUser = Users(uid.toString(), email.toString(), userSettings)
                allUser.add(tmpUser)
            }
            allUserLiveData.value = allUser
        }
        return allUserLiveData
    }

    fun getUserByCommentsOfBooks(isbn: String): LiveData<ArrayList<Users>> {
        val allUserLiveData = MutableLiveData<ArrayList<Users>>()

        this.getAllDocument().observeForever { allDocument ->
            val allUser = ArrayList<Users>()

            for (document in allDocument) {
                val uid = document?.get("uid") as? String
                val email = document?.get("email") as? String
                val impostazioniData = document?.get("userSettings") as? HashMap<*, *>
                val commentiData = impostazioniData?.get("commenti") as? ArrayList<HashMap<*, *>>
                val miPiaceData = impostazioniData?.get("miPiace") as? ArrayList<HashMap<*, *>>

                val commenti =
                    commentiData?.mapNotNull { convertHashMapToReview(it) } as ArrayList<Review>?

                commenti?.let {
                    val filteredComments = it.filter { comment -> comment.isbn == isbn }

                    if (filteredComments.isNotEmpty()) {
                        val libriPrenotatiData =
                            impostazioniData?.get("libriPrenotati") as? ArrayList<HashMap<*, *>>
                        val libriPrenotati =
                            libriPrenotatiData?.map { convertHashMapToMiniBook(it) } as ArrayList<MiniBook>?
                        val miPiace =
                            miPiaceData?.map { convertHashMapToLike(it) } as ArrayList<Like>?

                        val userSettings = UserSettings(
                            libriPrenotati,
                            filteredComments as ArrayList<Review>,
                            miPiace
                        )
                        val tmpUser = Users(uid.toString(), email.toString(), userSettings)
                        allUser.add(tmpUser)
                    }
                }
            }
            allUserLiveData.value = allUser
        }

        return allUserLiveData
    }

    fun getUsersCommentByIsbn(isbn: String): LiveData<Review?> {
        val uid = firebase.getCurrentUid()
        val review = MutableLiveData<Review?>()

        if (uid != null) {
            val currentUser = this.getCurrentUser()
            currentUser.thenAccept { user ->
                if (user.userSettings != null) {
                    for (commento in user.userSettings!!.commenti!!) {
                        if (commento.isbn == isbn) {
                            review.postValue(commento)
                            return@thenAccept
                        }
                    }
                } else {
                    review.postValue(null)
                }
                review.postValue(null) // Nessuna recensione trovata, passa null
            }
        } else {
            review.postValue(null) // Nessun utente corrente, passa null
        }
        return review
    }

    fun getUsersComments(): MutableLiveData<ArrayList<Review>?> {
        val uid = firebase.getCurrentUid()
        val reviews = MutableLiveData<ArrayList<Review>?>()

        if (uid != null) {
            val currentUser = getCurrentUser()
            currentUser.thenAccept { user ->
                if (user?.userSettings != null) {
                    reviews.postValue(user.userSettings!!.commenti)
                } else {
                    reviews.postValue(null)
                }
            }.exceptionally { error ->
                reviews.postValue(null) // Gestione dell'errore, passa null
                null
            }
        } else {
            reviews.postValue(null) // Nessun utente corrente, passa null
        }
        return reviews
    }


    fun getAllCommentsByIsbn(isbn: String): LiveData<ArrayList<Review>> {
        val allCommentsLiveData = MutableLiveData<ArrayList<Review>>()

        this.getAllDocument().observeForever { allDocument ->
            val allComments = ArrayList<Review>()
            for (document in allDocument) {
                val impostazioniData = document?.get("userSettings") as? HashMap<*, *>
                val commentiData = impostazioniData?.get("commenti") as? ArrayList<HashMap<*, *>>

                val commenti =
                    commentiData?.map { convertHashMapToReview(it) } as ArrayList<Review>?

                // Aggiungi i commenti che hanno il valore di ISBN desiderato
                commenti?.let {
                    val filteredComments = it.filter { comment -> comment.isbn == isbn }
                    allComments.addAll(filteredComments)
                }
            }
            allCommentsLiveData.value = allComments
        }
        return allCommentsLiveData
    }


    fun getBookInfoResponseFromDB(idLibro: String): MutableLiveData<DocumentSnapshot> {
        return firebase.getAllBookInfoFromId(idLibro)
    }

    fun getBookInfo(idLibro: String): BookFirebase {
        var allData = this.getBookInfoResponseFromDB("ID_LIBRO")
        val data = allData.value?.data
        val allComment =
            data?.get("Commenti") as? HashMap<String, HashMap<String, HashMap<String, String>>>
        val allRankingStar = data?.get("Stelle recensioni") as? HashMap<String, String>

        return BookFirebase(allComment!!, allRankingStar!!)
    }

    fun addNewMiPiace(bookId: String): CompletableFuture<Boolean> {
        val uid = firebase.getCurrentUid()
        val result = CompletableFuture<Boolean>()
        val currentUser = this.getCurrentUser()
        currentUser.thenAccept { utente ->
            if (utente.userSettings == null) {
                utente.userSettings = UserSettings(ArrayList(), ArrayList(), ArrayList())
            }
            utente.userSettings?.addNewLike(bookId)
            firebase.updateBookPrenoted(utente)
                .thenApply {
                    result.complete(true)
                    true
                }
        }.exceptionally { throwable ->
            result.complete(false)
            null
        }
        return result
    }

    fun deleteMiPiace(bookId: String): CompletableFuture<Boolean> {
        val uid = firebase.getCurrentUid()
        val result = CompletableFuture<Boolean>()
        val currentUser = this.getCurrentUser()
        currentUser.thenAccept { utente ->
            if (utente.userSettings == null) {
                utente.userSettings = UserSettings(ArrayList(), ArrayList(), ArrayList())
            }
            utente.userSettings?.deleteLike(bookId)
            firebase.updateBookPrenoted(utente)
                .thenApply {
                    result.complete(true)
                    true
                }
        }.exceptionally { throwable ->
            result.complete(false)
            null
        }
        return result
    }


    fun addNewBookBooked(
        idLibro: String,
        isbn: String,
        placeBooked: String,
        image: String,
        title: String,
        dateOfBooked: String,
    ): CompletableFuture<Boolean> {
        val uid = firebase.getCurrentUid()
        val result = CompletableFuture<Boolean>()
        val currentUser = this.getCurrentUser()
        currentUser.thenAccept { utente ->
            if (utente.userSettings == null) {
                utente.userSettings = UserSettings(ArrayList(), ArrayList(), ArrayList())
            }
            utente.userSettings?.addNewBook(idLibro, isbn, placeBooked, image, title, dateOfBooked)
            firebase.updateBookPrenoted(utente)
                .thenApply {
                    result.complete(true)
                    true
                }
            println(utente.userSettings?.libriPrenotati?.get(utente.userSettings?.libriPrenotati!!.size - 1))
        }.exceptionally { throwable ->
            result.complete(false)
            null
        }
        return result
    }

    fun getExpirationDate(idBook: String, place: String): CompletableFuture<String> {
        val futureExpiringDate = CompletableFuture<String>()
        val firebase = FirebaseDB()
        this.getCurrentUser().thenAccept { user ->
            println(user)
            var foundMatchingBook = false

            for (libroPrenotato in user.userSettings?.libriPrenotati!!) {
                Log.d("idBook-user ", libroPrenotato.isbn)
                Log.d("place-user ", libroPrenotato.bookPlace)
                Log.d("date: ", libroPrenotato.date)
                Log.d(
                    "Condizion nel if ",
                    ((libroPrenotato.isbn == idBook) && (libroPrenotato.bookPlace == place)).toString()
                )
                if ((libroPrenotato.isbn == idBook) && (libroPrenotato.bookPlace == place)) {
                    val inputFormat = "dd/MM/yyyy"
                    val outputFormat = "dd/MM/yyyy"

                    val inputFormatter = SimpleDateFormat(inputFormat)
                    val outputFormatter = SimpleDateFormat(outputFormat)

                    val date = inputFormatter.parse(libroPrenotato.date)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.add(Calendar.DAY_OF_MONTH, 14)
                    val datePlus14Days = calendar.time
                    val datePlus14DaysString = outputFormatter.format(datePlus14Days)

                    futureExpiringDate.complete(datePlus14DaysString)

                    break

                }
            }

            futureExpiringDate.complete("")
        }
        return futureExpiringDate
    }
// TRUE -> Libro gia presente e prenotato   ||||||    FALSE -> Libro non presente e non prenotato

    fun bookIsBooked(id: String, place: String): CompletableFuture<Boolean> {
        val uid = getUidLoggedUser()
        val currentUser = this.getCurrentUser()

        val futureIsBooked = CompletableFuture<Boolean>()

        currentUser.thenAccept { user ->
            var isBooked = false
            for (libro in user.userSettings?.libriPrenotati!!) {
                if (libro.isbn == id && libro.bookPlace == place) {
                    isBooked = true
                    break
                }
            }
            futureIsBooked.complete(isBooked)
        }.exceptionally { throwable ->
            futureIsBooked.complete(false)
            null
        }

        return futureIsBooked
    }

    fun getMiPiace(bookId: String): CompletableFuture<ArrayList<Like>> {

        val result = CompletableFuture<ArrayList<Like>>()

        var likes = ArrayList<Like>()

        getAllUser().observeForever { users ->
            for (user in users) {
                if (user.userSettings?.miPiace?.isNotEmpty() == true) {
                    for (like in user.userSettings?.miPiace!!) {
                        if (like.bookId.equals(bookId)) {
                            likes.add(like)
                        }
                    }
                }
            }
            result.complete(likes)
        }
        return result
    }

    fun getUserMiPiace(bookId: String): CompletableFuture<ArrayList<Like>> {

        val result = CompletableFuture<ArrayList<Like>>()

        var likes = ArrayList<Like>()

        getCurrentUser().thenAccept { user ->
            if (user.userSettings?.miPiace != null) {
                for (like in user.userSettings?.miPiace!!) {
                    if (like.bookId.equals(bookId)) {
                        likes.add(like)
                    }
                }
            }
            result.complete(likes)
        }
        return result
    }


    fun removeBookBooked(idLibro: String, onSuccess: () -> Unit, onError: () -> Unit) {

        val uid = getUidLoggedUser()
        val currentUser = this.getCurrentUser()

        currentUser.thenAccept { user ->
            user.userSettings?.removeBook(idLibro)
            firebase.updateBookPrenoted(user)
            onSuccess() // Richiama il callback in caso di successo
        }.exceptionally { throwable ->
            onError() // Richiama il callback in caso di errore
            null
        }
    }

    fun removeComment(idComment: String): CompletableFuture<Void> {
        val uid = getUidLoggedUser()
        val currentUser = this.getCurrentUser()

        return currentUser.thenCompose { user ->
            user.userSettings?.removeComment(idComment)
            firebase.updateBookPrenoted(user)
        }.thenApply { null }
    }


    fun addNewCommentUserSide(
        reviewText: String,
        reviewTitle: String,
        isbn: String,
        vote: Float,
        idComment: String? = null,
        title: String,
        image: String
    ): CompletableFuture<Void> {

        val result = CompletableFuture<Void>()

        val currentUser =
            this.getCurrentUser()
        currentUser.thenAccept { user ->
            if (user.userSettings == null) {
                user.userSettings = UserSettings(ArrayList(), ArrayList(), ArrayList())
            }
            user.userSettings?.addNewComment(
                reviewText,
                reviewTitle,
                isbn,
                vote,
                idComment,
                title,
                image
            )
            firebase.updateBookPrenoted(user).thenAccept {
                result.complete(null)
            }
        }.exceptionally { throwable ->
            null
        }
        return result
    }

    // Sommo alle date di prenotazione i 14 giorni
    fun getAllDate(): CompletableFuture<List<MiniBook>> {
        val currentUser = this.getCurrentUser()
        val today = LocalDate.now()
        val result = CompletableFuture<List<MiniBook>>()

        currentUser.thenAccept { user ->
            val userMiniList = user.userSettings?.libriPrenotati
            if (userMiniList != null) {
                val miniList: MutableList<MiniBook> = mutableListOf()

                val inputFormat = "dd/MM/yyyy"
                val outputFormat = "dd/MM/yyyy"
                val inputFormatter = SimpleDateFormat(inputFormat)
                val outputFormatter = SimpleDateFormat(outputFormat)

                for (libro in userMiniList) {
                    val date = inputFormatter.parse(libro.date)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.add(Calendar.DAY_OF_MONTH, 14)
                    val datePlus14Days = calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val today = LocalDate.now()
                    //val prova= LocalDate.of(2023, 7, 28)
                    if (ChronoUnit.DAYS.between(today, datePlus14Days) <= 2) {
                        miniList += libro
                    }
                }
                result.complete(miniList)
            }
        }
        return result
    }

    fun removeCommentUserSide(idComment: String): CompletableFuture<Void> {
        val result = CompletableFuture<Void>()
        val currentUser =
            this.getCurrentUser()
        currentUser.thenAccept { user ->
            user.userSettings?.removeComment(idComment)
            println(user.userSettings)
            firebase.updateBookPrenoted(user).thenAccept {
                result.complete(null)
            }
        }.exceptionally { throwable ->
            null
        }
        return result
    }


    fun deleteAccount(): CompletableFuture<Void> {
        val result = CompletableFuture<Void>()

        println("yolo")

        this.getCurrentUser().thenAccept { user ->
            println(user)
            this.firebase.deleteUser(user.UID).thenAccept {
                result.complete(null)
            }
        }.exceptionally {
            null
        }
        return result
    }

}

