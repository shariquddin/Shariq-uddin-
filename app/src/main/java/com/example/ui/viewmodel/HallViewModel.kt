package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.BookedDate
import com.example.data.model.BookingRequest
import com.example.data.model.Review
import com.example.data.repository.HallRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HallViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = HallRepository(
        bookingDao = db.bookingDao(),
        bookedDateDao = db.bookedDateDao(),
        reviewDao = db.reviewDao(),
        notificationDao = db.notificationDao()
    )

    val bookedDates: StateFlow<List<BookedDate>> = repository.allBookedDates.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bookingRequests: StateFlow<List<BookingRequest>> = repository.allBookingRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val reviews: StateFlow<List<Review>> = repository.allReviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val customerNotifications: StateFlow<List<AppNotification>> = repository.customerNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val adminNotifications: StateFlow<List<AppNotification>> = repository.adminNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _selectedCalendarMonth = MutableStateFlow(Calendar.getInstance())
    val selectedCalendarMonth: StateFlow<Calendar> = _selectedCalendarMonth.asStateFlow()

    private val _lastSubmittedRequest = MutableStateFlow<BookingRequest?>(null)
    val lastSubmittedRequest: StateFlow<BookingRequest?> = _lastSubmittedRequest.asStateFlow()

    private val _showRequestSuccessDialog = MutableStateFlow(false)
    val showRequestSuccessDialog: StateFlow<Boolean> = _showRequestSuccessDialog.asStateFlow()

    private val _submissionMessage = MutableStateFlow<String?>(null)
    val submissionMessage: StateFlow<String?> = _submissionMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun submitBookingRequest(
        customerName: String,
        mobileNumber: String,
        email: String,
        eventType: String,
        guestCount: Int,
        functionDate: String,
        startTime: String,
        endTime: String,
        specialRequirements: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (success, message) = repository.submitBookingRequest(
                customerName = customerName,
                mobileNumber = mobileNumber,
                email = email,
                eventType = eventType,
                guestCount = guestCount,
                functionDate = functionDate,
                startTime = startTime,
                endTime = endTime,
                specialRequirements = specialRequirements
            )

            if (success) {
                val newRequest = BookingRequest(
                    customerName = customerName,
                    mobileNumber = mobileNumber,
                    email = email,
                    eventType = eventType,
                    guestCount = guestCount,
                    functionDate = functionDate,
                    startTime = startTime,
                    endTime = endTime,
                    specialRequirements = specialRequirements
                )
                _lastSubmittedRequest.value = newRequest
                _showRequestSuccessDialog.value = true
            }
            _submissionMessage.value = message
            onResult(success, message)
        }
    }

    fun dismissSuccessDialog() {
        _showRequestSuccessDialog.value = false
    }

    fun loginAdmin(pinOrPassword: String): Boolean {
        // Simple secure admin login check (e.g. "admin123" or "7777")
        return if (pinOrPassword == "admin123" || pinOrPassword == "7777" || pinOrPassword == "admin") {
            _isAdminLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun approveRequest(id: Long) {
        viewModelScope.launch {
            repository.approveBookingRequest(id)
        }
    }

    fun rejectRequest(id: Long, notes: String = "") {
        viewModelScope.launch {
            repository.rejectBookingRequest(id, notes)
        }
    }

    fun setDateStatus(date: String, status: String, title: String = "", name: String = "", notes: String = "") {
        viewModelScope.launch {
            repository.setDateStatus(date, status, title, name, notes)
        }
    }

    fun submitReview(name: String, rating: Float, comment: String, eventType: String) {
        viewModelScope.launch {
            repository.addReview(name, rating, comment, eventType)
        }
    }

    fun changeMonth(monthOffset: Int) {
        val newCal = Calendar.getInstance().apply {
            time = _selectedCalendarMonth.value.time
            add(Calendar.MONTH, monthOffset)
        }
        _selectedCalendarMonth.value = newCal
    }

    fun markCustomerNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead(AppNotification.ROLE_CUSTOMER)
        }
    }

    fun markAdminNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead(AppNotification.ROLE_ADMIN)
        }
    }

    fun getPricingPackages() = repository.getPricingPackages()
    fun getGalleryItems() = repository.getGalleryItems()
    fun getFacilities() = repository.getFacilities()
}
