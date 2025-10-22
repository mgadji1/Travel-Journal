package com.example.traveljournal.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.traveljournal.R
import com.example.traveljournal.db.Trip
import com.example.traveljournal.imageloader.GlideImageLoader
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val TRIP_KEY = "trip"

const val NAME_IMAGE = "\uD83D\uDDFA\uFE0F"
const val DATE_IMAGE = "\uD83D\uDCC5"
const val DESCRIPTION_IMAGE = "\uD83D\uDCDD"

class TripDetailsFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trip_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageLoader = GlideImageLoader(requireContext())

        val imDialogPhoto = view.findViewById<ImageView>(R.id.imDialogPhoto)
        val tvDialogName = view.findViewById<TextView>(R.id.tvDialogName)
        val tvDialogDate = view.findViewById<TextView>(R.id.tvDialogDate)
        val tvDialogDescription = view.findViewById<TextView>(R.id.tvDialogDescription)

        val trip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TRIP_KEY, Trip::class.java)!!
        } else {
            arguments?.getParcelable(TRIP_KEY)!!
        }

        imageLoader.loadImage(trip.imageUri, imDialogPhoto)

        tvDialogName.text = String.format("%s $NAME_IMAGE: %s", getString(R.string.name), trip.name)
        tvDialogDate.text = String.format("%s $DATE_IMAGE: %s", getString(R.string.date), trip.date)
        tvDialogDescription.text = String.format("%s $DESCRIPTION_IMAGE: %s", getString(R.string.description), trip.description)
    }

    companion object {
        @JvmStatic
        fun newInstance(trip : Trip) =
            TripDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TRIP_KEY, trip)
                }
            }
    }
}