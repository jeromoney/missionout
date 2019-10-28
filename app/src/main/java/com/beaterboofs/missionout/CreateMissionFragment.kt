package com.beaterboofs.missionout

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beaterboofs.missionout.ui.mission.DisplayMissionFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_create_mission.*
import kotlinx.android.synthetic.main.mission_activity.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CreateMissionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CreateMissionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateMissionFragment : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // set onClickListener for FAB
        create_mission.setOnClickListener{
            // submit mission to firestore database
            val isDataValid = true // TODO - Check if data is valid
            if (isDataValid){
                // create object from input text
                val description = mission_description_edit_text_val.text.toString()
                val needForAction = need_for_action_edit_text_val.text.toString()
                val locationDescription = location_description_edit_text_val.text.toString()
                val lat = lat_edit_text_val.text.toString().toDouble()
                val lon = lon_edit_text_val.text.toString().toDouble()
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
                        // TODO - Pass object to displayfragment
                        val fragment = DisplayMissionFragment.newInstance(mission)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.mission_detail, fragment)
                            .commitNow()

                    }
                    .addOnFailureListener {
                        //update did not work
                    }
            }

        }




    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_mission, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateMissionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CreateMissionFragment()
    }
}
