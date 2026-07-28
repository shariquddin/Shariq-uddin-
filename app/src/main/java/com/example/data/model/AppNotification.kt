package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetRole: String = ROLE_CUSTOMER // CUSTOMER, ADMIN
) {
    companion object {
        const val ROLE_CUSTOMER = "CUSTOMER"
        const val ROLE_ADMIN = "ADMIN"
    }
}
