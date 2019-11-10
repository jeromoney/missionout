package com.beaterboofs.missionout.ui.mission

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.beaterboofs.missionout.FirestoreRemoteDataSource
import com.beaterboofs.missionout.*
import com.beaterboofs.missionout.databinding.FragmentMissionDisplayBinding
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.fragment_mission_display.*


class DisplayMissionFragment(docIdVal: String) : Fragment() {
    private val docIdFrag = docIdVal
    private val TAG = "DisplayMissionFragment"
    private lateinit var viewModel: DisplayMissionViewModel
    companion object {
        @JvmStatic
        fun newInstance(docIdVal: String) =
            DisplayMissionFragment(docIdVal)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_mission_display, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val binding: FragmentMissionDisplayBinding =
            DataBindingUtil.setContentView(this.requireActivity(), R.layout.fragment_mission_display)
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this).get(DisplayMissionViewModel::class.java)
        viewModel.apply {
            docId = docIdFrag
            teamDocId = SharedPrefUtil.getTeamDocId(context!!.applicationContext)!!
            getMission().observe(this@DisplayMissionFragment, Observer {
                // update UI
                binding.setVariable(BR.missionInstance, viewModel.getMission().value)
            })
        }




        // set up raise alarm
        binding.alarmFab.setOnClickListener {
            // People with editor status (TODO - add check in database) create a document in the
            // "alarms" collection. Google Cloud Function will then run send out the alarm.
            val mission = binding.missionInstance

            // TODO - Add a confirmation screen to prevent butt dials
            val db = FirebaseFirestore.getInstance()
            val teamDocId = SharedPrefUtil.getTeamDocId(this.requireContext())
            val alarm = Alarm(
                description = mission?.description,
                action = mission?.needForAction,
                teamDocId = teamDocId!!,
                missionDocID = docIdFrag
            )
            db.collection("alarms").add(alarm)
                .addOnSuccessListener {
                var i  = 1
                    //TODO - handle sucess
            }
                .addOnFailureListener {
                    var i = 1
                    // TODO - handle failure
                }



        }

       // set up dropdown box
        val respondingList = resources.getStringArray(R.array.responding_dropdown_menu)
        val adapter = ArrayAdapter<String>(context!!,R.layout.dropdown_menu_popup_item,respondingList)

        binding.respondingDropdownItems.apply {
            setAdapter(adapter) // todo - remove binding
            onItemClickListener = object : AdapterView.OnItemClickListener{
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val response = (view as MaterialTextView).text as String
                    FirestoreRemoteDataSource.sendResponse(context, response, viewModel.docId!!)
                }
            }
        }

        // Set up alarm fab
        // Floating action bar with create should only be shown to editors
        alarm_fab.hide()
        if (SharedPrefUtil.isEditor(this.requireContext())){
            alarm_fab.show()
        }
    }

}
