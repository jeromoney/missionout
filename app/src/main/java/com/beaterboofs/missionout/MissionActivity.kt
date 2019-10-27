package com.beaterboofs.missionout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.beaterboofs.missionout.ui.mission.DisplayMissionFragment

class MissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mission_activity)
        val create_mission = intent.getBooleanExtra("create_mission", false)
        var fragment : Fragment
        if (create_mission){
            fragment = CreateMissionFragment.newInstance()
        }
        else {
            fragment = DisplayMissionFragment.newInstance()
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }

}
