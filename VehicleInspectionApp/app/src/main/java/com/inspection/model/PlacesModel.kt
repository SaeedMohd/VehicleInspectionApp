package com.inspection.model

data class PlaceSearchResponse(
    val places: List<Place>
)

data class Place(
    val id: String,
    val types: List<String>,
    val shortFormattedAddress: String,
    val location: LatLng,
    val businessStatus: String,
    val displayName: DisplayName,
    val primaryTypeDisplayName: DisplayName?,
    val websiteUri: String? = null
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)

data class DisplayName(
    val text: String,
    val languageCode: String
)

