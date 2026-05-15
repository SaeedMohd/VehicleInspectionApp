package com.inspection.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup


import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.graphics.drawable.ColorDrawable
import android.widget.TextView

import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.Utility
import com.inspection.Utils.addTodayVisitation
import com.inspection.Utils.getTodayVisitations
import com.inspection.Utils.monthNoToName
import com.inspection.Utils.removeTodayVisitation
import com.inspection.Utils.saveTodayVisitations
import com.inspection.Utils.updateVisitationETA
import com.inspection.Utils.updateVisitationLocation
import com.inspection.Utils.updateVisitationNotes
import com.inspection.Utils.visitationExists

import com.inspection.databinding.FragmentTodayVisitationBinding
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.PRGDataModel
import com.inspection.model.PRGFacilityDetails
import com.inspection.model.PRGFacilityDirectors
import com.inspection.model.PRGFacilityPhotos
import com.inspection.model.PRGFacilityShopHolidayTimes
import com.inspection.model.PRGLogChanges
import com.inspection.model.PRGPersonnelDetails
import com.inspection.model.PRGRepairDiscountFactors
import com.inspection.model.PRGVisitationHeader
import com.inspection.model.PRGVisitationsLog

import com.inspection.model.TodayVisitationModel
import com.inspection.model.VisitationTypes
import okhttp3.Call
import okhttp3.Callback

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
//import shaded.org.json.JSONObject
//import shaded.org.json.XML
//import org.json.JSONObject
//import org.json.XML
import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import androidx.core.graphics.createBitmap
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.inspection.Utils.normalizeJson
import com.inspection.model.AffiliateVendorFacilities
import com.inspection.model.FacilityDataModelOrg
import com.inspection.model.FacilityPhotos
import com.inspection.model.HasChangedModel
import com.inspection.model.InvoiceInfo
import com.inspection.model.TblAAAPortalEmailFacilityRepTable
import com.inspection.model.TblAARPortalAdmin
import com.inspection.model.TblAARPortalTracking
import com.inspection.model.TblAddress
import com.inspection.model.TblAffiliations
import com.inspection.model.TblAmendmentOrderTracking
import com.inspection.model.TblBilling
import com.inspection.model.TblBillingAdjustments
import com.inspection.model.TblBillingHistory
import com.inspection.model.TblBillingPlan
import com.inspection.model.TblBusinessType
import com.inspection.model.TblComments
import com.inspection.model.TblComplaintFiles
import com.inspection.model.TblContractType
import com.inspection.model.TblDeficiency
import com.inspection.model.TblFacVehicles
import com.inspection.model.TblFacilities
import com.inspection.model.TblFacilityBillingDetail
import com.inspection.model.TblFacilityBillingHeader
import com.inspection.model.TblFacilityClosure
import com.inspection.model.TblFacilityEmail
import com.inspection.model.TblFacilityManagers
import com.inspection.model.TblFacilityPhotos
import com.inspection.model.TblFacilityServiceProvider
import com.inspection.model.TblFacilityServices
import com.inspection.model.TblFacilityType
import com.inspection.model.TblGeocodes
import com.inspection.model.TblHours
import com.inspection.model.TblInvoiceInfo
import com.inspection.model.TblLanguage
import com.inspection.model.TblOfficeType
import com.inspection.model.TblPaymentMethods
import com.inspection.model.TblPersonnel
import com.inspection.model.TblPersonnelCertification
import com.inspection.model.TblPersonnelSigner
import com.inspection.model.TblPhone
import com.inspection.model.TblPrograms
import com.inspection.model.TblPromotions
import com.inspection.model.TblScopeofService
import com.inspection.model.TblSurveySoftwares
import com.inspection.model.TblTerminationCodeType
import com.inspection.model.TblTimezoneType
import com.inspection.model.TblVehicleServices
import com.inspection.model.TblVendorRevenue
import com.inspection.model.TblVisitationTracking
import com.inspection.model.justifiedComplaintRatio
import com.inspection.model.numberofComplaints
import com.inspection.model.numberofJustifiedComplaints
import com.inspection.serverTasks.GetShopUserProfileDetails
import com.inspection.utils.XmlUtils
import com.inspection.utils.XmlUtils.xmlToJsonObject

//import org.json.XML

//import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TodayVisitationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodayVisitationFragment : Fragment(),
    PlacesAdapter.VisitActionListener,
    PlacesAdapter.FullDataListener,
    PlacesAdapter.OnVoiceClickListener,
    PlacesAdapter.CardListener,
    PlacesAdapter.OnVoiceStopClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentTodayVisitationBinding? = null
    private val binding get() = _binding!!
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var specialistPostition: LatLng? = null
    val mapFragment = SupportMapFragment.newInstance()
    var gMap: GoogleMap? = null
    private var clearETA = true
    private lateinit var speechRecognizer: SpeechRecognizer
//    private lateinit var speechIntent: Intent
//    private var currentListeningFacNo = 0
//    private var currentListeningClubCode = ""
    var markerMap = mutableMapOf<TodayVisitationModel, Marker>()
    var visitsList = mutableListOf<TodayVisitationModel>()
    lateinit var placesAdapter: PlacesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            1
        )
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
//
//        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // or "ar-EG"
//            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
//        }
//
//        setupListener()
    }

    private fun vibrate(duration: Long = 40) {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(duration)
        }
    }

    fun createNumberedMarker(number: Int): BitmapDescriptor {
        val size = 50
        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)
        val paintCircle = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val paintText = Paint().apply {
            color = Color.WHITE
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
        }

        // Draw circle
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paintCircle)

        // Draw number
        canvas.drawText(number.toString(), size / 2f, size / 2f + 10f, paintText)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_visitation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTodayVisitationBinding.bind(view)
        binding.listRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        binding.reloadBtn.setOnClickListener {
            binding.progressBarText.text = "Loading ..."
            binding.recordsProgressView.visibility = View.VISIBLE
            loadDataAndMap(true)
            binding.recordsProgressView.visibility = View.GONE
        }
        binding.clearBtn.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.decision_dialog, null)
            val alertBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
            val dialogMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
            val btnPositiveAction = dialogView.findViewById<Button>(R.id.btnActionPositive)
            val btnNegativeAction = dialogView.findViewById<Button>(R.id.btnActionNegative)
            dialogTitle.text = "Clear Planned Visits"
            dialogMessage.text = "This will remove all planned visits for today. Continue?"
            btnPositiveAction.text = "Clear All"
            btnNegativeAction.text = "Cancel"
            val dialog = alertBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            btnPositiveAction.setOnClickListener {
                dialog.dismiss()
                binding.progressBarText.text = "Loading ..."
                binding.recordsProgressView.visibility = View.VISIBLE
                saveTodayVisitations(requireContext(), ArrayList())
                loadDataAndMap(true)
                binding.recordsProgressView.visibility = View.GONE
            }
            btnNegativeAction.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
        visitsList = getTodayVisitations(requireContext()).toMutableList()
        if (clearETA) {
            for (visit in visitsList) {
                visit.etaLabel = ""
                updateVisitationETA(
                    requireContext(),
                    visit.facNum.toString(),
                    visit.clubCode,
                    ""
                )
            }
            clearETA = false
        }
        loadDataAndMap(true)

    }

    override fun onReloadMap(updateAdapter: Boolean) {
        loadDataAndMap(updateAdapter)  // your actual method
    }

    override fun onVoiceClicked(facilityNumber: Int, clubCode: String) {
        VoiceNoteBottomSheet.show(parentFragmentManager) { recognizedText ->
            updateVisitationNotes(requireContext(), facilityNumber, clubCode, recognizedText)
            loadDataAndMap(true)
        }
    }

    override fun onVoiceStopClicked() {
        speechRecognizer.stopListening()
    }

    override fun onGetFullData(
        facilityNumber: Int,
        clubCode: String,
        visitationType: VisitationTypes
    ) {
        getFullFacilityDataFromAAA(facilityNumber, clubCode, false, visitationType)
    }

    fun loadDataAndMap(updateAdapter: Boolean) {

        visitsList = getTodayVisitations(requireContext()).toMutableList()
//        if (specialistPostition == null) {
//            for (visit in visitsList) {
//                if (specialistPostition == null) {
//                    visit.etaLabel = ""
//                    break
//                }
//            }
//        }
        visitsList = visitsList.sortedBy { it.order }.toMutableList()
        if (visitsList.isEmpty()) {
            binding.emptyStateView.visibility = View.VISIBLE
            binding.listRecyclerView.visibility = View.GONE
            binding.emptyStateLottie.playAnimation()
        } else {
            binding.emptyStateView.visibility = View.GONE
            binding.listRecyclerView.visibility = View.VISIBLE
        }
        binding.plannedVal.text = visitsList.size.toString()
        binding.completedVal.text =
            visitsList.filter { s -> s.status == "Completed" }.size.toString()
        val totalRequests = visitsList.count { it.latitude == 0.0 && it.longitude == 0.0 }
        Log.v("Total location requests", totalRequests.toString())
        var completedRequests = 0
        if (totalRequests == 0) {
            if (updateAdapter) {
                placesAdapter = PlacesAdapter(visitsList.toMutableList(), this, this, this, this,this)
                binding.listRecyclerView.adapter = placesAdapter
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
                        placesAdapter.moveItem(context,from, to)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        // No swipe behavior
                    }

                    override fun isLongPressDragEnabled(): Boolean = true // or false if you use a drag handle
                }
                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(binding.listRecyclerView)
            }
            showMapView(visitsList)
            return
        } else {
            for (visit in visitsList) {
                Log.v("Checking location for ", "${visit.facNum} - ${visit.clubCode}")
                if (visit.latitude != 0.0 && visit.longitude != 0.0) {
                    continue
                }
                Volley.newRequestQueue(activity).add(
                    StringRequest(Request.Method.GET,
                        Constants.getFacilityLocation + visit.facNum + "&clubCode=" + visit.clubCode,
                        { response ->
                            requireActivity().runOnUiThread {
                                Log.v("RESPONSE", response.toString())
                                if (response.toString().isNotEmpty()) {
                                    val parts = response.split(",")
                                    if (parts.size == 2) {
                                        val latitude = parts[0].toDoubleOrNull()
                                        val longitude = parts[1].toDoubleOrNull()
                                        if (latitude != null && longitude != null) {
                                            visit.latitude = latitude
                                            visit.longitude = longitude
                                            updateVisitationLocation(
                                                requireContext(),
                                                visit.facNum,
                                                visit.clubCode,
                                                latitude,
                                                longitude
                                            )
                                            Log.v("Location updated ", "$latitude , $longitude")
                                        }
                                    }
                                }
                                completedRequests++
                                if (completedRequests == totalRequests) {
                                    if (updateAdapter)
                                        binding.listRecyclerView.adapter = PlacesAdapter(
                                            visitsList.toMutableList(),
                                            this,
                                            this,
                                            this,
                                            this,
                                            this
                                        )
                                    showMapView(visitsList)
                                }
                            }
                        },
                        {
                            completedRequests++
                            if (completedRequests == totalRequests) {
                                if (updateAdapter)
                                    binding.listRecyclerView.adapter = PlacesAdapter(
                                        visitsList.toMutableList(),
                                        this,
                                        this,
                                        this,
                                        this,
                                        this
                                    )
                                showMapView(visitsList)
                            }
                        })
                )
            }
        }
        if (updateAdapter)
            binding.listRecyclerView.adapter =
                PlacesAdapter(visitsList.toMutableList(), this, this, this, this,this)
    }

    fun buildDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"
        val mode = "mode=driving"
        val params =
            "$strOrigin&$strDest&$mode&key=${resources.getString(R.string.GoogleMapPlaces_Key)}"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    fun getETA(origin: LatLng, dest: LatLng, callback: (String?) -> Unit) {
        val url = buildDirectionsUrl(origin, dest)

        val client = OkHttpClient()
        val request = okhttp3.Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response.body?.string()
                Log.v("Directions Response", body ?: "No response body")
                callback(body)
            }
        })
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
                    callback(bodyString)
                }
            })
        } catch (e: Exception) {
            Log.e("Routes API Call", "Error logging coordinates: ${e.message}")
            callback("Unable to determine a route")
        }
    }

