package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_requests")
data class BookingRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val mobileNumber: String,
    val email: String = "",
    val eventType: String,
    val guestCount: Int,
    val functionDate: String, // YYYY-MM-DD
    val startTime: String,
    val endTime: String,
    val specialRequirements: String = "",
    val status: String = STATUS_PENDING, // PENDING, APPROVED, REJECTED
    val createdAt: Long = System.currentTimeMillis(),
    val adminNotes: String = ""
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_REJECTED = "REJECTED"
    }
}
