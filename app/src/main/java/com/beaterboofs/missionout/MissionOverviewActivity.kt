package com.beaterboofs.missionout

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_mission_overview.*

/**
 * Retrieves all the last missions within a time period. Displays as a scrolled list.
 *
 * The add button (only displayed for editors) sends user to create new mission.
 */
class MissionOverviewActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val TAG = "MissionOverviewActivity"
    private lateinit var dataset : List<Mission>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission_overview)
        setSupportActionBar(toolbar)

        dataset = listOf()
        viewManager = LinearLayoutManager(this)
        viewAdapter = MissionAdapter(dataset)


        recyclerView = findViewById<RecyclerView>(R.id.mission_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        loadMissions()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }



     fun loadMissions() {
        // TODO -- move to own class
        // asynchronous operation
        var db = FirebaseFirestore.getInstance()
        // Get missions within a certain timeframe
        val domain = AuthUtil.getDomain()

        val query = db
            .collection("teams")
            .whereEqualTo("domain",domain).get()



            //.collection("missions")
            //.get()

//        docRef.addOnSuccessListener {snapshots->
//            val missionList = snapshots.toObjects<Mission>()
//            recyclerView.adapter = MissionAdapter(missionList)
//            }
        }


}
