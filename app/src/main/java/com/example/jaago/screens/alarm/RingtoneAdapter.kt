package com.example.jaago.screens.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jaago.R
import com.example.jaago.model.RingtoneModel

class RingtoneAdapter(
    private val ringtoneList: List<RingtoneModel>,
    private val onItemClickListener: (RingtoneModel) -> Unit
) : RecyclerView.Adapter<RingtoneAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ringtoneName: TextView = itemView.findViewById(R.id.ringtoneName)
        var currentRingtone: RingtoneModel? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_ringtone, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ringtone = ringtoneList[position]

        holder.currentRingtone = ringtone
        holder.ringtoneName.text = ringtone.name

        holder.itemView.setOnClickListener {
            onItemClickListener.invoke(ringtone)
        }
    }

    override fun getItemCount(): Int {
        return ringtoneList.size
    }
}