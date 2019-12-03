package com.beaterboofs.missionout.data

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class Response(
    @PropertyName("name") val name: String,
    @PropertyName("response") val response: String,
    @PropertyName("location") var location: GeoPoint? = null,
    @PropertyName("driving_time") val driving_time: String? = null) {
    constructor() : this(name = "", response = "")
}
