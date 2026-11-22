package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdMobManager
import com.example.ads.MobileAdsInitializer
import com.example.data.model.Booking
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.ui.Ra7alatViewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppScreen
import com.example.ui.components.AppTopBar
import com.example.ui.dialogs.AgencyVerificationDialog
import com.example.ui.dialogs.AllFiltersDialog
import com.example.ui.dialogs.BoardingPassDialog
import com.example.ui.dialogs.BookingDialog
import com.example.ui.dialogs.BookingSuccessDialog
import com.example.ui.dialogs.EditTripDialog
import com.example.ui.dialogs.FirstLaunchLanguageDialog
import com.example.ui.dialogs.FollowersListDialog
import com.example.ui.dialogs.ForceUpdateDialog
import com.example.ui.dialogs.NotificationPreferencesDialog
import com.example.ui.dialogs.OrganizerProfileDialog
import com.example.ui.dialogs.ReviewDialog
import com.example.ui.dialogs.ThemeSelectorDialog
import com.example.ui.dialogs.TripChatBottomSheet
import com.example.ui.dialogs.TripDetailDialog
import com.example.ui.screens.AddTripScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.GoogleSignInDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyTripsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WelcomeOnboardingScreen
import com.example.ui.dialogs.UserSurveyDialog
import com.example.ui.components.AppCoachmarksOverlay
import com.example.util.OnboardingPreferencesManager
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.Ra7alatTheme
import com.example.util.AppLanguage
import com.example.util.UpdateCheckResult
import com.example.util.UpdateType

class MainActivity : ComponentActivity() {

