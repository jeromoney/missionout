package com.beaterboofs.missionout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration


class OverviewViewModel() : ViewModel() {
    val missions: MutableLiveData<List<Mission>> = MutableLiveData()
    var registration : ListenerRegistration? = null
    private var firebaseRepository = FirestoreRemoteDataSource()
    lateinit var teamDocID: String


    companion object {
        val TAG = "OverviewViewModel"
    }

    fun updateModel() {
        // Remove old registration if it exists
        registration?.remove()
        if (!::teamDocID.isInitialized){
            return
        }

        registration = firebaseRepository.fetchMissions(teamDocID)
            .addSnapshotListener{value, error ->
                if (error != null){
                    Log.w(TAG, "Listener failed", error)
                    missions.value = null
                    return@addSnapshotListener
                }
                val missionsFromDB = value?.toObjects(Mission::class.java)
                missions.value = missionsFromDB
        }
    }





}