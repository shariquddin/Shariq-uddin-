package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import com.example.data.model.BookingRequest
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object IntentUtils {
    const val OWNER_PHONE = "+919440023196"
    const val OWNER_PHONE_RAW = "919440023196"
    const val BUSINESS_NAME = "AR FUNCTION HALL"

    fun makePhoneCall(context: Context, phoneNumber: String = OWNER_PHONE) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to launch phone dialer", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsAppBooking(
        context: Context,
        name: String,
        date: String,
        eventType: String,
        phoneNumber: String = OWNER_PHONE_RAW
    ) {
        val message = """
            Hello,
            I would like to book AR FUNCTION HALL.

            Name: $name
            Date: $date
            Event Type: $eventType
        """.trimIndent()

        try {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val url = "https://wa.me/$phoneNumber?text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    fun openGoogleMaps(context: Context, locationQuery: String = "AR FUNCTION HALL") {
        try {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${URLEncoder.encode(locationQuery, "UTF-8")}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${URLEncoder.encode(locationQuery, "UTF-8")}")
                )
                context.startActivity(browserIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Opening Maps...", Toast.LENGTH_SHORT).show()
        }
    }

    fun addToGoogleCalendar(context: Context, booking: BookingRequest) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateObj = sdf.parse(booking.functionDate) ?: Calendar.getInstance().time

            val startCal = Calendar.getInstance().apply {
                time = dateObj
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
            }

            val endCal = Calendar.getInstance().apply {
                time = dateObj
                set(Calendar.HOUR_OF_DAY, 22)
                set(Calendar.MINUTE, 0)
            }

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "${booking.eventType} - $BUSINESS_NAME")
                putExtra(CalendarContract.Events.EVENT_LOCATION, BUSINESS_NAME)
                putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    "Booking for ${booking.customerName}. Guest count: ${booking.guestCount}. Mobile: ${booking.mobileNumber}"
                )
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCal.timeInMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.timeInMillis)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to sync with calendar app", Toast.LENGTH_SHORT).show()
        }
    }
}
