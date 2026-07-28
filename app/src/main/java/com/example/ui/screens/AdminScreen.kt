package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookedDate
import com.example.data.model.BookingRequest
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusAvailableBg
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusBookedBg
import com.example.ui.theme.StatusBookedRed
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusPendingOrange
import com.example.util.IntentUtils

@Composable
fun AdminScreen(
    isLoggedIn: Boolean,
    bookingRequests: List<BookingRequest>,
    bookedDates: List<BookedDate>,
    onLogin: (String) -> Boolean,
    onLogout: () -> Unit,
    onApproveRequest: (Long) -> Unit,
    onRejectRequest: (Long, String) -> Unit,
    onSetDateStatus: (date: String, status: String, title: String, name: String, notes: String) -> Unit
) {
    if (!isLoggedIn) {
        AdminLoginCard(onLogin = onLogin)
    } else {
        AdminDashboard(
            bookingRequests = bookingRequests,
            bookedDates = bookedDates,
            onLogout = onLogout,
            onApproveRequest = onApproveRequest,
            onRejectRequest = onRejectRequest,
            onSetDateStatus = onSetDateStatus
        )
    }
}

@Composable
fun AdminLoginCard(
    onLogin: (String) -> Boolean
) {
    var passwordText by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(EmeraldDark, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "AR FUNCTION HALL",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EmeraldDark
        )
        Text(
            text = "Admin Management Portal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Enter Password / PIN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )

                Text(
                    text = "Default PIN: admin123 or 7777",
                    fontSize = 12.sp,
                    color = GoldPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordText,
                    onValueChange = {
                        passwordText = it
                        loginError = false
                    },
                    label = { Text("Password or PIN") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input"),
                    singleLine = true
                )

                if (loginError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Invalid credentials. Use admin123 or 7777.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val success = onLogin(passwordText.trim())
                        if (!success) {
                            loginError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_login_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Login to Admin Dashboard", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AdminDashboard(
    bookingRequests: List<BookingRequest>,
    bookedDates: List<BookedDate>,
    onLogout: () -> Unit,
    onApproveRequest: (Long) -> Unit,
    onRejectRequest: (Long, String) -> Unit,
    onSetDateStatus: (date: String, status: String, title: String, name: String, notes: String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Overview & Requests, 1: Calendar Control

    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("ALL") }

    var showAddDateDialog by remember { mutableStateOf(false) }

    val filteredRequests = remember(bookingRequests, searchQuery, statusFilter) {
        bookingRequests.filter { req ->
            val matchesStatus = (statusFilter == "ALL" || req.status == statusFilter)
            val matchesQuery = (searchQuery.isEmpty() ||
                    req.customerName.contains(searchQuery, ignoreCase = true) ||
                    req.mobileNumber.contains(searchQuery, ignoreCase = true) ||
                    req.eventType.contains(searchQuery, ignoreCase = true))
            matchesStatus && matchesQuery
        }
    }

    val pendingCount = bookingRequests.count { it.status == BookingRequest.STATUS_PENDING }
    val approvedCount = bookingRequests.count { it.status == BookingRequest.STATUS_APPROVED }
    val bookedDatesCount = bookedDates.count { it.status == BookedDate.STATUS_BOOKED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Top Bar Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Admin Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "AR FUNCTION HALL Controls",
                            fontSize = 12.sp,
                            color = GoldPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Text("Logout", color = GoldPrimary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBox(title = "Total Req", count = bookingRequests.size.toString())
                    MetricBox(title = "Pending", count = pendingCount.toString(), highlightColor = StatusPendingOrange)
                    MetricBox(title = "Approved", count = approvedCount.toString(), highlightColor = StatusAvailableGreen)
                    MetricBox(title = "Reserved", count = bookedDatesCount.toString(), highlightColor = GoldPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldDark
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Booking Requests ($pendingCount Pending)", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                modifier = Modifier.testTag("admin_tab_requests")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Calendar Dates", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                modifier = Modifier.testTag("admin_tab_calendar")
            )
        }

        if (selectedTab == 0) {
            // Requests List Tab
            Column(modifier = Modifier.padding(16.dp)) {
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, phone, or event...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "PENDING", "APPROVED", "REJECTED").forEach { status ->
                        val isSelected = statusFilter == status
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) EmeraldDark else MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { statusFilter = status }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("admin_filter_$status")
                        ) {
                            Text(
                                text = status,
                                color = if (isSelected) GoldPrimary else EmeraldDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredRequests) { request ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_request_card_${request.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${request.eventType} - ${request.customerName}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when (request.status) {
                                                    BookingRequest.STATUS_APPROVED -> StatusAvailableBg
                                                    BookingRequest.STATUS_REJECTED -> StatusBookedBg
                                                    else -> StatusPendingBg
                                                },
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = request.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (request.status) {
                                                BookingRequest.STATUS_APPROVED -> StatusAvailableGreen
                                                BookingRequest.STATUS_REJECTED -> StatusBookedRed
                                                else -> StatusPendingOrange
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Date: ${request.functionDate} (${request.startTime} - ${request.endTime})",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = EmeraldDark
                                )

                                Text(
                                    text = "Guests: ${request.guestCount} | Mobile: ${request.mobileNumber}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (request.specialRequirements.isNotEmpty()) {
                                    Text(
                                        text = "Notes: ${request.specialRequirements}",
                                        fontSize = 12.sp,
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (request.status == BookingRequest.STATUS_PENDING) {
                                        Button(
                                            onClick = { onApproveRequest(request.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusAvailableGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .testTag("admin_approve_button_${request.id}")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { onRejectRequest(request.id, "Slot unavailable") },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusBookedRed),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .testTag("admin_reject_button_${request.id}")
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reject", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { IntentUtils.makePhoneCall(context, request.mobileNumber) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .height(40.dp)
                                            .testTag("admin_call_customer_${request.id}")
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Calendar Control Tab
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Booked Dates Control",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    Button(
                        onClick = { showAddDateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("admin_add_date_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Date", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(bookedDates) { bDate ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_date_item_${bDate.date}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = bDate.date,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                    Text(
                                        text = if (bDate.eventTitle.isNotEmpty()) bDate.eventTitle else bDate.status,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when (bDate.status) {
                                                    BookedDate.STATUS_BOOKED -> StatusBookedBg
                                                    BookedDate.STATUS_PENDING -> StatusPendingBg
                                                    else -> StatusAvailableBg
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = bDate.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (bDate.status) {
                                                BookedDate.STATUS_BOOKED -> StatusBookedRed
                                                BookedDate.STATUS_PENDING -> StatusPendingOrange
                                                else -> StatusAvailableGreen
                                            }
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            // Set as available / delete entry
                                            onSetDateStatus(bDate.date, BookedDate.STATUS_AVAILABLE, "", "", "")
                                        },
                                        modifier = Modifier.testTag("admin_delete_date_${bDate.date}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusBookedRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Date Dialog
    if (showAddDateDialog) {
        var newDateText by remember { mutableStateOf("") }
        var newTitleText by remember { mutableStateOf("") }
        var newCustomerText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDateDialog = false },
            title = { Text("Add Booked Date", fontWeight = FontWeight.Bold, color = EmeraldDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newDateText,
                        onValueChange = { newDateText = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_dialog_date_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newTitleText,
                        onValueChange = { newTitleText = it },
                        label = { Text("Event Title (e.g. Wedding)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_dialog_title_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newCustomerText,
                        onValueChange = { newCustomerText = it },
                        label = { Text("Customer Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_dialog_customer_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDateText.trim().isNotEmpty()) {
                            onSetDateStatus(
                                newDateText.trim(),
                                BookedDate.STATUS_BOOKED,
                                newTitleText.trim(),
                                newCustomerText.trim(),
                                "Added by Admin"
                            )
                            showAddDateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    modifier = Modifier.testTag("admin_confirm_add_date")
                ) {
                    Text("Reserve Date", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MetricBox(title: String, count: String, highlightColor: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = highlightColor
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}
