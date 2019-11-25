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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.beaterboofs.missionout.*
import com.beaterboofs.missionout.data.MissionViewModel
import com.beaterboofs.missionout.data.LoginViewModel
import com.beaterboofs.missionout.data.Mission
import com.beaterboofs.missionout.databinding.FragmentCreateBinding
import com.beaterboofs.missionout.repository.FirestoreRemoteDataSource
import com.beaterboofs.missionout.util.LATITUDE
import com.beaterboofs.missionout.util.LONGITUDE
import com.beaterboofs.missionout.util.LatLon
import com.beaterboofs.missionout.util.UIUtil.getVisibility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.GlobalScope
import java.lang.Exception


class CreateMissionFragment : Fragment() {
    private val TAG = "CreateMissionFragment"
    private val loginViewModel: LoginViewModel by activityViewModels()
    private var listener: OnFragmentInteractionListener? = null
    private val missionViewModel: MissionViewModel by activityViewModels()
    private lateinit var binding: FragmentCreateBinding
    private val args: CreateMissionFragmentArgs by navArgs()





    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        missionViewModel.mission.apply {
            if (args.isCreateNewMission){
                value = Mission()
            }
            else {
                // Update an existing mission
                value = missionViewModel.mission.value
            }
        }

        missionViewModel.mission.observe(viewLifecycleOwner, Observer { mission->
            binding.missionInstance = mission})

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

            hideKeyboard()
            val teamDocID = loginViewModel.teamDocID.value!!
            try {
                input_group.visibility = View.GONE
                createProgressBar.visibility = View.VISIBLE
                GlobalScope.launch {
                    missionViewModel.saveMission(teamDocID)
                    // Navigating with empty arugments means that MissionViewModel is already populated
                    val action = CreateMissionFragmentDirections.actionCreateFragmentToDetailFragment()
                    findNavController().navigate(action)
                }
            }
            catch (e: Exception){
                createProgressBar.visibility = View.GONE
                input_group.visibility = View.VISIBLE
                Snackbar.make(it,getString(R.string.error_uploading),Snackbar.LENGTH_LONG)
                    .also {
                        it.show()
                    }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create,
            container,
            false
        )

        return binding.root
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
        val geopoint : GeoPoint?
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
        return
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
