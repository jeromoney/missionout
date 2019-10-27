package com.beaterboofs.missionout.ui.mission

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.beaterboofs.FirestoreRepository
import com.beaterboofs.missionout.BR
import com.beaterboofs.missionout.MissionActivity
import com.beaterboofs.missionout.R
import com.beaterboofs.missionout.databinding.FragmentMissionDisplayBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId

import kotlinx.android.synthetic.main.fragment_mission_display.*


class DisplayMissionFragment : Fragment() {
    companion object {
        fun newInstance() = DisplayMissionFragment()
        val TAG = "DisplayMissionFragment"
    }

    private lateinit var viewModel: DisplayMissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_mission_display, container, false)
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

        viewModel = ViewModelProviders.of(this).get(DisplayMissionViewModel::class.java)


        val binding: FragmentMissionDisplayBinding =
            DataBindingUtil.setContentView<FragmentMissionDisplayBinding>(this.requireActivity(), R.layout.fragment_mission_display)
        binding.lifecycleOwner = this

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
        viewModel.docId = (context as MissionActivity).intent.getStringExtra("mission_id")

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
