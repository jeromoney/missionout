package com.beaterboofs.missionout

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MissionAdapter(var missionDataset: List<Mission>, val clickListener: (Mission) -> Unit) :
    RecyclerView.Adapter<MissionAdapter.MissionViewHolder>() {

    override fun getItemCount()= missionDataset.size

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(missionDataset[position], clickListener)
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.content_mission_overview, parent, false) as CardView
        // set view's size, margins, padding and layout parameters
        return MissionViewHolder(cardView)
    }




    class MissionViewHolder(val cardView: CardView): RecyclerView.ViewHolder(cardView){
        fun bind(missionInstance: Mission, clickListener: (Mission) -> Unit){
            cardView.apply{
                setOnClickListener{clickListener(missionInstance)}
                findViewById<TextView>(R.id.card_title).text = missionInstance.description
                val date: Date = missionInstance.time!!.toDate()
                findViewById<TextView>(R.id.card_subtitle).text = date.toString()
            }
        }
    }
}