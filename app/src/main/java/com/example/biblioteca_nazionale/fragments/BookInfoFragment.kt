package com.example.biblioteca_nazionale.fragments


import RequestViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.SpannableString
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.cache.GeocodingCache
import com.example.biblioteca_nazionale.databinding.FragmentBookInfoBinding
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
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


class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    lateinit var binding: FragmentBookInfoBinding

    private val modelRequest: RequestViewModel = RequestViewModel()
    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()
    val db = Firebase.firestore

    private var isExpanded = false

    private lateinit var progressBar: ProgressBar

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val locationPermissionRequestCode = 1

        ActivityCompat.requestPermissions(
            requireActivity(),
            locationPermissions,
            locationPermissionRequestCode
        )

        binding = FragmentBookInfoBinding.bind(view)

        binding.scrollViewInfo.visibility = View.GONE

        progressBar = binding.progressBar

        progressBar.visibility = View.VISIBLE

        val buttonReview = binding.buttonScriviRecensione

        manageToolbar()

        val book = arguments?.getParcelable<Book>("book")

        book?.let {

            modelRequest.fetchDataBook(it)

            binding.textViewBookName.text = it.info?.title ?: ""
            binding.textViewAutore.text = it.info?.authors?.toString() ?: ""

            val description = it.info?.description
            if (description.isNullOrEmpty()) {
                binding.textViewDescription.text = "Descrizione non disponibile"
                binding.textMoreDescription.visibility = View.GONE
            } else
                binding.textViewDescription.text = description

            Glide.with(requireContext())
                .load(book.info.imageLinks?.thumbnail.toString())
                .apply(RequestOptions().placeholder(R.drawable.baseline_book_24)) // Immagine di fallback
                .into(binding.imageViewBook)


            val mapView: MapView = binding.mapView
            mapView.onCreate(savedInstanceState)

            mapView.getMapAsync { googleMap ->

                val clusterManager = ClusterManager<MyItem>(requireContext(), googleMap)

                googleMap.setOnCameraIdleListener(clusterManager)
                googleMap.setOnMarkerClickListener(clusterManager)

                // Personalizzazione e visualizzazione della mappa
                googleMap.uiSettings.isZoomControlsEnabled =
                    true // Abilita i controlli di zoom
                googleMap.uiSettings.isMyLocationButtonEnabled =
                    true // Abilita il pulsante "La mia posizione"
                googleMap.uiSettings.isScrollGesturesEnabled =
                    true // Abilita il gesto di scorrimento sulla mappa
                googleMap.uiSettings.isRotateGesturesEnabled = true
                googleMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true

                googleMap.setMapStyle(context?.let {
                    MapStyleOptions.loadRawResourceStyle(
                        it,
                        R.raw.map_style
                    )
                })

                val startLatLng = LatLng(
                    41.87194,
                    12.56738
                )

                val cameraUpdate =
                    CameraUpdateFactory.newLatLngZoom(startLatLng, 4.4f)
                googleMap.moveCamera(cameraUpdate)

                val geoApiContext = GeoApiContext.Builder()
                    .apiKey("AIzaSyCtTj2ohggFHtNX2asYNXL1kj31pO8wO_Y") // Replace with your actual API key
                    .build()

                modelRequest.getLibraries().observe(viewLifecycleOwner) { libraries ->
                    libraries?.let { libraryList ->

                        progressBar.visibility = View.GONE
                        binding.scrollViewInfo.visibility = View.VISIBLE
                        buttonReview.visibility = View.GONE

                        manageRatingBar(book)

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
                                        val libraryLatLng =
                                            LatLng(location.lat, location.lng)

                                        withContext(Dispatchers.Main) {
                                            val markerOptions = MyItem(
                                                libraryLatLng,
                                                libraryName,
                                                "Biblioteca"
                                            )  // Descrizione opzionale
                                            clusterManager.addItem(markerOptions)
                                            clusterManager.cluster()

                                            counter++

                                            Log.d("conto", counter.toString())

                                            markerList.add(markerOptions)

                                            Log.d("Marker", "M${markerList.size}")

                                            println(book.id)

                                            clusterManager.setOnClusterItemClickListener { marker ->
                                                setDefaultLibrary(marker, book, googleMap)
                                                true
                                            }
                                        }
                                    }
                                }
                            }

                            val fusedLocationClient =
                                LocationServices.getFusedLocationProviderClient(
                                    requireContext()
                                )

                            if (ContextCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                setDefaultLibrary(markerList[0], book, googleMap)
                            } else {
                                withContext(Dispatchers.IO) {
                                    fusedLocationClient.lastLocation
                                        .addOnSuccessListener { location: Location? ->
                                            Log.d("Mannaia", "Mannaia$location")
                                            if (location != null) {
                                                val latitude = location.latitude
                                                val longitude = location.longitude

                                                val nearestMarker =
                                                    findNearestMarker(
                                                        latitude,
                                                        longitude,
                                                        markerList
                                                    )

                                                if (nearestMarker != null) {
                                                    setDefaultLibrary(
                                                        nearestMarker,
                                                        book,
                                                        googleMap
                                                    )
                                                } else {
                                                    noLibraryFound()
                                                }
                                            } else {
                                                if (!markerList.isEmpty()) {
                                                    setDefaultLibrary(
                                                        markerList[0],
                                                        book,
                                                        googleMap
                                                    )
                                                } else {
                                                    noLibraryFound()
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
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
        latLng: LatLng,
        title: String,
        snippet: String
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

    private fun setDefaultLibrary(marker: MyItem, book: Book, googleMap: GoogleMap) {

        val startLatLng = LatLng(
            marker.position.latitude,
            marker.position.longitude
        )

        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(
                startLatLng,
                12f
            )
        googleMap.animateCamera(cameraUpdate)

        binding.textViewNomeBiblioteca.text =
            marker.title
        binding.buttonPrenota.setOnClickListener {
            fbViewModel.addNewBookBooked(
                book.id.toString(),
                book.id.toString(),
                binding.textViewNomeBiblioteca.text.toString(),
                book?.info?.imageLinks?.thumbnail.toString()
            )
            Toast.makeText(
                requireContext(),
                "Your book has booked succesfully!",
                Toast.LENGTH_SHORT
            ).show()

            binding.buttonPrenota.isEnabled =
                false

            binding.textViewDataRiconsegna.setOnClickListener {
                fbViewModel.newExpirationDate(
                    it.id.toString()
                )
            }
        }
    }

    private fun noLibraryFound() {
        binding.textViewNomeBiblioteca.text =
            "Nessuna biblioteca trovata"

        binding.textViewDataRiconsegna.visibility =
            View.GONE

        binding.buttonPrenota.isEnabled =
            false
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
        private fun manageToolbar(){
            val toolbar = binding.toolbar

            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

            toolbar.setNavigationOnClickListener {
                val action = BookInfoFragmentDirections.actionBookInfoFragmentToBookListFragment()
                findNavController().navigate(action)
            }

            binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    val action =
                        BookInfoFragmentDirections.actionBookInfoFragmentToBookListFragment(
                            focusSearchView = true
                        )
                    findNavController().navigate(action)
                }
            }
        }

        private fun manageRatingBar(book: Book){

        val ratingBar: RatingBar = binding.ratingBarInserimento

        val buttonReview = binding.buttonScriviRecensione

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->

            val ratingValue = rating.toFloat()
            buttonReview.visibility = View.VISIBLE

            buttonReview.setOnClickListener {

                val bundle = Bundle().apply {
                    putFloat("reviewVote", ratingValue)
                    putParcelable("book", book)
                }

                findNavController().navigate(
                    R.id.action_bookInfoFragment_to_writeReviewFragment,
                    bundle
                )
            }

            Toast.makeText(
                requireContext(),
                "Hai votato: $ratingValue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun manageDescription() {

        val spannableString = SpannableString("Leggi di più")
        spannableString.setSpan(UnderlineSpan(), 0, "Leggi di più".length, 0)
        binding.textMoreDescription.text = spannableString

        binding.textViewDescription.post {
            if (binding.textViewDescription.lineCount < 5) {
                binding.textMoreDescription.visibility = View.GONE
            } else {
                binding.textMoreDescription.visibility = View.VISIBLE
                binding.textMoreDescription.setOnClickListener {
                    isExpanded = !isExpanded
                    val maxLines = if (isExpanded) Integer.MAX_VALUE else 5
                    binding.textViewDescription.maxLines = maxLines

                    var buttonText = ""
                    if (isExpanded) {
                        buttonText = "Leggi meno"
                        binding.textViewDescription.ellipsize = null
                    } else {
                        buttonText = "Leggi di più"
                        binding.textViewDescription.ellipsize = TextUtils.TruncateAt.END
                    }
                    val spannableString = SpannableString(buttonText)
                    spannableString.setSpan(UnderlineSpan(), 0, buttonText.length, 0)
                    binding.textMoreDescription.text = spannableString
                }
                binding.textViewDescription.maxLines = 5
                binding.textViewDescription.ellipsize = TextUtils.TruncateAt.END
            }
        }
    }
}

