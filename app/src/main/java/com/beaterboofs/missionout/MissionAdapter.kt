package com.beaterboofs.missionout

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MissionAdapter(var missionDataset: List<Mission>, val clickListener: (Mission) -> Unit) :
    RecyclerView.Adapter<MissionAdapter.MissionViewHolder>() {

    override fun getItemCount()= missionDataset.size

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.textView.text = missionDataset[position].description
        (holder as MissionViewHolder).bind(missionDataset[position], clickListener)
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.mission_text_view, parent, false) as TextView
        // set view's size, margins, padding and layout parameters
        return MissionViewHolder(textView)
    }


    class MissionViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView){
        fun bind(missionInstance: Mission, clickListener: (Mission) -> Unit){
            textView.setOnClickListener{clickListener(missionInstance)}
        }
    }
}