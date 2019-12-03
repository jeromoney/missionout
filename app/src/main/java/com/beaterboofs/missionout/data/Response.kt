package com.beaterboofs.missionout.data

import com.google.firebase.firestore.GeoPoint

data class Response(
    val name: String,
    val response: String,
    var location: GeoPoint? = null
)