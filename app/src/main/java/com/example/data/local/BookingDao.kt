package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookingRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM booking_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<BookingRequest>>

    @Query("SELECT * FROM booking_requests WHERE status = :status ORDER BY createdAt DESC")
    fun getRequestsByStatus(status: String): Flow<List<BookingRequest>>

    @Query("SELECT * FROM booking_requests WHERE functionDate = :date AND status != 'REJECTED'")
    suspend fun getActiveRequestsForDate(date: String): List<BookingRequest>

    @Query("SELECT * FROM booking_requests WHERE id = :id")
    suspend fun getRequestById(id: Long): BookingRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BookingRequest): Long

    @Update
    suspend fun updateRequest(request: BookingRequest)

    @Query("UPDATE booking_requests SET status = :status, adminNotes = :notes WHERE id = :id")
    suspend fun updateRequestStatus(id: Long, status: String, notes: String)

    @Query("DELETE FROM booking_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Long)
}
