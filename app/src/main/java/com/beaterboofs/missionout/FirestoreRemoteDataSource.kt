package com.beaterboofs.missionout

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.beaterboofs.missionout.DataClass.Alarm
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreRemoteDataSource {
    private val TAG = "FirestoreRemoteDataSource"
    val db = Firebase.firestore


    fun sendResponse(path: String, response: String) {
        val docRef = db.document(path)
        val user = FirebaseAuth.getInstance().currentUser
        val name = user!!.displayName
        docRef.update("responseMap.${name}", response)
    }

    suspend fun putMission(teamDocId: String, missionInstance: Mission) : String? {
        // submit mission to firestore database
        try{
            val docRef = db.collection("/teams/$teamDocId/missions").add(missionInstance).await()
            return docRef.path
        }
        catch (e: FirebaseException){
            // Display error to user
            Log.e(TAG, e.toString())
            return null
        }

    }

    /**
     * When a new token is generated, the client sends the token to the server so it can generate
     * message addresses. The old token -- if exists --  is deleted from database
     */
    suspend fun putToken(userUID: String, token: String, oldToken: String?){
        try {
            val query = db.collection("users").whereEqualTo("userUID",userUID).limit(1).get().await()
            if (query.size() == 0){
                // Either no documents or more than one documents found so we have an error
                throw FirebaseException("Found ${query.size()} documents, expecting 1")
            }
            val docSnapshot = query.documents[0]
            val docRef = db.document("users/${docSnapshot.id}")
            docRef.update("tokens", FieldValue.arrayUnion(token))
            if (oldToken != null){
                docRef.update("tokens", FieldValue.arrayRemove(oldToken))
            }
        }
        catch (e: FirebaseException){
            Log.e(TAG, e.toString())
        }
    }

    fun fetchMission(path: String) : DocumentReference {
        // Get missions within a certain timeframe
        val documentReference = db.document(path)
        return documentReference
    }

    fun fetchMissions(teamDocID : String) : Query {
        // Gets the last 5 missions
        val collectionPath = "/teams/${teamDocID}/missions"
        val query = db
            .collection(collectionPath)
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(5)
        return query
    }



    fun putAlarm(mission: Mission, myPath: String) {
        val alarm = Alarm(
            description = mission.description,
            action = mission.needForAction,
            path = myPath
        )
        db.collection("${myPath}/alarms").add(alarm)
            .addOnSuccessListener {
                var i  = 1
                //TODO - handle success
            }
            .addOnFailureListener {
                var i = 1
                // TODO - handle failure
            }
    }



    fun deleteResponse(path: String) {
        // removes a response from the mission document
        val docRef = db.document(path)
        val user = FirebaseAuth.getInstance().currentUser
        val name = user!!.displayName
        val updates = hashMapOf<String, Any>(
            "responseMap.${name}" to FieldValue.delete()
        )
        docRef.update(updates)
    }

}
