package com.example.traveljournal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    suspend fun getAllTrips() : List<Trip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip : Trip)

    @Delete
    suspend fun deleteTrip(trip : Trip)

    @Query("SELECT * FROM trips WHERE name LIKE :query OR description LIKE :query")
    suspend fun searchByQuery(query: String) : List<Trip>
}