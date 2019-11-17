package com.beaterboofs.missionout

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.beaterboofs.missionout.DataClass.Alarm
import com.beaterboofs.missionout.Util.SharedPrefUtil
import com.beaterboofs.missionout.databinding.FragmentDetailBinding
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        docIdVal = args.docID
    }
    private val detailViewModel: DetailViewModel by activityViewModels()

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
            docId = args.docID
            teamDocId = SharedPrefUtil.getTeamDocId(context!!.applicationContext)!!
            getMission().observe(viewLifecycleOwner, Observer { mission->
                binding.missionInstance = mission
            })
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        // enable click on geopoint to external uri
        location_text_view.setOnClickListener {
            val geoPoint = detailViewModel.getMission().value!!.location!!
            val gmmIntentUri = Uri.parse("geo:${geoPoint.latitude},${geoPoint.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(mapIntent)
        }


        // set up raise alarm
        alarm_fab.setOnClickListener {
            // People with editor status (TODO - add check in database) create a document in the
            // "alarms" collection. Google Cloud Function will then run send out the alarm.
            val mission = binding.missionInstance
            // TODO - Add a confirmation screen to prevent butt dials
            val db = FirebaseFirestore.getInstance()
            val teamDocId = SharedPrefUtil.getTeamDocId(this.requireContext())
            val alarm = Alarm(
                description = mission!!.description,
                action = mission.needForAction,
                teamDocId = teamDocId!!,
                missionDocID = docIdVal
            )
            db.collection("alarms").add(alarm)
                .addOnSuccessListener {
                var i  = 1
                    //TODO - handle success
            }
                .addOnFailureListener {
                    var i = 1
                    // TODO - handle failure
                }



        }

       // set up dropdown box
        val respondingList = resources.getStringArray(R.array.responding_dropdown_menu)
        val adapter = ArrayAdapter<String>(context!!,R.layout.dropdown_menu_popup_item,respondingList)

        responding_dropdown_items.apply {
            setAdapter(adapter) // todo - remove binding
            onItemClickListener = object : AdapterView.OnItemClickListener{
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val response = (view as MaterialTextView).text as String
                    FirestoreRemoteDataSource.sendResponse(context, response, detailViewModel.docId)
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
