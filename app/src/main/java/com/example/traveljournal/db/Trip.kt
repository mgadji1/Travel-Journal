package com.example.traveljournal.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val imageUrl : String,
    val name : String,
    val date : String,
    val description : String
)