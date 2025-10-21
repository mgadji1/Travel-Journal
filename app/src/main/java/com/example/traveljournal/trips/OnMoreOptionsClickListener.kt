package com.example.traveljournal.trips

import com.example.traveljournal.db.Trip

interface OnMoreOptionsClickListener {
    fun onEdit()
    fun onDelete(trip : Trip)
}