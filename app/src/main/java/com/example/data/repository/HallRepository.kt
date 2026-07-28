package com.example.data.repository

import com.example.R
import com.example.data.local.BookedDateDao
import com.example.data.local.BookingDao
import com.example.data.local.NotificationDao
import com.example.data.local.ReviewDao
import com.example.data.model.AppNotification
import com.example.data.model.BookedDate
import com.example.data.model.BookingRequest
import com.example.data.model.Facility
import com.example.data.model.GalleryItem
import com.example.data.model.PricingPackage
import com.example.data.model.Review
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HallRepository(
    private val bookingDao: BookingDao,
    private val bookedDateDao: BookedDateDao,
    private val reviewDao: ReviewDao,
    private val notificationDao: NotificationDao
) {
    val allBookedDates: Flow<List<BookedDate>> = bookedDateDao.getAllBookedDates()
    val allBookingRequests: Flow<List<BookingRequest>> = bookingDao.getAllRequests()
    val allReviews: Flow<List<Review>> = reviewDao.getAllReviews()
    val customerNotifications: Flow<List<AppNotification>> =
        notificationDao.getNotificationsForRole(AppNotification.ROLE_CUSTOMER)
    val adminNotifications: Flow<List<AppNotification>> =
        notificationDao.getNotificationsForRole(AppNotification.ROLE_ADMIN)

    suspend fun getBookedDate(date: String): BookedDate? {
        return bookedDateDao.getBookedDate(date)
    }

    suspend fun submitBookingRequest(
        customerName: String,
        mobileNumber: String,
        email: String,
        eventType: String,
        guestCount: Int,
        functionDate: String,
        startTime: String,
        endTime: String,
        specialRequirements: String
    ): Pair<Boolean, String> {
        // Double booking check
        val existingBookedDate = bookedDateDao.getBookedDate(functionDate)
        if (existingBookedDate != null && existingBookedDate.status == BookedDate.STATUS_BOOKED) {
            return Pair(false, "Selected date $functionDate is already booked. Please choose another date.")
        }

        val request = BookingRequest(
            customerName = customerName,
            mobileNumber = mobileNumber,
            email = email,
            eventType = eventType,
            guestCount = guestCount,
            functionDate = functionDate,
            startTime = startTime,
            endTime = endTime,
            specialRequirements = specialRequirements,
            status = BookingRequest.STATUS_PENDING
        )

        val requestId = bookingDao.insertRequest(request)

        // Mark date as pending in booked_dates calendar
        bookedDateDao.insertOrUpdateDate(
            BookedDate(
                date = functionDate,
                status = BookedDate.STATUS_PENDING,
                eventTitle = "$eventType - $customerName",
                customerName = customerName
            )
        )

        // Notify Admin
        notificationDao.insertNotification(
            AppNotification(
                title = "New Booking Request",
                message = "New $eventType request from $customerName for $functionDate ($guestCount guests).",
                targetRole = AppNotification.ROLE_ADMIN
            )
        )

        // Customer confirmation notification
        notificationDao.insertNotification(
            AppNotification(
                title = "Booking Request Sent",
                message = "Your request for $eventType on $functionDate has been submitted successfully.",
                targetRole = AppNotification.ROLE_CUSTOMER
            )
        )

        return Pair(true, "Your booking request has been sent successfully.")
    }

    suspend fun approveBookingRequest(id: Long) {
        val request = bookingDao.getRequestById(id) ?: return
        bookingDao.updateRequestStatus(id, BookingRequest.STATUS_APPROVED, "Approved by admin")

        // Mark date as BOOKED in calendar
        bookedDateDao.insertOrUpdateDate(
            BookedDate(
                date = request.functionDate,
                status = BookedDate.STATUS_BOOKED,
                eventTitle = "${request.eventType} - ${request.customerName}",
                customerName = request.customerName,
                notes = "Confirmed for ${request.guestCount} guests"
            )
        )

        // Send customer notification
        notificationDao.insertNotification(
            AppNotification(
                title = "Booking Approved! 🎉",
                message = "Your booking request for ${request.eventType} on ${request.functionDate} has been approved by AR FUNCTION HALL.",
                targetRole = AppNotification.ROLE_CUSTOMER
            )
        )
    }

    suspend fun rejectBookingRequest(id: Long, notes: String = "") {
        val request = bookingDao.getRequestById(id) ?: return
        bookingDao.updateRequestStatus(id, BookingRequest.STATUS_REJECTED, notes)

        // Reset date status in calendar
        bookedDateDao.deleteDate(request.functionDate)

        // Send customer notification
        notificationDao.insertNotification(
            AppNotification(
                title = "Booking Request Update",
                message = "Your booking request for ${request.functionDate} could not be confirmed. Notes: $notes",
                targetRole = AppNotification.ROLE_CUSTOMER
            )
        )
    }

    suspend fun setDateStatus(date: String, status: String, title: String = "", name: String = "", notes: String = "") {
        if (status == BookedDate.STATUS_AVAILABLE) {
            bookedDateDao.deleteDate(date)
        } else {
            bookedDateDao.insertOrUpdateDate(
                BookedDate(
                    date = date,
                    status = status,
                    eventTitle = title,
                    customerName = name,
                    notes = notes
                )
            )
        }
    }

    suspend fun addReview(customerName: String, rating: Float, comment: String, eventType: String) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        reviewDao.insertReview(
            Review(
                customerName = customerName,
                rating = rating,
                comment = comment,
                date = dateFormat.format(Date()),
                eventType = eventType
            )
        )
    }

    suspend fun markNotificationsRead(role: String) {
        notificationDao.markAllAsReadForRole(role)
    }

    // Static display data
    fun getPricingPackages(): List<PricingPackage> {
        return listOf(
            PricingPackage(
                id = "wedding",
                title = "Grand Wedding Package",
                subtitle = "Complete royal experience for your special day",
                priceRange = "₹1,50,000 - ₹2,50,000",
                guestCapacity = "800 - 1500 Guests",
                inclusions = listOf(
                    "Full Central AC Function Hall",
                    "Grand Stage Flower Decoration",
                    "VIP Bride & Groom AC Green Rooms",
                    "1000+ Dining Seating Capacity",
                    "Full Power Backup (High KVA Generator)",
                    "Ample Parking for 200+ Vehicles & Autos",
                    "24/7 CCTV & Security Personnel",
                    "Pure RO Drinking Water Plant"
                ),
                isPopular = true,
                badgeText = "Most Requested"
            ),
            PricingPackage(
                id = "reception",
                title = "Reception & Sangeet",
                subtitle = "Vibrant lighting & spacious stage arrangement",
                priceRange = "₹90,000 - ₹1,40,000",
                guestCapacity = "500 - 1000 Guests",
                inclusions = listOf(
                    "AC Hall with Dynamic Stage Lighting",
                    "Spacious Buffet Dining Layout",
                    "DJ & Audio System Setup Provision",
                    "Generator Power Backup",
                    "Dedicated Valet Parking Area",
                    "Clean Restrooms & Housekeeping Staff"
                )
            ),
            PricingPackage(
                id = "engagement",
                title = "Engagement & Naming Ceremony",
                subtitle = "Elegant intimate gathering with cozy charm",
                priceRange = "₹50,000 - ₹85,000",
                guestCapacity = "200 - 500 Guests",
                inclusions = listOf(
                    "Air-Conditioned Main Hall",
                    "Stage Seating & Backdrop Setup",
                    "Dining Hall with Catering Counters",
                    "Basic Floral Decoration Included",
                    "Power Generator Backup"
                )
            ),
            PricingPackage(
                id = "custom",
                title = "Corporate & Custom Events",
                subtitle = "Tailored solutions for conferences & parties",
                priceRange = "Custom Quote",
                guestCapacity = "Flexible (100 - 1200 Guests)",
                inclusions = listOf(
                    "Customized Hall Duration Options",
                    "Flexible Dining & Exhibition Space",
                    "AV Projection & Mic Audio Support",
                    "Dedicated Event Co-ordinator",
                    "24/7 Security & Parking"
                ),
                badgeText = "Tailored"
            )
        )
    }

    fun getGalleryItems(): List<GalleryItem> {
        return listOf(
            GalleryItem(
                id = "g1",
                title = "AR Function Hall Exterior & Night View",
                category = "Hall",
                drawableResId = R.drawable.img_hall_banner,
                description = "Grand illuminated facade with spacious grounds, fireworks display, and palm trees."
            ),
            GalleryItem(
                id = "g2",
                title = "Royal Floral Stage Decoration",
                category = "Stage",
                drawableResId = R.drawable.img_stage_decoration,
                description = "Opulent wedding stage featuring floral backdrop arches, chandeliers, and plush sofas."
            ),
            GalleryItem(
                id = "g3",
                title = "Grand Dining Banquet Hall",
                category = "Dining Area",
                drawableResId = R.drawable.img_dining_area,
                description = "Spacious dining hall with polished seating, chandeliers, and clean buffet counters."
            ),
            GalleryItem(
                id = "g4",
                title = "Ample Parking & Open Grounds",
                category = "Parking",
                drawableResId = R.drawable.img_hall_banner,
                description = "Expansive paved parking grounds supporting cars, buses, and auto-rickshaws easily."
            ),
            GalleryItem(
                id = "g5",
                title = "Entrance Flower Arch & Lighting",
                category = "Decoration",
                drawableResId = R.drawable.img_stage_decoration,
                description = "Warm welcoming entrance arch with fresh floral arrangements and festive lights."
            )
        )
    }

    fun getFacilities(): List<Facility> {
        return listOf(
            Facility("f1", "Central AC Hall", "Fully air-conditioned main auditorium for guest comfort", "ac"),
            Facility("f2", "Ample Parking Grounds", "Dedicated parking space for 200+ cars, buses & autos", "parking"),
            Facility("f3", "Generator Power Backup", "High KVA silent power generator ensuring uninterrupted events", "power"),
            Facility("f4", "Spacious Dining Hall", "Separate dining hall accommodating 600+ guests per batch", "dining"),
            Facility("f5", "Stage & Flower Decoration", "In-house creative team for theme backdrops & flower arches", "stage"),
            Facility("f6", "Dedicated Catering Space", "Hygiene-compliant kitchen & prep area for caterers", "catering"),
            Facility("f7", "24/7 CCTV Security", "Surveillance coverage across hall, stage, and parking areas", "security"),
            Facility("f8", "RO Water & Green Rooms", "Filtered drinking water and private AC Bride/Groom rooms", "water")
        )
    }

    suspend fun seedInitialDataIfEmpty() {
        // Seed initial Booked Dates if empty
        val currentDates = bookedDateDao.getAllBookedDates().first()
        if (currentDates.isEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()

            // Seed a few dates for the current and next month
            cal.add(Calendar.DAY_OF_MONTH, 3)
            val d1 = sdf.format(cal.time)
            bookedDateDao.insertOrUpdateDate(
                BookedDate(d1, BookedDate.STATUS_BOOKED, "Grand Wedding - Reddy Family", "Srinivas Reddy", "Booked")
            )

            cal.add(Calendar.DAY_OF_MONTH, 5)
            val d2 = sdf.format(cal.time)
            bookedDateDao.insertOrUpdateDate(
                BookedDate(d2, BookedDate.STATUS_PENDING, "Reception Request - Ananya", "Ananya Sharma", "Pending Approval")
            )

            cal.add(Calendar.DAY_OF_MONTH, 4)
            val d3 = sdf.format(cal.time)
            bookedDateDao.insertOrUpdateDate(
                BookedDate(d3, BookedDate.STATUS_BOOKED, "Engagement Ceremony", "Kiran Kumar", "Booked")
            )

            cal.add(Calendar.DAY_OF_MONTH, 8)
            val d4 = sdf.format(cal.time)
            bookedDateDao.insertOrUpdateDate(
                BookedDate(d4, BookedDate.STATUS_BOOKED, "Corporate Annual Meet", "TechCorp Ltd", "Booked")
            )
        }

        // Seed initial Reviews if empty
        val currentReviews = reviewDao.getAllReviews().first()
        if (currentReviews.isEmpty()) {
            reviewDao.insertReview(
                Review(
                    customerName = "Rajeshwar Rao",
                    rating = 5.0f,
                    comment = "We conducted my daughter's wedding at AR Function Hall. The stage decoration, spacious parking, and AC cooling were top class! Highly recommended.",
                    date = "12 Jun 2026",
                    eventType = "Wedding"
                )
            )
            reviewDao.insertReview(
                Review(
                    customerName = "Priya & Vikram",
                    rating = 5.0f,
                    comment = "Excellent dining hall capacity and polite management. Parking was seamless for all our 900+ guests. Thank you AR Function Hall team!",
                    date = "28 May 2026",
                    eventType = "Reception"
                )
            )
            reviewDao.insertReview(
                Review(
                    customerName = "Mohammed Imran",
                    rating = 4.5f,
                    comment = "Very clean venue, good generator power backup during heavy rain. Great location and easy directions on Google Maps.",
                    date = "04 Apr 2026",
                    eventType = "Engagement"
                )
            )
        }
    }
}
