package com.beaterboofs.missionout

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlin.collections.HashMap

data class Mission(
    @PropertyName("description") var description: String,
    @ServerTimestamp var  time: Timestamp? = null,
    @PropertyName("location")  var location: GeoPoint?,
    @PropertyName("needForAction") var needForAction: String?,
    @PropertyName("locationDescription") var locationDescription: String?,
    @PropertyName("responseMap") var responseMap: HashMap<String,String>?,
    var docId: String?) {
    constructor() : this("",null, null, null, null, null, null)
}
