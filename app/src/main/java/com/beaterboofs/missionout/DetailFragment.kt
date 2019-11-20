package com.beaterboofs.missionout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.beaterboofs.missionout.databinding.FragmentDetailBinding
import com.google.android.material.chip.Chip

import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment(),AdapterView.OnItemSelectedListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        docIdVal = args.docID
        detailViewModel.updateModel()
    }
    private val detailViewModel: DetailViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()

    private val TAG = "DetailFragment"
    private lateinit var docIdVal : String
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding
    private var checkedChipID : Int = -1


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
        detailViewModel.apply {
            docID = args.docID
            teamDocId = loginViewModel.teamDocID.value!!

            mission.observe(viewLifecycleOwner, Observer { mission->
                binding.missionInstance = mission
                // see if user RSVPed to mission. Set that chip as checked
                val displayName = loginViewModel.user.value!!.displayName
                val response = mission.responseMap?.get(displayName) ?: return@Observer
                for (i in 0 until response_chip_group.size){
                    val chip = response_chip_group.get(i) as Chip
                    if (chip.text == response){
                        checkedChipID = chip.id
                        response_chip_group.check(checkedChipID)
                    }
                }
            })
        }

        loginViewModel.editor.observe(viewLifecycleOwner, Observer { isEditor ->
            if (isEditor){
                // Floating action bar with alarm should only be shown to editors
                    alarm_fab.show()
            }
            else {
                alarm_fab.hide()
            }
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        response_chip_group.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1 || checkedChipID == checkedId){
                // User clicked on the same option twice
                return@setOnCheckedChangeListener
            }
            val response = group.findViewById<Chip>(checkedId).text.toString()
            val teamDocID = loginViewModel.teamDocID.value!!
            FirestoreRemoteDataSource().sendResponse(teamDocID,response,docIdVal)
        }


        // enable click on geopoint to external uri
//        location_text_view.setOnClickListener {
//            val geoPoint = detailViewModel.mission.value!!.location!!
//            val gmmIntentUri = Uri.parse("geo:0,0?z=5&q=${geoPoint.latitude},${geoPoint.longitude}")
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            startActivity(mapIntent)
//        }


        // set up raise alarm
        alarm_fab.setOnClickListener {
            // People with editor status (TODO - add check in database) create a document in the
            // "alarms" collection. Google Cloud Function will then run send out the alarm.
            // TODO - Add a confirmation screen to prevent butt dials
            val mission = binding.missionInstance!!
            val teamDocId = loginViewModel.teamDocID.value!!
            FirestoreRemoteDataSource().addAlarmToDB(mission, teamDocId, docIdVal)
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
        FirestoreRemoteDataSource().sendResponse(loginViewModel.teamDocID.value!!, response, detailViewModel.docID)
    }

}
