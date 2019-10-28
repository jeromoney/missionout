package com.beaterboofs.missionout.ui.mission

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.beaterboofs.missionout.Mission
import kotlinx.coroutines.Dispatchers

class DisplayMissionViewModel : ViewModel() {
    var docId: String? = null
    lateinit var cachedMission: Mission

    companion object {
        val TAG = "DisplayMissionViewModel"
    }

    private val missionInstance: LiveData<Mission> = liveData(Dispatchers.IO) {
        emit(cachedMission)

        //val actualMission = FirestoreRepository.loadMission("4l6OAaCyVyLAaN9tUrSR")
        //emit(actualMission)
    }

    fun getMission(): LiveData<Mission> {
        return missionInstance
    }
}

