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
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.beaterboofs.missionout.DataClass.Alarm
import com.beaterboofs.missionout.databinding.FragmentDetailBinding
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment(),AdapterView.OnItemSelectedListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        docIdVal = args.docID
        detailViewModel.updateModel(docIdVal)

    }
    private val detailViewModel: DetailViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()

    private val TAG = "DetailFragment"
    private lateinit var docIdVal : String
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding


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
            vmDocId = args.docID
            teamDocId = loginViewModel.teamDocID.value!!

            getMission().observe(viewLifecycleOwner, Observer { mission->
                binding.missionInstance = mission
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


        // enable click on geopoint to external uri
        location_text_view.setOnClickListener {
            val geoPoint = detailViewModel.getMission().value!!.location!!
            val gmmIntentUri = Uri.parse("geo:0,0?z=5&q=${geoPoint.latitude},${geoPoint.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(mapIntent)
        }


        // set up raise alarm
        alarm_fab.setOnClickListener {
            // People with editor status (TODO - add check in database) create a document in the
            // "alarms" collection. Google Cloud Function will then run send out the alarm.
            // TODO - Add a confirmation screen to prevent butt dials
            val mission = binding.missionInstance!!
            val teamDocId = loginViewModel.teamDocID.value!!
            FirestoreRemoteDataSource.addAlarmToDB(mission, teamDocId, docIdVal)
        }

       // set up dropdown box
        responding_spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this.requireActivity(),
            R.array.responding_dropdown_menu,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            responding_spinner.adapter = adapter
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
        FirestoreRemoteDataSource.sendResponse(this.requireContext(), response, detailViewModel.vmDocId)
    }

}
