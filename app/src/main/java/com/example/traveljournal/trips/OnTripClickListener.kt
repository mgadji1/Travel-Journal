package com.example.traveljournal.trips

import com.example.traveljournal.db.Trip

interface OnTripClickListener {
    fun onClick(trip : Trip)
}