//    fun parseETA(json: String?): String? {
//        if (json == null) return null
//        val obj = JSONObject(json)
//
//        val routes = obj.getJSONArray("routes")
//        if (routes.length() == 0) return null
//
//        val legs = routes.getJSONObject(0).getJSONArray("legs")
//        val leg = legs.getJSONObject(0)
//
//        // You can read either:
//        val durationText = leg.getJSONObject("duration").getString("text")
//        // Or:
//        // val durationTraffic = leg.getJSONObject("duration_in_traffic").getString("text")
//
//        return durationText
//    }

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

    private fun captureLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (checkLocationPermissions()) {
            try {
                val task = fusedLocationProviderClient!!.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                            return CancellationTokenSource().token
                        }

                        override fun isCancellationRequested(): Boolean {
                            return false
                        }
                    })
                task.addOnSuccessListener {
                    if (it != null) {
                        Log.v(
                            "Location Captured",
                            it.getLatitude().toString() + " " + it.getLongitude()
                        )
//                        searchCarRepairShopsNewPlacesAPI(it.latitude, it.longitude, getString(R.string.GoogleMapPlaces_Key), selected_radius)
                        specialistPostition = LatLng(it.latitude, it.longitude)
                        if (gMap != null)
                            addMyLocationMarker(specialistPostition!!, gMap!!)
                    } else {
                        Utility.showUnifiedErrorDialog(
                            activity,
                            "Unable to capture current location"
                        )
                    }
                }
            } catch (e: SecurityException) {

            }
        } else {
            if (!checkLocationPermissions()) {
                if (MainActivity.activity != null) {
                    requestPermissionAndContinue()
                }
            } else {
                try {
                    val task = fusedLocationProviderClient!!.getLastLocation()
                    task.addOnSuccessListener {
                        if (it != null) {
                            Log.v(
                                "Location Captured",
                                it.getLatitude().toString() + " " + it.getLongitude()
                            )
                            specialistPostition = LatLng(it.latitude, it.longitude)
                            if (gMap != null)
                                addMyLocationMarker(specialistPostition!!, gMap!!)
//                            searchCarRepairShopsNewPlacesAPI(it.latitude, it.longitude, getString(R.string.GoogleMapPlaces_Key), selected_radius)
                        } else {
//                            Utility.showMessageDialog(activity,"Information","Unable to capture current location")
                            Utility.showUnifiedErrorDialog(
                                activity,
                                "Unable to capture current location"
                            )
                        }
                    }
                } catch (e: SecurityException) {

                }
            }
        }
    }

    fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun addMyLocationMarker(myLocation: LatLng, map: GoogleMap) {
//        map.clear() // clear previous markers if any

        val boundsBuilder = LatLngBounds.Builder()
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
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            for (place in visitsList) {
                if (place.latitude != 0.0 && place.etaLabel.isEmpty()) {
                    place.etaLabel = "Calculating route…"
                    updateVisitationETA(requireContext(), place.facNum.toString(), place.clubCode, "Calculating route…")
                }
            }
            if (::placesAdapter.isInitialized) {
                placesAdapter.notifyDataSetChanged()
            }

            var completed = 0
            for (place in visitsList) {
                if ((place.latitude == 0.0 && place.longitude == 0.0) || (place.etaLabel.isNotEmpty() && place.etaLabel.contains(
                        "Distance:"
                    ))
                ) {
                    completed++
                    if (completed == visitsList.size) {
                        loadDataAndMap(true)
                    }
                    continue
                }
                getETAUsingRoutesAPI(
                    specialistPostition!!,
                    LatLng(place.latitude, place.longitude)
                ) { response ->
                    requireActivity().runOnUiThread {
                        Log.v("getETAUsingRoutesAPI", " Called for ${place.facNum} - ${place.clubCode}")
                        val eta = if (response == "Unable to determine a route") {
                            response
                        } else {
                            parseNewRoutesETA(response) ?: "N/A"
                        }
                        updateVisitationETA(
                            requireContext(),
                            place.facNum.toString(),
                            place.clubCode,
                            eta
                        )
                        Log.d("ETA", "ETA: $eta")
                        completed++
                        if (completed == visitsList.size) {
                            loadDataAndMap(true)
                        }
                    }
                }
            }
            boundsBuilder.include(position)
        }
//
//        val positions = places.map {
//            LatLng(it.latitude, it.longitude)
//        }
//        zoomToFitAllMarkers(map, positions)
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
        val place = marker.tag as? TodayVisitationModel
        if (place != null ) {
            if (visitationExists(requireContext(), place.facNum, place.clubCode)) {
                actionTxt.text = "Do you want to remove this visitation?"
                okBtn.text = "Remove"
                cancelBtn.text = "Cancel"
            } else {
                actionTxt.text = "Do you want to add this visitation?"
                okBtn.text = "Add"
                cancelBtn.text = "Cancel"
            }
            val txt = place.facName + "\n\nFac Num: " + place.facNum + " - Club Code: " + place.clubCode + "\n\nCity: " + place.city + "\n\n" +
                    "Status: " + place.status + " - Type: " + place.type
            titleView.text = txt
        }
