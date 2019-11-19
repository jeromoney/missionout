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