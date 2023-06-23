package com.example.biblioteca_nazionale.fragments


import RequestViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentBookInfoBinding
import com.example.biblioteca_nazionale.model.Book
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    lateinit var binding: FragmentBookInfoBinding
    private lateinit var toolbar: MaterialToolbar

    private val modelRequest: RequestViewModel = RequestViewModel()
    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()
    val db = Firebase.firestore


    private var isExpanded = false

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

        toolbar = binding.toolbar

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

            /*binding.buttonPrenota.setOnClickListener {
                fbViewModel.addNewBookBooked(it.id.toString(), it.id.toString(), binding.textViewNomeBiblioteca.text.toString(), book.info.imageLinks?.thumbnail.toString())
            }*/
        }

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
                    updateDescriptionText()
                }
                binding.textViewDescription.maxLines = 5
                binding.textViewDescription.ellipsize = TextUtils.TruncateAt.END
            }
        }

        val mapView: MapView = binding.mapView
        mapView.onCreate(savedInstanceState)



        mapView.getMapAsync { googleMap ->
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
            }) // Carica lo stile personalizzato della mappa

            val geoApiContext = GeoApiContext.Builder()
                .apiKey("AIzaSyCtTj2ohggFHtNX2asYNXL1kj31pO8wO_Y") // Replace with your actual API key
                .build()

            modelRequest.getLibraries().observe(viewLifecycleOwner) { libraries ->
                libraries?.let { libraryList ->
                    lifecycleScope.launch(Dispatchers.Main) {

                        val librariesNames = libraryList.flatMap { library ->
                            library.shelfmarks.mapNotNull { it.shelfmark }
                        }

                        val markerList = mutableListOf<Marker>()

                        withContext(Dispatchers.IO) {

                            for (libraryName in librariesNames) {
                                val geocodingResult =
                                    GeocodingApi.geocode(geoApiContext, libraryName)
                                        .await()

                                if (geocodingResult.isNotEmpty()) {
                                    val location = geocodingResult[0].geometry.location
                                    val libraryLatLng =
                                        LatLng(location.lat, location.lng)

                                    println(libraryLatLng.latitude)

                                    withContext(Dispatchers.Main) {
                                        val markerOptions = MarkerOptions()
                                            .position(libraryLatLng)
                                            .title(libraryName)
                                            .snippet("Seleziona questa biblioteca") // Descrizione opzionale
                                        val marker = googleMap.addMarker(markerOptions)

                                        if (marker != null) {
                                            markerList.add(marker)
                                        }

                                        // Aggiungi un marker per la biblioteca sulla mappa
                                        withContext(Dispatchers.Main) {
                                            val markerOptions = MarkerOptions()
                                                .position(libraryLatLng)
                                                .title(libraryName)
                                                .snippet("Seleziona questa biblioteca") // Descrizione opzionale
                                            googleMap.addMarker(markerOptions)

                                            googleMap.setOnMarkerClickListener { marker ->
                                                binding.textViewNomeBiblioteca.text =
                                                    marker.title
                                                binding.buttonPrenota.setOnClickListener {
                                                    fbViewModel.addNewBookBooked(
                                                        it.id.toString(),
                                                        it.id.toString(),
                                                        binding.textViewNomeBiblioteca.text.toString(),
                                                        book?.info?.imageLinks?.thumbnail.toString()
                                                    )
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Your book has booked succesfully!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    binding.buttonPrenota.isEnabled = false

                                                    binding.textViewDataRiconsegna.setOnClickListener {
                                                        fbViewModel.newExpirationDate(it.id.toString())
                                                    }
                                                }
                                                true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        println(markerList[0].position.latitude)
                        println(markerList[0].position.longitude)


                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())



                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            val startLatLng = LatLng(
                                markerList[0].position.latitude,
                                markerList[0].position.longitude
                            )

                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, 12f)
                            googleMap.moveCamera(cameraUpdate)

                            binding.textViewNomeBiblioteca.text =
                                markerList[0].title
                            binding.buttonPrenota.setOnClickListener {
                                fbViewModel.addNewBookBooked(
                                    it.id.toString(),
                                    it.id.toString(),
                                    binding.textViewNomeBiblioteca.text.toString(),
                                    book?.info?.imageLinks?.thumbnail.toString()
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Your book has booked succesfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.buttonPrenota.isEnabled = false

                                binding.textViewDataRiconsegna.setOnClickListener {
                                    fbViewModel.newExpirationDate(it.id.toString())
                                }
                            }
                        } else {
                            println(markerList)
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location: Location? ->
                                    println(location)
                                    if (location != null) {
                                        println(location)
                                        val latitude = location.latitude
                                        val longitude = location.longitude

                                        val nearestMarker = findNearestMarker(latitude, longitude, markerList)

                                        if (nearestMarker != null) {
                                            val startLatLng = LatLng(
                                                nearestMarker.position.latitude,
                                                nearestMarker.position.longitude
                                            )

                                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, 12f)
                                            googleMap.moveCamera(cameraUpdate)

                                            binding.textViewNomeBiblioteca.text =
                                                nearestMarker.title
                                            binding.buttonPrenota.setOnClickListener {
                                                fbViewModel.addNewBookBooked(
                                                    it.id.toString(),
                                                    it.id.toString(),
                                                    binding.textViewNomeBiblioteca.text.toString(),
                                                    book?.info?.imageLinks?.thumbnail.toString()
                                                )
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Your book has booked succesfully!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                binding.buttonPrenota.isEnabled = false

                                                binding.textViewDataRiconsegna.setOnClickListener {
                                                    fbViewModel.newExpirationDate(it.id.toString())
                                                }
                                            }
                                        } else {
                                            // Nessun marker trovato, esegui una logica di fallback o posiziona la mappa in un punto predefinito
                                        }
                                    } else {
                                        val startLatLng = LatLng(
                                            markerList[0].position.latitude,
                                            markerList[0].position.longitude
                                        )

                                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, 12f)
                                        googleMap.moveCamera(cameraUpdate)

                                        binding.textViewNomeBiblioteca.text =
                                            markerList[0].title
                                        binding.buttonPrenota.setOnClickListener {
                                            fbViewModel.addNewBookBooked(
                                                it.id.toString(),
                                                it.id.toString(),
                                                binding.textViewNomeBiblioteca.text.toString(),
                                                book?.info?.imageLinks?.thumbnail.toString()
                                            )
                                            Toast.makeText(
                                                requireContext(),
                                                "Your book has booked succesfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            binding.buttonPrenota.isEnabled = false

                                            binding.textViewDataRiconsegna.setOnClickListener {
                                                fbViewModel.newExpirationDate(it.id.toString())
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

    fun setButtonTitle(marker:Marker){

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

    fun findNearestMarker(a: Double, b: Double, markerList: List<Marker>): Marker? {
        val targetLocation = Location("")
        targetLocation.latitude = a
        targetLocation.longitude = b

        var nearestMarker: Marker? = null
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


    private fun updateDescriptionText() {
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
}