//        titleView.text = marker.title ?: "Selected Marker"

        okBtn.setOnClickListener {
            if (okBtn.text == "Remove") {
                removeTodayVisitation(requireContext(), place!!.facNum.toString(), place.clubCode)
//                requireActivity().runOnUiThread {
//                    binding.progressBarText.text = "Loading ..."
//                    binding.recordsProgressView.visibility = View.VISIBLE
                    loadDataAndMap(true)
//                    binding.recordsProgressView.visibility = View.GONE
//                }

            } else {
                addTodayVisitation(requireContext(), place!!)
//                requireActivity().runOnUiThread {
//                    binding.progressBarText.text = "Loading ..."
//                    binding.recordsProgressView.visibility = View.VISIBLE
                    loadDataAndMap(true)
//                    binding.recordsProgressView.visibility = View.GONE
//                }
            }

            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun addMarkersFromPlaces(places: List<TodayVisitationModel>, map: GoogleMap) {
        map.clear() // clear previous markers if any

        val boundsBuilder = LatLngBounds.Builder()
        markerMap.clear()
        for (place in places) {
            val position = LatLng(place.latitude, place.longitude)
            if (place.latitude == 0.0 && place.longitude == 0.0) {
                continue
            }
            val title = place.facName
            val snippet = "Fac Num: ${place.facNum} - Club Code: ${place.clubCode}"

            val marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(title)
                    .snippet(snippet)
                    .icon(createNumberedMarker(place.order))
            )
            if (marker != null) {
                markerMap[place] = marker
            }
            marker?.tag = place
            boundsBuilder.include(position)
        }
        if (specialistPostition != null) {
            map.addMarker(
                MarkerOptions()
                    .position(specialistPostition!!)
                    .title("Your Location")
                    .snippet("")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
//            getETA(specialistPostition!!, LatLng(places[0].latitude,places[0].longitude) ) { json ->
//                val eta = parseETA(json)
//                Log.d("ETA", "ETA is: $eta")
//            }
//            getETAUsingRoutesAPI(specialistPostition!!, LatLng(places[0].latitude,places[0].longitude)) { response ->
//                Log.v("getETAUsingRoutesAPI 2 ", " Called for ${place.facNum} - ${place.clubCode}")
//                val eta = parseNewRoutesETA(response)
//                Log.d("ETA", "ETA: $eta")
//            }
            boundsBuilder.include(specialistPostition!!)
        }

        // Move/zoom camera to fit all markers
//        if (places.isNotEmpty()) {
//            val bounds = boundsBuilder.build()
//            val padding = 100 // pixels
//            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
//        }
        val positions = places.map {
            LatLng(it.latitude, it.longitude)
        }

        zoomToFitAllMarkers(map, positions)
    }

    fun getMarkerColor(order: Int): Float {
        val color = when (order) {
            1 -> Color.BLUE
            2 -> Color.GREEN
            3 -> Color.YELLOW
            4 -> Color.parseColor("#FFA500")
            5 -> Color.parseColor("#FF007F")
            6 -> Color.parseColor("#8F00FF")
            7 -> Color.CYAN
            8 -> Color.parseColor("#007FFF")
            9 -> Color.MAGENTA
            10 -> Color.RED
            else -> Color.GRAY
        }
        return getHueFromColor(color)
    }

    fun getHueFromColor(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[0]
    }

    fun zoomToFitAllMarkers(googleMap: GoogleMap, markerPositions: List<LatLng>) {
        if (markerPositions.isEmpty()) return
        var hasPositions = false
        val builder = LatLngBounds.Builder()
        for (position in markerPositions) {
            if (position.latitude == 0.0 && position.longitude == 0.0) {
                continue
            }
            builder.include(position)
            hasPositions = true
        }
        if (hasPositions) {
            val bounds = builder.build()
            val padding = 100 // pixels

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }
    }

    fun showMapView(places: List<TodayVisitationModel>) {
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
            addMarkersFromPlaces(places, googleMap)
            googleMap.setOnMarkerClickListener { marker ->
                if (marker.title == "Your Location") {
                    return@setOnMarkerClickListener false
                }
                showCenteredDialog(marker)
                true // consume the click, don't show default info window
            }

            if (specialistPostition == null) {
                captureLocation()
            }
        }
    }

    fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                MainActivity.activity as MainActivity, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 350
            );
        } else {
            try {
                val task = fusedLocationProviderClient!!.getLastLocation();
                task.addOnSuccessListener {
                    if (it != null) {
                        Log.v(
                            "Location Captured",
                            it.getLatitude().toString() + " " + it.getLongitude()
                        );
                        specialistPostition = LatLng(it.latitude, it.longitude)
                        if (gMap != null)
                            addMyLocationMarker(specialistPostition!!, gMap!!)
                    } else {
                        Utility.showUnifiedErrorDialog(
                            activity,
                            "Unable to capture current location"
                        )
                    }
                }
//                task.addOnFailureListener {e: Exception ->
//                    Utility.showMessageDialog(activity,"Information","Unable to capture current location - ${e.message}")
//                }
            } catch (e: SecurityException) {

            }
        }
    }

    fun getColoredMarker(context: Context, @ColorInt color: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, R.drawable.icon_marker_fill)!!
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, color)

        val metrics = context.resources.displayMetrics
        val widthPx = (50 * metrics.density).toInt()
        val heightPx = (50 * metrics.density).toInt()

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        wrappedDrawable.setBounds(0, 0, canvas.width, canvas.height)
        wrappedDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun getFullFacilityDataFromAAA(
        facilityNumber: Int,
        clubCode: String,
        isCompleted: Boolean,
        visitationType: VisitationTypes
    )
    {
        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request2 = okhttp3.Request.Builder().url(
            String.format(
                Constants.getFacilityData + Utility.getLoggingParameters(
                    activity,
                    0,
                    "Load Visitations ..."
                ), facilityNumber, clubCode
            )
        ).build()
        binding.progressBarText.text = "Loading ..."
        binding.recordsProgressView.visibility = View.VISIBLE

        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
//                    Utility.showMessageDialog(activity, "Retrieve Data Error", "Origin ERROR Connection Error. Please check internet connection - " + e?.message)
                    Utility.showUnifiedErrorDialog(activity, "Get Facility Data - " + e.message)
                    binding.recordsProgressView.visibility = View.GONE
                    binding.progressBarText.text = "Loading ..."
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                var responseString = response!!.body!!.string()
                activity!!.runOnUiThread {
//                    binding.recordsProgressView.visibility = View.GONE
                    binding.progressBarText.text = "Loading ..."
                    if (!responseString.contains("FacID not found")) {
                        if (responseString.toString().contains("returnCode>1<", false)) {
                            activity!!.runOnUiThread {
//                                Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
                                Utility.showUnifiedErrorDialog(
                                    activity,
                                    "Get Facility Data - " + responseString.substring(
                                        responseString.indexOf("<message") + 9,
                                        responseString.indexOf("</message")
                                    )
                                )
                            }
                        } else {
//                            var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&")
//                                    .replace("<tblSurveySoftwares/><tblSurveySoftwares><ShopMgmtSoftwareName/></tblSurveySoftwares>", ""))
                            val xmlPart = responseString.substring(
                                responseString.indexOf("<responseXml"),
                                responseString.indexOf("<returnCode")
                            ).replace(
                                "<tblSurveySoftwares/><tblSurveySoftwares><ShopMgmtSoftwareName/></tblSurveySoftwares>",
                                ""
                            )
//                                .replace("&amp;", "&")
                            val rootJson = xmlToJsonObject(xmlPart)
//                            val jsonObj = rootJson.getAsJsonObject("responseXml")
//                            var jsonObj = rootJson
                            var jsonObj = normalizeJson(rootJson)
                            parseFacilityDataJsonToObject(jsonObj,clubCode)
                            getFacilityPRGData(isCompleted)
                            FirebaseCrashlytics.getInstance().log("User Clicked Load Facility")
                            FirebaseCrashlytics.getInstance()
                                .setCustomKey("Facility", facilityNumber.toString())
                            FirebaseCrashlytics.getInstance()
                                .setCustomKey("ClubCode", clubCode.toString())
                            FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = visitationType
                        }
                    } else {
                        activity!!.runOnUiThread {
//                            Utility.showMessageDialog(activity, "Retrieve Data Error", "Facility data not found")
                            Utility.showUnifiedErrorDialog(activity, "Facility data not found")
                        }
                    }
                }
            }

        })

    }

    fun parseFacilityDataJsonToObject(jsonObj: JsonObject,clubCode: String) {
        FacilityDataModel.getInstance().clear()
        FacilityDataModelOrg.getInstance().clear()
        FacilityDataModel.getInstance().clubCode = clubCode
        FacilityDataModelOrg.getInstance().clubCode = clubCode
        if (jsonObj.has("tblFacilities")) {
            if (jsonObj.get("tblFacilities").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilities = Gson().fromJson<ArrayList<TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<TblFacilities>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilities = Gson().fromJson<ArrayList<TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<TblFacilities>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilities.add(Gson().fromJson<TblFacilities>(jsonObj.get("tblFacilities").toString(), TblFacilities::class.java))
                FacilityDataModelOrg.getInstance().tblFacilities.add(Gson().fromJson<TblFacilities>(jsonObj.get("tblFacilities").toString(), TblFacilities::class.java))
            }
        }

        if (jsonObj.has("tblBusinessType")) {
            if (jsonObj.get("tblBusinessType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBusinessType = Gson().fromJson<ArrayList<TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<TblBusinessType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBusinessType = Gson().fromJson<ArrayList<TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<TblBusinessType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBusinessType.add(Gson().fromJson<TblBusinessType>(jsonObj.get("tblBusinessType").toString(), TblBusinessType::class.java))
                FacilityDataModelOrg.getInstance().tblBusinessType.add(Gson().fromJson<TblBusinessType>(jsonObj.get("tblBusinessType").toString(), TblBusinessType::class.java))
            }
        }

        if (jsonObj.has("tblContractType")) {
            if (jsonObj.get("tblContractType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblContractType = Gson().fromJson<ArrayList<TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<TblContractType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblContractType = Gson().fromJson<ArrayList<TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<TblContractType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblContractType.add(Gson().fromJson<TblContractType>(jsonObj.get("tblContractType").toString(), TblContractType::class.java))
                FacilityDataModelOrg.getInstance().tblContractType.add(Gson().fromJson<TblContractType>(jsonObj.get("tblContractType").toString(), TblContractType::class.java))
            }
        }

        if (jsonObj.has("tblFacilityServiceProvider")) {
            if (jsonObj.get("tblFacilityServiceProvider").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<TblFacilityServiceProvider>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<TblFacilityServiceProvider>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), TblFacilityServiceProvider::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), TblFacilityServiceProvider::class.java))
            }
        }

        if (jsonObj.has("tblTerminationCodeType")) {
            if (jsonObj.get("tblTerminationCodeType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<TblTerminationCodeType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<TblTerminationCodeType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblTerminationCodeType.add(Gson().fromJson<TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), TblTerminationCodeType::class.java))
                FacilityDataModelOrg.getInstance().tblTerminationCodeType.add(Gson().fromJson<TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), TblTerminationCodeType::class.java))
            }
        }

        if (jsonObj.has("tblOfficeType")) {
            if (jsonObj.get("tblOfficeType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblOfficeType = Gson().fromJson<ArrayList<TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<TblOfficeType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblOfficeType = Gson().fromJson<ArrayList<TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<TblOfficeType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblOfficeType.add(Gson().fromJson<TblOfficeType>(jsonObj.get("tblOfficeType").toString(), TblOfficeType::class.java))
                FacilityDataModelOrg.getInstance().tblOfficeType.add(Gson().fromJson<TblOfficeType>(jsonObj.get("tblOfficeType").toString(), TblOfficeType::class.java))
            }
        }

        if (jsonObj.has("tblFacilityManagers")) {
            if (jsonObj.get("tblFacilityManagers").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<TblFacilityManagers>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<TblFacilityManagers>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityManagers.add(Gson().fromJson<TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), TblFacilityManagers::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityManagers.add(Gson().fromJson<TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), TblFacilityManagers::class.java))
            }
        }

        if (jsonObj.has("tblTimezoneType")) {
            if (jsonObj.get("tblTimezoneType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<TblTimezoneType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<TblTimezoneType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblTimezoneType.add(Gson().fromJson<TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), TblTimezoneType::class.java))
                FacilityDataModelOrg.getInstance().tblTimezoneType.add(Gson().fromJson<TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), TblTimezoneType::class.java))
            }
        }


        if (jsonObj.has("tblVisitationTracking")) {
            if (jsonObj.get("tblVisitationTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<TblVisitationTracking>>() {}.type)
                FacilityDataModelOrg.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<TblVisitationTracking>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblVisitationTracking.add(Gson().fromJson<TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), TblVisitationTracking::class.java))
                FacilityDataModelOrg.getInstance().tblVisitationTracking.add(Gson().fromJson<TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), TblVisitationTracking::class.java))
            }
        }

        if (jsonObj.has("tblFacilityType")) {
            if (jsonObj.get("tblFacilityType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityType = Gson().fromJson<ArrayList<TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<TblFacilityType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityType = Gson().fromJson<ArrayList<TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<TblFacilityType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityType.add(Gson().fromJson<TblFacilityType>(jsonObj.get("tblFacilityType").toString(), TblFacilityType::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityType.add(Gson().fromJson<TblFacilityType>(jsonObj.get("tblFacilityType").toString(), TblFacilityType::class.java))
            }
        }

        if (jsonObj.has("tblSurveySoftwares")) {
            if (jsonObj.get("tblSurveySoftwares").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<TblSurveySoftwares>>() {}.type)
                FacilityDataModelOrg.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<TblSurveySoftwares>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblSurveySoftwares.add(Gson().fromJson<TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), TblSurveySoftwares::class.java))
                FacilityDataModelOrg.getInstance().tblSurveySoftwares.add(Gson().fromJson<TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), TblSurveySoftwares::class.java))
            }
        }


        if (jsonObj.has("tblPaymentMethods")) {
            if (jsonObj.get("tblPaymentMethods").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<TblPaymentMethods>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<TblPaymentMethods>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPaymentMethods.add(Gson().fromJson<TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), TblPaymentMethods::class.java))
                FacilityDataModelOrg.getInstance().tblPaymentMethods.add(Gson().fromJson<TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), TblPaymentMethods::class.java))
            }
        }

        if (jsonObj.has("tblAddress")) {
            if (jsonObj.get("tblAddress").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAddress = Gson().fromJson<ArrayList<TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<TblAddress>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAddress = Gson().fromJson<ArrayList<TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<TblAddress>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAddress.add(Gson().fromJson<TblAddress>(jsonObj.get("tblAddress").toString(), TblAddress::class.java))
                FacilityDataModel.getInstance().tblAddress.add(Gson().fromJson<TblAddress>(jsonObj.get("tblAddress").toString(), TblAddress::class.java))
            }
        }

        if (jsonObj.has("tblPhone")) {
            if (jsonObj.get("tblPhone").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPhone = Gson().fromJson<ArrayList<TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<TblPhone>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPhone = Gson().fromJson<ArrayList<TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<TblPhone>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPhone.add(Gson().fromJson<TblPhone>(jsonObj.get("tblPhone").toString(), TblPhone::class.java))
                FacilityDataModelOrg.getInstance().tblPhone.add(Gson().fromJson<TblPhone>(jsonObj.get("tblPhone").toString(), TblPhone::class.java))
            }
        }

        if (jsonObj.has("tblFacilityEmail")) {
            if (jsonObj.get("tblFacilityEmail").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<TblFacilityEmail>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<TblFacilityEmail>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityEmail.add(Gson().fromJson<TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), TblFacilityEmail::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityEmail.add(Gson().fromJson<TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), TblFacilityEmail::class.java))
            }
        }

        if (jsonObj.has("tblHours")) {
            if (jsonObj.get("tblHours").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblHours = Gson().fromJson<ArrayList<TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<TblHours>>() {}.type)
                FacilityDataModelOrg.getInstance().tblHours = Gson().fromJson<ArrayList<TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<TblHours>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblHours.add(Gson().fromJson<TblHours>(jsonObj.get("tblHours").toString(), TblHours::class.java))
                FacilityDataModelOrg.getInstance().tblHours.add(Gson().fromJson<TblHours>(jsonObj.get("tblHours").toString(), TblHours::class.java))
            }
        }

        if (jsonObj.has("tblFacilityClosure")) {
            if (jsonObj.get("tblFacilityClosure").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<TblFacilityClosure>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<TblFacilityClosure>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityClosure.add(Gson().fromJson<TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), TblFacilityClosure::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityClosure.add(Gson().fromJson<TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), TblFacilityClosure::class.java))
            }
        }

        if (jsonObj.has("tblLanguage")) {
            if (jsonObj.get("tblLanguage").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblLanguage = Gson().fromJson<ArrayList<TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<TblLanguage>>() {}.type)
                FacilityDataModelOrg.getInstance().tblLanguage = Gson().fromJson<ArrayList<TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<TblLanguage>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblLanguage.add(Gson().fromJson<TblLanguage>(jsonObj.get("tblLanguage").toString(), TblLanguage::class.java))
                FacilityDataModelOrg.getInstance().tblLanguage.add(Gson().fromJson<TblLanguage>(jsonObj.get("tblLanguage").toString(), TblLanguage::class.java))
            }
        }

        if (jsonObj.has("tblPersonnel")) {
            if (jsonObj.get("tblPersonnel").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPersonnel = Gson().fromJson<ArrayList<TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<TblPersonnel>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPersonnel = Gson().fromJson<ArrayList<TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<TblPersonnel>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPersonnel.add(Gson().fromJson<TblPersonnel>(jsonObj.get("tblPersonnel").toString(), TblPersonnel::class.java))
                FacilityDataModelOrg.getInstance().tblPersonnel.add(Gson().fromJson<TblPersonnel>(jsonObj.get("tblPersonnel").toString(), TblPersonnel::class.java))
            }
        }

        if (jsonObj.has("tblAmendmentOrderTracking")) {
            if (jsonObj.get("tblAmendmentOrderTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<TblAmendmentOrderTracking>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<TblAmendmentOrderTracking>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), TblAmendmentOrderTracking::class.java))
                FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), TblAmendmentOrderTracking::class.java))
            }
        }

        if (jsonObj.has("tblAARPortalAdmin")) {
            if (jsonObj.get("tblAARPortalAdmin").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<TblAARPortalAdmin>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<TblAARPortalAdmin>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAARPortalAdmin.add(Gson().fromJson<TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), TblAARPortalAdmin::class.java))
                FacilityDataModelOrg.getInstance().tblAARPortalAdmin.add(Gson().fromJson<TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), TblAARPortalAdmin::class.java))
            }
        }

        if (jsonObj.has("tblScopeofService")) {
            if (jsonObj.get("tblScopeofService").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblScopeofService = Gson().fromJson<ArrayList<TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<TblScopeofService>>() {}.type)
                FacilityDataModelOrg.getInstance().tblScopeofService = Gson().fromJson<ArrayList<TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<TblScopeofService>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblScopeofService.add(Gson().fromJson<TblScopeofService>(jsonObj.get("tblScopeofService").toString(), TblScopeofService::class.java))
                FacilityDataModelOrg.getInstance().tblScopeofService.add(Gson().fromJson<TblScopeofService>(jsonObj.get("tblScopeofService").toString(), TblScopeofService::class.java))
            }
        }

        if (jsonObj.has("tblPrograms")) {
            if (jsonObj.get("tblPrograms").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPrograms = Gson().fromJson<ArrayList<TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<TblPrograms>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPrograms = Gson().fromJson<ArrayList<TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<TblPrograms>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPrograms.add(Gson().fromJson<TblPrograms>(jsonObj.get("tblPrograms").toString(), TblPrograms::class.java))
                FacilityDataModelOrg.getInstance().tblPrograms.add(Gson().fromJson<TblPrograms>(jsonObj.get("tblPrograms").toString(), TblPrograms::class.java))
            }
        }

        if (jsonObj.has("tblFacilityServices")) {
            if (jsonObj.get("tblFacilityServices").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<TblFacilityServices>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<TblFacilityServices>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityServices.add(Gson().fromJson<TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), TblFacilityServices::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityServices.add(Gson().fromJson<TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), TblFacilityServices::class.java))
            }
        }

        if (jsonObj.has("tblAffiliations")) {
            if (jsonObj.get("tblAffiliations").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAffiliations = Gson().fromJson<ArrayList<TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<TblAffiliations>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAffiliations = Gson().fromJson<ArrayList<TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<TblAffiliations>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAffiliations.add(Gson().fromJson<TblAffiliations>(jsonObj.get("tblAffiliations").toString(), TblAffiliations::class.java))
                FacilityDataModelOrg.getInstance().tblAffiliations.add(Gson().fromJson<TblAffiliations>(jsonObj.get("tblAffiliations").toString(), TblAffiliations::class.java))
            }
        }

        if (jsonObj.has("tblDeficiency")) {
            if (jsonObj.get("tblDeficiency").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblDeficiency = Gson().fromJson<ArrayList<TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<TblDeficiency>>() {}.type)
                FacilityDataModelOrg.getInstance().tblDeficiency = Gson().fromJson<ArrayList<TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<TblDeficiency>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblDeficiency.add(Gson().fromJson<TblDeficiency>(jsonObj.get("tblDeficiency").toString(), TblDeficiency::class.java))
                FacilityDataModelOrg.getInstance().tblDeficiency.add(Gson().fromJson<TblDeficiency>(jsonObj.get("tblDeficiency").toString(), TblDeficiency::class.java))
            }
        }

        if (jsonObj.has("tblComplaintFiles")) {
            if (jsonObj.get("tblComplaintFiles").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<TblComplaintFiles>>() {}.type)
                FacilityDataModelOrg.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<TblComplaintFiles>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblComplaintFiles.add(Gson().fromJson<TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), TblComplaintFiles::class.java))
                FacilityDataModelOrg.getInstance().tblComplaintFiles.add(Gson().fromJson<TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), TblComplaintFiles::class.java))
            }
        }

        if (jsonObj.has("NumberofComplaints")) {
            if (jsonObj.get("NumberofComplaints").toString().startsWith("[")) {
                FacilityDataModel.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<numberofComplaints>>() {}.type)
                FacilityDataModelOrg.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<numberofComplaints>>() {}.type)
            } else {
                FacilityDataModel.getInstance().NumberofComplaints.add(Gson().fromJson<numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), numberofComplaints::class.java))
                FacilityDataModelOrg.getInstance().NumberofComplaints.add(Gson().fromJson<numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), numberofComplaints::class.java))
            }
        }

        if (jsonObj.has("NumberofJustifiedComplaints")) {
            if (jsonObj.get("NumberofJustifiedComplaints").toString().startsWith("[")) {
                FacilityDataModel.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<numberofJustifiedComplaints>>() {}.type)
                FacilityDataModelOrg.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<numberofJustifiedComplaints>>() {}.type)
            } else {
                FacilityDataModel.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), numberofJustifiedComplaints::class.java))
                FacilityDataModelOrg.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), numberofJustifiedComplaints::class.java))
            }
        }

        if (jsonObj.has("JustifiedComplaintRatio")) {
            if (jsonObj.get("JustifiedComplaintRatio").toString().startsWith("[")) {
                FacilityDataModel.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<justifiedComplaintRatio>>() {}.type)
                FacilityDataModelOrg.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<justifiedComplaintRatio>>() {}.type)
            } else {
                FacilityDataModel.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), justifiedComplaintRatio::class.java))
                FacilityDataModelOrg.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), justifiedComplaintRatio::class.java))
            }
        }

        if (jsonObj.has("tblFacilityPhotos")) {
            if (jsonObj.get("tblFacilityPhotos").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<TblFacilityPhotos>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<TblFacilityPhotos>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityPhotos.add(Gson().fromJson<TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), TblFacilityPhotos::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityPhotos.add(Gson().fromJson<TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), TblFacilityPhotos::class.java))
            }
        }

        if (jsonObj.has("Billing")) {
            if (jsonObj.get("Billing").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBilling = Gson().fromJson<ArrayList<TblBilling>>(jsonObj.get("Billing").toString(), object : TypeToken<ArrayList<TblBilling>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBilling = Gson().fromJson<ArrayList<TblBilling>>(jsonObj.get("Billing").toString(), object : TypeToken<ArrayList<TblBilling>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBilling.add(Gson().fromJson<TblBilling>(jsonObj.get("Billing").toString(), TblBilling::class.java))
                FacilityDataModelOrg.getInstance().tblBilling.add(Gson().fromJson<TblBilling>(jsonObj.get("Billing").toString(), TblBilling::class.java))
            }
        }

        if (jsonObj.has("BillingPlan")) {
            if (jsonObj.get("BillingPlan").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBillingPlan = Gson().fromJson<ArrayList<TblBillingPlan>>(jsonObj.get("BillingPlan").toString(), object : TypeToken<ArrayList<TblBillingPlan>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBillingPlan = Gson().fromJson<ArrayList<TblBillingPlan>>(jsonObj.get("BillingPlan").toString(), object : TypeToken<ArrayList<TblBillingPlan>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBillingPlan.add(Gson().fromJson<TblBillingPlan>(jsonObj.get("BillingPlan").toString(), TblBillingPlan::class.java))
                FacilityDataModelOrg.getInstance().tblBillingPlan.add(Gson().fromJson<TblBillingPlan>(jsonObj.get("BillingPlan").toString(), TblBillingPlan::class.java))
            }
        }

        if (jsonObj.has("tblFacilityBillingDetail")) {
            if (jsonObj.get("tblFacilityBillingDetail").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityBillingDetail = Gson().fromJson<ArrayList<TblFacilityBillingDetail>>(jsonObj.get("tblFacilityBillingDetail").toString(), object : TypeToken<ArrayList<TblFacilityBillingDetail>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityBillingDetail = Gson().fromJson<ArrayList<TblFacilityBillingDetail>>(jsonObj.get("tblFacilityBillingDetail").toString(), object : TypeToken<ArrayList<TblFacilityBillingDetail>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityBillingDetail.add(Gson().fromJson<TblFacilityBillingDetail>(jsonObj.get("tblFacilityBillingDetail").toString(), TblFacilityBillingDetail::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityBillingDetail.add(Gson().fromJson<TblFacilityBillingDetail>(jsonObj.get("tblFacilityBillingDetail").toString(), TblFacilityBillingDetail::class.java))
            }
        }

        if (jsonObj.has("tblInvoiceInfo")) {
            if (jsonObj.get("tblInvoiceInfo").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblInvoiceInfo = Gson().fromJson<ArrayList<TblInvoiceInfo>>(jsonObj.get("tblInvoiceInfo").toString(), object : TypeToken<ArrayList<TblInvoiceInfo>>() {}.type)
                FacilityDataModelOrg.getInstance().tblInvoiceInfo = Gson().fromJson<ArrayList<TblInvoiceInfo>>(jsonObj.get("tblInvoiceInfo").toString(), object : TypeToken<ArrayList<TblInvoiceInfo>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblInvoiceInfo.add(Gson().fromJson<TblInvoiceInfo>(jsonObj.get("tblInvoiceInfo").toString(), TblInvoiceInfo::class.java))
                FacilityDataModelOrg.getInstance().tblInvoiceInfo.add(Gson().fromJson<TblInvoiceInfo>(jsonObj.get("tblInvoiceInfo").toString(), TblInvoiceInfo::class.java))
            }
        }

        if (jsonObj.has("VendorRevenue")) {
            if (jsonObj.get("VendorRevenue").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblVendorRevenue = Gson().fromJson<ArrayList<TblVendorRevenue>>(jsonObj.get("VendorRevenue").toString(), object : TypeToken<ArrayList<TblVendorRevenue>>() {}.type)
                FacilityDataModelOrg.getInstance().tblVendorRevenue = Gson().fromJson<ArrayList<TblVendorRevenue>>(jsonObj.get("VendorRevenue").toString(), object : TypeToken<ArrayList<TblVendorRevenue>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblVendorRevenue.add(Gson().fromJson<TblVendorRevenue>(jsonObj.get("VendorRevenue").toString(), TblVendorRevenue::class.java))
                FacilityDataModelOrg.getInstance().tblVendorRevenue.add(Gson().fromJson<TblVendorRevenue>(jsonObj.get("VendorRevenue").toString(), TblVendorRevenue::class.java))
            }
        }

        if (jsonObj.has("BillingHistory")) {
            if (jsonObj.get("BillingHistory").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBillingHistory = Gson().fromJson<ArrayList<TblBillingHistory>>(jsonObj.get("BillingHistory").toString(), object : TypeToken<ArrayList<TblBillingHistory>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBillingHistory = Gson().fromJson<ArrayList<TblBillingHistory>>(jsonObj.get("BillingHistory").toString(), object : TypeToken<ArrayList<TblBillingHistory>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBillingHistory.add(Gson().fromJson<TblBillingHistory>(jsonObj.get("BillingHistory").toString(), TblBillingHistory::class.java))
                FacilityDataModelOrg.getInstance().tblBillingHistory.add(Gson().fromJson<TblBillingHistory>(jsonObj.get("BillingHistory").toString(), TblBillingHistory::class.java))
            }
        }

        if (jsonObj.has("tblComments")) {
            if (jsonObj.get("tblComments").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblComments = Gson().fromJson<ArrayList<TblComments>>(jsonObj.get("tblComments").toString(), object : TypeToken<ArrayList<TblComments>>() {}.type)
                FacilityDataModelOrg.getInstance().tblComments = Gson().fromJson<ArrayList<TblComments>>(jsonObj.get("tblComments").toString(), object : TypeToken<ArrayList<TblComments>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblComments.add(Gson().fromJson<TblComments>(jsonObj.get("tblComments").toString(), TblComments::class.java))
                FacilityDataModelOrg.getInstance().tblComments.add(Gson().fromJson<TblComments>(jsonObj.get("tblComments").toString(), TblComments::class.java))
            }
        }

        if (jsonObj.has("tblVehicleServices")) {
            if (jsonObj.get("tblVehicleServices").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblVehicleServices = Gson().fromJson<ArrayList<TblVehicleServices>>(jsonObj.get("tblVehicleServices").toString(), object : TypeToken<ArrayList<TblVehicleServices>>() {}.type)
                FacilityDataModelOrg.getInstance().tblVehicleServices= Gson().fromJson<ArrayList<TblVehicleServices>>(jsonObj.get("tblVehicleServices").toString(), object : TypeToken<ArrayList<TblVehicleServices>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblVehicleServices.add(Gson().fromJson<TblVehicleServices>(jsonObj.get("tblVehicleServices").toString(), TblVehicleServices::class.java))
                FacilityDataModelOrg.getInstance().tblVehicleServices.add(Gson().fromJson<TblVehicleServices>(jsonObj.get("tblVehicleServices").toString(), TblVehicleServices::class.java))
            }
        }

        if (jsonObj.has("tblAARPortalTracking")) {
            if (jsonObj.get("tblAARPortalTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAARPortalTracking = Gson().fromJson<ArrayList<TblAARPortalTracking>>(jsonObj.get("tblAARPortalTracking").toString(), object : TypeToken<ArrayList<TblAARPortalTracking>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAARPortalTracking= Gson().fromJson<ArrayList<TblAARPortalTracking>>(jsonObj.get("tblAARPortalTracking").toString(), object : TypeToken<ArrayList<TblAARPortalTracking>>() {}.type)
                FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
                FacilityDataModelOrg.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
            } else {
                FacilityDataModel.getInstance().tblAARPortalTracking.add(Gson().fromJson<TblAARPortalTracking>(jsonObj.get("tblAARPortalTracking").toString(), TblAARPortalTracking::class.java))
                FacilityDataModelOrg.getInstance().tblAARPortalTracking.add(Gson().fromJson<TblAARPortalTracking>(jsonObj.get("tblAARPortalTracking").toString(), TblAARPortalTracking::class.java))
                FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
                FacilityDataModelOrg.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
            }
        }

        if (jsonObj.has("tblPersonnelCertification")) {
            if (jsonObj.get("tblPersonnelCertification").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPersonnelCertification = Gson().fromJson<ArrayList<TblPersonnelCertification>>(jsonObj.get("tblPersonnelCertification").toString(), object : TypeToken<ArrayList<TblPersonnelCertification>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPersonnelCertification= Gson().fromJson<ArrayList<TblPersonnelCertification>>(jsonObj.get("tblPersonnelCertification").toString(), object : TypeToken<ArrayList<TblPersonnelCertification>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPersonnelCertification.add(Gson().fromJson<TblPersonnelCertification>(jsonObj.get("tblPersonnelCertification").toString(), TblPersonnelCertification::class.java))
                FacilityDataModelOrg.getInstance().tblPersonnelCertification.add(Gson().fromJson<TblPersonnelCertification>(jsonObj.get("tblPersonnelCertification").toString(), TblPersonnelCertification::class.java))
            }
        }

        if (jsonObj.has("BillingAdjustments")) {
            if (jsonObj.get("BillingAdjustments").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBillingAdjustments = Gson().fromJson<ArrayList<TblBillingAdjustments>>(jsonObj.get("BillingAdjustments").toString(), object : TypeToken<ArrayList<TblBillingAdjustments>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBillingAdjustments= Gson().fromJson<ArrayList<TblBillingAdjustments>>(jsonObj.get("BillingAdjustments").toString(), object : TypeToken<ArrayList<TblBillingAdjustments>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBillingAdjustments.add(Gson().fromJson<TblBillingAdjustments>(jsonObj.get("BillingAdjustments").toString(), TblBillingAdjustments::class.java))
                FacilityDataModelOrg.getInstance().tblBillingAdjustments.add(Gson().fromJson<TblBillingAdjustments>(jsonObj.get("BillingAdjustments").toString(), TblBillingAdjustments::class.java))
            }
        }

        if (jsonObj.has("AAAPortalEmailFacilityRepTable")) {
            if (jsonObj.get("AAAPortalEmailFacilityRepTable").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable = Gson().fromJson<ArrayList<TblAAAPortalEmailFacilityRepTable>>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), object : TypeToken<ArrayList<TblAAAPortalEmailFacilityRepTable>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable= Gson().fromJson<ArrayList<TblAAAPortalEmailFacilityRepTable>>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), object : TypeToken<ArrayList<TblAAAPortalEmailFacilityRepTable>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.add(Gson().fromJson<TblAAAPortalEmailFacilityRepTable>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), TblAAAPortalEmailFacilityRepTable::class.java))
                FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable.add(Gson().fromJson<TblAAAPortalEmailFacilityRepTable>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), TblAAAPortalEmailFacilityRepTable::class.java))
            }
            FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year}).sortedWith(compareBy { it.Quarter }).sortedWith(compareBy { it.Month })
            FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year}).sortedWith(compareBy { it.Quarter }).sortedWith(compareBy { it.Month })
        }

        if (jsonObj.has("InvoiceInfo")) {
            if (jsonObj.get("InvoiceInfo").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblInvoiceInfoUpdated = Gson().fromJson<ArrayList<InvoiceInfo>>(jsonObj.get("InvoiceInfo").toString(), object : TypeToken<ArrayList<InvoiceInfo>>() {}.type)
                FacilityDataModelOrg.getInstance().tblInvoiceInfoUpdated = Gson().fromJson<ArrayList<InvoiceInfo>>(jsonObj.get("InvoiceInfo").toString(), object : TypeToken<ArrayList<InvoiceInfo>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblInvoiceInfoUpdated.add(Gson().fromJson<InvoiceInfo>(jsonObj.get("InvoiceInfo").toString(), InvoiceInfo::class.java))
                FacilityDataModelOrg.getInstance().tblInvoiceInfoUpdated.add(Gson().fromJson<InvoiceInfo>(jsonObj.get("InvoiceInfo").toString(), InvoiceInfo::class.java))
            }
        }
        if (jsonObj.has("tblFacVehicles")) {
            if (jsonObj.get("tblFacVehicles").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacVehicles = Gson().fromJson<ArrayList<TblFacVehicles>>(jsonObj.get("tblFacVehicles").toString(), object : TypeToken<ArrayList<TblFacVehicles>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacVehicles = Gson().fromJson<ArrayList<TblFacVehicles>>(jsonObj.get("tblFacVehicles").toString(), object : TypeToken<ArrayList<TblFacVehicles>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacVehicles.add(Gson().fromJson<TblFacVehicles>(jsonObj.get("tblFacVehicles").toString(), TblFacVehicles::class.java))
                FacilityDataModelOrg.getInstance().tblFacVehicles.add(Gson().fromJson<TblFacVehicles>(jsonObj.get("tblFacVehicles").toString(), TblFacVehicles::class.java))
            }
        }

        if (jsonObj.has("tblPersonnelSigner")) {
            if (jsonObj.get("tblPersonnelSigner").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPersonnelSigner = Gson().fromJson<ArrayList<TblPersonnelSigner>>(jsonObj.get("tblPersonnelSigner").toString(), object : TypeToken<ArrayList<TblPersonnelSigner>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPersonnelSigner = Gson().fromJson<ArrayList<TblPersonnelSigner>>(jsonObj.get("tblPersonnelSigner").toString(), object : TypeToken<ArrayList<TblPersonnelSigner>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPersonnelSigner.add(Gson().fromJson<TblPersonnelSigner>(jsonObj.get("tblPersonnelSigner").toString(), TblPersonnelSigner::class.java))
                FacilityDataModelOrg.getInstance().tblPersonnelSigner.add(Gson().fromJson<TblPersonnelSigner>(jsonObj.get("tblPersonnelSigner").toString(), TblPersonnelSigner::class.java))
            }
        }

        if (jsonObj.has("tblGeocodes")) {
            if (jsonObj.get("tblGeocodes").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblGeocodes = Gson().fromJson<ArrayList<TblGeocodes>>(jsonObj.get("tblGeocodes").toString(), object : TypeToken<ArrayList<TblGeocodes>>() {}.type)
                FacilityDataModelOrg.getInstance().tblGeocodes = Gson().fromJson<ArrayList<TblGeocodes>>(jsonObj.get("tblGeocodes").toString(), object : TypeToken<ArrayList<TblGeocodes>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblGeocodes.add(Gson().fromJson<TblGeocodes>(jsonObj.get("tblGeocodes").toString(), TblGeocodes::class.java))
                FacilityDataModelOrg.getInstance().tblGeocodes.add(Gson().fromJson<TblGeocodes>(jsonObj.get("tblGeocodes").toString(), TblGeocodes::class.java))
            }
        }

        if (jsonObj.has("AffiliateVendorFacilities")) {
            if (jsonObj.get("AffiliateVendorFacilities").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAffiliateVendorFacilities = Gson().fromJson<ArrayList<AffiliateVendorFacilities>>(jsonObj.get("AffiliateVendorFacilities").toString(), object : TypeToken<ArrayList<AffiliateVendorFacilities>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities = Gson().fromJson<ArrayList<AffiliateVendorFacilities>>(jsonObj.get("AffiliateVendorFacilities").toString(), object : TypeToken<ArrayList<AffiliateVendorFacilities>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAffiliateVendorFacilities.add(Gson().fromJson<AffiliateVendorFacilities>(jsonObj.get("AffiliateVendorFacilities").toString(), AffiliateVendorFacilities::class.java))
                FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities.add(Gson().fromJson<AffiliateVendorFacilities>(jsonObj.get("AffiliateVendorFacilities").toString(), AffiliateVendorFacilities::class.java))
            }
        }

        if (jsonObj.has("Promotions")) {
            if (jsonObj.get("Promotions").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPromotions = Gson().fromJson<ArrayList<TblPromotions>>(jsonObj.get("Promotions").toString(), object : TypeToken<ArrayList<TblPromotions>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPromotions = Gson().fromJson<ArrayList<TblPromotions>>(jsonObj.get("Promotions").toString(), object : TypeToken<ArrayList<TblPromotions>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPromotions.add(Gson().fromJson<TblPromotions>(jsonObj.get("Promotions").toString(), TblPromotions::class.java))
                FacilityDataModelOrg.getInstance().tblPromotions.add(Gson().fromJson<TblPromotions>(jsonObj.get("Promotions").toString(), TblPromotions::class.java))
            }
        }

        if (jsonObj.has("FacilityPhotos")) {
            if (jsonObj.get("FacilityPhotos").toString().startsWith("[")) {
                FacilityDataModel.getInstance().FacilityPhotos = Gson().fromJson<ArrayList<FacilityPhotos>>(jsonObj.get("FacilityPhotos").toString(), object : TypeToken<ArrayList<FacilityPhotos>>() {}.type)
                FacilityDataModelOrg.getInstance().FacilityPhotos = Gson().fromJson<ArrayList<FacilityPhotos>>(jsonObj.get("FacilityPhotos").toString(), object : TypeToken<ArrayList<FacilityPhotos>>() {}.type)
            } else {
                FacilityDataModel.getInstance().FacilityPhotos.add(Gson().fromJson<FacilityPhotos>(jsonObj.get("FacilityPhotos").toString(), FacilityPhotos::class.java))
                FacilityDataModelOrg.getInstance().FacilityPhotos.add(Gson().fromJson<FacilityPhotos>(jsonObj.get("FacilityPhotos").toString(), FacilityPhotos::class.java))
            }
//            if (FacilityDataModel.getInstance().FacilityPhotos.size>0) {
//                getPhotosS3Urls()
//            }
        }

        if (jsonObj.has("tblFacilityBillingHeader")) {
            if (jsonObj.get("tblFacilityBillingHeader").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityBillingHeader = Gson().fromJson<ArrayList<TblFacilityBillingHeader>>(jsonObj.get("tblFacilityBillingHeader").toString(), object : TypeToken<ArrayList<TblFacilityBillingHeader>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityBillingHeader = Gson().fromJson<ArrayList<TblFacilityBillingHeader>>(jsonObj.get("tblFacilityBillingHeader").toString(), object : TypeToken<ArrayList<TblFacilityBillingHeader>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityBillingHeader.add(Gson().fromJson<TblFacilityBillingHeader>(jsonObj.get("tblFacilityBillingHeader").toString(), TblFacilityBillingHeader::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityBillingHeader.add(Gson().fromJson<TblFacilityBillingHeader>(jsonObj.get("tblFacilityBillingHeader").toString(), TblFacilityBillingHeader::class.java))
            }
        }

        IndicatorsDataModel.getInstance().init()
        HasChangedModel.getInstance().init()
    }

    fun getFacilityPRGData(isCompleted: Boolean) {
        PRGDataModel.getInstance().tblPRGVisitationHeader.clear()
        PRGDataModel.getInstance().tblPRGFacilitiesPhotos.clear()
        PRGDataModel.getInstance().tblPRGLogChanges.clear()
        PRGDataModel.getInstance().tblPRGFacilityDetails.clear()
        PRGDataModel.getInstance().tblPRGPersonnelDetails.clear()
        PRGDataModel.getInstance().tblPRGRepairDiscountFactors.clear()
        Volley.newRequestQueue(activity).add(
            StringRequest(Request.Method.GET,
                Constants.getFacilityPhotos + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}",
                { response ->
                    requireActivity().runOnUiThread {
                        if (!response.toString().replace(" ", "").equals("[ ]")) {
                            PRGDataModel.getInstance().tblPRGFacilitiesPhotos = Gson().fromJson(
                                response.toString(),
                                Array<PRGFacilityPhotos>::class.java
                            ).toCollection(
                                ArrayList()
                            )
                        } else {

                            var item = PRGFacilityPhotos()
                            item.photoid = -1
                            PRGDataModel.getInstance().tblPRGFacilitiesPhotos.add(item)
                        }
                        Volley.newRequestQueue(activity).add(
                            StringRequest(Request.Method.GET,
                                Constants.getLoggedActions + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}&userId=" + ApplicationPrefs.getInstance(
                                    context
                                ).loggedInUserID,
                                { response ->
                                    requireActivity().runOnUiThread {
                                        if (!response.toString().replace(" ", "").equals("[]")) {
                                            PRGDataModel.getInstance().tblPRGLogChanges =
                                                Gson().fromJson(
                                                    response.toString(),
                                                    Array<PRGLogChanges>::class.java
                                                ).toCollection(
                                                    ArrayList()
                                                )
                                        } else {
                                            var item = PRGLogChanges()
                                            item.recordid = -1
                                            PRGDataModel.getInstance().tblPRGLogChanges.add(item)
                                        }
                                        Volley.newRequestQueue(activity).add(
                                            StringRequest(Request.Method.GET,
                                                Constants.getVisitationHeader + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}",
                                                { response ->
                                                    requireActivity().runOnUiThread {
                                                        if (!response.toString().replace(" ", "")
                                                                .equals("[]")
                                                        ) {
                                                            PRGDataModel.getInstance().tblPRGVisitationHeader =
                                                                Gson().fromJson(
                                                                    response.toString(),
                                                                    Array<PRGVisitationHeader>::class.java
                                                                ).toCollection(
                                                                    ArrayList()
                                                                )
                                                        } else {
                                                            var item = PRGVisitationHeader()
                                                            item.recordid = -1
                                                            PRGDataModel.getInstance().tblPRGVisitationHeader.add(
                                                                item
                                                            )
                                                        }
                                                        Volley.newRequestQueue(activity).add(
                                                            StringRequest(Request.Method.GET,
                                                                Constants.getRepairDiscountFactors + "${FacilityDataModel.getInstance().clubCode}",
                                                                Response.Listener { response ->
                                                                    requireActivity().runOnUiThread {
                                                                        if (!response.toString()
                                                                                .replace(" ", "")
                                                                                .equals("[]")
                                                                        ) {
                                                                            PRGDataModel.getInstance().tblPRGRepairDiscountFactors =
                                                                                Gson().fromJson(
                                                                                    response.toString(),
                                                                                    Array<PRGRepairDiscountFactors>::class.java
                                                                                ).toCollection(
                                                                                    ArrayList()
                                                                                )
                                                                        } else {
                                                                            var item =
                                                                                PRGRepairDiscountFactors()
                                                                            item.clubcode =
                                                                                FacilityDataModel.getInstance().clubCode
                                                                            PRGDataModel.getInstance().tblPRGRepairDiscountFactors.add(
                                                                                item
                                                                            )
                                                                        }
                                                                        Volley.newRequestQueue(
                                                                            activity
                                                                        ).add(
                                                                            StringRequest(Request.Method.GET,
                                                                                Constants.getPersonnelDetails + "${FacilityDataModel.getInstance().clubCode}&facNum=" + FacilityDataModel.getInstance().tblFacilities[0].FACNo,
                                                                                Response.Listener { response ->
                                                                                    requireActivity().runOnUiThread {
                                                                                        if (!response.toString()
                                                                                                .replace(
                                                                                                    " ",
                                                                                                    ""
                                                                                                )
                                                                                                .equals(
                                                                                                    "[]"
                                                                                                )
                                                                                        ) {
                                                                                            PRGDataModel.getInstance().tblPRGPersonnelDetails =
                                                                                                Gson().fromJson(
                                                                                                    response.toString(),
                                                                                                    Array<PRGPersonnelDetails>::class.java
                                                                                                )
                                                                                                    .toCollection(
                                                                                                        ArrayList()
                                                                                                    )
                                                                                        } else {
                                                                                            var item =
                                                                                                PRGPersonnelDetails()
                                                                                            item.clubcode =
                                                                                                FacilityDataModel.getInstance().clubCode.toInt()
                                                                                            item.facnum =
                                                                                                FacilityDataModel.getInstance().tblFacilities[0].FACNo
                                                                                            PRGDataModel.getInstance().tblPRGPersonnelDetails.add(
                                                                                                item
                                                                                            )
                                                                                        }
                                                                                        Volley.newRequestQueue(
                                                                                            activity
                                                                                        ).add(
                                                                                            StringRequest(
                                                                                                Request.Method.GET,
                                                                                                Constants.getPRGFacilityDetails + "${FacilityDataModel.getInstance().clubCode}&facNum=" + FacilityDataModel.getInstance().tblFacilities[0].FACNo,
                                                                                                Response.Listener { response ->
                                                                                                    requireActivity().runOnUiThread {
                                                                                                        if (!response.toString()
                                                                                                                .replace(
                                                                                                                    " ",
                                                                                                                    ""
                                                                                                                )
                                                                                                                .equals(
                                                                                                                    "[]"
                                                                                                                )
                                                                                                        ) {
                                                                                                            PRGDataModel.getInstance().tblPRGFacilityDetails =
                                                                                                                Gson().fromJson(
                                                                                                                    response.toString(),
                                                                                                                    Array<PRGFacilityDetails>::class.java
                                                                                                                )
                                                                                                                    .toCollection(
                                                                                                                        ArrayList()
                                                                                                                    )
                                                                                                        } else {
                                                                                                            var item =
                                                                                                                PRGFacilityDetails()
                                                                                                            item.clubcode =
                                                                                                                FacilityDataModel.getInstance().clubCode.toInt()
                                                                                                            item.facid =
                                                                                                                FacilityDataModel.getInstance().tblFacilities[0].FACNo
                                                                                                            item.napanumber =
                                                                                                                ""
                                                                                                            item.nationalnumber =
                                                                                                                ""
                                                                                                            PRGDataModel.getInstance().tblPRGFacilityDetails.add(
                                                                                                                item
                                                                                                            )
                                                                                                        }
                                                                                                        Volley.newRequestQueue(
                                                                                                            activity
                                                                                                        )
                                                                                                            .add(
                                                                                                                StringRequest(
                                                                                                                    Request.Method.GET,
                                                                                                                    Constants.getFacilityDirectors + "${FacilityDataModel.getInstance().clubCode}&facNum=" + FacilityDataModel.getInstance().tblFacilities[0].FACNo,
                                                                                                                    Response.Listener { response ->
                                                                                                                        requireActivity().runOnUiThread {
                                                                                                                            if (!response.toString()
                                                                                                                                    .replace(
                                                                                                                                        " ",
                                                                                                                                        ""
                                                                                                                                    )
                                                                                                                                    .equals(
                                                                                                                                        "[]"
                                                                                                                                    )
                                                                                                                            ) {
                                                                                                                                PRGDataModel.getInstance().tblPRGFacilityDirectors =
                                                                                                                                    Gson().fromJson(
                                                                                                                                        response.toString(),
                                                                                                                                        Array<PRGFacilityDirectors>::class.java
                                                                                                                                    )
                                                                                                                                        .toCollection(
                                                                                                                                            ArrayList()
                                                                                                                                        )
                                                                                                                            } else {
                                                                                                                                var item =
                                                                                                                                    PRGFacilityDirectors()
                                                                                                                                item.clubcode =
                                                                                                                                    FacilityDataModel.getInstance().clubCode.toInt()
                                                                                                                                item.facnum =
                                                                                                                                    FacilityDataModel.getInstance().tblFacilities[0].FACNo
                                                                                                                                item.specialistid =
                                                                                                                                    -1
                                                                                                                                item.directorid =
                                                                                                                                    -1
                                                                                                                                item.directoremail =
                                                                                                                                    ""
                                                                                                                                PRGDataModel.getInstance().tblPRGFacilityDirectors.add(
                                                                                                                                    item
                                                                                                                                )
                                                                                                                            }
                                                                                                                            Volley.newRequestQueue(
                                                                                                                                activity
                                                                                                                            )
                                                                                                                                .add(
                                                                                                                                    StringRequest(
                                                                                                                                        Request.Method.GET,
                                                                                                                                        Constants.getFacilityHolidays + "${FacilityDataModel.getInstance().clubCode}&facNum=" + FacilityDataModel.getInstance().tblFacilities[0].FACNo,
                                                                                                                                        Response.Listener { response ->
                                                                                                                                            requireActivity().runOnUiThread {
                                                                                                                                                if (!response.toString()
                                                                                                                                                        .replace(
                                                                                                                                                            " ",
                                                                                                                                                            ""
                                                                                                                                                        )
                                                                                                                                                        .equals(
                                                                                                                                                            "[]"
                                                                                                                                                        )
                                                                                                                                                ) {
                                                                                                                                                    PRGDataModel.getInstance().tblPRGFacilityShopHolidayTimes =
                                                                                                                                                        Gson().fromJson(
                                                                                                                                                            response.toString(),
                                                                                                                                                            Array<PRGFacilityShopHolidayTimes>::class.java
                                                                                                                                                        )
                                                                                                                                                            .toCollection(
                                                                                                                                                                ArrayList()
                                                                                                                                                            )
                                                                                                                                                } else {
                                                                                                                                                    var item =
                                                                                                                                                        PRGFacilityShopHolidayTimes()
                                                                                                                                                    item.clubcode =
                                                                                                                                                        FacilityDataModel.getInstance().clubCode.toString()
                                                                                                                                                    item.FacNum =
                                                                                                                                                        FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
                                                                                                                                                    item.comments =
                                                                                                                                                        "-1"
                                                                                                                                                    item.startdate =
                                                                                                                                                        ""
                                                                                                                                                    item.enddate =
                                                                                                                                                        ""
                                                                                                                                                    PRGDataModel.getInstance().tblPRGFacilityShopHolidayTimes.add(
                                                                                                                                                        item
                                                                                                                                                    )
                                                                                                                                                }
                                                                                                                                                launchNextAction()
                                                                                                                                            }
                                                                                                                                        },
                                                                                                                                        Response.ErrorListener {
                                                                                                                                            Log.v(
                                                                                                                                                "Loading PRG Data error",
                                                                                                                                                "" + it.message
                                                                                                                                            )
//                                                                                                            launchNextAction(isCompleted)
                                                                                                                                            it.printStackTrace()
                                                                                                                                        })
                                                                                                                                )
                                                                                                                        }
                                                                                                                    },
                                                                                                                    Response.ErrorListener {
                                                                                                                        Log.v(
                                                                                                                            "Loading PRG Data error",
                                                                                                                            "" + it.message
                                                                                                                        )
//                                                                                                            launchNextAction(isCompleted)
                                                                                                                        it.printStackTrace()
                                                                                                                    })
                                                                                                            )
                                                                                                    }
                                                                                                },
                                                                                                Response.ErrorListener {
                                                                                                    Log.v(
                                                                                                        "Loading PRG Data error",
                                                                                                        "" + it.message
                                                                                                    )
                                                                                                    launchNextAction()
                                                                                                    it.printStackTrace()
                                                                                                })
                                                                                        )
                                                                                    }
                                                                                },
                                                                                Response.ErrorListener {
                                                                                    Log.v(
                                                                                        "Loading PRG Data error",
                                                                                        "" + it.message
                                                                                    )
                                                                                    it.printStackTrace()
                                                                                })
                                                                        )
                                                                    }
                                                                },
                                                                Response.ErrorListener {
                                                                    Log.v(
                                                                        "Loading PRG Data error",
                                                                        "" + it.message
                                                                    )
                                                                    it.printStackTrace()
                                                                })
                                                        )
                                                    }
                                                },
                                                {
                                                    Log.v("Loading PRG Data error", "" + it.message)
//                                            launchNextAction(isCompleted)
                                                    it.printStackTrace()
                                                })
                                        )
//                                        launchNextAction(isCompleted)
                                    }
                                },
                                {
                                    Log.v("Loading PRG Data error", "" + it.message)
//                            launchNextAction(isCompleted)
                                    it.printStackTrace()
                                })
                        )

                    }
                },
                {
                    Log.v("Loading PRG Data error", "" + it.message)
                    it.printStackTrace()
                })
        )
    }

    fun launchNextAction() {
        IndicatorsDataModel.getInstance().init()
        binding.recordsProgressView.visibility = View.VISIBLE

        Volley.newRequestQueue(activity).add(
            StringRequest(Request.Method.GET, Constants.getPRGVisitationsLog,
                { response ->
                    requireActivity().runOnUiThread {
                        if (!response.toString().replace(" ", "").equals("[]")) {
                            PRGDataModel.getInstance().tblPRGVisitationsLog = Gson().fromJson(
                                response.toString(),
                                Array<PRGVisitationsLog>::class.java
                            ).toCollection(ArrayList())
                        } else {
                            var item = PRGVisitationsLog()
                            item.recordid = -1
                            PRGDataModel.getInstance().tblPRGVisitationsLog.add(item)
                        }
                        AdjustIndicatorsAndStartActivity()
                    }
                },
                {
                    Log.v("Loading PRG Data error", "" + it.message)
                    var item = PRGVisitationsLog()
                    item.recordid = -1
                    PRGDataModel.getInstance().tblPRGVisitationsLog.add(item)
                    it.printStackTrace()
                    AdjustIndicatorsAndStartActivity()
                })
        )
    }

    fun AdjustIndicatorsAndStartActivity() {
//        FirebaseCrashlytics.getInstance().setCustomKey("Details", "Starting Activity")
        if (PRGDataModel.getInstance().tblPRGVisitationsLog[0].recordid > -1) {
            if (PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == FacilityDataModel.getInstance().tblFacilities[0].FACNo && s.clubcode == FacilityDataModel.getInstance().clubCode.toInt() && s.facannualinspectionmonth == FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth && s.inspectioncycle == FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle && s.visitationtype == FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString() }
                    .isNotEmpty()) {
                IndicatorsDataModel.getInstance()
                    .markVisitedScreen(PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == FacilityDataModel.getInstance().tblFacilities[0].FACNo && s.clubcode == FacilityDataModel.getInstance().clubCode.toInt() && s.facannualinspectionmonth == FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth && s.inspectioncycle == FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle && s.visitationtype == FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString() }
                        .sortedByDescending { it.changedate }[0].visitedscreens)
            }
        }
        var intent = Intent(context, com.inspection.FormsActivity::class.java)
        startActivity(intent)
        binding.recordsProgressView.visibility = View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TodayVisitationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TodayVisitationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCardClick(place: TodayVisitationModel) {
        val marker = markerMap.filter { it.key.facNum == place.facNum && it.key.clubCode == place.clubCode }.map { it.value }.firstOrNull()

        // 1. Move the camera to the marker
        if (marker != null) {
            gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16f))
        }

        // Optional: Highlight marker (e.g., change icon)
//        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
    }
}



class PlacesAdapter(
    private val places: MutableList<TodayVisitationModel>,
    private val actionListener: VisitActionListener,
    private val getFullDataListener: FullDataListener,
    private val cardListener: CardListener,
    private val onVoiceClicked: OnVoiceClickListener,
    private val onVoiceStopClicked: OnVoiceStopClickListener
) : RecyclerView.Adapter<PlacesAdapter.StepViewHolder>() {

    // ViewHolder for each step item

    interface OnVoiceClickListener {
        fun onVoiceClicked(facilityNumber: Int, clubCode: String)
    }

    interface OnVoiceStopClickListener {
        fun onVoiceStopClicked()
    }

    interface VisitActionListener {
        fun onReloadMap(updateAdapter: Boolean)
    }

    interface CardListener {
        fun onCardClick(place: TodayVisitationModel)
    }

    interface FullDataListener {
        fun onGetFullData(facilityNumber: Int, clubCode: String, visitationType: VisitationTypes)
    }

    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val fac_name_text: TextView = itemView.findViewById(R.id.facNameVal)
        val fac_no_text: TextView = itemView.findViewById(R.id.facNoVal)
        val club_no_text: TextView = itemView.findViewById(R.id.clubNoVal)
        val type_text: TextView = itemView.findViewById(R.id.typeVal)
        val statusBadge: TextView = itemView.findViewById(R.id.statusBadge)
        val city_text: TextView = itemView.findViewById(R.id.cityVal)
        val fac_annual_text: TextView = itemView.findViewById(R.id.facAnnualMonthVal)
        val notes_text: EditText = itemView.findViewById(R.id.notesVal)
        val saveBtn: Button = itemView.findViewById(R.id.saveNotesBtn)
        val removeBtn: Button = itemView.findViewById(R.id.removeBtn)
        val order_text: TextView = itemView.findViewById(R.id.orderCircle)
        val eta_text: TextView = itemView.findViewById(R.id.etaLabel)
        val loc_row: LinearLayout = itemView.findViewById(R.id.locFoundRow)
        val eta_row: LinearLayout = itemView.findViewById(R.id.etaRow)
        val cardRL : ConstraintLayout = itemView.findViewById(R.id.cardRL)

        val loadBtn: Button = itemView.findViewById(R.id.loadBtn)
        val moveUpBtn: ImageButton = itemView.findViewById(R.id.moveUpBtn)
        val moveDownBtn: ImageButton = itemView.findViewById(R.id.moveDownBtn)

        val moveLastBtn: ImageButton = itemView.findViewById(R.id.moveLastBtn)
        val moveFirstBtn: ImageButton = itemView.findViewById(R.id.moveFirstBtn)

        val startRecordingBtn: ImageButton = itemView.findViewById(R.id.recordBtn)
        val stopRecordingBtn: ImageButton = itemView.findViewById(R.id.stopBtn)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.today_visit_item, parent, false)

        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
//        holder.itemView.alpha = 0f
//        holder.itemView.translationY = 50f
//        holder.itemView.animate()
//            .alpha(1f)
//            .translationY(0f)
//            .setStartDelay(position * 30L) // staggering
//            .setDuration(300)
//            .start()
        holder.fac_name_text.text = places[position].facName
        holder.fac_no_text.text = places[position].facNum.toString()
        holder.club_no_text.text = places[position].clubCode
        holder.type_text.text = places[position].type.toString()
        val isCompleted = places[position].status.equals("Completed", ignoreCase = true)
        holder.statusBadge.text = if (isCompleted) "Completed" else "Planned"
        holder.statusBadge.setBackgroundResource(
            if (isCompleted) R.drawable.badge_completed else R.drawable.badge_planned
        )
        holder.cardRL.setBackgroundColor(
            if (isCompleted) ContextCompat.getColor(holder.itemView.context, R.color.light_green)
            else Color.WHITE
        )
        holder.cardRL.setOnClickListener {
            cardListener.onCardClick(places[position])
        }
        val etaLabel = places[position].etaLabel
        when {
            etaLabel.isEmpty() -> {
                holder.eta_row.isVisible = false
                holder.eta_text.text = ""
            }
            etaLabel == "Calculating route…" -> {
                holder.eta_row.isVisible = true
                holder.eta_text.text = "Calculating route…"
            }
            else -> {
                holder.eta_row.isVisible = true
                holder.eta_text.text = etaLabel
            }
        }
        if (places[position].latitude != 0.0) {
            holder.loc_row.isVisible = true
        } else {
            holder.loc_row.isVisible = false
        }
        holder.notes_text.setText(places[position].notes)
        holder.fac_annual_text.text = places[position].facAnnualMonth.monthNoToName()
        holder.city_text.text = places[position].city
        holder.order_text.text = places[position].order.toString()
        val drawable = holder.order_text.background as GradientDrawable
//        drawable.setColor(getOrderColor(places[position].order).colorInt)
        if (places[position].latitude != 0.0 && places[position].longitude != 0.0) {

            holder.order_text.setTextColor(Color.WHITE)
//            drawable.setColor(getOrderColor(places[position].order).colorInt)
            drawable.setColor(Color.RED)
        } else {

            holder.order_text.setTextColor(Color.BLUE)
            drawable.setColor(Color.WHITE)
        }
//        holder.mapBtn.setBackgroundColor(getOrderColor(places[position].order).colorInt)
//        holder.mapBtn.visibility = View.GONE
        holder.loadBtn.setOnClickListener {
            getFullDataListener.onGetFullData(
                places[position].facNum,
                places[position].clubCode,
                places[position].type
            )
        }
        holder.moveUpBtn.isEnabled = position > 0

        holder.moveDownBtn.isEnabled = position < places.size - 1

        holder.moveFirstBtn.isEnabled = position > 0

        holder.moveLastBtn.isEnabled = position < places.size - 1

        holder.startRecordingBtn.setOnClickListener {
            Log.v(
                "Voice Clicked -->",
                "FacNum: ${places[position].facNum} , ClubCode: ${places[position].clubCode}"
            )
            onVoiceClicked.onVoiceClicked(places[position].facNum, places[position].clubCode)
        }
        holder.stopRecordingBtn.setOnClickListener {
            Log.v(
                "Voice Stop Clicked -->",
                "FacNum: ${places[position].facNum} , ClubCode: ${places[position].clubCode}"
            )
            onVoiceStopClicked.onVoiceStopClicked()
        }

        Log.v(
            "Location -->",
            "Lat: ${places[position].latitude} , Long: ${places[position].longitude}"
        )
        holder.saveBtn.setOnClickListener {
            updateVisitationNotes(
                holder.itemView.context,
                places[position].facNum,
                places[position].clubCode,
                holder.notes_text.text.toString()
            )
            Utility.showUnifiedConfirmationDialog(
                holder.itemView.context,
                "Notes Saved successfully"
            )
            var visitsList = getTodayVisitations(holder.itemView.context)
            visitsList = visitsList.sortedBy { it.order }.toMutableList()
            places.clear()
            places.addAll(visitsList)
            notifyDataSetChanged()
        }

        holder.removeBtn.setOnClickListener {
            removeTodayVisitation(
                holder.itemView.context,
                places[position].facNum.toString(),
                places[position].clubCode,
            )
//            Utility.showUnifiedConfirmationDialog(
//                holder.itemView.context,
//                "Facility Removed successfully"
//            )
            var visitsList = getTodayVisitations(holder.itemView.context)
            visitsList = visitsList.sortedBy { it.order }.toMutableList()
            places.clear()
            places.addAll(visitsList)
            notifyDataSetChanged()
            actionListener.onReloadMap(updateAdapter = true)
        }
        holder.moveUpBtn.setOnClickListener {
            if (position > 0) {
                Collections.swap(places, position, position - 1)
                notifyItemMoved(position, position - 1)
                for (i in places.indices) {
                    places[i].order = i + 1
                }
                saveTodayVisitations(holder.itemView.context, places)
                notifyItemChanged(position)
                notifyItemChanged(position - 1)
                actionListener.onReloadMap(false)
            }
        }
        holder.moveFirstBtn.setOnClickListener {
            moveItem(holder.itemView.context, position, 0)
        }

        holder.moveLastBtn.setOnClickListener {
            moveItem(holder.itemView.context, position, places.size-1)
        }

        holder.moveDownBtn.setOnClickListener {
            if (position < places.size - 1) {
                Collections.swap(places, position, position + 1)
                notifyItemMoved(position, position + 1)
                for (i in places.indices) {
                    places[i].order = i + 1
                }
                saveTodayVisitations(holder.itemView.context, places)
                notifyItemChanged(position)
                notifyItemChanged(position + 1)
                actionListener.onReloadMap(false)
            }
        }
    }

    override fun getItemCount() = places.size

    fun moveItem(context: Context,from: Int, to: Int) {
        if (from < to) {
            for (i in from until to) {
                Collections.swap(places, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(places, i, i - 1)
            }
        }

        // Update orders
        for (i in places.indices) {
            places[i].order = i + 1
        }

        saveTodayVisitations(context, places)
        notifyItemMoved(from, to)
        notifyItemRangeChanged(minOf(from, to), abs(from - to) + 1)
        actionListener.onReloadMap(false)
    }

}

data class OrderColor(val colorInt: Int, val hue: Float)

fun getOrderColor(order: Int): OrderColor {
    val color = when (order) {

        1 -> Color.parseColor("#4a80f5")
        2 -> Color.parseColor("#9bbff4")
        3 -> Color.parseColor("#13355c")
        4 -> Color.parseColor("#f18d00")
        5 -> Color.parseColor("#ce903d")
        6 -> Color.parseColor("#7a8790")
        7 -> Color.parseColor("#102049")
        8 -> Color.parseColor("#2b626a")
        9 -> Color.parseColor("#579098")
        10 -> Color.parseColor("#e49edd")
        11 -> Color.parseColor("#ffd700")
        12 -> Color.parseColor("#4291bf")
        13 -> Color.parseColor("#839a60")
        14 -> Color.parseColor("#637942")
        else -> Color.parseColor("#ff5a5f")
    }

    val hueArray = FloatArray(3)
    Color.colorToHSV(color, hueArray)
    return OrderColor(colorInt = color, hue = hueArray[0])
}






