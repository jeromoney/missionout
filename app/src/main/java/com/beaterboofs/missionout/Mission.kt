package com.beaterboofs.missionout

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import java.util.*

data class Mission(
    @PropertyName("description") var description: String,
    @PropertyName("time") var time: Date,
    @PropertyName("location") var location: GeoPoint){
    constructor() : this("",Date(0), GeoPoint(0.0,0.0)){
    }
}
