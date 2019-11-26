package com.beaterboofs.missionout.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beaterboofs.missionout.repository.FirestoreRemoteDataSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject

class MissionViewModel: ViewModel() {


    lateinit var path: String
    private var firebaseRepository =
        FirestoreRemoteDataSource()
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
        registration = firebaseRepository.fetchMission(path).addSnapshotListener(EventListener<DocumentSnapshot> { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                mission.value = null
                return@EventListener
            }
            val myPath = value!!.reference.path
            val missionFromDB = value.toObject<Mission>()?.apply {
                path = myPath
            }
            mission.value = missionFromDB
        }
        )
    }

    /**
     *
     */
    suspend fun saveMission(teamDocID: String) {
        // tell UI that mission is saving
        if (!isMissionValid()) {
            // If the data is somehow not valid, do not save to database
            throw Error("Data is not valid")
        }
        val fsPath = FirestoreRemoteDataSource().putMission(teamDocID, mission.value!!)
        if (fsPath != null) {
            // mission is sucessfully saved, report back to UI.
            path = fsPath
        } else {
            // error saving mission
            throw Error("Error saving mission")
        }
    }

    private fun isMissionValid(): Boolean {
        return true
    }
}

