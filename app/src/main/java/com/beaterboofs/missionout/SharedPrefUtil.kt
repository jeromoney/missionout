package com.beaterboofs.missionout

import android.content.Context
import androidx.preference.PreferenceManager

object SharedPrefUtil {

    fun getTeamDocId(context: Context) : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context /* Activity context */)
        return sharedPreferences.getString("teamDocID", null)
    }

    fun isEditor(context: Context) : Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context /* Activity context */)
        return sharedPreferences.getBoolean("editor", false)
    }
}