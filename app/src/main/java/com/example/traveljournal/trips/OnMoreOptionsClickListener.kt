package com.example.traveljournal.trips

import com.example.traveljournal.db.Trip

interface OnMoreOptionsClickListener {
    fun onEdit(trip : Trip)
    fun onDelete(trip : Trip)
    fun onShare(trip : Trip)
}