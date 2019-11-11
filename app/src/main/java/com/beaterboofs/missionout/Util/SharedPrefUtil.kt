package com.beaterboofs.missionout.Util

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseUser

object SharedPrefUtil {

    fun getTeamDocId(context: Context) : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("teamDocID", null)
    }

    fun isEditor(context: Context) : Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("editor", false)
    }

    fun updateSharedPreferences(context: Context, firebaseUser: FirebaseUser?) {
        var i = 1
        firebaseUser?.getIdToken(true)?.addOnSuccessListener { result -> // TODO - convert to coroutine
            val isEditor = result?.claims?.getOrDefault("editor", false)
            val teamDocId = result?.claims?.getOrDefault("teamDocID", null)
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit(commit = true) {
                putBoolean("editor", (isEditor as Boolean?)!!)
                putString("teamDocID", teamDocId as String?)
            }
        }
    }

    /***
     * Stores token so it can be removed from firestore when it becomes outdated
     */
    fun setToken(context: Context, token: String){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit(commit = true){
            putString("token",token)
        }
    }

    fun getToken(context: Context) : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("token", null)
    }
}