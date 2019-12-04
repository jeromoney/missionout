package com.beaterboofs.missionout.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beaterboofs.missionout.repository.Repository
import com.google.firebase.firestore.ListenerRegistration

class ResponseViewModel: ViewModel() {
    lateinit var missionPath: String
    val responses: MutableLiveData<List<Response>> = MutableLiveData()
    var registration : ListenerRegistration? = null
    private var repository = Repository()
    companion object {
        val TAG = "ResponseViewModel"
    }

    fun updateResponses() {
        // remove the old listener
        registration?.remove()
        registration = repository.fetchResponses(missionPath)
            .addSnapshotListener{values, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    responses.value = null
                    return@addSnapshotListener
                }
                responses.value = values!!.toObjects(Response::class.java)
            }
    }

    override fun onCleared() {
        super.onCleared()
        // stop listening once use navigates away from fragment
        registration?.remove()
    }

}