package com.beaterboofs.missionout.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlin.collections.HashMap

data class Mission(
    @DocumentId var key: String?= null,
    @PropertyName("description") var description: String,
    @ServerTimestamp var  time: Timestamp? = null,
    @PropertyName("location")  var location: GeoPoint?= null,
    @PropertyName("needForAction") var needForAction: String?= null,
    @PropertyName("locationDescription") var locationDescription: String,
    @PropertyName("responseMap") var responseMap: HashMap<String,String>?= null,
    @PropertyName("isStoodDown") var isStoodDown: Boolean = false,
    var path: String?= null) {
    constructor() : this(description = "", locationDescription = "")
}
