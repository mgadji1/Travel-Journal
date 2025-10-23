package com.example.traveljournal.trips

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R

class TripViewHolder(containerView : View) : RecyclerView.ViewHolder(containerView) {
    val imPhoto = containerView.findViewById<ImageView>(R.id.imPhoto)
    val tvName = containerView.findViewById<TextView>(R.id.tvTitle)
    val btnMore = containerView.findViewById<ImageButton>(R.id.btnMore)
}