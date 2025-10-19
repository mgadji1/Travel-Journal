package com.example.traveljournal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun getAllTrips() : List<Trip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrip(trip : Trip)

    @Delete
    fun deleteTrip(trip : Trip)
}