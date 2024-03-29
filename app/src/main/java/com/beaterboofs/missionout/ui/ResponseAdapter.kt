package com.beaterboofs.missionout.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.beaterboofs.missionout.R
import com.beaterboofs.missionout.data.Response
import kotlinx.android.synthetic.main.content_response.view.*

class ResponseAdapter(var responseDataset: List<Response>) : RecyclerView.Adapter<ResponseAdapter.ResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        val constraintLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.content_response, parent, false) as ConstraintLayout
        // set view's size, margins, padding and layout parameters
        return ResponseViewHolder(
            constraintLayout
        )
    }

    override fun getItemCount(): Int = responseDataset.size

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        holder.bind(responseDataset[position])
    }

    class ResponseViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout) {
        fun bind(response: Response) {
            constraintLayout.apply {
                response_name_textview.setText(response.name)
                response_response_textview.setText(response.response)
                response_driving_time.setText(response.driving_time)
            }
        }
    }

}