package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booked_dates")
data class BookedDate(
    @PrimaryKey
    val date: String, // YYYY-MM-DD
    val status: String, // BOOKED, AVAILABLE, PENDING
    val eventTitle: String = "",
    val customerName: String = "",
    val notes: String = ""
) {
    companion object {
        const val STATUS_BOOKED = "BOOKED"
        const val STATUS_AVAILABLE = "AVAILABLE"
        const val STATUS_PENDING = "PENDING"
    }
}
