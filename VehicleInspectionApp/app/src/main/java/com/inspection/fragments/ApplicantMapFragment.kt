package com.inspection.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.Utils.Utility
import com.inspection.Utils.addSavedApplicantShop
import com.inspection.Utils.applicantShopSaved
import com.inspection.Utils.applicantShopVisited
import com.inspection.Utils.capitalizeFirst
import com.inspection.Utils.clearSavedApplicantShops
import com.inspection.Utils.getApplicantShops
import com.inspection.Utils.getSavedApplicantShops
import com.inspection.Utils.getTodayVisitations

import com.inspection.Utils.removeApplicantShop
import com.inspection.Utils.removeSavedApplicantShop
import com.inspection.Utils.saveApplicantShops

import com.inspection.Utils.updateApplicantShopETA
import com.inspection.Utils.updateSavedApplicantShopContents
import com.inspection.Utils.updateSavedApplicantShopETA
import com.inspection.Utils.updateSavedApplicantShopNotes
import com.inspection.Utils.updateSavedApplicantShopVisited
import com.inspection.model.ApplicantMatchingFacilitiesModel
import com.inspection.model.Place
import com.inspection.model.PlaceSearchResponse
import com.inspection.model.TodayVisitationModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
//import okhttp3.internal.notifyAll
//import shaded.org.json.JSONArray
//import shaded.org.json.JSONObject
//import org.json.JSONArray
//import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

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
class ApplicantMapFragment : Fragment(), ApplicantPlacesAdapter.onRefreshListener, ApplicantPlacesAdapter.onCardClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var placesRV: RecyclerView? = null
    private var loadingDialog: AlertDialog? = null
    private var clearETA = true
    private var ETALoaded = false
    private var myLocationAdded = false
    var rgNearBy: RadioButton? = null

    var rgSearch: RadioButton? = null
    var rgLoad: RadioButton? = null

    var searchLL: LinearLayout? = null
    var textLL: LinearLayout? = null
    var sliderLL: LinearLayout? = null
    var resultsLL: LinearLayout? = null

    var rgText: RadioButton? = null
    var label: EditText? = null
    var rgClearAll: RadioButton? = null
    var rgKeepSaved: RadioButton? = null


    var searchText: EditText? = null

    var gMap: GoogleMap? = null

    var markerMap = mutableMapOf<String, Marker>()
    var shopsList = mutableListOf<Place>()

    lateinit var placesAdapter: ApplicantPlacesAdapter

    private var specialistPostition: LatLng? = null

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
        shopsList = getApplicantShops(requireContext()).toMutableList();
        val slider = view.findViewById<Slider>(R.id.radiusSlider)
        label = view.findViewById<EditText>(R.id.radiusET)
        resultsLL = view.findViewById(R.id.ll_results)
        rgText = view.findViewById(R.id.rgText)
        rgClearAll = view.findViewById(R.id.rgClearALL)
        rgKeepSaved = view.findViewById(R.id.rgClear)

        rgNearBy = view.findViewById(R.id.rgNearBy)
        placesRV = view.findViewById<RecyclerView>(R.id.placesRecyclerView)
        textLL =  view.findViewById(R.id.text_ll)
        sliderLL =  view.findViewById(R.id.slider_ll)

        searchText =  view.findViewById(R.id.searchText)
        rgSearch = view.findViewById(R.id.rgSearch)
        rgLoad = view.findViewById(R.id.rgLoad)
        searchLL =  view.findViewById(R.id.ll_search)

        var startBtn =  view.findViewById<Button>(R.id.startBtn)

        rgNearBy?.setOnCheckedChangeListener {
            _, isChecked ->
            handleRadioSelection()
        }
        rgText?.setOnCheckedChangeListener {
                _, isChecked ->
            handleRadioSelection()
        }
//        rgSearch?.setOnCheckedChangeListener {
//                _, isChecked ->
//            if (isChecked) {
//                searchLL?.isVisible = true
//            }
//        }
//
//        rgLoad?.setOnCheckedChangeListener {
//                _, isChecked ->
//            if (isChecked) {
//                searchLL?.isVisible = false
//            }
//        }

        startBtn.setOnClickListener {
            if (rgSearch!!.isChecked) {
                placesRV?.adapter = null
                searchLL?.isVisible = true
                resultsLL?.isVisible = true
            } else {
                placesRV?.adapter = null
                searchLL?.isVisible = false
                if (specialistPostition == null) {
                    captureLocation(true)
                } else {
                    loadSavedData()
                }
            }
        }



        placesRV?.layoutManager = LinearLayoutManager(requireContext())
