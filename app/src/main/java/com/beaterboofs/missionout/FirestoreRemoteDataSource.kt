package com.beaterboofs.missionout

import android.content.Context
import android.util.Log
import com.beaterboofs.missionout.Util.SharedPrefUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirestoreRemoteDataSource {
    private val TAG = "FirestoreRemoteDataSource"
    fun sendResponse(context: Context, response: String, docId: String) {
        val db = Firebase.firestore
        val teamDocId = SharedPrefUtil.getTeamDocId(context)
        val path = "/teams/$teamDocId/missions/$docId"
        val docRef = db.document(path)
        val user = FirebaseAuth.getInstance().currentUser
        val name = user!!.displayName
        docRef.update("responseMap.${name}", response)
    }

    suspend fun addMissionToDB(context: Context, missionInstance: Mission) : String? {
        // submit mission to firestore database
        val db = Firebase.firestore
        val teamDocId = SharedPrefUtil.getTeamDocId(context)
        try{
            val docRef = db.collection("/teams/$teamDocId/missions").add(missionInstance).await()
            return docRef.id
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
    suspend fun sendTokenToServer(userUID: String, token: String, oldToken: String?){
        val db = Firebase.firestore
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

}
