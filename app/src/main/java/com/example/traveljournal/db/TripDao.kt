package com.example.traveljournal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    suspend fun getAllTrips() : List<Trip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip : Trip)

    @Update
    suspend fun updateTrip(trip : Trip)

    @Delete
    suspend fun deleteTrip(trip : Trip)

    @Query("SELECT * FROM trips WHERE title LIKE :query OR description LIKE :query")
    suspend fun searchByQuery(query: String) : List<Trip>
}