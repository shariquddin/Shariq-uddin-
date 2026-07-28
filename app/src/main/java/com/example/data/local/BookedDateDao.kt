package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BookedDate
import kotlinx.coroutines.flow.Flow

@Dao
interface BookedDateDao {
    @Query("SELECT * FROM booked_dates")
    fun getAllBookedDates(): Flow<List<BookedDate>>

    @Query("SELECT * FROM booked_dates WHERE date = :date")
    suspend fun getBookedDate(date: String): BookedDate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDate(bookedDate: BookedDate)

    @Query("DELETE FROM booked_dates WHERE date = :date")
    suspend fun deleteDate(date: String)
}
