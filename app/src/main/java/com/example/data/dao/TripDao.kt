package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY createdAt DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips")
    suspend fun getAllTripsDirect(): List<Trip>

    @Query("SELECT * FROM trips WHERE id = :id")
    fun getTripById(id: Long): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    suspend fun findTripById(id: Long): Trip?

    @Query("SELECT * FROM trips WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE destinationWilaya = :wilaya ORDER BY createdAt DESC")
    fun getTripsByWilaya(wilaya: String): Flow<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trips: List<Trip>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<Trip>)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("UPDATE trips SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE trips SET availableSeats = :seats, status = CASE WHEN :seats <= 0 THEN 'COMPLETED' ELSE 'AVAILABLE' END WHERE id = :id")
    suspend fun updateAvailableSeats(id: Long, seats: Int)

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteTripById(id: Long)

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()

    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getCount(): Int
}
