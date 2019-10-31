package com.beaterboofs.missionout

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.beaterboofs.missionout.ui.mission.DisplayMissionFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_create_mission.*

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

    fun validNumber(editable: Editable, low: Double, high: Double): Boolean{
        val number = editable.toString().toDouble()
        return (number in low..high)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Set data validation for lat/lon
        lat_edit_text_val.doAfterTextChanged {
            if (it.isNullOrBlank() || it.toString() == "-"){
                return@doAfterTextChanged
            }
            val number = it.toString().toDouble()
            if (!(number in -90.0..90.0)){
                lat_edit_text_val.error = "Latitude is between -90 and 90"
            }
        }

        lon_edit_text_val.doAfterTextChanged {
            if (it.isNullOrBlank() || it.toString() == "-"){
                return@doAfterTextChanged
            }
            val number = it.toString().toDouble()
            if (!(number in -180.0..180.0)){
                lon_edit_text_val.error = "Longitude is between -180 and 180"
            }
        }

        // set onClickListener for FAB
        create_mission.setOnClickListener{
            if (isTextInError()){
                return@setOnClickListener
            }

            val missionInstance = getMissionFromText()
            // submit mission to firestore database
            var db = FirebaseFirestore.getInstance()
            val teamDocId = SharedPrefUtil.getTeamDocId(this.requireContext())
            val fragment = DisplayMissionFragment.newInstance(missionInstance)

            db.collection("/teams/${teamDocId}/missions").add(missionInstance)
                .addOnSuccessListener { docRef ->
                    // Document sucessfuly pushed to firestore so now we send docId to next fragment
                    val docId = docRef.id
                    // TODO - pass this docId to the viewmodel
                }
            //TODO - what to do if fails
            parentFragmentManager.beginTransaction()
                .replace(R.id.mission_detail, fragment)
                .commitNow()
        }
    }

    private fun getMissionFromText(): Mission {
        val description = mission_description_edit_text_val.text.toString()
        val needForAction = need_for_action_edit_text_val.text.toString()
        val locationDescription = location_description_edit_text_val.text.toString()
        val lat = lat_edit_text_val.text.toString().toDoubleOrNull()
        val lon = lon_edit_text_val.text.toString().toDoubleOrNull()
        var geopoint : GeoPoint?
        if (lat != null && lon != null){
            geopoint = GeoPoint(lat, lon)
        }
        else {
            geopoint = null
        }

        return Mission(
            description = description,
            location = geopoint,
            locationDescription = locationDescription,
            needForAction = needForAction,
            docId = null,
            responseMap = null)
    }

    private fun isTextInError(): Boolean { // TODO - is there a better way to do data validation?
        if (mission_description_edit_text_val.text.isNullOrBlank()){
            mission_description_edit_text_val.error = getString(R.string.required_error)
            return true
        }
        if (lon_edit_text_val.text.isNullOrBlank() && lat_edit_text_val.text.isNullOrBlank()){
            return false
        }
        with(lon_edit_text_val){ 
            if (text.toString() == "-"){
                error = getString(R.string.number_error)
                return true
            }
            if (text.isNullOrBlank()){
                error = context.getString(R.string.latlonerror)
                lat_edit_text_val.error = context.getString(R.string.latlonerror)
                return true
            }
        }
        with(lat_edit_text_val){
            if (text.toString() == "-"){
                error = getString(R.string.number_error)
                return true
            }
            if (text.isNullOrBlank()){
                error = context.getString(R.string.latlonerror)
                lon_edit_text_val.error = context.getString(R.string.latlonerror)
                return true
            }
        }

        if (!(lat_edit_text_val.text.toString().toDouble() in -90.0..90.0) ||
                    !(lon_edit_text_val.text.toString().toDouble() in -180.0..180.0)){
            return true
        }
        return false
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
