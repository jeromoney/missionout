package com.beaterboofs.missionout.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.beaterboofs.missionout.*
import com.beaterboofs.missionout.data.MissionViewModel
import com.beaterboofs.missionout.data.LoginViewModel
import com.beaterboofs.missionout.util.UIUtil.getVisibility
import com.beaterboofs.missionout.databinding.FragmentDetailBinding
import com.beaterboofs.missionout.repository.FirestoreRemoteDataSource
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment(),AdapterView.OnItemSelectedListener {


    private val missionViewModel: MissionViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding

    companion object{
        private val TAG = "DetailFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // If user navigated here with a blank path, that means that the viewmodel is already established
        missionViewModel.updateModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )
        missionViewModel.apply {
            if (!args.path.isNullOrBlank()){
                path = args.path!!
            }
            mission.observe(viewLifecycleOwner, Observer { mission->
                binding.missionInstance = mission
                if (mission == null){
                    return@Observer
                }
                // see if user RSVPed to mission. Set that chip as checked
                val displayName = loginViewModel.user.value!!.displayName
                val response = mission.responseMap?.getOrDefault(displayName, null) ?: return@Observer
                for (i in 0 until response_chip_group.size){
                    val chip = response_chip_group[i] as Chip
                    if (chip.text == response){
                        response_chip_group.check(chip.id)
                    }
                }
                // check if lat/lon is given and set visibility of map icon accordingly
                map_icon.visibility = getVisibility(mission.location != null)
            })
        }

        loginViewModel.editor.observe(viewLifecycleOwner, Observer { isEditor ->
            // Floating action bar with alarm should only be shown to editors
            edit_text_button.visibility = getVisibility(isEditor)
            alert_text_button.visibility = getVisibility(isEditor)

        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        response_chip_group.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1){
            // User just deselected a button so remove response
            FirestoreRemoteDataSource().deleteResponse(missionViewModel.path)
            return@setOnCheckedChangeListener
            }
            // Check if we entered this state by an automatic selection of viewmodel. This is detected
            // by observing the state of the chip group matches firestore database records
            val displayName = loginViewModel.user.value!!.displayName
            val vmResponse = missionViewModel.mission.value?.responseMap?.getOrDefault(displayName, null)
            val chipResponse = activity!!.findViewById<Chip>(checkedId).text
            if (vmResponse == chipResponse){
                // Entered this state when the viewmodel changed, and no actual human interaction
                return@setOnCheckedChangeListener
            }

            val response = group.findViewById<Chip>(checkedId).text.toString()
            FirestoreRemoteDataSource()
                .sendResponse(missionViewModel.path,response)
        }


        // enable click on geopoint to external uri
        map_icon.setOnClickListener {
            val geoPoint = missionViewModel.mission.value!!.location!!
            val gmmIntentUri = Uri.parse("geo:0,0?z=5&q=${geoPoint.latitude},${geoPoint.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(mapIntent)
        }
        // set up edit button
        edit_text_button.setOnClickListener{
            val action = DetailFragmentDirections.actionDetailFragmentToCreateFragment(isCreateNewMission = false)
            findNavController().navigate(action)
        }


        // set up raise alarm
        alert_text_button.setOnClickListener {
            // People with editor status (TODO - add check in database) create a document in the
            // "alarms" collection. Google Cloud Function will then run send out the alarm.
            MaterialAlertDialogBuilder(context,
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
            )
                .setTitle(getString(R.string.page_the_team))
                .setMessage(getString(R.string.page_message))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    val mission = binding.missionInstance!!
                    FirestoreRemoteDataSource()
                        .putAlarm(mission, missionViewModel.path)
                }
                .show()

        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do Nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0){
            // The first position is the response description and not a response value
            return
        }
        val response = (view as TextView).text as String
        FirestoreRemoteDataSource()
            .sendResponse(missionViewModel.path, response)
    }

}
