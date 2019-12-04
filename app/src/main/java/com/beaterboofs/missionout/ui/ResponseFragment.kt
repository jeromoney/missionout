package com.beaterboofs.missionout.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.beaterboofs.missionout.R
import com.beaterboofs.missionout.data.Response
import com.beaterboofs.missionout.data.ResponseViewModel
import com.beaterboofs.missionout.util.UIUtil.getVisibility
import kotlinx.android.synthetic.main.fragment_response.*

/**
 * A [Fragment] subclass that displays the RSVPs from responding team members.
 */
class ResponseFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private val responseViewModel: ResponseViewModel by activityViewModels()
    private lateinit var dataset : List<Response>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        responseViewModel.missionPath = arguments!!.getString("responseCollection")!!
        responseViewModel.updateResponses() // I don't like how the UI is seeing into the workings of the viewmodel

        responseViewModel.responses.observe(viewLifecycleOwner, Observer { responses ->
            // if null, just return
            responses?: return@Observer
            // if there are no responses, display no response text
            no_response_textview.visibility = getVisibility(responses.size == 0)
            // update RecyclerView
            viewAdapter = ResponseAdapter(responses)
            recyclerView = view!!.findViewById<RecyclerView>(R.id.reponse_recyclerview).apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(context)
                adapter = viewAdapter
            }

        })
        dataset = listOf()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_response, container, false)
    }


}
