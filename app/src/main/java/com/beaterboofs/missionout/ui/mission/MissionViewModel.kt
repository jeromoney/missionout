package com.beaterboofs.missionout.ui.mission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.beaterboofs.FirestoreRepository
import com.beaterboofs.missionout.Mission
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers

class MissionViewModel : ViewModel() {
    var docId: String? = null

    companion object {
        val TAG = "MissionViewModel"
    }

    private val missionInstance: LiveData<Mission> = liveData(Dispatchers.IO) {
        val cachedMission = Mission() // TODO - replace this with cached version
        emit(cachedMission)

        //val actualMission = FirestoreRepository.loadMission("4l6OAaCyVyLAaN9tUrSR")
        //emit(actualMission)
    }

    fun getMission(): LiveData<Mission> {
        return missionInstance
    }
}

