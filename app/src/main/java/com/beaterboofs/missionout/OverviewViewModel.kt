package com.beaterboofs.missionout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers

class OverviewViewModel : ViewModel() {
    private var missionOverview: LiveData<Array<Mission>> = liveData (Dispatchers.IO){
        val db = Firebase.firestore
        // Get missions within a certain timeframe
//        val collectionPath = "/teams/${teamDocId}/missions"
//        val query = db
//            .collection(collectionPath)
//            .orderBy("time", Query.Direction.DESCENDING)
//            .limit(5)
//            .get()
//
//        query.addOnSuccessListener {snapshots->
//            val missionList = snapshots.toObjects<Mission>()
//            // I need to pass doc id to mission. probably a better way to do this
//            for (i in 0 until snapshots.size())
//            {
//                val docId = snapshots.documents[i].id
//                missionList[i].docId = docId
//            }
//            recyclerView.adapter = MissionAdapter(missionList,{ missionInstance : Mission ->
//                FirestoreRemoteDataSource.missionItemClicked(
//                    missionInstance.docId!!, fragment
//                )
//            })
//        }
    }
    fun getMissionOverview(): MutableLiveData<Array<Mission>> {
        return missionOverview as MutableLiveData<Array<Mission>>
    }
}
