package com.beaterboofs.missionout.DataClass
// TODO - remove null allowment
data class Alarm(
    val description: String,
    val action: String?,
    val path: String
)