//        captureLocation(false)
        slider.addOnChangeListener { _, value, _ ->
//            val display = when {
//                value < 1000 -> value.toInt()
//                else -> value / 1000
//            }
            Log.v("ERROR -->", " Slider Changed Listener Called")
            label?.setText(value.toInt().toString())
            Log.v("ERROR -->", " Slider Changed Listener Called 2")
            selected_radius = value.toInt()
//            val fadeInUp = AnimationSet(true).apply {
//                val fade = AlphaAnimation(0f, 1f).apply { duration = 300 }
//                val moveUp = TranslateAnimation(0f, 0f, 20f, 0f).apply { duration = 300 }
//                addAnimation(fade)
//                addAnimation(moveUp)
//            }
//
//            label.startAnimation(fadeInUp)
        }
//        label.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                Log.v("ERROR -->", " Text Changed Listener Called")
//                if (!p0.isNullOrEmpty()) selected_radius =  p0.toString().replace(".0","").toInt()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//        })

        val searchBtn = view.findViewById<TextView>(R.id.searchMapBtn)
        searchBtn.setOnClickListener {
            if (label?.text.isNullOrEmpty()) {
                Utility.showUnifiedErrorDialog(activity,"Please enter a valid radius")
                return@setOnClickListener
            } else if (label?.text.toString().toInt() >50 ) {
                Utility.showUnifiedErrorDialog(activity,"Please enter a valid radius (maximum 50 miles)")
                return@setOnClickListener
            }
            label?.text.toString().toInt().let {
                selected_radius = it
            }
            slider.value = selected_radius.toFloat()
            requireActivity().runOnUiThread {
                showLoadingDialog("Searching ...")
            }
            if (rgNearBy?.isChecked ?: false) {
                if (specialistPostition == null) {
                    Log.v("FUNCTION ---->", " captureLocation called from searchBtn");
                    captureLocation(true)
                } else {
                    Log.v(
                        "FUNCTION ---->",
                        " searchCarRepairShopsNewPlacesAPI called from searchBtn"
                    );
                    searchCarRepairShopsNewPlacesAPI(
                        specialistPostition!!.latitude,
                        specialistPostition!!.longitude,
                        getString(R.string.GoogleMapPlaces_Key),
                        (selected_radius*1609.34)
                    )
                }
            } else {
                if (specialistPostition == null) {
                    Log.v("FUNCTION ---->", " captureLocation called from searchBtn");
                    captureLocation(true)
                } else {
                    Log.v(
                        "FUNCTION ---->",
                        " searchCarRepairShopsNewPlacesAPI called from searchBtn"
                    );
                    searchCarRepairShopsNewPlacesAPI(
                        specialistPostition!!.latitude,
                        specialistPostition!!.longitude,
                        getString(R.string.GoogleMapPlaces_Key),
                        selected_radius*1609.34
                    )
                }
            }
        }
    }

    fun handleRadioSelection() {
        if (rgText != null && rgText!!.isChecked) {
            textLL?.isVisible = true
            sliderLL?.isVisible = false
        } else {
            textLL?.isVisible = false
            sliderLL?.isVisible = true
        }
    }

    fun loadSavedData() {

        shopsList = getSavedApplicantShops(requireContext()).toMutableList()
        for (i in shopsList.indices) {
            shopsList[i].order = i + 1
            shopsList[i].etaLabel = ""
        }
        placesRV?.adapter = ApplicantPlacesAdapter(shopsList,requireContext(),true,this,this)
        showMapView(shopsList)
        var count = 0
        for (place in shopsList) {
            Log.v("PLACE URL", Constants.getApplicantMatchingFacilities + place.id + "&placeName=" + place.displayName + "&placeAddr=" + place.shortFormattedAddress,)
            Volley.newRequestQueue(activity).add(
                StringRequest(
                    com.android.volley.Request.Method.GET,
                    Constants.getApplicantMatchingFacilities + place.id + "&placeName=" + place.displayName.text + "&placeAddr=" + place.shortFormattedAddress,
                    { response ->
                        requireActivity().runOnUiThread {
                            Log.v("RESPONSE", response.toString())
                            if (response.toString().isNotEmpty()) {
                                val json = response.toString().trim()
                                val matchingFacilitiesList: ArrayList<ApplicantMatchingFacilitiesModel> =
                                    Gson().fromJson(
                                        json,
                                        object : TypeToken<ArrayList<ApplicantMatchingFacilitiesModel>>() {}.type
                                    )
                                if (matchingFacilitiesList.isNotEmpty()) {
                                    place.matchingFacilities =
                                        matchingFacilitiesList
                                    place.aarStatus =
                                        "Found Matching Facilities (${matchingFacilitiesList.size})"
                                } else {
                                    place.matchingFacilities = emptyList()
                                    place.aarStatus = "Not AAR Participant"
                                }
                            } else {
                                place.matchingFacilities = emptyList()
                                place.aarStatus = "Not AAR Participant"
                            }
                            place.etaLabel= ""
                            Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 6");
                            updateSavedApplicantShopContents(requireContext(),shopsList)
//                                                    placesRV?.adapter = PlacesAdapter(shopsList,requireContext())
                            count++
                            if (count == shopsList.size) {
                                Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 7");
                                placesRV?.adapter = ApplicantPlacesAdapter(shopsList,requireContext(),true,this,this)
                            }

                        }
                    },
                    {
                        it.printStackTrace()
                    })
            )
        }
        resultsLL?.isVisible = true
    }

    fun addMyLocationMarkerOld(myLocation: LatLng, map: GoogleMap) {

        Log.v("FUNCTION ---->"," addMyLocationMarker Bookmark 1");
        val boundsBuilder = LatLngBounds.Builder()
        var needETA = true
        val position = LatLng(myLocation.latitude, myLocation.longitude)
        if (myLocation.latitude == 0.0 && myLocation.longitude == 0.0) {

        } else {
            val title = "Your Location"
            val snippet = ""
            map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            myLocationAdded = true
            var completed = 0
            Log.v("FUNCTION ---->"," addMyLocationMarker Bookmark 2");
            for (place in shopsList) {
                Log.v("Skipping ETA ", "Call for ETA LABEL ${place.etaLabel} ")
//                if (place.etaLabel != null && place.etaLabel.isNotEmpty() && place.etaLabel.contains("Distance:")
                if (place.etaLabel != null && place.etaLabel.isNotEmpty()
                ) {
                    Log.v("Skipping ETA Call for", place.id)
                    completed++
                    if (completed == shopsList.size) {
                        Log.v("FUNCTION ---->"," addMyLocationMarker Bookmark 3");
                        loadDataAndMap(true)
//                        showMapView(shopsList)
//                        needETA = false
                    }
                    continue
                }
                if (place.etaLabel == null || place.etaLabel.isEmpty()) {
                    Log.v("STARTED ETA Call for", place.id)
                    Log.v("FUNCTION ---->", " addMyLocationMarker Bookmark 4");
                    getETAUsingRoutesAPI(
                        specialistPostition!!,
                        LatLng(place.location.latitude, place.location.longitude)
                    ) { response ->
                        Log.v("getETAUsingRoutesAPI", " Called for ${place.id}")
                        var eta = ""
                        if (response == "Unable to determine a route") {
                            eta = response
                        } else {
                            eta = parseNewRoutesETA(response) ?: "N/A"
                        }
                        Log.d("BEFORE --> ", "Updating ETA for ${place.displayName.text} : $eta")
                        Log.v("FUNCTION ---->", " addMyLocationMarker Bookmark 5");
                        updateApplicantShopETA(
                            requireContext(),
                            place.id,
                            eta
                        )
                        updateSavedApplicantShopETA(
                            requireContext(),
                            place.id,
                            eta
                        )
//                    requireActivity().runOnUiThread {
//                        placesRV?.adapter?.notifyItemChanged(shopsList.indexOf(place))
//                    }

                        Log.d("AFTER --> ", "Updating ETA for ${place.id} : $eta")
//                    formatRouteInfo( eta ?: "0s")  // you can pass actual distance if needed
                        Log.d("ETA", "ETA LABEL: $eta")
                        completed++
                        Log.d("COMPLETED --> ", completed.toString())
                        Log.d("SIZE --> ", shopsList.size.toString())
                        if (completed == shopsList.size) {
                            requireActivity().runOnUiThread {
                                Log.d("loadDataAndMap --> ", "1")
                                Log.v("FUNCTION ---->", " addMyLocationMarker Bookmark 7");
                                loadDataAndMap(true)
//                            showMapView(shopsList)
                            }
                        }
                    }
                }
            }



            boundsBuilder.include(position)
        }
    }

    fun addMyLocationMarker(myLocation: LatLng, map: GoogleMap) {

        if (myLocation.latitude == 0.0 && myLocation.longitude == 0.0) {
            Log.w("ETA", "Invalid location")
            return
        }

        // Add my location marker
        map.addMarker(
            MarkerOptions()
                .position(myLocation)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )

        myLocationAdded = true

        // 🔹 Collect only places that NEED ETA
        val placesNeedingETA = shopsList.filter {
            it.etaLabel.isNullOrBlank() ||
                    it.etaLabel == "Unable to determine a route"
        }.toMutableList()

        if (placesNeedingETA.isEmpty()) {
            Log.d("ETA", "No ETA needed, refreshing UI")
            loadDataAndMap(true)
            return
        }

        Log.d("ETA", "Starting sequential ETA for ${placesNeedingETA.size} places")

        // 🔁 Sequential processor
        fun processNextETA() {

            if (placesNeedingETA.isEmpty()) {
                Log.d("ETA", "All ETA processed")
                requireActivity().runOnUiThread {
                    loadDataAndMap(true)
                }
                return
            }

            val place = placesNeedingETA.removeAt(0)

            Log.d("ETA", "Fetching ETA for ${place.id}")

            getETAUsingRoutesAPI(
                specialistPostition!!,
                LatLng(place.location.latitude, place.location.longitude)
            ) { response ->

                val eta = if (response == "Unable to determine a route") {
                    response
                } else {
                    parseNewRoutesETA(response) ?: "N/A"
                }

                Log.d("ETA", "ETA for ${place.id} = $eta")

                // ✅ Update in-memory (CRITICAL)
                place.etaLabel = eta

                // ✅ Persist update
                updateApplicantShopETA(
                    requireContext(),
                    place.id,
                    eta
                )
                updateSavedApplicantShopETA(
                    requireContext(),
                    place.id,
                    eta
                )
                // 🔁 Process next ONLY after this finishes
                processNextETA()
            }
        }

        // 🚀 Start the queue
        processNextETA()
    }

    fun loadDataAndMap(updateAdapter: Boolean) {
        if (rgSearch!!.isChecked)
            shopsList = getApplicantShops(requireContext()).toMutableList()
        else
            shopsList = getSavedApplicantShops(requireContext()).toMutableList()

        if (updateAdapter) {
            placesAdapter = ApplicantPlacesAdapter(shopsList,requireContext(), rgLoad!!.isChecked || !rgSearch!!.isChecked,this,this)
            placesRV?.adapter = placesAdapter
            val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.bindingAdapterPosition
                    val to = target.bindingAdapterPosition
                    val context = recyclerView.context
//                        placesAdapter.moveItem(context,from, to)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // No swipe behavior
                }

                override fun isLongPressDragEnabled(): Boolean = true // or false if you use a drag handle
            }
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(placesRV)
        }

    }

    private fun captureLocation(callSearch: Boolean) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (checkLocationPermissions()) {
            Log.v("FUNCTION ---->"," captureLocation Bookmark 1");
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
                        Log.v("Location Captured", it.latitude.toString() + " " + it.longitude);
                        specialistPostition = LatLng(it.latitude, it.longitude)
                        Log.v("FUNCTION ---->"," captureLocation Bookmark 2");
                        if (gMap != null) {
                            Log.v("FUNCTION ---->"," captureLocation Bookmark 3");
                            addMyLocationMarker(specialistPostition!!, gMap!!)
                        }
                        if (callSearch && rgSearch!!.isChecked) {
                            Log.v("FUNCTION ---->"," captureLocation Bookmark 4");
                            searchCarRepairShopsNewPlacesAPI(it.latitude, it.longitude, getString(R.string.GoogleMapPlaces_Key), selected_radius*1609.34)
                        } else if (callSearch && rgLoad!!.isChecked) {
                            loadSavedData()
                        }
                    } else {
                        Utility.showUnifiedErrorDialog(activity,"Unable to capture current location")
                    }
                }
            } catch (e: SecurityException) {

            }
        } else {
            if (!checkLocationPermissions()) {
                if (MainActivity.activity != null) {
                    Log.v("FUNCTION ---->"," captureLocation Bookmark 5");
                    requestPermissionAndContinue();
                }
            } else {
                try {
                    val task = fusedLocationProviderClient!!.getLastLocation();
                    task.addOnSuccessListener {
                        if (it != null) {
                            Log.v("Location Captured", it.getLatitude().toString() + " " + it.getLongitude());
//                            searchCarRepairShopsNewPlacesAPI(it.latitude, it.longitude, getString(R.string.GoogleMapPlaces_Key), selected_radius)
                            Log.v("FUNCTION ---->"," captureLocation Bookmark 6");
                            if (gMap != null) {
                                Log.v("FUNCTION ---->"," captureLocation Bookmark 7");
                                addMyLocationMarker(specialistPostition!!, gMap!!)
                            }
                            if (callSearch && rgSearch!!.isChecked) {
                                Log.v("FUNCTION ---->"," captureLocation Bookmark 4");
                                searchCarRepairShopsNewPlacesAPI(it.latitude, it.longitude, getString(R.string.GoogleMapPlaces_Key), selected_radius*1609.34)
                            } else if (callSearch && rgLoad!!.isChecked) {
                                loadSavedData()
                            }
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

    fun parseNewRoutesETA(json: String?): String? {
        if (json.isNullOrBlank()) return null
        try {
            val root = JsonParser.parseString(json).asJsonObject

            if (!root.has("routes")) return null
            val routes = root.getAsJsonArray("routes")
            if (routes.size() == 0) return null

            val firstRoute = routes[0].asJsonObject

            if (!firstRoute.has("duration") || !firstRoute.has("distanceMeters"))
                return null

            val durationSec = firstRoute.get("duration").asString
                .replace("s", "")
                .toIntOrNull() ?: return null

            val distanceMeters = firstRoute.get("distanceMeters").asDouble

            val km = distanceMeters / 1000.0
            val miles = distanceMeters / 1609.34

            val distanceText = String.format("%.1f km (%.1f miles)", km, miles)

            val minutes = durationSec / 60

            return "* ETA: $minutes min\n\n* Distance: $distanceText"
        } catch (e: Exception) {
            Log.e("ETA Parsing", "Error parsing ETA: ${e.message}")
            return null
        }
    }

    fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun showMapView(places: List<Place>) {
        Log.v("FUNCTION ---->"," showMapView Bookmark 1");
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager
            .beginTransaction()
            .replace(R.id.mapFragment, mapFragment)
            .commit()

        mapFragment.getMapAsync { googleMap ->
            gMap = googleMap
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isScrollGesturesEnabled = true
            googleMap.uiSettings.isTiltGesturesEnabled = true
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(usaBounds, padding))

//            val usaBounds = LatLngBounds(
//                LatLng(24.396308, -124.848974), // Southwest (California / Mexico border)
//                LatLng(49.384358, -66.885444)   // Northeast (Maine)
//            )
//
//            val padding = 100 // px
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(usaBounds, padding))
            Log.v("FUNCTION ---->"," showMapView Bookmark 2");
            addMarkersFromPlaces(places, googleMap)
            Log.v("HERE ---->"," LOADED MAP VIEW");
            Log.v("HERE ---->"," ${specialistPostition.toString()}");
            Log.v("HERE ---->"," $myLocationAdded");

//            if (specialistPostition != null ){//&& !myLocationAdded) {
//                Log.v("HERE ---->"," ENTERED");
//                addMyLocationMarker(specialistPostition!!, googleMap)
//            }
            googleMap.setOnMarkerClickListener { marker ->
                if (marker.title == "Your Location") {
                    return@setOnMarkerClickListener false
                }
                showCenteredDialog(marker)
                true // consume the click, don't show default info window
            }
            Log.v("FUNCTION ---->"," showMapView Bookmark 3");
            if (specialistPostition == null) {
                Log.v("FUNCTION ---->"," showMapView Bookmark 4");
                captureLocation(false)
            } else {
                addMyLocationMarker(specialistPostition!!, googleMap)
            }
//            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(loc.latitude, loc.longitude), 10f))
        }
    }

    fun getETAUsingRoutesAPI(
        origin: LatLng,
        dest: LatLng,
        callback: (String?) -> Unit
    ) {
        try {
            val client = OkHttpClient()
            val jsonBody = """
        {
          "origin": {
            "location": {
              "latLng": {"latitude": ${origin.latitude}, "longitude": ${origin.longitude}}
            }
          },
          "destination": {
            "location": {
              "latLng": {"latitude": ${dest.latitude}, "longitude": ${dest.longitude}}
            }
          },
          "travelMode": "DRIVE"
        }
    """.trimIndent()

            val request = okhttp3.Request.Builder()
                .url("https://routes.googleapis.com/directions/v2:computeRoutes")
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", resources.getString(R.string.GoogleMapPlaces_Key))
                .addHeader("X-Goog-FieldMask", "routes.duration,routes.distanceMeters")
                .post(RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Routes API Error", e.toString())
                    callback(null)
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    val bodyString = response.body?.string()  // ✔ read once

                    if (bodyString == null) {
                        callback(null)
                        return
                    }
                    Log.v("Routes API Response", bodyString)
                    if (bodyString.contains("routes")) {
                        callback(bodyString)
                    } else {
                        Log.w("JSON", "routes key not found")
                        callback(null)
                    }

                }
            })
        } catch (e: Exception) {
            Log.e("Routes API Call", "Error logging coordinates: ${e.message}")
            callback("Unable to determine a route")
        }
    }

    fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.activity as MainActivity, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 350);
        } else {
            try {
                Log.v("FUNCTION ---->"," requestPermissionAndContinue Bookmark 1");
                val task = fusedLocationProviderClient!!.getLastLocation();
                task.addOnSuccessListener {
                    if (it != null) {
//                        Log.v("Location Captured", it.getLatitude().toString() + " " + it.getLongitude());
                        specialistPostition = LatLng(it.latitude, it.longitude)
                        Log.v("FUNCTION ---->"," requestPermissionAndContinue Bookmark 2");
                        if (gMap != null) {
                            Log.v("FUNCTION ---->", " requestPermissionAndContinue Bookmark 3");
                            addMyLocationMarker(specialistPostition!!, gMap!!)
                        }
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



    private fun showLoadingDialog(message: String = "Loading...") {
        if (loadingDialog?.isShowing == true) return

        val view = layoutInflater.inflate(R.layout.dialog_loading, null)
        view.findViewById<TextView>(R.id.tvMessage).text = message

        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()

        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun searchCarRepairShopsNewPlacesAPI(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        radius: Double
    ) {
        Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 1");
        val fieldMask = "places.displayName,places.location,places.shortFormattedAddress,places.id,places.primaryTypeDisplayName,places.types,places.businessStatus,places.websiteUri"
        var url = "https://places.googleapis.com/v1/places:searchNearby?key=$apiKey&fields=$fieldMask"
        Log.v("SEARCH =>", "Clear Saved called")
//        if (rgClearAll!!.isChecked) clearSavedApplicantShops(requireContext())
        val jsonBody = JsonObject().apply {

            add("includedTypes", JsonArray().apply {
                add("car_repair")
            })

            addProperty("maxResultCount", 20)

            add("locationRestriction", JsonObject().apply {
                add("circle", JsonObject().apply {
                    add("center", JsonObject().apply {
                        addProperty("latitude", latitude)
                        addProperty("longitude", longitude)
                    })
                    addProperty("radius", radius)
                })
            })
        }

        val jsonBodyText = JsonObject().apply {
            addProperty("textQuery", "${searchText?.text} in USA")
            addProperty("maxResultCount", 10)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        var body = jsonBody.toString().toRequestBody(mediaType)
        if (rgText != null && rgText!!.isChecked) {
            url = "https://places.googleapis.com/v1/places:searchText?key=$apiKey&fields=$fieldMask"
            body = jsonBodyText.toString().toRequestBody(mediaType)
        }

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
//                        val json = JSONObject(responseData)
//                        val places = json.optJSONArray("places") ?: JSONArray()
                        val root = JsonParser.parseString(responseData).asJsonObject

                        val places = if (root.has("places") && root.get("places").isJsonArray) {
                            root.getAsJsonArray("places")
                        } else {
                            JsonArray()
                        }
                        Log.v("PLACES RESPONSE", places.toString())

                        val gson = Gson()
                        val responseObject = gson.fromJson(responseData, PlaceSearchResponse::class.java)
//                        responseObject.places.forEach { place ->
                        if (responseObject.places.isNullOrEmpty()) {
                            activity?.runOnUiThread {
                                Utility.showUnifiedErrorDialog(activity,"No car repair shops found within the selected radius.")
                            }
                        } else {
                            Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 2");
                            activity?.runOnUiThread {
                                shopsList = responseObject.places.toMutableList()
                                saveApplicantShops(requireContext(),shopsList,
                                    clearAll = rgClearAll!!.isChecked,
                                )
                                if (clearETA) {
                                    Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 3");
                                    for (shop in shopsList) {
                                        shop.etaLabel = ""
//                                        updateApplicantShopETA(
//                                            requireContext(),
//                                            shop.id,
//                                            ""
//                                        )
                                    }
                                    clearETA = false
                                    Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 4");
                                    saveApplicantShops(requireContext(),shopsList,
                                        clearAll = false,
                                    )
                                    placesRV?.adapter = ApplicantPlacesAdapter(shopsList,requireContext(),rgLoad!!.isChecked || !rgSearch!!.isChecked,
                                        this@ApplicantMapFragment,this@ApplicantMapFragment
                                    )
                                }
                                placesRV?.adapter = ApplicantPlacesAdapter(shopsList,requireContext(),rgLoad!!.isChecked || !rgSearch!!.isChecked,this@ApplicantMapFragment,this@ApplicantMapFragment)

                                Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 5");
                                showMapView(shopsList)
//                                loadDataAndMap(true)
                                var count = 0
                                for (place in shopsList) {
                                    Log.v("PLACE URL", Constants.getApplicantMatchingFacilities + place.id + "&placeName=" + place.displayName + "&placeAddr=" + place.shortFormattedAddress,)
                                    Volley.newRequestQueue(activity).add(
                                        StringRequest(
                                            com.android.volley.Request.Method.GET,
                                            Constants.getApplicantMatchingFacilities + place.id + "&placeName=" + place.displayName.text + "&placeAddr=" + place.shortFormattedAddress,
                                            { response ->
                                                requireActivity().runOnUiThread {
                                                    Log.v("RESPONSE", response.toString())
                                                    if (response.toString().isNotEmpty()) {
                                                        val json = response.toString().trim()
                                                        val matchingFacilitiesList: ArrayList<ApplicantMatchingFacilitiesModel> =
                                                            Gson().fromJson(
                                                                json,
                                                                object : TypeToken<ArrayList<ApplicantMatchingFacilitiesModel>>() {}.type
                                                            )
                                                        if (matchingFacilitiesList.isNotEmpty()) {
                                                            place.matchingFacilities =
                                                                matchingFacilitiesList
                                                            place.aarStatus =
                                                                "Found Matching Facilities (${matchingFacilitiesList.size})"
                                                        } else {
                                                            place.matchingFacilities = emptyList()
                                                            place.aarStatus = "Not AAR Participant"
                                                        }
                                                    } else {
                                                        place.matchingFacilities = emptyList()
                                                        place.aarStatus = "Not AAR Participant"
                                                    }
                                                    place.etaLabel= ""
                                                    Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 6");
                                                    saveApplicantShops(requireContext(),shopsList,
                                                        clearAll = false,
                                                    )
//                                                    placesRV?.adapter = PlacesAdapter(shopsList,requireContext())
                                                    count++
                                                    if (count == shopsList.size) {
                                                        Log.v("FUNCTION ---->"," searchCarRepairShopsNewPlacesAPI Bookmark 7");
                                                        placesRV?.adapter = ApplicantPlacesAdapter(shopsList,requireContext(),rgLoad!!.isChecked || !rgSearch!!.isChecked,this@ApplicantMapFragment,this@ApplicantMapFragment)
                                                    }

                                                }
                                            },
                                            {
                                                it.printStackTrace()
                                            })
                                    )
                                }
                            }
                        }

//                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    println("API error: ${response.code} ${responseData}")
                }
                hideLoadingDialog()

            }
        })
    }




    fun showCenteredDialog(marker: Marker) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_marker_actions, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Optional styling
        dialog.setOnShowListener {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val titleView = dialogView.findViewById<TextView>(R.id.markerTitle)
        val actionTxt = dialogView.findViewById<TextView>(R.id.tvMessage)
        val okBtn = dialogView.findViewById<Button>(R.id.btnOK)
        val cancelBtn = dialogView.findViewById<Button>(R.id.btnCancel)
        val place = marker.tag as? Place
        if (place != null ) {
            ///HERE
            if (applicantShopSaved(requireContext(), place.id)) {
                actionTxt.text = "Do you want to remove this shop from Saved Shops?"
                okBtn.text = "Remove"
                cancelBtn.text = "Cancel"
            } else {
                actionTxt.text = "Do you want to add this shop to Saved Shops?"
                okBtn.text = "Add"
                cancelBtn.text = "Cancel"
            }
            val txt = place.displayName.text + "\n\nAddress: " + place.shortFormattedAddress
            titleView.text = txt
        }
//        titleView.text = marker.title ?: "Selected Marker"

        okBtn.setOnClickListener {
            if (okBtn.text == "Remove") {
                removeSavedApplicantShop(requireContext(), place!!)
                loadDataAndMap(true)
            } else {
//                addApplicantShop(requireContext(), place!!)
                addSavedApplicantShop(requireContext(), place!!)
                loadDataAndMap(true)

            }

            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }




    fun addMarkersFromPlaces(places: List<Place>, map: GoogleMap) {
        map.clear() // clear previous markers if any

        val boundsBuilder = LatLngBounds.Builder()
        markerMap.clear()
        for (place in places) {
            val position = LatLng(place.location.latitude, place.location.longitude)

            val title = place.displayName.text
            val snippet = place.shortFormattedAddress

            val marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            if (marker != null ) {
                markerMap[place.id] = marker
            }
            marker?.tag = place
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

    override fun refreshData() {
        loadSavedData()
    }

    override fun onCardClick(place: Place) {
        val marker = markerMap[place.id]

        // 1. Move the camera to the marker
        if (marker != null) {
            gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16f))
        }

        // Optional: Highlight marker (e.g., change icon)
//        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
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

class ApplicantPlacesAdapter(
    private val places: List<Place>,
    private val context: Context,
    private val isSavedView: Boolean,
    private val onRefresh: onRefreshListener,
    private val onClick: onCardClickListener,
) : RecyclerView.Adapter<ApplicantPlacesAdapter.StepViewHolder>() {

    interface onRefreshListener {
        fun refreshData()
    }
    interface onCardClickListener {
        fun onCardClick(place: Place)
    }

    // ViewHolder for each step item
    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_text: TextView = itemView.findViewById(R.id.place_name_val)
        val card_RL: CardView = itemView.findViewById(R.id.cardMainBorder)
        val address_text: TextView = itemView.findViewById(R.id.address_val)
        val cat_text: TextView = itemView.findViewById(R.id.main_cat_val)
//            val all_cat_text: TextView = itemView.findViewById(R.id.all_cat_val)

        val status_text: TextView = itemView.findViewById(R.id.status_val)
        val bus_status_val: TextView = itemView.findViewById(R.id.bus_status_val)
        val view_matches: ImageView = itemView.findViewById(R.id.view_matches)

        val website_val: TextView = itemView.findViewById(R.id.website_val)
        val eta_text: TextView = itemView.findViewById(R.id.etaLabel)

        val notesTxt: TextView = itemView.findViewById(R.id.notesTxt)
        val notesLL: LinearLayout = itemView.findViewById(R.id.notesLL)
        val notesET: EditText = itemView.findViewById(R.id.notesET)
        val saveNotesBtn: ImageButton = itemView.findViewById(R.id.saveNotesBtn)

        //            val eta_row: TableRow = itemView.findViewById(R.id.etaRow)
        val visitedCB: CheckBox = itemView.findViewById(R.id.visitedCB)
        val savedCB: CheckBox = itemView.findViewById(R.id.savedCB)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.name_text.text = places[position].displayName.text
        holder.address_text.text = places[position].shortFormattedAddress
        holder.cat_text.text = places[position].primaryTypeDisplayName?.text ?: "N/A"
//            holder.all_cat_text.text = places[position].types.joinToString(", ")
        holder.status_text.text = places[position].aarStatus
        holder.bus_status_val.text = capitalizeFirst(places[position].businessStatus)
        holder.website_val.text = places[position].websiteUri

        holder.savedCB.isChecked = applicantShopSaved(context,places[position].id)
        holder.saveNotesBtn.setOnClickListener {
            val notes = holder.notesET.text.toString()
            places[position].notes = notes
            updateSavedApplicantShopNotes(context, places[position].id, notes)
            Toast.makeText(context, "Notes saved", Toast.LENGTH_SHORT).show()
        }
        holder.visitedCB.isChecked = applicantShopVisited(context,places[position].id)
        holder.visitedCB.setOnClickListener {
            places[position].visited = !places[position].visited
            updateSavedApplicantShopVisited(context, places[position].id)
        }
        if (!places[position].notes.isNullOrEmpty()) {
            holder.notesTxt.setText(places[position].notes)
        }
        if (isSavedView) {
            holder.notesTxt.visibility = View.VISIBLE
            holder.notesLL.visibility = View.VISIBLE
            holder.notesET.isEnabled = true
            holder.visitedCB.isEnabled = true
            holder.visitedCB.visibility = View.VISIBLE
        } else {
            holder.notesTxt.visibility = if (places[position].notes.isNullOrEmpty()) View.GONE else View.VISIBLE
            holder.notesLL.visibility = if (places[position].notes.isNullOrEmpty()) View.GONE else View.VISIBLE
            holder.notesET.isEnabled = false
            holder.visitedCB.visibility = View.VISIBLE
            holder.visitedCB.isEnabled = false
        }

        holder.card_RL.setOnClickListener {
            onClick.onCardClick(places[position])
        }
//            holder.visitedCB.isChecked = applicantShopVisited(context,places[position].id)

        holder.savedCB.setOnClickListener {
            Log.v("SAVED CB CLICKED", "Place Name: ${places[position].displayName.text}, Checked: ${holder.savedCB.isChecked}")
            if (holder.savedCB.isChecked) {
                Log.v("SAVED CB CLICKED","  Adding to saved shops")
                addSavedApplicantShop(context, places[position])
                if (isSavedView) onRefresh.refreshData()
            } else {
                Log.v("SAVED CB CLICKED","  Remove from saved shops")
                removeSavedApplicantShop(context, places[position])
                if (isSavedView) onRefresh.refreshData()
            }
        }
        if (!places[position].matchingFacilities.isNullOrEmpty()) {
            var matchesInfo = "Matching Facilities:\n"
            for (facility in places[position].matchingFacilities) {
                matchesInfo += "• ${facility.facname} (${facility.address1})\n"
            }
            holder.view_matches.tooltipText = matchesInfo
            holder.view_matches.visibility = View.VISIBLE
            holder.view_matches.setOnClickListener {
                Utility.showUnifiedInformationDialog(context,matchesInfo)
            }
        } else {
            holder.view_matches.tooltipText = ""
            holder.view_matches.visibility = View.GONE
        }
        Log.v("ETA LABEL", "Place Name: ${places[position].displayName.text}, ETA: ${places[position].etaLabel}")
        if (places[position].etaLabel !=null && places[position].etaLabel.isNotEmpty()) {
            holder.eta_text.text = places[position].etaLabel
            holder.eta_text.isVisible = true
        } else {
            holder.eta_text.text = ""
            holder.eta_text.isVisible = false
        }
    }

    override fun getItemCount() = places.size
}