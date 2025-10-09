package com.inspection.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.Utility
import com.inspection.Utils.recyclerView
import com.inspection.databinding.FragmentArravScopeOfServiceBinding
import com.inspection.fragments.FragmentVisitation.requestedSignature
import com.inspection.model.Place
import com.inspection.model.PlaceSearchResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import shaded.org.json.JSONArray
import shaded.org.json.JSONObject
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var selected_radius = 0
private var fusedLocationProviderClient : FusedLocationProviderClient? = null
/**
 * A simple [Fragment] subclass.
 * Use the [ApplicantMapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ApplicantMapFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var placesRV: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_applicant_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val slider = view.findViewById<Slider>(R.id.radiusSlider)
        val label = view.findViewById<TextView>(R.id.radiusValueTv)
        placesRV = view.findViewById<RecyclerView>(R.id.placesRecyclerView)
        placesRV?.layoutManager = LinearLayoutManager(requireContext())
        slider.addOnChangeListener { _, value, _ ->
            val display = when {
                value < 1000 -> "${value.toInt()} m"
                else -> "${"%.1f".format(value / 1000f)} km"
            }
            label.text = "Selected Radius: $display"
            selected_radius = value.toInt()
            val fadeInUp = AnimationSet(true).apply {
                val fade = AlphaAnimation(0f, 1f).apply { duration = 300 }
                val moveUp = TranslateAnimation(0f, 0f, 20f, 0f).apply { duration = 300 }
                addAnimation(fade)
                addAnimation(moveUp)
            }

            label.startAnimation(fadeInUp)
        }
        val searchBtn = view.findViewById<TextView>(R.id.searchMapBtn)
        searchBtn.setOnClickListener {
            captureLocation()
        }
    }

    private fun captureLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (checkLocationPermissions()) {
            try {
                val task = fusedLocationProviderClient!!.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return CancellationTokenSource().token
                    }
                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                })
                task.addOnSuccessListener {
                    if (it != null) {
                        Log.v("Location Captured", it.getLatitude().toString() + " " + it.getLongitude());
                        searchCarRepairShopsNewPlacesAPI(it.latitude, it.longitude, getString(R.string.GoogleMapPlaces_Key), selected_radius)

                    } else {
                        Utility.showUnifiedErrorDialog(activity,"Unable to capture current location")
                    }
                }
            } catch (e: SecurityException) {

            }
        } else {
            if (!checkLocationPermissions()) {
                if (MainActivity.activity != null) {
                    requestPermissionAndContinue();
                }
            } else {
                try {
                    val task = fusedLocationProviderClient!!.getLastLocation();
                    task.addOnSuccessListener {
                        if (it != null) {
                            Log.v("Location Captured", it.getLatitude().toString() + " " + it.getLongitude());
                            searchCarRepairShopsNewPlacesAPI(it.latitude, it.longitude, getString(R.string.GoogleMapPlaces_Key), selected_radius)
                        } else {
//                            Utility.showMessageDialog(activity,"Information","Unable to capture current location")
                            Utility.showUnifiedErrorDialog(activity,"Unable to capture current location")
                        }
                    }
                } catch (e: SecurityException) {

                }
            }
        }
    }

    fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun showMapView(loc : LatLng,places: List<Place>) {
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager
            .beginTransaction()
            .replace(R.id.mapFragment, mapFragment)
            .commit()

        mapFragment.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true
            addMarkersFromPlaces(places, googleMap)
//            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(loc.latitude, loc.longitude), 10f))
        }
    }

    fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.activity as MainActivity, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 350);
        } else {
            try {
                val task = fusedLocationProviderClient!!.getLastLocation();
                task.addOnSuccessListener {
                    if (it != null) {
                        Log.v("Location Captured", it.getLatitude().toString() + " " + it.getLongitude());
                    } else {
                        Utility.showUnifiedErrorDialog(activity,"Unable to capture current location")
                    }
                }
//                task.addOnFailureListener {e: Exception ->
//                    Utility.showMessageDialog(activity,"Information","Unable to capture current location - ${e.message}")
//                }
            } catch (e: SecurityException) {

            }
        }
    }



    fun searchCarRepairShopsNewPlacesAPI(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        radius: Int,
    ) {
        val fieldMask = "places.displayName,places.location,places.shortFormattedAddress,places.id,places.primaryTypeDisplayName,places.types,places.businessStatus"//,places.websiteUri"
        val url = "https://places.googleapis.com/v1/places:searchNearby?key=$apiKey&fields=$fieldMask"

        val jsonBody = JSONObject().apply {
            put("includedTypes", listOf("car_repair"))
            put("maxResultCount", 10)
            put("locationRestriction", JSONObject().apply {
                put("circle", JSONObject().apply {
                    put("center", JSONObject().apply {
                        put("latitude", latitude)
                        put("longitude", longitude)
                    })
                    put("radius", radius)
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url) // ✅ fieldMask is in the URL only
            .post(body) // ✅ JSON body does NOT contain fieldMask
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
//                val names = mutableListOf<String>()

                if (response.isSuccessful && responseData != null) {
                    try {
                        val json = JSONObject(responseData)
                        val places = json.optJSONArray("places") ?: JSONArray()

                        val gson = Gson()
                        val responseObject = gson.fromJson(responseData, PlaceSearchResponse::class.java)
//                        responseObject.places.forEach { place ->
                        activity?.runOnUiThread {
                            placesRV?.adapter = PlacesAdapter(responseObject.places)
                            showMapView(LatLng(latitude, longitude),responseObject.places)
                        }

//                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    println("API error: ${response.code} ${responseData}")
                }


            }
        })
    }




    class PlacesAdapter(
        private val places: List<Place>
    ) : RecyclerView.Adapter<PlacesAdapter.StepViewHolder>() {

        // ViewHolder for each step item
        inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name_text: TextView = itemView.findViewById(R.id.place_name_val)
            val address_text: TextView = itemView.findViewById(R.id.address_val)
            val cat_text: TextView = itemView.findViewById(R.id.main_cat_val)
            val all_cat_text: TextView = itemView.findViewById(R.id.all_cat_val)
            val status_text: TextView = itemView.findViewById(R.id.status_val)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
            return StepViewHolder(view)
        }

        override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
            holder.name_text.text = places[position].displayName.text
            holder.address_text.text = places[position].shortFormattedAddress
            holder.cat_text.text = places[position].primaryTypeDisplayName?.text ?: "N/A"
            holder.all_cat_text.text = places[position].types.joinToString(", ")
        }

        override fun getItemCount() = places.size
    }


    fun addMarkersFromPlaces(places: List<Place>, map: GoogleMap) {
        map.clear() // clear previous markers if any

        val boundsBuilder = LatLngBounds.Builder()

        for (place in places) {
            val position = LatLng(place.location.latitude, place.location.longitude)

            val title = place.displayName.text
            val snippet = place.shortFormattedAddress

            map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            boundsBuilder.include(position)
        }

        // Move/zoom camera to fit all markers
//        if (places.isNotEmpty()) {
//            val bounds = boundsBuilder.build()
//            val padding = 100 // pixels
//            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
//        }
        val positions = places.map {
            LatLng(it.location.latitude, it.location.longitude)
        }

        zoomToFitAllMarkers(map, positions)
    }

    fun zoomToFitAllMarkers(googleMap: GoogleMap, markerPositions: List<LatLng>) {
        if (markerPositions.isEmpty()) return

        val builder = LatLngBounds.Builder()
        for (position in markerPositions) {
            builder.include(position)
        }

        val bounds = builder.build()
        val padding = 100 // pixels

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ApplicantMapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ApplicantMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}