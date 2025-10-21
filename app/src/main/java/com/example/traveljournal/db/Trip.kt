package com.example.traveljournal.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val imageUrl : String,
    val name : String,
    val date : String,
    val description : String,
    val latitude : Double,
    val longitude : Double
) : Parcelable, Comparable<Trip> {
    override fun compareTo(other: Trip): Int {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val thisDate = format.parse(this.date) ?: Date(0)
        val otherDate = format.parse(other.date) ?: Date(0)

        return thisDate.compareTo(otherDate)
    }
}