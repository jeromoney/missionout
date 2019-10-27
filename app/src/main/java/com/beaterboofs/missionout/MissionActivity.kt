package com.beaterboofs.missionout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beaterboofs.missionout.ui.mission.MissionFragment

class MissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mission_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MissionFragment.newInstance())
                .commitNow()
        }
    }

}