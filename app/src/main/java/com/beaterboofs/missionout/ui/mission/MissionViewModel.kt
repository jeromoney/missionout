package com.beaterboofs.missionout.ui.mission

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beaterboofs.missionout.Mission
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class MissionViewModel : ViewModel() {
    companion object {
        val TAG = "MissionViewModel"
    }

    private val missionInstance: MutableLiveData<Mission> by lazy {
        MutableLiveData<Mission>().also{
            loadMission()
        }
    }

    fun getMission(): LiveData<Mission> {
        return missionInstance
    }


    private fun loadMission() {
        // TODO -- move to own class
        //asynchronous operation
        var db = FirebaseFirestore.getInstance()
        // TODO - Just get last mission
        val docRef = db
            .collection("teams")
            .document("raux5KIhuIL84bBmPSPs")
            .collection("missions")
            .document("ROHvCLqBiMh8ONsSYhch")

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
                val mission = snapshot.toObject<Mission>()
                missionInstance.value = mission
            }

        }
    }
}
