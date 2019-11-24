package com.beaterboofs.missionout.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.Activity
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.beaterboofs.missionout.*
import com.beaterboofs.missionout.data.LoginViewModel
import com.beaterboofs.missionout.data.Mission
import com.beaterboofs.missionout.repository.FirestoreRemoteDataSource
import com.beaterboofs.missionout.util.LATITUDE
import com.beaterboofs.missionout.util.LONGITUDE
import com.beaterboofs.missionout.util.LatLon
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout


class CreateMissionFragment : Fragment() {
    private val TAG = "CreateMissionFragment"
    private val loginViewModel: LoginViewModel by activityViewModels()
    private var listener: OnFragmentInteractionListener? = null



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set data validation for lat/lon
        lat_edit_text_val.apply {
            doAfterTextChanged {
                validateLatLon(this, LATITUDE)
            }
        }

        lon_edit_text_val.apply {
            doAfterTextChanged {
                validateLatLon(this, LONGITUDE)
            }
        }

        // set onClickListener for FAB
        create_mission.setOnClickListener{

            if (isTextInError()){
                return@setOnClickListener
            }
            // Start spinner
            createProgressBar.visibility = View.VISIBLE
            hideKeyboard()
            // Hide text
            for (child in create_mission_layout.children){
                if (child is TextInputLayout || child is FloatingActionButton){
                    child.visibility = View.GONE
                }
            }

            val missionInstance = getMissionFromText()
            CoroutineScope(Dispatchers.Main).launch {
                val teamDocID = loginViewModel.teamDocID.value!!
                val docID = FirestoreRemoteDataSource()
                    .putMission(teamDocID, missionInstance)
                //Stop spinner
                if (docID != null) {
                    val action =
                        MobileNavigationDirections.actionGlobalDetailFragment(
                            docID
                        )
                    val options = NavOptions.Builder().setLaunchSingleTop(true).build()
                    findNavController().navigate(action,options)
                }
                else {
                    // error occured so display snackbar
                    createProgressBar.visibility = View.GONE
                    Snackbar.make(it,getString(R.string.error_uploading),Snackbar.LENGTH_LONG)
                        .also {
                        it.show()
                    }
                    // Show text
                    for (child in create_mission_layout.children){
                        if (child is TextInputLayout || child is FloatingActionButton){
                            child.visibility = View.VISIBLE
                        }
                    }

                }
            }


        }
    }

    private fun hideKeyboard() {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
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
            needForAction = needForAction
        )
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

        if (!(lat_edit_text_val.text.toString().toDouble() in -LATITUDE.range..LATITUDE.range) ||
                    !(lon_edit_text_val.text.toString().toDouble() in -LONGITUDE.range..LONGITUDE.range)){
            return true
        }
        return false
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false)
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
    /***
     * Validates Lat and Lon entries while typing. Checks if number is outside of range.
     * Returns null if everything is good, or an error message.
     */
    private fun validateLatLon(latLon: TextInputEditText, latLonConst : LatLon){
        val number = latLon.text.toString()
        val range = latLonConst.range
        if (number.isBlank() || number == "-"){
            // User probably just started to type the number. Not that this still needs to be checked
            return
        }
        if (number.toDouble() in -1 * range..range){
            // number is in range so everything is good
            return
        }
        latLon.error = "${latLonConst.type} is between -${range.toInt()} and ${range.toInt()}"
    }
}
