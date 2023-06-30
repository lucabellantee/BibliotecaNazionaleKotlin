package com.example.biblioteca_nazionale.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.ReviewsAdapter
import com.example.biblioteca_nazionale.cache.GeocodingCache
import com.example.biblioteca_nazionale.databinding.FragmentBookInfoBinding
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.example.biblioteca_nazionale.viewmodel.RequestViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.model.GeocodingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    lateinit var binding: FragmentBookInfoBinding

    private val modelRequest: RequestViewModel = RequestViewModel()
    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()
    val db = Firebase.firestore

    private var isExpandedReview = false
    private var isExpandedDescription = false


    private lateinit var progressBar: ProgressBar

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val locationPermissionRequestCode = 1



        ActivityCompat.requestPermissions(
            requireActivity(), locationPermissions, locationPermissionRequestCode
        )

        binding = FragmentBookInfoBinding.bind(view)

        binding.recyclerViewReviews.setOnTouchListener { _, event ->
            // Blocca l'elaborazione dell'evento touch sulla RecyclerView
            true
        }

        binding.myReview.visibility = View.GONE

        binding.scrollViewInfo.visibility = View.GONE

        progressBar = binding.progressBar

        progressBar.visibility = View.VISIBLE

        val buttonReview = binding.buttonScriviRecensione

        manageToolbar()

        val book = arguments?.getParcelable<Book>("book")

        book?.let {

            manageRecyclerView(it)

            modelRequest.fetchDataBook(it)

            binding.textViewBookName.text = it.info?.title ?: ""
            binding.textViewAutore.text = it.info?.authors?.toString() ?: ""

            val description = it.info?.description
            if (description.isNullOrEmpty()) {
                binding.textViewDescription.text = "Descrizione non disponibile"
                binding.textMoreDescription.visibility = View.GONE
            } else binding.textViewDescription.text = description

            Glide.with(requireContext()).load(book.info.imageLinks?.thumbnail.toString())
                .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                .into(binding.imageViewBook)


            val mapView: MapView = binding.mapView
            mapView.onCreate(savedInstanceState)

            mapView.getMapAsync { googleMap ->

                val clusterManager = ClusterManager<MyItem>(requireContext(), googleMap)

                googleMap.setOnCameraIdleListener(clusterManager)
                googleMap.setOnMarkerClickListener(clusterManager)

                // Personalizzazione e visualizzazione della mappa
                googleMap.uiSettings.isZoomControlsEnabled = true // Abilita i controlli di zoom
                googleMap.uiSettings.isMyLocationButtonEnabled =
                    true // Abilita il pulsante "La mia posizione"
                googleMap.uiSettings.isScrollGesturesEnabled =
                    true // Abilita il gesto di scorrimento sulla mappa
                googleMap.uiSettings.isRotateGesturesEnabled = true
                googleMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true

                googleMap.setMapStyle(context?.let {
                    MapStyleOptions.loadRawResourceStyle(
                        it, R.raw.map_style
                    )
                })

                val startLatLng = LatLng(
                    41.87194, 12.56738
                )

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, 4.4f)
                googleMap.moveCamera(cameraUpdate)

                val geoApiContext = GeoApiContext.Builder()
                    .apiKey("AIzaSyCtTj2ohggFHtNX2asYNXL1kj31pO8wO_Y") // Replace with your actual API key
                    .build()

                modelRequest.getLibraries().observe(viewLifecycleOwner) { libraries ->
                    libraries?.let { libraryList ->

                        progressBar.visibility = View.GONE
                        binding.scrollViewInfo.visibility = View.VISIBLE
                        buttonReview.visibility = View.GONE

                        manageRatingBars(book)

                        manageDescription()

                        lifecycleScope.launch(Dispatchers.Main) {

                            val librariesNames = libraryList.flatMap { library ->
                                library.shelfmarks.mapNotNull { it.shelfmark }
                            }

                            val markerList = mutableListOf<MyItem>()
                            var counter = 0

                            withContext(Dispatchers.IO) {

                                val uniqueLibraryNames = librariesNames.distinct()

                                for (libraryName in uniqueLibraryNames) {
                                    val cacheKey = GeocodingCache.getCacheKey(libraryName)
                                    val cachedResult = GeocodingCache.getResult(cacheKey)
                                    var geocodingResult: Array<GeocodingResult>

                                    if (cachedResult != null) {

                                        geocodingResult = arrayOf(cachedResult)

                                    } else {
                                        geocodingResult =
                                            GeocodingApi.geocode(geoApiContext, libraryName).await()
                                        if (geocodingResult.isNotEmpty()) {
                                            GeocodingCache.putResult(cacheKey, geocodingResult[0])
                                        }
                                    }

                                    if (geocodingResult.isNotEmpty()) {
                                        val location = geocodingResult[0].geometry.location
                                        val libraryLatLng = LatLng(location.lat, location.lng)

                                        withContext(Dispatchers.Main) {
                                            val markerOptions = MyItem(
                                                libraryLatLng, libraryName, "Biblioteca"
                                            )  // Descrizione opzionale
                                            clusterManager.addItem(markerOptions)
                                            clusterManager.cluster()

                                            markerList.add(markerOptions)

                                            counter++

                                            if (markerList.isNotEmpty()) {
                                                if (markerList.size == 1) {
                                                    setDefaultLibrary(markerList[0], book)
                                                }
                                            }
                                            clusterManager.setOnClusterItemClickListener { marker ->
                                                setDefaultLibraryCamera(marker, book, googleMap)
                                                true
                                            }
                                        }
                                    }
                                }
                            }
                            setDefaultCamera(markerList, book, googleMap)
                        }
                    }
                }
            }
        }
    }

    private fun setDefaultLibraryCamera(marker: MyItem, book: Book, googleMap: GoogleMap) {

        val startLatLng = LatLng(
            marker.position.latitude, marker.position.longitude
        )

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            startLatLng, 12f
        )
        googleMap.animateCamera(cameraUpdate)

        setDefaultLibrary(marker, book)
    }

    private fun setDefaultLibrary(marker: MyItem, book: Book) {
        Log.d("prima: ", marker.title)
        binding.textViewNomeBiblioteca.text =
            marker.title
        expirationDate(
            book.id,
            marker.title.toString()
        )  //todo luca vedere qui
        binding.buttonPrenota.setOnClickListener {
            var nomeBiblioteca =
                binding.textViewNomeBiblioteca.text.toString()
            fbViewModel.bookIsBooked(
                book.id,
                nomeBiblioteca
            ).thenAccept { isBooked ->
                if (isBooked == true) {
                    Toast.makeText(
                        requireContext(),
                        "Book already reserved for the same library",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("dentro IsBooked = true : ", marker.title)
                    expirationDate(
                        book.id.toString(),
                        marker.title.toString()
                    )
                } else if (isBooked == false) {
                    println(book.info)

                    book?.info?.title?.let { it1 ->
                        fbViewModel.addNewBookBooked(
                            book.id,
                            book.id,
                            binding.textViewNomeBiblioteca.text.toString(),
                            book?.info?.imageLinks?.thumbnail.toString(),
                            it1
                        ).thenAccept { result ->
                            if (result) {
                                Toast.makeText(
                                    requireContext(),
                                    "Your book has booked succesfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                expirationDate(
                                    book.id,
                                    marker.title.toString()
                                )
                            }
                        }
                    }

                }
            }

            /*binding.buttonPrenota.isEnabled =
                false */
        }

    }

    private fun expirationDate(bookId: String, nomeBiblioteca: String) {
        fbViewModel.getExpirationDate(
            bookId,
            nomeBiblioteca
        ).thenAccept { expirationDate ->
            println(expirationDate)
            if (!(expirationDate.equals(""))) {
                showViewWithAnimation(binding.textViewDataRiconsegna)
                binding.textViewDataRiconsegna.text =
                    "To be returned " + expirationDate.toString()
            } else hideViewWithAnimation(binding.textViewDataRiconsegna)
        }
    }

    private suspend fun setDefaultCamera(
        markerList: MutableList<MyItem>,
        book: Book,
        googleMap: GoogleMap
    ) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(
                requireContext()
            )

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            setDefaultLibraryCamera(markerList[0], book, googleMap)
        } else {
            withContext(Dispatchers.IO) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        val nearestMarker = findNearestMarker(
                            latitude, longitude, markerList
                        )

                        if (nearestMarker != null) {
                            setDefaultLibraryCamera(
                                nearestMarker, book, googleMap
                            )
                        } else {
                            noLibraryFound()
                        }
                    } else {
                        if (markerList.isNotEmpty()) {
                            setDefaultLibraryCamera(
                                markerList[0], book, googleMap
                            )
                        } else {
                            noLibraryFound()
                        }
                    }
                }
            }
        }

    }

    private fun noLibraryFound() {
        binding.textViewNomeBiblioteca.text = "No library found"

        binding.textViewDataRiconsegna.visibility = View.GONE

        binding.buttonPrenota.isEnabled = false

    }

    private fun findNearestMarker(a: Double, b: Double, markerList: MutableList<MyItem>): MyItem? {
        val targetLocation = Location("")
        targetLocation.latitude = a
        targetLocation.longitude = b

        var nearestMarker: MyItem? = null
        var shortestDistance = Float.MAX_VALUE

        for (marker in markerList) {
            val markerLocation = Location("")
            markerLocation.latitude = marker.position.latitude
            markerLocation.longitude = marker.position.longitude

            val distance = targetLocation.distanceTo(markerLocation)
            if (distance < shortestDistance) {
                shortestDistance = distance
                nearestMarker = marker
            }
        }
        return nearestMarker
    }

    private fun manageToolbar() {
        val toolbar = binding.toolbar

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbar.setNavigationOnClickListener {
            val action = BookInfoFragmentDirections.actionBookInfoFragmentToBookListFragment()
            findNavController().navigate(action)
        }

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val action = BookInfoFragmentDirections.actionBookInfoFragmentToBookListFragment(
                    focusSearchView = true
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun manageRatingBars(book: Book) {

        val ratingDetail = binding.detailReview

        val textratingBarStella1 = binding.textViewInforatingbar1
        val progressBar1: ProgressBar = binding.progressBar1

        val textratingBarStella2 = binding.textViewInforatingbar2
        val progressBar2: ProgressBar = binding.progressBar2

        val textratingBarStella3 = binding.textViewInforatingbar3
        val progressBar3: ProgressBar = binding.progressBar3

        val textratingBarStella4 = binding.textViewInforatingbar4
        val progressBar4: ProgressBar = binding.progressBar4

        val textratingBarStella5 = binding.textViewInforatingbar5
        val progressBar5: ProgressBar = binding.progressBar5

        val ratingBarIndicator: RatingBar = binding.ratingBarIndicator
        val textRatingIndicator = binding.textRatingIndicator

        fbViewModel.getAllCommentsByIsbn(book.id).observe(viewLifecycleOwner) { comments ->

            val numReviews = comments.size
            val numReviews5 = comments.count { it.vote == 5.0f }
            val numReviews4 = comments.count { it.vote == 4.0f }
            val numReviews3 = comments.count { it.vote == 3.0f }
            val numReviews2 = comments.count { it.vote == 2.0f }
            val numReviews1 = comments.count { it.vote == 1.0f }

            val reviewsAverage = comments.map { it.vote }.average()
            val formattedAverage =
                if (reviewsAverage.isNaN()) "0.0" else String.format("%.2f", reviewsAverage)
            val perc5Star = (numReviews5.toFloat() / numReviews.toFloat()) * 100
            val perc4Star = (numReviews4.toFloat() / numReviews.toFloat()) * 100
            val perc3Star = (numReviews3.toFloat() / numReviews.toFloat()) * 100
            val perc2Star = (numReviews2.toFloat() / numReviews.toFloat()) * 100
            val perc1Star = (numReviews1.toFloat() / numReviews.toFloat()) * 100

            ratingDetail.text = "${formattedAverage} over 5.0  ${numReviews} reviews"

            textRatingIndicator.text = "${formattedAverage}"
            ratingBarIndicator.rating =
                if (reviewsAverage.isNaN()) 0.0f else reviewsAverage.toFloat()


            textratingBarStella1.text =
                if (perc1Star.isNaN()) "0%" else "${perc1Star.roundToInt()}%"
            progressBar1.progress = if (perc1Star.isNaN()) 0 else perc1Star.roundToInt()

            textratingBarStella2.text =
                if (perc2Star.isNaN()) "0%" else "${perc2Star.roundToInt()}%"
            progressBar2.progress = if (perc2Star.isNaN()) 0 else perc2Star.roundToInt()

            textratingBarStella3.text =
                if (perc3Star.isNaN()) "0%" else "${perc3Star.roundToInt()}%"
            progressBar3.progress = if (perc3Star.isNaN()) 0 else perc3Star.roundToInt()

            textratingBarStella4.text =
                if (perc4Star.isNaN()) "0%" else "${perc4Star.roundToInt()}%"
            progressBar4.progress = if (perc4Star.isNaN()) 0 else perc4Star.roundToInt()

            textratingBarStella5.text =
                if (perc5Star.isNaN()) "0%" else "${perc5Star.roundToInt()}%"
            progressBar5.progress = if (perc5Star.isNaN()) 0 else perc5Star.roundToInt()

        }

        val ratingBar: RatingBar = binding.ratingBarInserimento

        val buttonReview = binding.buttonScriviRecensione

        fbViewModel.getUsersCommentByIsbn(book.id).observe(viewLifecycleOwner) { review ->

            if (review != null) {
                binding.textViewVote.text = requireContext().getText(R.string.your_vote)
                ratingBar.rating = review.vote
                ratingBar.setIsIndicator(true)
                binding.myReview.visibility = View.VISIBLE

                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                val date: Date = inputFormat.parse(review.date)
                val outputDateString: String = outputFormat.format(date)

                binding.textReviewDate.text = outputDateString
                binding.textTitleReview1.text = review.reviewTitle
                binding.textReview1.text = review.reviewText

                manageMyReview()

                binding.buttonModificaRecensione.setOnClickListener {
                    val bundle = Bundle().apply {
                        putFloat("reviewVote", review.vote)
                        putParcelable("book", book)
                        putParcelable("review", review)
                    }

                    findNavController().navigate(
                        R.id.action_bookInfoFragment_to_writeReviewFragment, bundle
                    )
                }
            } else {
                if (ratingBar.rating != (0).toFloat()) {
                    showViewWithAnimation(buttonReview)
                }
                ratingBar.setOnRatingBarChangeListener { _, rating, _ ->

                    println(buttonReview.visibility != View.VISIBLE)


                    val ratingValue = rating.toFloat()

                    if (rating != (0).toFloat()) {
                        if (buttonReview.visibility != View.VISIBLE) {
                            showViewWithAnimation(buttonReview)
                        }

                        buttonReview.setOnClickListener {

                            val bundle = Bundle().apply {
                                putFloat("reviewVote", ratingValue)
                                putParcelable("book", book)
                                putParcelable("review", null)
                            }

                            findNavController().navigate(
                                R.id.action_bookInfoFragment_to_writeReviewFragment, bundle
                            )
                        }
                    } else {
                        hideViewWithAnimation(buttonReview)
                        buttonReview.setOnClickListener {}
                    }

                    Toast.makeText(
                        requireContext(), "Your vote: $ratingValue", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showViewWithAnimation(button: View) {
        button.alpha = 0F
        button.visibility = View.VISIBLE

        button.animate().alpha(1F).setDuration(500).start()
    }

    private fun hideViewWithAnimation(button: View) {
        button.animate().alpha(0F).setDuration(500).withEndAction { button.visibility = View.GONE }
            .start()
    }

    private fun manageRecyclerView(book: Book) {
        fbViewModel.getUserByCommentsOfBooks(book.id).observe(viewLifecycleOwner) { users ->
            val commentsList = ArrayList<TemporaryReview>()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            for (user in users) {
                for (comment in user.userSettings?.commenti!!) {
                    if (comment.isbn == book.id) {
                        commentsList.add(
                            TemporaryReview(
                                comment.idComment,
                                comment.reviewText,
                                comment.reviewTitle,
                                comment.isbn,
                                comment.vote,
                                comment.date,
                                user.email
                            )
                        )
                    }
                }
            }

            if (commentsList.isNotEmpty()) {
                binding.layoutReviews.visibility = View.VISIBLE
                binding.textViewTitleRecensioni.text = "Reviews"
                binding.layoutReviews.setOnClickListener {
                    val action =
                        BookInfoFragmentDirections.actionBookInfoFragmentToReviewsFragment(book)
                    findNavController().navigate(action)
                }
            } else {
                binding.layoutReviews.visibility = View.GONE
                binding.textViewTitleRecensioni.text = "No reviews found"
            }

            commentsList.sortByDescending { dateFormat.parse(it.date) }

            val first3Comments = ArrayList(commentsList.subList(0, minOf(commentsList.size, 3)))

            val adapter = ReviewsAdapter(first3Comments as ArrayList<TemporaryReview>)
            val layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewReviews.layoutManager = layoutManager
            binding.recyclerViewReviews.adapter = adapter
        }
    }

    private fun manageMyReview() {
        binding.textShowMyReview.text = requireContext().getString(R.string.read_more)

        binding.textReview1.post {
            if (binding.textReview1.lineCount < 5 && binding.textTitleReview1.lineCount < 2) {
                binding.textReview1.visibility = View.GONE
            } else {
                binding.textShowMyReview.visibility = View.VISIBLE
                binding.textShowMyReview.setOnClickListener {
                    isExpandedReview = !isExpandedReview

                    binding.textReview1.maxLines = if (isExpandedReview) Integer.MAX_VALUE else 5
                    binding.textTitleReview1.maxLines =
                        if (isExpandedReview) Integer.MAX_VALUE else 2

                    var buttonText = ""
                    if (isExpandedReview) {
                        buttonText = requireContext().getString(R.string.read_less)
                        binding.textReview1.ellipsize = null
                        binding.textTitleReview1.ellipsize = null
                    } else {
                        buttonText = requireContext().getString(R.string.read_more)
                        if (binding.textReview1.lineCount > 5) {
                            binding.textReview1.ellipsize = TextUtils.TruncateAt.END
                        }
                        if (binding.textTitleReview1.lineCount > 2) {
                            binding.textTitleReview1.ellipsize = TextUtils.TruncateAt.END
                        }
                    }
                    binding.textShowMyReview.text = buttonText
                }
                binding.textShowMyReview.maxLines = 5
                binding.textShowMyReview.ellipsize = TextUtils.TruncateAt.END
            }
        }
    }

    private fun manageDescription() {

        binding.textMoreDescription.text = requireContext().getString(R.string.read_more)

        binding.textViewDescription.post {
            if (binding.textViewDescription.lineCount < 5) {
                binding.textMoreDescription.visibility = View.GONE
            } else {
                binding.textMoreDescription.visibility = View.VISIBLE
                binding.textMoreDescription.setOnClickListener {
                    isExpandedDescription = !isExpandedDescription
                    val maxLines = if (isExpandedDescription) Integer.MAX_VALUE else 5
                    binding.textViewDescription.maxLines = maxLines

                    var buttonText = ""
                    if (isExpandedDescription) {
                        buttonText = requireContext().getString(R.string.read_less)
                        binding.textViewDescription.ellipsize = null
                    } else {
                        buttonText = requireContext().getString(R.string.read_more)
                        binding.textViewDescription.ellipsize = TextUtils.TruncateAt.END
                    }
                    binding.textMoreDescription.text = buttonText
                }
                binding.textViewDescription.maxLines = 5
                binding.textViewDescription.ellipsize = TextUtils.TruncateAt.END
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val mapView: MapView = binding.mapView

        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        val mapView: MapView = binding.mapView
        mapView.onPause()
    }

    inner class MyItem(
        latLng: LatLng, title: String, snippet: String
    ) : ClusterItem {

        private val position: LatLng
        private val title: String
        private val snippet: String

        override fun getPosition(): LatLng {
            return position
        }


        override fun getTitle(): String {
            return title
        }

        override fun getSnippet(): String {
            return snippet
        }

        fun getZIndex(): Float {
            return 0f
        }

        init {
            position = latLng
            this.title = title
            this.snippet = snippet
        }
    }
}