    private val viewModel: Ra7alatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            MobileAdsInitializer.initialize(this)
            AdMobManager.initialize(application)
        } catch (_: Exception) {}

        intent?.data?.let { data ->
            val path = data.path
            if (path != null && path.contains("/trip/")) {
                val tripIdStr = path.substringAfterLast("/trip/")
                tripIdStr.toLongOrNull()?.let { id ->
                    viewModel.handleDeepLink(id)
                }
            }
        }

        setContent {
            val currentPalette by viewModel.currentPalette.collectAsStateWithLifecycle()
            Ra7alatTheme(palette = currentPalette) {
                Ra7alatApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Ra7alatApp(viewModel: Ra7alatViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val showSplash by viewModel.showSplashScreen.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val currentPalette by viewModel.currentPalette.collectAsStateWithLifecycle()
    val userProfile by viewModel.currentUser.collectAsStateWithLifecycle()

    val allTrips by viewModel.allTrips.collectAsStateWithLifecycle()
    val filteredTrips by viewModel.filteredTrips.collectAsStateWithLifecycle()
    val favoriteTrips by viewModel.favoriteTrips.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val notificationPrefs by viewModel.notificationPreferences.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val selectedTripDetail by viewModel.selectedTrip.collectAsStateWithLifecycle()
    val bookingTrip by viewModel.bookingTrip.collectAsStateWithLifecycle()
    val bookingSuccessDialog by viewModel.lastConfirmedBooking.collectAsStateWithLifecycle()
    val editingTrip by viewModel.editingTrip.collectAsStateWithLifecycle()
    val activeChatTrip by viewModel.activeChatTrip.collectAsStateWithLifecycle()
    val selectedOrganizerProfile by viewModel.selectedOrganizerProfile.collectAsStateWithLifecycle()
    val followersList by viewModel.followersList.collectAsStateWithLifecycle()
    val isLoadingFollowers by viewModel.isLoadingFollowers.collectAsStateWithLifecycle()
    val showFollowersDialog by viewModel.showFollowersDialog.collectAsStateWithLifecycle()
    val isFollowingDialogMode by viewModel.isFollowingDialogMode.collectAsStateWithLifecycle()
    val followersDialogOrganizer by viewModel.followersDialogOrganizer.collectAsStateWithLifecycle()

    val showLoginSheet by viewModel.showAuthDialog.collectAsStateWithLifecycle()
    val showThemeSelector by viewModel.showThemeSelector.collectAsStateWithLifecycle()

    val onboardingPrefs = remember { OnboardingPreferencesManager(context) }
    val isWelcomeCompleted by onboardingPrefs.isWelcomeCompleted.collectAsStateWithLifecycle()
    val isSurveyCompleted by onboardingPrefs.isSurveyCompleted.collectAsStateWithLifecycle()
    val isCoachmarksCompleted by onboardingPrefs.isCoachmarksCompleted.collectAsStateWithLifecycle()
    var showUserSurvey by remember { mutableStateOf(false) }
    var showCoachmarks by remember { mutableStateOf(false) }

    var showFilterDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showVerificationDialog by remember { mutableStateOf(false) }
    var showDashboard by remember { mutableStateOf(false) }
    var reviewTripId by remember { mutableStateOf<Long?>(null) }
    var boardingPassBooking by remember { mutableStateOf<Booking?>(null) }

    val isAr = currentLanguage == AppLanguage.ARABIC

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { viewModel.dismissSplash() }
        )
        return
    }

    if (!isWelcomeCompleted) {
        WelcomeOnboardingScreen(
            onFinish = {
                onboardingPrefs.completeWelcome()
            },
            isAr = isAr,
            onToggleLanguage = {
                viewModel.toggleLanguage()
            }
        )
        return
    }

    if (!userProfile.isLoggedIn) {
        GoogleSignInDialog(
            onDismiss = { },
            onSuccess = { profile ->
                viewModel.onUserSignedIn(profile)
                if (!isSurveyCompleted) {
                    showUserSurvey = true
                } else if (!isCoachmarksCompleted) {
                    showCoachmarks = true
                }
            },
            isAr = isAr,
            allowDismiss = false,
            authManager = viewModel.authManager
        )
        return
    }

    val currentScreen = when (currentTab) {
        0 -> AppScreen.HOME
        1 -> AppScreen.MY_TRIPS
        2 -> AppScreen.ADD_TRIP
        3 -> AppScreen.FAVORITES
        4 -> AppScreen.PROFILE
        else -> AppScreen.HOME
    }

    val screenTitle = when (currentScreen) {
        AppScreen.HOME -> if (isAr) "رحلات الجزائر 🇩🇿" else "Ra7alat DZ 🇩🇿"
        AppScreen.MY_TRIPS -> if (isAr) "حجوزاتي وتذاكري" else "Mes Réservations"
        AppScreen.ADD_TRIP -> if (isAr) "نشر رحلة جديدة" else "Publier un Voyage"
        AppScreen.FAVORITES -> if (isAr) "الرحلات المفضلة" else "Voyages Favoris"
        AppScreen.PROFILE -> if (isAr) "الملف الشخصي" else "Mon Profil"
    }

    Scaffold(
        containerColor = currentPalette.darkCanvasBg,
        topBar = {
            if (currentScreen != AppScreen.PROFILE) {
                AppTopBar(
                    title = "🇩🇿 RA7ALAT DZ",
                    subtitle = if (isAr) "استكشف الجزائر" else "EXPLORER L'ALGÉRIE",
                    userName = if (userProfile.agencyName.isNotBlank()) userProfile.agencyName else if (userProfile.fullName.isNotBlank()) userProfile.fullName else "ra7alt",
                    isAr = isAr,
                    isAgency = userProfile.role == "AGENCY" || userProfile.agencyName.isNotBlank(),
                    onFilterClick = { showFilterDialog = true },
                    onThemeClick = { viewModel.openThemeSelector() },
                    onLangClick = { viewModel.toggleLanguage() },
                    onRefreshClick = { viewModel.refreshTrips() },
                    onNotificationPrefsClick = { showNotificationsDialog = true },
                    onDashboardClick = { showDashboard = true },
                    onProfileClick = { viewModel.setTab(4) },
                    showFilters = currentScreen == AppScreen.HOME,
                    showDashboard = true
                )
            }
        },
        bottomBar = {
            AppBottomNav(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    when (screen) {
                        AppScreen.HOME -> viewModel.setTab(0)
                        AppScreen.MY_TRIPS -> viewModel.setTab(1)
                        AppScreen.ADD_TRIP -> viewModel.setTab(2)
                        AppScreen.FAVORITES -> viewModel.setTab(3)
                        AppScreen.PROFILE -> viewModel.setTab(4)
                    }
                },
                isAr = isAr,
                isAgency = userProfile.role == "AGENCY"
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    val selWilaya by viewModel.selectedWilaya.collectAsStateWithLifecycle()
                    val selType by viewModel.selectedTripType.collectAsStateWithLifecycle()
                    val selPrice by viewModel.selectedPriceFilter.collectAsStateWithLifecycle()

                    HomeScreen(
                        trips = filteredTrips,
                        onTripClick = { trip ->
                            viewModel.openTripDetails(trip)
                            AdMobManager.incrementTripInteraction(context)
                        },
                        onFavoriteToggle = { trip -> viewModel.toggleFavorite(trip) },
                        onChatClick = { trip -> viewModel.openTripChat(trip) },
                        onOpenFilters = { showFilterDialog = true },
                        onRefresh = { viewModel.refreshTrips() },
                        onEditTrip = { trip -> viewModel.startEditingTrip(trip) },
                        onDeleteTrip = { trip -> viewModel.deleteTripByOrganizer(trip) },
                        onManageTrip = { trip -> showDashboard = true },
                        userProfile = userProfile,
                        userBookings = allBookings.filter { it.userEmail == userProfile.email || it.userName == userProfile.fullName },
                        selectedWilaya = selWilaya,
                        onWilayaSelected = { viewModel.setSelectedWilaya(it) },
                        selectedType = selType,
                        onTypeSelected = { viewModel.setSelectedTripType(it) },
                        selectedPrice = selPrice,
                        themePalette = currentPalette,
                        isAr = isAr
                    )
                }
                AppScreen.MY_TRIPS -> {
                    val agencyTrips = allTrips.filter { it.organizerName == userProfile.agencyName || it.organizerEmail == userProfile.email }
                    val userBookings = allBookings.filter { it.userEmail == userProfile.email || it.userName == userProfile.fullName }

                    MyTripsScreen(
                        userProfile = userProfile,
                        bookings = if (userBookings.isNotEmpty()) userBookings else allBookings,
                        agencyTrips = agencyTrips,
                        onBookingClick = { booking -> boardingPassBooking = booking },
                        onTripClick = { trip -> viewModel.openTripDetails(trip) },
                        onChatClick = { trip -> viewModel.openTripChat(trip) },
                        onEditTripClick = { trip -> viewModel.startEditingTrip(trip) },
                        onDeleteTripClick = { trip -> viewModel.deleteTripByOrganizer(trip) },
                        onCancelBooking = { booking -> viewModel.cancelBooking(booking) },
                        onOpenDashboard = { showDashboard = true },
                        isAr = isAr
                    )
                }
                AppScreen.ADD_TRIP -> {
                    val isPublishingTrip by viewModel.isPublishingTrip.collectAsState()
                    val context = androidx.compose.ui.platform.LocalContext.current
                    AddTripScreen(
                        userProfile = userProfile,
                        isPublishing = isPublishingTrip,
                        onPublishTripWithImages = { trip, uris ->
                            viewModel.publishNewTripWithImages(trip, uris, context) {
                                viewModel.setTab(0)
                            }
                        },
                        onTripAdded = { trip ->
                            viewModel.publishNewTrip(trip)
                            viewModel.setTab(0)
                        },
                        onRequireLogin = { viewModel.showLogin() },
                        isAr = isAr
                    )
                }
                AppScreen.FAVORITES -> {
                    FavoritesScreen(
                        favoriteTrips = favoriteTrips,
                        onTripClick = { trip -> viewModel.openTripDetails(trip) },
                        onRemoveFavorite = { trip -> viewModel.toggleFavorite(trip) },
                        onChatClick = { trip -> viewModel.openTripChat(trip) },
                        isAr = isAr
                    )
                }
                AppScreen.PROFILE -> {
                    val userBookingsCount = allBookings.count { it.userEmail == userProfile.email }
                    val userFavsCount = favoriteTrips.size
                    val userPublishedTrips = allTrips.filter {
                        (userProfile.email.isNotBlank() && it.organizerEmail.equals(userProfile.email, ignoreCase = true)) ||
                        (userProfile.agencyName.isNotBlank() && it.organizerName.equals(userProfile.agencyName, ignoreCase = true)) ||
                        (userProfile.fullName.isNotBlank() && it.organizerName.equals(userProfile.fullName, ignoreCase = true))
                    }
                    val userBookings = allBookings.filter { it.userEmail == userProfile.email }

                    ProfileScreen(
                        userProfile = userProfile,
                        bookingsCount = userBookingsCount,
                        favoritesCount = userFavsCount,
                        publishedTripsCount = userPublishedTrips.size,
                        language = currentLanguage,
                        currentPalette = currentPalette,
                        notificationPrefs = notificationPrefs,
                        userTrips = userPublishedTrips,
                        userBookings = userBookings,
                        onTripClick = { trip -> viewModel.openTripDetails(trip) },
                        onAddTrip = { viewModel.setTab(2) },
                        onUpdateProfile = { name, phone, bio, an, aw, aa, cr ->
                            viewModel.updateUserProfile(name, phone, userProfile.role, an, aw, bio, userProfile.avatarUrl)
                        },
                        onUploadAvatar = { bytes, name -> viewModel.uploadAvatar(bytes, name) },
                        onUpdateNotifications = { viewModel.updateNotificationPreferences(it) },
                        onOpenNotificationsDialog = { showNotificationsDialog = true },
                        onOpenThemeSelector = { viewModel.openThemeSelector() },
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onOpenVerificationDialog = { showVerificationDialog = true },
                        onOpenFollowers = { viewModel.openFollowersList(userProfile) },
                        onOpenFollowing = { viewModel.openFollowingList(userProfile) },
                        onToggleFollowersPrivacy = { viewModel.toggleFollowersPrivacy(it) },
                        onOpenPrivacyPolicy = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ra7alat.dz/privacy"))
                            context.startActivity(intent)
                        },
                        onOpenAboutDialog = {
                            Toast.makeText(context, if (isAr) "تطبيق رحلات الجزائر 🇩🇿 - الإصدار 1.0" else "Ra7alat DZ 🇩🇿 - v1.0", Toast.LENGTH_LONG).show()
                        },
                        onOpenShareApp = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "حمل تطبيق رحلات الجزائر لاكتشاف أفضل الرحلات السياحية في الجزائر 🇩🇿: https://ra7alat.dz")
                            }
                            context.startActivity(Intent.createChooser(intent, "مشاركة التطبيق"))
                        },
                        onOpenRateApp = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                            }
                        },
                        onOpenEditProfile = {},
                        onSwitchToAgency = { viewModel.switchToAgencyRole() },
                        onRequireLogin = { viewModel.showLogin() },
                        onLogout = {
                            viewModel.logout()
                            viewModel.showLogin()
                        },
                        onDeleteAccount = {
                            viewModel.deleteAccount()
                            viewModel.showLogin()
                        },
                        onOpenDashboard = { showDashboard = true },
                        isAr = isAr,
                        onSelectPalette = { viewModel.setPalette(it) }
                    )
                }
            }
        }
    }

    // Trip Details Dialog
    selectedTripDetail?.let { trip ->
        val isTripOwner = userProfile.isOwnerOf(trip)
        val isBooked = allBookings.any { (it.userEmail == userProfile.email || it.userName == userProfile.fullName) && it.tripId == trip.id && !it.status.equals("CANCELLED", ignoreCase = true) }

        TripDetailDialog(
            trip = trip,
            onBookClick = {
                viewModel.closeTripDetails()
                viewModel.openBookingDialog(trip)
            },
            onOrganizerClick = {
                viewModel.closeTripDetails()
                viewModel.openOrganizerProfile(trip.organizerName, trip.organizerPhone, trip.departureWilaya, trip.organizerEmail)
            },
            onReviewClick = {
                reviewTripId = trip.id
            },
            onChatClick = {
                viewModel.closeTripDetails()
                viewModel.openTripChat(trip)
            },
            onDismiss = { viewModel.closeTripDetails() },
            isAr = isAr,
            isOwner = isTripOwner,
            isBooked = isBooked,
            onEditClick = {
                viewModel.closeTripDetails()
                viewModel.startEditingTrip(trip)
            },
            onDeleteClick = {
                viewModel.closeTripDetails()
                viewModel.deleteTripByOrganizer(trip)
            },
            onManageClick = {
                viewModel.closeTripDetails()
                showDashboard = true
            }
        )
    }

    // Booking Dialog
    bookingTrip?.let { trip ->
        BookingDialog(
            trip = trip,
            userProfile = userProfile,
            onDismiss = { viewModel.closeBookingDialog() },
            onConfirmBooking = { seats, notes ->
                viewModel.confirmBooking(trip, seats, notes)
            },
            isAr = isAr
        )
    }

    // Booking Success Dialog
    bookingSuccessDialog?.let { booking ->
        BookingSuccessDialog(
            booking = booking,
            onViewTicket = {
                viewModel.closeBookingSuccessDialog()
                boardingPassBooking = booking
            },
            onDismiss = { viewModel.closeBookingSuccessDialog() },
            isAr = isAr
        )
    }

    // Boarding Pass Dialog
    boardingPassBooking?.let { booking ->
        BoardingPassDialog(
            booking = booking,
            onDismiss = { boardingPassBooking = null },
            isAr = isAr
        )
    }

    // Edit Trip Dialog
    editingTrip?.let { trip ->
        EditTripDialog(
            trip = trip,
            onDismiss = { viewModel.closeEditingTrip() },
            onSaveTrip = { updated -> viewModel.saveEditedTrip(updated) },
            isAr = isAr
        )
    }

    // Trip Chat Bottom Sheet
    activeChatTrip?.let { trip ->
        val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
        TripChatBottomSheet(
            tripId = trip.id,
            tripTitle = trip.title,
            currentUserName = userProfile.fullName.ifBlank { "مسافر جزائري" },
            messages = messages,
            organizerName = trip.organizerName,
            organizerEmail = trip.organizerEmail,
            onSendMessage = { msg ->
                viewModel.sendChatMessage(msg.text)
            },
            onDismiss = { viewModel.closeTripChat() },
            isAr = isAr
        )
    }

    // Organizer Profile Dialog
    selectedOrganizerProfile?.let { organizer ->
        val orgTrips = allTrips.filter { it.organizerEmail == organizer.email || (organizer.agencyName.isNotBlank() && it.organizerName == organizer.agencyName) }
        val isFollowing = viewModel.isFollowingOrganizer(organizer.email, organizer.agencyName.ifBlank { organizer.fullName })
        val isSelf = userProfile.isLoggedIn && (
            (organizer.email.isNotBlank() && organizer.email.equals(userProfile.email, ignoreCase = true)) ||
            (organizer.id.isNotBlank() && organizer.id == userProfile.id) ||
            (userProfile.agencyName.isNotBlank() && organizer.agencyName.equals(userProfile.agencyName, ignoreCase = true))
        )
        OrganizerProfileDialog(
            organizer = organizer,
            organizerTrips = orgTrips,
            isFollowing = isFollowing,
            isSelfProfile = isSelf,
            onToggleFollow = {
                if (!isSelf) {
                    viewModel.toggleFollowOrganizer(organizer.email, organizer.agencyName.ifBlank { organizer.fullName })
                }
            },
            onOpenFollowers = {
                viewModel.openFollowersList(organizer)
            },
            onTripClick = { trip ->
                viewModel.closeOrganizerProfile()
                viewModel.openTripDetails(trip)
            },
            onDismiss = { viewModel.closeOrganizerProfile() },
            isAr = isAr
        )
    }

    // Followers / Following List Dialog
    if (showFollowersDialog && (followersDialogOrganizer != null || isFollowingDialogMode)) {
        val org = followersDialogOrganizer ?: userProfile
        val isOwnProfile = org.email == userProfile.email ||
                (userProfile.agencyName.isNotBlank() && org.agencyName == userProfile.agencyName)
        FollowersListDialog(
            organizerName = org.agencyName.ifBlank { org.fullName },
            followers = followersList,
            isLoading = isLoadingFollowers,
            isOwner = isOwnProfile,
            showFollowersPublicly = org.showFollowersPublicly,
            isFollowingMode = isFollowingDialogMode,
            isAr = isAr,
            onSelectOrganizer = { name, email ->
                viewModel.openOrganizerProfile(
                    organizerName = name,
                    fallbackPhone = "",
                    fallbackWilaya = "",
                    organizerEmail = email
                )
            },
            onUnfollow = { follower ->
                viewModel.unfollowFromFollowingList(follower)
            },
            onTabChange = if (isOwnProfile) {
                { isFollowing ->
                    viewModel.switchFollowersTab(isFollowing, org)
                }
            } else null,
            onDismiss = { viewModel.closeFollowersList() }
        )
    }

    // Auth / Login Dialog
    if (showLoginSheet) {
        GoogleSignInDialog(
            onDismiss = { viewModel.hideLogin() },
            onSuccess = { profile ->
                viewModel.onUserSignedIn(profile)
            },
            isAr = isAr,
            authManager = viewModel.authManager
        )
    }

    // Theme Selector Dialog
    if (showThemeSelector) {
        ThemeSelectorDialog(
            selectedPalette = currentPalette,
            onPaletteSelected = { palette ->
                viewModel.setPalette(palette)
                viewModel.closeThemeSelector()
            },
            onDismiss = { viewModel.closeThemeSelector() },
            isAr = isAr
        )
    }

    // Filter Dialog
    if (showFilterDialog) {
        val selWilaya by viewModel.selectedWilaya.collectAsStateWithLifecycle()
        val selType by viewModel.selectedTripType.collectAsStateWithLifecycle()
        val selPrice by viewModel.selectedPriceFilter.collectAsStateWithLifecycle()

        AllFiltersDialog(
            selectedWilaya = selWilaya,
            selectedTripType = selType,
            selectedPriceFilter = selPrice,
            onApply = { wilaya, type, price ->
                viewModel.setSelectedWilaya(wilaya)
                viewModel.setSelectedTripType(type)
                viewModel.setSelectedPriceFilter(price)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false },
            isAr = isAr
        )
    }

    // Notification Preferences Dialog
    if (showNotificationsDialog) {
        NotificationPreferencesDialog(
            currentPrefs = notificationPrefs,
            onSave = { updatedPrefs ->
                viewModel.updateNotificationPreferences(updatedPrefs)
                showNotificationsDialog = false
            },
            onTest = { testPrefs ->
                viewModel.testNotificationPreferences(testPrefs)
            },
            onDismiss = { showNotificationsDialog = false },
            isAr = isAr
        )
    }

    // Agency Verification Dialog
    if (showVerificationDialog) {
        val publishedCount = allTrips.count { it.organizerEmail == userProfile.email || (userProfile.agencyName.isNotBlank() && it.organizerName == userProfile.agencyName) }
        AgencyVerificationDialog(
            userProfile = userProfile,
            publishedTripsCount = publishedCount,
            onSwitchToAgency = { viewModel.switchToAgencyRole() },
            onDismiss = { showVerificationDialog = false },
            isAr = isAr
        )
    }

    // Review Dialog
    reviewTripId?.let { tripId ->
        ReviewDialog(
            tripId = tripId,
            userName = userProfile.fullName.ifBlank { "مسافر" },
            onDismiss = { reviewTripId = null },
            onSubmitReview = { review ->
                viewModel.addTripReview(tripId, review.rating, review.comment)
                reviewTripId = null
            },
            isAr = isAr
        )
    }

    // Force Update Dialog
    updateState?.let { info ->
        ForceUpdateDialog(
            updateResult = UpdateCheckResult(
                type = if (info.isForceUpdate) UpdateType.FORCE else UpdateType.OPTIONAL,
                info = info
            ),
            onDismiss = { viewModel.dismissOptionalUpdate() },
            isAr = isAr
        )
    }

    // Agency Dashboard Dialog
    if (showDashboard) {
        val myAgencyTrips = allTrips.filter { userProfile.isOwnerOf(it) }
        val myAgencyBookings = allBookings.filter { booking ->
            myAgencyTrips.any { it.id == booking.tripId } || booking.userEmail == userProfile.email
        }
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDashboard = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                DashboardScreen(
                    userProfile = userProfile,
                    trips = myAgencyTrips,
                    bookings = myAgencyBookings,
                    language = currentLanguage,
                    isAr = isAr,
                    onRefresh = { viewModel.refreshAllData() },
                    onTripClick = { trip ->
                        showDashboard = false
                        viewModel.openTripDetails(trip)
                    },
                    onEditTrip = { trip ->
                        showDashboard = false
                        viewModel.startEditingTrip(trip)
                    },
                    onCancelTrip = { trip ->
                        viewModel.cancelTripByOrganizer(trip)
                    },
                    onDeleteTrip = { trip ->
                        viewModel.deleteTripByOrganizer(trip)
                    },
                    onClose = { showDashboard = false }
                )
            }
        }
    }

    // Onboarding User Survey (Displayed on first launch after sign in)
    if (showUserSurvey || (!isSurveyCompleted && userProfile.isLoggedIn)) {
        UserSurveyDialog(
            onComplete = { gender, ageGroup, heardFrom, interests, termsAccepted ->
                onboardingPrefs.completeSurvey(gender, ageGroup, heardFrom, interests, termsAccepted)
                showUserSurvey = false
                if (!isCoachmarksCompleted) {
                    showCoachmarks = true
                }
            },
            onDismiss = {
                onboardingPrefs.completeSurvey("غير محدد", "18 - 24 سنة", "أخرى", emptySet(), true)
                showUserSurvey = false
                if (!isCoachmarksCompleted) {
                    showCoachmarks = true
                }
            },
            isAr = isAr
        )
    }

    // Interactive Coachmarks Overlay (Spotlight tutorial with arrows and skip option)
    if (showCoachmarks || (!isCoachmarksCompleted && isSurveyCompleted && userProfile.isLoggedIn && !showUserSurvey)) {
        AppCoachmarksOverlay(
            onDismiss = {
                onboardingPrefs.completeCoachmarks()
                showCoachmarks = false
            },
            isAr = isAr
        )
    }
}
