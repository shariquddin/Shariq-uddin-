package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.GoldPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BookingFormScreen(
    initialDate: String = "",
    onSubmitBooking: (
        customerName: String,
        mobileNumber: String,
        email: String,
        eventType: String,
        guestCount: Int,
        functionDate: String,
        startTime: String,
        endTime: String,
        specialRequirements: String
    ) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val defaultDateString = if (initialDate.isNotEmpty()) initialDate else dateFormat.format(calendar.time)

    var customerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedEventType by remember { mutableStateOf("Wedding") }
    var eventTypeExpanded by remember { mutableStateOf(false) }

    val eventTypes = listOf(
        "Wedding",
        "Reception",
        "Engagement",
        "Birthday",
        "Naming Ceremony",
        "Corporate Event",
        "Other"
    )

    var guestCountText by remember { mutableStateOf("500") }
    var functionDate by remember { mutableStateOf(defaultDateString) }
    var startTime by remember { mutableStateOf("09:00 AM") }
    var endTime by remember { mutableStateOf("11:00 PM") }
    var specialRequirements by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Date Picker Dialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            functionDate = dateFormat.format(cal.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Request Booking",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EmeraldDark
        )
        Text(
            text = "Fill in your details to check date availability & lock your slot.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp
                )
            }
        }

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Customer Name
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Mobile Number
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text("Mobile Number *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_mobile_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Email (Optional)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = GoldPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_email_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Event Type Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedEventType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Event Type *") },
                        leadingIcon = { Icon(Icons.Default.EventAvailable, contentDescription = null, tint = GoldPrimary) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { eventTypeExpanded = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { eventTypeExpanded = true }
                            .testTag("booking_event_type_dropdown"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    DropdownMenu(
                        expanded = eventTypeExpanded,
                        onDismissRequest = { eventTypeExpanded = false }
                    ) {
                        eventTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedEventType = type
                                    eventTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Number of Guests
                OutlinedTextField(
                    value = guestCountText,
                    onValueChange = { guestCountText = it },
                    label = { Text("Number of Guests *") },
                    leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, tint = GoldPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_guest_count_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Function Date Picker Button
                OutlinedTextField(
                    value = functionDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Function Date (YYYY-MM-DD) *") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.clickable { datePickerDialog.show() }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                        .testTag("booking_function_date_picker"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Timing Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = GoldPrimary) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("booking_start_time_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = GoldPrimary) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("booking_end_time_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Special Requirements
                OutlinedTextField(
                    value = specialRequirements,
                    onValueChange = { specialRequirements = it },
                    label = { Text("Special Requirements / Food / Decoration") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = GoldPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("booking_special_req_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (customerName.trim().isEmpty()) {
                            errorMessage = "Please enter customer name."
                            return@Button
                        }
                        if (mobileNumber.trim().length < 8) {
                            errorMessage = "Please enter a valid mobile number."
                            return@Button
                        }
                        val count = guestCountText.toIntOrNull() ?: 0
                        if (count <= 0) {
                            errorMessage = "Please enter guest count."
                            return@Button
                        }
                        if (functionDate.trim().isEmpty()) {
                            errorMessage = "Please select function date."
                            return@Button
                        }

                        errorMessage = null
                        onSubmitBooking(
                            customerName.trim(),
                            mobileNumber.trim(),
                            email.trim(),
                            selectedEventType,
                            count,
                            functionDate.trim(),
                            startTime.trim(),
                            endTime.trim(),
                            specialRequirements.trim()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("booking_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Submit Booking Request",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
