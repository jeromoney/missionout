package com.beaterboofs.missionout.ui.mission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.beaterboofs.missionout.Mission
import com.beaterboofs.missionout.SharedPrefUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.Dispatchers

class DisplayMissionViewModel() : ViewModel() {

    var docId: String? = null
    lateinit var cachedMission: Mission
    lateinit var teamDocId: String

    companion object {
        val TAG = "DisplayMissionViewModel"
    }

    private val missionInstance: LiveData<Mission> = liveData(Dispatchers.IO) {
        emit(cachedMission)
        loadData() // todo - REFACTOR OUTSIDE

        //val actualMission = FirestoreRepository.loadMission("4l6OAaCyVyLAaN9tUrSR")
        //emit(actualMission)
    }

    fun getMission(): MutableLiveData<Mission> {
        return missionInstance as MutableLiveData<Mission>
    }


    fun loadData() {
        //TODO - Move to seperate container
        val db = FirebaseFirestore.getInstance()
        // Get missions within a certain timeframe
        val collectionPath = "/teams/${teamDocId}/missions/${docId}"
        val query = db
            .document(collectionPath)
            .get()
        query.addOnSuccessListener { snapshot ->
            val mission = snapshot.toObject<Mission>()
            getMission().value = mission
        }
    }
}

