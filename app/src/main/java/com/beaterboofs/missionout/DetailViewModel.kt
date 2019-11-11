package com.beaterboofs.missionout.ui.mission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.beaterboofs.missionout.Mission
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers

class DetailViewModel() : ViewModel() {

    lateinit var docId: String
    lateinit var teamDocId: String

    companion object {
        val TAG = "DetailViewModel"
    }

    private val missionInstance: LiveData<Mission> = liveData(Dispatchers.IO) {
        val db = Firebase.firestore
        // Get missions within a certain timeframe
        val path = "/teams/${teamDocId}/missions/${docId}"
        db.document(path).addSnapshotListener{snapshot, error ->
            if (error != null){
                // handle error
            }
            else {
                val mission = snapshot?.toObject<Mission>()
                if (mission != null){
                    // update ui
                    getMission().value = mission
                }
            }

        }
    }

    fun getMission(): MutableLiveData<Mission> {
        return missionInstance as MutableLiveData<Mission>
    }
}

