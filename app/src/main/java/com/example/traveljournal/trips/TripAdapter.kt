package com.example.traveljournal.trips

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R
import com.example.traveljournal.db.Trip
import com.example.traveljournal.imageloader.GlideImageLoader

class TripAdapter(
    private val layoutInflater: LayoutInflater,
    private val context : Context,
    private val listener : OnTripClickListener,
    private val onMoreOptionsClickListener: OnMoreOptionsClickListener
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

        imageLoader.loadImage(trip.imageUri, holder.imPhoto)

        holder.tvName.text = trip.name

        holder.itemView.setOnClickListener {
            listener.onClick(trip)
        }

        holder.btnMore.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.btnMore)

            popupMenu.menu.add(context.getString(R.string.edit))
            popupMenu.menu.add(context.getString(R.string.delete))

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    context.getString(R.string.edit) -> onMoreOptionsClickListener.onEdit()
                    context.getString(R.string.delete) -> onMoreOptionsClickListener.onDelete(trip)
                }
                true
            }

            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = trips.size

    fun deleteTrip(trip : Trip) {
        val position = trips.indexOf(trip)
        trips.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setTrips(newTrips : List<Trip>) {
        trips.clear()
        trips.addAll(newTrips)
        notifyDataSetChanged()
    }

    fun getTrips() : List<Trip> = trips
}