package com.beaterboofs.missionout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject

class DetailViewModel: ViewModel() {


    lateinit var docID: String
    lateinit var teamDocId: String
    private var firebaseRepository = FirestoreRemoteDataSource()
    val mission: MutableLiveData<Mission> = MutableLiveData()
    var registration : ListenerRegistration? = null


    companion object {
        val TAG = "DetailViewModel"
    }

    // The user can navigate back to the detail screen with a new mission. The viewmodel needs to
    // update for the new document ID
    fun updateModel(){
        if (registration != null){
            // Remove the old listener
            registration!!.remove()
        }
        registration = firebaseRepository.fetchMission(teamDocId, docID).addSnapshotListener(EventListener<DocumentSnapshot> { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                mission.value = null
                return@EventListener
            }
            val missionFromDB = value?.toObject<Mission>()
            mission.value = missionFromDB
        }
        )
    }
}

