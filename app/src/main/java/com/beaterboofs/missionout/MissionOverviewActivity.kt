package com.beaterboofs.missionout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects

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
        viewAdapter = MissionAdapter(dataset, { missionInstance : Mission -> missionItemClicked(missionInstance) })


        recyclerView = findViewById<RecyclerView>(R.id.mission_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        loadMissions()

        // Floating action bar with create should only be shown to editors
        val user = FirebaseAuth.getInstance().currentUser
        fab.hide()
        user?.getIdToken(true)?.addOnSuccessListener { result ->
            val isEditor = result?.claims?.get("editor")
            if (isEditor == true){
                fab.show()
            }
        }
        // Create a new mission
        fab.setOnClickListener {
            intent = Intent(this, MissionActivity::class.java).apply {
                putExtra("create_mission", true)
            }
            startActivity(intent)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)





    }

    private fun missionItemClicked(missionInstance: Mission){
        // Launch Mission Activity Detail with clicked item
        intent = Intent(this, MissionActivity::class.java).apply {
            putExtra("mission_id", missionInstance.docId)
        }
        startActivity(intent)
    }


      fun loadMissions() {
        // TODO -- move to own class
        // asynchronous operation
          // direct user based on sign in status
          val user = FirebaseAuth.getInstance().currentUser

          user?.getIdToken(true)?.addOnSuccessListener { result ->
              val teamDocId = result?.claims?.get("teamDocID")
              var db = FirebaseFirestore.getInstance()
              // Get missions within a certain timeframe
              val collectionPath = "/teams/${teamDocId}/missions"
              val query = db
                  .collection(collectionPath)
                  .orderBy("time", Query.Direction.DESCENDING)
                  .limit(5)
                  .get()

              query.addOnSuccessListener {snapshots->
                  val missionList = snapshots.toObjects<Mission>()
                  // I need to pass doc id to mission. probably a better way to do this
                  for (i in 0 until snapshots.size())
                    {
                        var docId = snapshots.documents[i].id
                        missionList[i].docId = docId
                    }
                  recyclerView.adapter = MissionAdapter(missionList,{ missionInstance : Mission -> missionItemClicked(missionInstance) })
              }
          }

      }





}
