package com.beaterboofs.missionout.ui.mission

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.beaterboofs.missionout.BR
import com.beaterboofs.missionout.R
import com.beaterboofs.missionout.databinding.MissionFragmentBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class MissionFragment : Fragment() {

    companion object {
        fun newInstance() = MissionFragment()
        val TAG = "MissionFragment"
    }

    private lateinit var viewModel: MissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.mission_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = token.toString()
                Log.d(TAG, msg)
                Toast.makeText(this.requireContext(), msg, Toast.LENGTH_SHORT).show()
            })

        viewModel = ViewModelProviders.of(this).get(MissionViewModel::class.java)


        val binding: MissionFragmentBinding =
            DataBindingUtil.setContentView<MissionFragmentBinding>(this.requireActivity(), R.layout.mission_fragment)
        binding.lifecycleOwner = this

        viewModel.getMission().observe(this, Observer {
            // update UI
            binding.setVariable(BR.missionInstance, viewModel.getMission().value)
        })
    }

}
