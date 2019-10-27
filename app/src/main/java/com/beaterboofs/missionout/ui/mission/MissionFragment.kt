package com.beaterboofs.missionout.ui.mission

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.beaterboofs.FirestoreRepository
import com.beaterboofs.missionout.BR
import com.beaterboofs.missionout.Mission
import com.beaterboofs.missionout.MissionActivity
import com.beaterboofs.missionout.R
import com.beaterboofs.missionout.databinding.MissionFragmentBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.iid.FirebaseInstanceId

import kotlinx.android.synthetic.main.mission_fragment.*
import kotlinx.android.synthetic.main.mission_fragment.view.*
import java.sql.Time
import java.sql.Timestamp
import java.util.*


class MissionFragment : Fragment() {
    companion object {
        fun newInstance() = MissionFragment()
        val TAG = "MissionFragment"
    }

    private lateinit var viewModel: MissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.mission_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FirebaseInstanceId.getInstance().instanceId  //TODO -DELETE THIS
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = token.toString()
                Log.d(TAG, msg)
            })

        viewModel = ViewModelProviders.of(this).get(MissionViewModel::class.java)


        val binding: MissionFragmentBinding =
            DataBindingUtil.setContentView<MissionFragmentBinding>(this.requireActivity(), R.layout.mission_fragment)
        binding.lifecycleOwner = this
        // set onClickListener for FAB
        binding.createMission.setOnClickListener{
            // submit mission to firestore database
            val isDataValid = true
            if (isDataValid){
                // create object from input text TODO - use todo to simplify code
                val description = binding.missionDescriptionEditTextVal.text.toString()
                val needForAction = binding.needForActionEditTextVal.text.toString()
                val locationDescription = binding.locationDescriptionEditTextVal.text.toString()
                val lat = binding.latEditTextVal.text.toString().toDouble()
                val lon = binding.lonEditTextVal.text.toString().toDouble()
                val geopoint = GeoPoint(lat, lon)

                val mission = Mission(
                    description = description,
                    location = geopoint,
                    locationDescription = locationDescription,
                    needForAction = needForAction,
                    docId = null,
                    responseMap = null)
                var db = FirebaseFirestore.getInstance()
                db.collection("/teams/raux5KIhuIL84bBmPSPs/missions").add(mission) // TODO - customize document field
                    .addOnSuccessListener {docRef ->
                    // update worked so hide edit views
                        binding.editTextGroup.visibility = View.GONE
                        binding.textViewGroup.visibility = View.VISIBLE
                        viewModel.docId = docRef.id
                    }
                    .addOnFailureListener {
                        //update did not work
                    }
            }

        }


       // set up dropdown box
        val respondingList = resources.getStringArray(R.array.responding_dropdown_menu)
        var adapter = ArrayAdapter<String>(activity,R.layout.dropdown_menu_popup_item,respondingList)

        binding.respondingDropdownItems.setAdapter(adapter)
        binding.respondingDropdownItems.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val response = (view as MaterialTextView).text as String
                FirestoreRepository.sendResponse(response!!, viewModel.docId!!)
            }
        }


        val createMission = (context as MissionActivity).intent.getBooleanExtra("create_mission", false)
        if (createMission){
            // show the edit text fields and hide the edit text fields
            binding.editTextGroup.visibility = View.VISIBLE
            binding.textViewGroup.visibility = View.GONE
        }
        else{
            binding.editTextGroup.visibility = View.GONE
            binding.textViewGroup.visibility = View.VISIBLE
            viewModel.docId = (context as MissionActivity).intent.getStringExtra("mission_id")
        }
        viewModel.getMission().observe(this, Observer {
            // update UI
            binding.setVariable(BR.missionInstance, viewModel.getMission().value)
        })

        // Set up alarm fab
        // Floating action bar with create should only be shown to editors
        val user = FirebaseAuth.getInstance().currentUser
        alarm_fab.hide()
        // alarm team
        binding.alarmFab.setOnClickListener {
            Log.i(TAG, "ALERT THE TEAM")
        }


        user?.getIdToken(true)?.addOnSuccessListener { result ->
            val isEditor = result?.claims?.get("editor")
            if (isEditor == true){
                alarm_fab.show()
            }
        }



    }

}
