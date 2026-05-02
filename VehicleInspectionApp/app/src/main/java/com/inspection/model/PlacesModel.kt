package com.inspection.model

import com.inspection.fragments.ApplicantMapFragment
import java.sql.Date

data class PlaceSearchResponse(
    val places: List<Place>
)

data class Place(
    val id: String,
    val types: List<String>,
    val shortFormattedAddress: String,
    val location: LatLng,
    val businessStatus: String,
    var aarStatus: String,
    val displayName: DisplayName,
    val primaryTypeDisplayName: DisplayName?,
    val websiteUri: String? = null,
    var matchingFacilities: List<ApplicantMatchingFacilitiesModel> = emptyList(),
    var notes: String = "",
//    var saved: Boolean = false,
    var visited: Boolean = false,
    var visitedDate: String = "",
    var plannedDate: String = "",
    var etaLabel: String = "",
    var order: Int = 0
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)

data class DisplayName(
    val text: String,
    val languageCode: String
)

