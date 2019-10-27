package com.beaterboofs

import android.util.Log
import androidx.lifecycle.LiveData
import com.beaterboofs.missionout.Mission
import com.beaterboofs.missionout.ui.mission.MissionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirestoreRepository {
    private val TAG = "FirestoreRepository"

    suspend fun loadMission(docId: String) : Mission =
        withContext(Dispatchers.IO){
            val db = FirebaseFirestore.getInstance()
            val doc = db
                .collection("teams/raux5KIhuIL84bBmPSPs/missions")
                .document(docId)// TODO - REMOVE HARD CODE
                .get()
                .result

            val mission = doc!!.toObject<Mission>()
            mission!!.docId = docId
            return@withContext mission!!
         }

    fun sendResponse(response: String, docId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db
            .collection("teams")
            .document("raux5KIhuIL84bBmPSPs")  //TODO - remove hard code
            .collection("missions")
            .document(docId!!)
        val user = FirebaseAuth.getInstance().currentUser
        val name = user!!.displayName
        docRef.update("responseMap.${name}", response)
    }
}
