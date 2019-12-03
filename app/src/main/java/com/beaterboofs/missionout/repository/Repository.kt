package com.beaterboofs.missionout.repository

import com.beaterboofs.missionout.data.Mission
import com.beaterboofs.missionout.data.Response


class Repository {

    fun sendAlarm(mission: Mission, myPath: String) {
        FirestoreRemoteDataSource().sendAlarm(mission, myPath)
    }

    fun deleteResponse(path: String) {
        FirestoreRemoteDataSource().deleteResponse(path)
    }

    fun sendResponse(path: String, response: Response, uid: String) {
        FirestoreRemoteDataSource().sendResponse(path, response, uid)
    }
}