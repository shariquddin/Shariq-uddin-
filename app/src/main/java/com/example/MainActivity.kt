package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.EmergencyFloatingCallButton
import com.example.ui.components.RequestConfirmationDialog
import com.example.ui.components.Screen
import com.example.ui.components.TopHallAppBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BookingFormScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.ContactScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.ReviewsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HallViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ARFunctionHallApp()
            }
        }
    }
}

@Composable
fun ARFunctionHallApp(
    viewModel: HallViewModel = viewModel()
) {
    val bookedDates by viewModel.bookedDates.collectAsStateWithLifecycle()
    val bookingRequests by viewModel.bookingRequests.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val customerNotifications by viewModel.customerNotifications.collectAsStateWithLifecycle()
    val adminNotifications by viewModel.adminNotifications.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val selectedCalendarMonth by viewModel.selectedCalendarMonth.collectAsStateWithLifecycle()

    val lastSubmittedRequest by viewModel.lastSubmittedRequest.collectAsStateWithLifecycle()
    val showRequestSuccessDialog by viewModel.showRequestSuccessDialog.collectAsStateWithLifecycle()

    var currentRoute by remember { mutableStateOf(Screen.Home.route) }
    var selectedBookingDate by remember { mutableStateOf("") }

    val unreadCount = customerNotifications.count { !it.isRead }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopHallAppBar(
                title = "AR FUNCTION HALL",
                unreadNotificationCount = unreadCount,
                onNotificationClick = {
                    viewModel.markCustomerNotificationsRead()
                    currentRoute = "notifications"
                },
                onAdminClick = {
                    currentRoute = Screen.Admin.route
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        },
        floatingActionButton = {
            EmergencyFloatingCallButton(visible = currentRoute != Screen.Admin.route)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                Screen.Home.route -> {
                    HomeScreen(
                        onBookClick = { currentRoute = Screen.Booking.route },
                        onCalendarClick = { currentRoute = Screen.Calendar.route },
                        onContactClick = { currentRoute = Screen.Contact.route },
                        onGalleryClick = { currentRoute = Screen.Gallery.route },
                        onPricingClick = { currentRoute = Screen.Pricing.route },
                        onReviewsClick = { currentRoute = "reviews" },
                        facilities = viewModel.getFacilities(),
                        packages = viewModel.getPricingPackages(),
                        reviews = reviews
                    )
                }

                Screen.Calendar.route -> {
                    CalendarScreen(
                        currentMonth = selectedCalendarMonth,
                        bookedDatesList = bookedDates,
                        onMonthChange = { offset -> viewModel.changeMonth(offset) },
                        onSelectAvailableDateToBook = { dateStr ->
                            selectedBookingDate = dateStr
                            currentRoute = Screen.Booking.route
                        }
                    )
                }

                Screen.Booking.route -> {
                    BookingFormScreen(
                        initialDate = selectedBookingDate,
                        onSubmitBooking = { name, mobile, email, eventType, guests, date, start, end, reqs ->
                            viewModel.submitBookingRequest(
                                customerName = name,
                                mobileNumber = mobile,
                                email = email,
                                eventType = eventType,
                                guestCount = guests,
                                functionDate = date,
                                startTime = start,
                                endTime = end,
                                specialRequirements = reqs,
                                onResult = { _, _ -> }
                            )
                        }
                    )
                }

                Screen.Gallery.route -> {
                    GalleryScreen(galleryItems = viewModel.getGalleryItems())
                }

                Screen.Pricing.route -> {
                    PricingScreen(
                        packages = viewModel.getPricingPackages(),
                        onSelectPackageToBook = { pkgTitle ->
                            currentRoute = Screen.Booking.route
                        }
                    )
                }

                Screen.Contact.route -> {
                    ContactScreen(facilities = viewModel.getFacilities())
                }

                "reviews" -> {
                    ReviewsScreen(
                        reviews = reviews,
                        onSubmitReview = { name, rating, comment, type ->
                            viewModel.submitReview(name, rating, comment, type)
                        }
                    )
                }

                Screen.Admin.route -> {
                    AdminScreen(
                        isLoggedIn = isAdminLoggedIn,
                        bookingRequests = bookingRequests,
                        bookedDates = bookedDates,
                        onLogin = { pass -> viewModel.loginAdmin(pass) },
                        onLogout = { viewModel.logoutAdmin() },
                        onApproveRequest = { reqId -> viewModel.approveRequest(reqId) },
                        onRejectRequest = { reqId, notes -> viewModel.rejectRequest(reqId, notes) },
                        onSetDateStatus = { date, status, title, name, notes ->
                            viewModel.setDateStatus(date, status, title, name, notes)
                        }
                    )
                }

                "notifications" -> {
                    NotificationsScreen(
                        notifications = customerNotifications,
                        onMarkAllRead = { viewModel.markCustomerNotificationsRead() }
                    )
                }
            }
        }

        if (showRequestSuccessDialog) {
            RequestConfirmationDialog(
                request = lastSubmittedRequest,
                onDismiss = { viewModel.dismissSuccessDialog() }
            )
        }
    }
}
