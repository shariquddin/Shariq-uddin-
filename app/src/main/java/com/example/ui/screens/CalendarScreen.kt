package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookedDate
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusAvailableBg
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusBookedBg
import com.example.ui.theme.StatusBookedRed
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusPendingOrange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
    currentMonth: Calendar,
    bookedDatesList: List<BookedDate>,
    onMonthChange: (Int) -> Unit,
    onSelectAvailableDateToBook: (String) -> Unit
) {
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val monthTitle = monthYearFormat.format(currentMonth.time)

    val dateFormatKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Map date strings to status
    val bookedDateMap = remember(bookedDatesList) {
        bookedDatesList.associateBy { it.date }
    }

    var selectedDateString by remember {
        mutableStateOf(dateFormatKey.format(Calendar.getInstance().time))
    }

    val selectedBookedObj = bookedDateMap[selectedDateString]
    val selectedStatus = selectedBookedObj?.status ?: BookedDate.STATUS_AVAILABLE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Month Selector Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onMonthChange(-1) },
                    modifier = Modifier.testTag("calendar_prev_month_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = GoldPrimary
                    )
                }

                Text(
                    text = monthTitle,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                IconButton(
                    onClick = { onMonthChange(1) },
                    modifier = Modifier.testTag("calendar_next_month_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = GoldPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = StatusAvailableGreen, label = "Available (Green)")
            LegendItem(color = StatusBookedRed, label = "Booked (Red)")
            LegendItem(color = StatusPendingOrange, label = "Pending (Orange)")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Day of Week Header
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EmeraldDark,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid Calendar Calculation
        val cal = currentMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-based index
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val totalCells = firstDayOfWeek + daysInMonth

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f)
        ) {
            items(totalCells) { index ->
                if (index < firstDayOfWeek) {
                    Box(modifier = Modifier.aspectRatio(1f))
                } else {
                    val dayNum = index - firstDayOfWeek + 1
                    val dayCal = currentMonth.clone() as Calendar
                    dayCal.set(Calendar.DAY_OF_MONTH, dayNum)
                    val dateKey = dateFormatKey.format(dayCal.time)

                    val bookedObj = bookedDateMap[dateKey]
                    val status = bookedObj?.status ?: BookedDate.STATUS_AVAILABLE

                    val isSelected = (dateKey == selectedDateString)

                    val bgColor = when (status) {
                        BookedDate.STATUS_BOOKED -> StatusBookedBg
                        BookedDate.STATUS_PENDING -> StatusPendingBg
                        else -> StatusAvailableBg
                    }

                    val borderColor = when (status) {
                        BookedDate.STATUS_BOOKED -> StatusBookedRed
                        BookedDate.STATUS_PENDING -> StatusPendingOrange
                        else -> StatusAvailableGreen
                    }

                    val textColor = when (status) {
                        BookedDate.STATUS_BOOKED -> StatusBookedRed
                        BookedDate.STATUS_PENDING -> StatusPendingOrange
                        else -> StatusAvailableGreen
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoldPrimary.copy(alpha = 0.3f) else bgColor)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) GoldPrimary else borderColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedDateString = dateKey
                            }
                            .testTag("calendar_date_$dateKey"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayNum.toString(),
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = textColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Date Action Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selected Date:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedDateString,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = when (selectedStatus) {
                                    BookedDate.STATUS_BOOKED -> StatusBookedBg
                                    BookedDate.STATUS_PENDING -> StatusPendingBg
                                    else -> StatusAvailableBg
                                },
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when (selectedStatus) {
                                BookedDate.STATUS_BOOKED -> "BOOKED 🔴"
                                BookedDate.STATUS_PENDING -> "PENDING 🟧"
                                else -> "AVAILABLE 🟢"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = when (selectedStatus) {
                                BookedDate.STATUS_BOOKED -> StatusBookedRed
                                BookedDate.STATUS_PENDING -> StatusPendingOrange
                                else -> StatusAvailableGreen
                            }
                        )
                    }
                }

                if (selectedBookedObj != null && selectedBookedObj.eventTitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Event: ${selectedBookedObj.eventTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedStatus == BookedDate.STATUS_AVAILABLE) {
                    Button(
                        onClick = { onSelectAvailableDateToBook(selectedDateString) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("calendar_book_selected_date_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Book For $selectedDateString",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = if (selectedStatus == BookedDate.STATUS_BOOKED)
                            "This date is already reserved for a private event. Please select another date."
                        else "A booking request is currently pending for this date.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = EmeraldDark
        )
    }
}
