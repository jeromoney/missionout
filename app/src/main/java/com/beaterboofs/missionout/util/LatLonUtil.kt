package com.beaterboofs.missionout.util

data class LatLon(val type: String, val range: Double)

val LATITUDE = LatLon("Latitude", 90.0)

val LONGITUDE = LatLon("Longitude", 180.0)