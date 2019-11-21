package com.beaterboofs.missionout

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlin.collections.HashMap

data class Mission(
    @DocumentId var key: String?,
    @PropertyName("description") var description: String,
    @ServerTimestamp var  time: Timestamp? = null,
    @PropertyName("location")  var location: GeoPoint?,
    @PropertyName("needForAction") var needForAction: String?,
    @PropertyName("locationDescription") var locationDescription: String?,
    @PropertyName("responseMap") var responseMap: HashMap<String,String>?,
    var docId: String?) {
    constructor() : this(null,"",null, null, null, null, null, null)
}
