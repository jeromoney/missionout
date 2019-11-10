package com.beaterboofs.missionout
// TODO - remove null allowment
data class Alarm(
    val description: String,
    val action: String?,
    val missionDocID: String,
    val teamDocId: String
)