package com.beaterboofs.missionout

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beaterboofs.missionout.Util.UIUtil.getVisibility
import kotlinx.android.synthetic.main.overview_fragment.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OverviewFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var dataset : List<Mission>
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val overviewViewModel: OverviewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Set up listener so create mission button only shows to editors
        loginViewModel.editor.observe(viewLifecycleOwner, Observer { isEditor ->
            // Floating action button with create should only be shown to editors
            fab.visibility = getVisibility(isEditor)
            fab?.setOnClickListener {
                findNavController().navigate(R.id.createFragment)
            }
        })



        loginViewModel.teamDocID.observe(viewLifecycleOwner, Observer { loginTeamDocID ->
            if (loginTeamDocID == null){
                return@Observer
            }
            // Only run OverviewViewModel when the TeamDocId has been established
            // Set up main missions view model
            overviewViewModel.apply {
                teamDocID = loginTeamDocID
                missions.observe(viewLifecycleOwner, Observer { missions ->
                    if (missions == null){
                        return@Observer
                    }
                    // update recycler view
                    viewAdapter = MissionAdapter(missions, { missionInstance : Mission -> missionItemClicked(missionInstance.key!!) })
                    recyclerView = view!!.findViewById<RecyclerView>(R.id.overview_recycler_view).apply {
                        setHasFixedSize(true)
                        layoutManager = viewManager
                        adapter = viewAdapter
                    }
                })
            }
            overviewViewModel.updateModel()
        })

        dataset = listOf()
        viewManager = LinearLayoutManager(context)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.overview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        overviewViewModel.updateModel()

        // If user is not logged in, navigate to login screen
        val navController = findNavController()
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            if (authenticationState == LoginViewModel.AuthenticationState.UNAUTHENTICATED) {
                navController.navigate(R.id.signInFragment)
            }
        } )
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private val viewModel: LoginViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
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
         * @return A new instance of fragment OverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OverviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun missionItemClicked(missionDocID: String){
        // Launch Mission Activity Detail with clicked item
        // Navigate to detail fragment
        val action = OverviewFragmentDirections.actionOverviewFragmentToDetailMissionFragment(missionDocID)
        findNavController().navigate(action)
    }
}
