package com.beaterboofs.missionout.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beaterboofs.missionout.repository.FirestoreRemoteDataSource
import com.beaterboofs.missionout.repository.Repository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                isStoodDown = value.getBoolean("isStoodDown")?: false
            }
            mission.value = missionFromDB
        }
        )
    }

    fun sendResponse(responseStr: String, loginViewModel: LoginViewModel){
        val uid = loginViewModel.user.value!!.uid
        val displayName = loginViewModel.user.value!!.displayName!!

        if (responseStr == "Responding"){
            // Send current location along with response
            GlobalScope.launch {
                val geoPoint = loginViewModel.getLocation()
                val response = Response(name = displayName, response = responseStr, location = geoPoint)
                Repository().sendResponse(path, response, uid)
            }
        }
        else {
            val response = Response(name = displayName, response = responseStr)
            Repository().sendResponse(path, response, uid)
        }
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
        val missionInstance = mission.value!!
        missionInstance.apply {
            if (description.isBlank()){
                return false
            }
            if (locationDescription.isNullOrBlank()){
                return false
            }
        }
        return true
    }

    fun standDownMission(isStoodDown: Boolean) {
        FirestoreRemoteDataSource().standDownMission(path,isStoodDown)
    }

    fun sendAlarm() {
        Repository().sendAlarm(mission.value!!, path)
    }

    fun deleteResponse() {
        Repository().deleteResponse(path)
    }
}

