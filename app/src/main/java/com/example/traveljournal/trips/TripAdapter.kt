package com.example.traveljournal.trips

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R
import com.example.traveljournal.db.Trip
import com.example.traveljournal.imageloader.GlideImageLoader

class TripAdapter(
    private val layoutInflater: LayoutInflater,
    context : Context,
    private val listener : OnTripClickListener
) : RecyclerView.Adapter<TripViewHolder>() {
    private val trips = mutableListOf<Trip>()

    private val imageLoader = GlideImageLoader(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripViewHolder {
        val view = layoutInflater.inflate(R.layout.trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TripViewHolder,
        position: Int
    ) {
        val trip = trips[position]

        imageLoader.loadImage(trip.imageUrl, holder.imPhoto)

        holder.tvName.text = trip.name

        holder.itemView.setOnClickListener {
            listener.onClick(trip)
        }
    }

    override fun getItemCount(): Int = trips.size

    fun addTrip(trip : Trip) {
        trips.add(trip)
        notifyItemInserted(trips.lastIndex)
    }

    fun setTrips(newTrips : List<Trip>) {
        trips.clear()
        trips.addAll(newTrips)
        notifyDataSetChanged()
    }

    fun getTrips() : List<Trip> = trips
}