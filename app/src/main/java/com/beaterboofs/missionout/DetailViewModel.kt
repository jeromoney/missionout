package com.beaterboofs.missionout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers

class DetailViewModel: ViewModel() {


    lateinit var vmDocId: String
    lateinit var teamDocId: String

    companion object {
        val TAG = "DetailViewModel"
    }
    // TODO - When the viewmodel already exists, the fragment is passing in a new docID, but it's not refreshing

    private var missionInstance: LiveData<Mission> = liveData(Dispatchers.IO) {
        val db = Firebase.firestore
        // Get missions within a certain timeframe
        val path = "/teams/${teamDocId}/missions/${vmDocId}"
        db.document(path).addSnapshotListener{snapshot, error ->
            if (error != null){
                // handle error
            }
            else {
                val mission = snapshot?.toObject<Mission>()?.apply {
                    docId = vmDocId
                }
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

    // The user can navigate back to the detail screen with a new mission. The viewmodel needs to
    // update for the new document ID
    fun updateModel(docIdfromFragment: String){
        val mission = getMission().value
        if (mission == null || mission.docId == docIdfromFragment){
            // The docIds match (or the mission hasn't been created yet, so we don't need to do anything
            return
        }

        missionInstance = liveData(Dispatchers.IO) {
            val db = Firebase.firestore
            // Get missions within a certain timeframe
            val path = "/teams/${teamDocId}/missions/${vmDocId}"
            db.document(path).addSnapshotListener{snapshot, error ->
                if (error != null){
                    // handle error
                    var i = 1
                    i = i + 1
                }
                else {
                    val mission = snapshot?.toObject<Mission>()?.apply {
                        docId = vmDocId
                    }
                    if (mission != null){
                        // update ui
                        getMission().value = mission
                    }
                }

            }
        }
    }
}

