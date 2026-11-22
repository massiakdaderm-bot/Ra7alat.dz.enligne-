package com.example.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.model.AppUpdateInfo
import com.example.data.model.Booking
import com.example.data.model.FollowerRecord
import com.example.data.model.NotificationPreferences
import com.example.data.model.Trip
import com.example.data.model.TripMessage
import com.example.data.model.TripReview
import com.example.data.model.UserProfile
import com.example.data.repository.TripRepository
import com.example.ui.theme.AppThemePalette
import com.example.util.AppLanguage
import com.example.util.LanguageManager
import com.example.util.ThemePaletteManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class Ra7alatViewModel(application: Application) : AndroidViewModel(application) {

    val repository: TripRepository = TripRepository(application)
    val authManager: AuthManager = AuthManager(application)
    val languageManager: LanguageManager = LanguageManager(application)
    val themePaletteManager: ThemePaletteManager = ThemePaletteManager(application)

    val currentUser: StateFlow<UserProfile> = authManager.currentUser
    val currentUserProfile: StateFlow<UserProfile> = authManager.currentUser

    val allTrips: StateFlow<List<Trip>> = repository.allTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTrips: StateFlow<List<Trip>> = repository.favoriteTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviews: StateFlow<List<TripReview>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chatMessages = MutableStateFlow<List<TripMessage>>(emptyList())
    val chatMessages: StateFlow<List<TripMessage>> = _chatMessages.asStateFlow()

    val notificationPreferences: StateFlow<NotificationPreferences> = repository.notificationPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationPreferences())

    private val _updateState = MutableStateFlow<AppUpdateInfo?>(null)
    val updateState: StateFlow<AppUpdateInfo?> = _updateState.asStateFlow()

    val currentPalette: StateFlow<AppThemePalette> = themePaletteManager.currentPalette
    val currentLanguage: StateFlow<AppLanguage> = languageManager.currentLanguage

    // UI States
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _showSplashScreen = MutableStateFlow(true)
    val showSplashScreen: StateFlow<Boolean> = _showSplashScreen.asStateFlow()

    private val _showThemeSelector = MutableStateFlow(false)
    val showThemeSelector: StateFlow<Boolean> = _showThemeSelector.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    private val _selectedTrip = MutableStateFlow<Trip?>(null)
    val selectedTrip: StateFlow<Trip?> = _selectedTrip.asStateFlow()

    private val _bookingTrip = MutableStateFlow<Trip?>(null)
    val bookingTrip: StateFlow<Trip?> = _bookingTrip.asStateFlow()

    private val _lastConfirmedBooking = MutableStateFlow<Booking?>(null)
    val lastConfirmedBooking: StateFlow<Booking?> = _lastConfirmedBooking.asStateFlow()

    private val _editingTrip = MutableStateFlow<Trip?>(null)
    val editingTrip: StateFlow<Trip?> = _editingTrip.asStateFlow()

    private val _isPublishingTrip = MutableStateFlow(false)
    val isPublishingTrip: StateFlow<Boolean> = _isPublishingTrip.asStateFlow()

    private val _activeChatTrip = MutableStateFlow<Trip?>(null)
    val activeChatTrip: StateFlow<Trip?> = _activeChatTrip.asStateFlow()

    private val _selectedOrganizerProfile = MutableStateFlow<UserProfile?>(null)
    val selectedOrganizerProfile: StateFlow<UserProfile?> = _selectedOrganizerProfile.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedWilaya = MutableStateFlow("الكل")
    val selectedWilaya: StateFlow<String> = _selectedWilaya.asStateFlow()

    private val _selectedDepartureWilaya = MutableStateFlow("الكل")
    val selectedDepartureWilaya: StateFlow<String> = _selectedDepartureWilaya.asStateFlow()

    private val _selectedTripType = MutableStateFlow("الكل")
    val selectedTripType: StateFlow<String> = _selectedTripType.asStateFlow()

    private val _selectedPriceFilter = MutableStateFlow("الكل")
    val selectedPriceFilter: StateFlow<String> = _selectedPriceFilter.asStateFlow()

    private val _onlyAvailableSeats = MutableStateFlow(false)
    val onlyAvailableSeats: StateFlow<Boolean> = _onlyAvailableSeats.asStateFlow()

    private val queryFiltersFlow = combine(
        searchQuery,
        selectedWilaya,
        selectedDepartureWilaya
    ) { q, w, dw -> Triple(q, w, dw) }

    private val optionFiltersFlow = combine(
        selectedTripType,
        selectedPriceFilter,
        onlyAvailableSeats
    ) { t, p, s -> Triple(t, p, s) }

    val filteredTrips: StateFlow<List<Trip>> = combine(
        allTrips,
        queryFiltersFlow,
        optionFiltersFlow
    ) { trips, queryFilters, optionFilters ->
        val (query, wilaya, depWilaya) = queryFilters
        val (type, price, onlySeats) = optionFilters
        trips.filter { trip ->
            val matchQuery = query.isBlank() ||
                    trip.title.contains(query, ignoreCase = true) ||
                    trip.destinationWilaya.contains(query, ignoreCase = true) ||
                    trip.destinationPlace.contains(query, ignoreCase = true) ||
                    trip.departureWilaya.contains(query, ignoreCase = true) ||
                    trip.organizerName.contains(query, ignoreCase = true)

            val matchWilaya = wilaya == "الكل" || wilaya.isBlank() || trip.destinationWilaya.contains(wilaya, ignoreCase = true)
            val matchDepWilaya = depWilaya == "الكل" || depWilaya.isBlank() || trip.departureWilaya.contains(depWilaya, ignoreCase = true)
            val matchType = type == "الكل" || type.isBlank() || trip.tripType.equals(type, ignoreCase = true)

            val matchPrice = when (price) {
                "< 3,000 دج" -> trip.priceDzd < 3000
                "3,000 - 10,000 دج" -> trip.priceDzd in 3000.0..10000.0
                "> 10,000 دج" -> trip.priceDzd > 10000
                else -> true
            }

            val matchSeats = !onlySeats || trip.availableSeats > 0

            matchQuery && matchWilaya && matchDepWilaya && matchType && matchPrice && matchSeats
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            authManager.checkSession()
            repository.setCurrentUser(currentUser.value.email, currentUser.value.id)
            repository.refreshData()
            refreshUserFollowCounts()
        }
    }

    fun setTab(tab: Int) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedWilaya(wilaya: String) {
        _selectedWilaya.value = wilaya
    }

    fun setSelectedDepartureWilaya(wilaya: String) {
        _selectedDepartureWilaya.value = wilaya
    }

    fun setSelectedTripType(type: String) {
        _selectedTripType.value = type
    }

    fun setSelectedPriceFilter(price: String) {
        _selectedPriceFilter.value = price
    }

    fun toggleOnlyAvailableSeats() {
        _onlyAvailableSeats.value = !_onlyAvailableSeats.value
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedWilaya.value = "الكل"
        _selectedDepartureWilaya.value = "الكل"
        _selectedTripType.value = "الكل"
        _selectedPriceFilter.value = "الكل"
        _onlyAvailableSeats.value = false
    }

    fun refreshTrips() {
        viewModelScope.launch {
            repository.refreshData()
        }
    }

    fun dismissSplash() {
        _showSplashScreen.value = false
    }

    fun openThemeSelector() {
        _showThemeSelector.value = true
    }

    fun closeThemeSelector() {
        _showThemeSelector.value = false
    }

    fun setPalette(palette: AppThemePalette) {
        themePaletteManager.setPalette(palette)
    }

    fun setLanguage(lang: AppLanguage) {
        languageManager.setLanguage(lang)
    }

    fun toggleLanguage() {
        languageManager.toggleLanguage()
    }

    fun completeFirstLaunchLanguage(lang: AppLanguage) {
        languageManager.setLanguage(lang)
    }

    fun showLogin() {
        _showAuthDialog.value = true
    }

    fun hideLogin() {
        _showAuthDialog.value = false
        _authErrorMessage.value = null
    }

    fun loginWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            val result = authManager.loginWithEmail(email, pass)
            _isAuthLoading.value = false
            if (result.isSuccess) {
                _showAuthDialog.value = false
                repository.setCurrentUser(email, result.getOrNull()?.id)
                repository.refreshData()
                _toastMessage.value = "مرحباً بك مجدداً!"
            } else {
                _authErrorMessage.value = result.exceptionOrNull()?.message ?: "فشل تسجيل الدخول"
            }
        }
    }

    fun registerWithEmail(
        email: String,
        pass: String,
        fullName: String,
        phone: String,
        role: String,
        agencyName: String,
        agencyWilaya: String
    ) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            val result = authManager.registerWithEmail(email, pass, fullName, phone, role, agencyName, agencyWilaya)
            _isAuthLoading.value = false
            if (result.isSuccess) {
                _showAuthDialog.value = false
                repository.setCurrentUser(email, result.getOrNull()?.id)
                repository.refreshData()
                _toastMessage.value = "تم إنشاء الحساب بنجاح!"
            } else {
                _authErrorMessage.value = result.exceptionOrNull()?.message ?: "فشل إنشاء الحساب"
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            val result = authManager.resetPassword(email)
            if (result.isSuccess) {
                _toastMessage.value = "تم إرسال رابط استعادة كلمة المرور إلى بريدك الإلكتروني"
            } else {
                _toastMessage.value = result.exceptionOrNull()?.message ?: "حدث خطأ"
            }
        }
    }

    fun onUserSignedIn(profile: UserProfile) {
        viewModelScope.launch {
            authManager.saveLocalProfile(profile)
            repository.setCurrentUser(profile.email, profile.id)
            repository.refreshData()
            _showAuthDialog.value = false
            _toastMessage.value = if (profile.fullName.isNotBlank()) "مرحباً بك ${profile.fullName}!" else "تم تسجيل الدخول بنجاح"
        }
    }

    fun launchGoogleSignIn(context: Context, role: String, agencyName: String, agencyWilaya: String) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle(context, "")
            if (result.isSuccess) {
                _showAuthDialog.value = false
                _toastMessage.value = "تم تسجيل الدخول بنجاح"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
            repository.setCurrentUser(null, null)
            _toastMessage.value = "تم تسجيل الخروج"
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val user = currentUser.value
            authManager.deleteAccount(user.id, user.email)
            repository.setCurrentUser(null, null)
            _toastMessage.value = "تم حذف الحساب نهائياً من قاعدة البيانات"
        }
    }

    fun updateUserProfile(
        name: String,
        phone: String,
        role: String,
        agencyName: String,
        agencyWilaya: String,
        bio: String,
        avatarUrl: String
    ) {
        viewModelScope.launch {
            val current = currentUser.value
            val updated = current.copy(
                fullName = name,
                phone = phone,
                role = role,
                agencyName = agencyName,
                agencyWilaya = agencyWilaya,
                bio = bio,
                avatarUrl = avatarUrl
            )
            authManager.updateProfile(updated)
            _toastMessage.value = "تم تحديث الملف الشخصي بنجاح"
        }
    }

    fun uploadAvatar(imageBytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            val result = repository.uploadAvatar(imageBytes, fileName)
            if (result.isSuccess) {
                val url = result.getOrNull().orEmpty()
                val updated = currentUser.value.copy(avatarUrl = url)
                authManager.updateProfile(updated)
                _toastMessage.value = "تم رفع الصورة بنجاح"
            } else {
                _toastMessage.value = "فشل رفع الصورة"
            }
        }
    }

    fun switchToAgencyRole() {
        viewModelScope.launch {
            authManager.upgradeToAgency(currentUser.value.agencyName.ifBlank { "وكالة سياحية" }, currentUser.value.agencyWilaya)
            _toastMessage.value = "تم التحويل إلى حساب وكالة سياحية"
        }
    }

    fun openTripDetails(trip: Trip) {
        _selectedTrip.value = trip
    }

    fun closeTripDetails() {
        _selectedTrip.value = null
    }

    fun openBookingDialog(trip: Trip) {
        _bookingTrip.value = trip
    }

    fun closeBookingDialog() {
        _bookingTrip.value = null
    }

    fun confirmBooking(trip: Trip, seats: Int, notes: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val result = repository.bookTrip(
                trip = trip,
                userName = user.fullName,
                userPhone = user.phone,
                userEmail = user.email,
                seatsCount = seats,
                specialNotes = notes
            )
            if (result.isSuccess) {
                _lastConfirmedBooking.value = result.getOrNull()
                _bookingTrip.value = null
                _toastMessage.value = "تم تأكيد حجزك بنجاح!"
                repository.refreshData()
            } else {
                _toastMessage.value = result.exceptionOrNull()?.message ?: "فشل الحجز"
            }
        }
    }

    fun closeBookingSuccessDialog() {
        _lastConfirmedBooking.value = null
    }

    fun cancelBooking(booking: Booking) {
        viewModelScope.launch {
            val result = repository.cancelBooking(booking)
            if (result.isSuccess) {
                _toastMessage.value = "تم إلغاء الحجز"
                repository.refreshData()
            } else {
                _toastMessage.value = "فشل إلغاء الحجز"
            }
        }
    }

    fun publishNewTrip(trip: Trip) {
        viewModelScope.launch {
            val result = repository.insertTrip(trip)
            if (result.isSuccess) {
                _toastMessage.value = "تم نشر الرحلة بنجاح!"
                repository.refreshData()
            } else {
                _toastMessage.value = "فشل نشر الرحلة"
            }
        }
    }

    fun publishNewTripWithImages(trip: Trip, imageUris: List<android.net.Uri>, context: android.content.Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isPublishingTrip.value = true
            try {
                var finalImageUris = trip.imageUris
                if (imageUris.isNotEmpty()) {
                    val uploadedUrls = mutableListOf<String>()
                    val contentResolver = context.contentResolver
                    for (uri in imageUris) {
                        try {
                            val uriString = uri.toString()
                            if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                                uploadedUrls.add(uriString)
                            } else {
                                val inputStream = contentResolver.openInputStream(uri)
                                val bytes = inputStream?.readBytes()
                                if (bytes != null && bytes.isNotEmpty()) {
                                    val fileName = "trip_${System.currentTimeMillis()}.jpg"
                                    val uploadResult = repository.uploadTripImage(bytes, fileName)
                                    if (uploadResult.isSuccess) {
                                        val url = uploadResult.getOrNull().orEmpty()
                                        if (url.isNotBlank()) {
                                            uploadedUrls.add(url)
                                        }
                                    }
                                }
                            }
                        } catch (eImg: Exception) {
                            Log.w("Ra7alatVM", "Failed to upload image $uri: ${eImg.message}")
                        }
                    }
                    if (uploadedUrls.isNotEmpty()) {
                        finalImageUris = uploadedUrls.joinToString(",")
                    }
                }

                val currentU = currentUser.value
                val finalBadge = when {
                    currentU.isGoldenVerified -> "GOLDEN_VERIFIED"
                    currentU.isAgency || trip.organizerType == "AGENCY" -> "AGENCY_BLUE"
                    else -> trip.organizerBadge
                }

                val nowIso = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }.format(java.util.Date())

                val tripToPublish = trip.copy(
                    createdAt = trip.createdAt?.ifBlank { null } ?: nowIso,
                    imageUris = finalImageUris,
                    organizerAvatarUrl = currentU.avatarUrl.ifBlank { trip.organizerAvatarUrl },
                    organizerEmail = currentU.email.ifBlank { trip.organizerEmail },
                    organizerBadge = finalBadge
                )

                val result = repository.insertTrip(tripToPublish)
                if (result.isSuccess) {
                    _toastMessage.value = "تم نشر الرحلة بنجاح!"
                    repository.refreshData()
                    onSuccess()
                } else {
                    _toastMessage.value = "فشل نشر الرحلة: ${result.exceptionOrNull()?.message ?: ""}"
                }
            } catch (e: Exception) {
                Log.e("Ra7alatVM", "Publish trip error: ${e.message}", e)
                _toastMessage.value = "حدث خطأ أثناء نشر الرحلة"
            } finally {
                _isPublishingTrip.value = false
            }
        }
    }

    fun startEditingTrip(trip: Trip) {
        _editingTrip.value = trip
    }

    fun closeEditingTrip() {
        _editingTrip.value = null
    }

    fun saveEditedTrip(updatedTrip: Trip) {
        viewModelScope.launch {
            val result = repository.updateTrip(updatedTrip)
            if (result.isSuccess) {
                _editingTrip.value = null
                _toastMessage.value = "تم حفظ التعديلات بنجاح"
                repository.refreshData()
            } else {
                _toastMessage.value = "فشل حفظ التعديلات"
            }
        }
    }

    fun cancelTripByOrganizer(trip: Trip) {
        viewModelScope.launch {
            val result = repository.cancelTripByOrganizer(trip.id)
            if (result.isSuccess) {
                _toastMessage.value = "تم إلغاء الرحلة"
                repository.refreshData()
            } else {
                _toastMessage.value = "فشل إلغاء الرحلة"
            }
        }
    }

    fun deleteTripByOrganizer(trip: Trip) {
        viewModelScope.launch {
            val result = repository.deleteTripByOrganizer(trip.id)
            if (result.isSuccess) {
                _toastMessage.value = "تم حذف الرحلة"
                repository.refreshData()
            } else {
                _toastMessage.value = "فشل حذف الرحلة"
            }
        }
    }

    fun toggleFavorite(trip: Trip) {
        viewModelScope.launch {
            repository.toggleFavorite(trip.id)
        }
    }

    fun openTripChat(trip: Trip) {
        _activeChatTrip.value = trip
        loadTripMessages(trip.id.toString())
    }

    fun closeTripChat() {
        _activeChatTrip.value = null
        _chatMessages.value = emptyList()
    }

    fun loadTripMessages(tripId: String) {
        viewModelScope.launch {
            val msgs = repository.getTripMessages(tripId)
            _chatMessages.value = msgs
        }
    }

    fun sendChatMessage(text: String, imageBytes: ByteArray? = null, imageName: String? = null) {
        val trip = _activeChatTrip.value ?: return
        viewModelScope.launch {
            val user = currentUser.value
            var imageUrl: String? = null
            if (imageBytes != null && imageName != null) {
                val uploadRes = repository.uploadChatImage(imageBytes, imageName)
                if (uploadRes.isSuccess) {
                    imageUrl = uploadRes.getOrNull()
                }
            }
            val msg = TripMessage(
                id = java.util.UUID.randomUUID().toString(),
                tripId = trip.id.toString(),
                senderId = user.id,
                senderName = user.fullName.ifBlank { "مشارك في الرحلة" },
                senderPhone = user.phone,
                text = text,
                imageUrl = imageUrl,
                isOrganizer = user.isOwnerOf(trip),
                createdAt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            )
            val result = repository.sendTripMessage(msg)
            if (result.isSuccess) {
                loadTripMessages(trip.id.toString())
            }
        }
    }

    fun addTripReview(tripId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val review = TripReview(
                id = java.util.UUID.randomUUID().toString(),
                tripId = tripId.toString(),
                userId = user.id,
                userName = user.fullName.ifBlank { "مسافر" },
                userAvatar = user.avatarUrl,
                rating = rating,
                comment = comment,
                createdAt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            )
            val result = repository.addTripReview(review)
            if (result.isSuccess) {
                _toastMessage.value = "تمت إضافة تقييمك بنجاح!"
            } else {
                _toastMessage.value = "فشل إضافة التقييم"
            }
        }
    }

    fun openOrganizerProfile(organizerName: String, fallbackPhone: String, fallbackWilaya: String, organizerEmail: String) {
        viewModelScope.launch {
            val profile = repository.fetchOrganizerProfile(organizerName, organizerEmail)
            _selectedOrganizerProfile.value = profile ?: UserProfile(
                fullName = organizerName,
                agencyName = organizerName,
                phone = fallbackPhone,
                agencyWilaya = fallbackWilaya,
                email = organizerEmail,
                role = "AGENCY"
            )
        }
    }

    private val _followersList = MutableStateFlow<List<FollowerRecord>>(emptyList())
    val followersList: StateFlow<List<FollowerRecord>> = _followersList.asStateFlow()

    private val _isLoadingFollowers = MutableStateFlow(false)
    val isLoadingFollowers: StateFlow<Boolean> = _isLoadingFollowers.asStateFlow()

    private val _showFollowersDialog = MutableStateFlow(false)
    val showFollowersDialog: StateFlow<Boolean> = _showFollowersDialog.asStateFlow()

    private val _isFollowingDialogMode = MutableStateFlow(false)
    val isFollowingDialogMode: StateFlow<Boolean> = _isFollowingDialogMode.asStateFlow()

    private val _followersDialogOrganizer = MutableStateFlow<UserProfile?>(null)
    val followersDialogOrganizer: StateFlow<UserProfile?> = _followersDialogOrganizer.asStateFlow()

    fun openFollowersList(organizer: UserProfile) {
        _isFollowingDialogMode.value = false
        _followersDialogOrganizer.value = organizer
        _showFollowersDialog.value = true
        viewModelScope.launch {
            _isLoadingFollowers.value = true
            val list = repository.getFollowers(organizer.email, organizer.agencyName.ifBlank { organizer.fullName })
            _followersList.value = list
            _isLoadingFollowers.value = false
            if (organizer.email.equals(currentUser.value.email, ignoreCase = true)) {
                authManager.updateFollowCounts(list.size, currentUser.value.followingCount)
            }
        }
    }

    fun openFollowingList(user: UserProfile) {
        _isFollowingDialogMode.value = true
        _followersDialogOrganizer.value = user
        _showFollowersDialog.value = true
        viewModelScope.launch {
            _isLoadingFollowers.value = true
            val list = repository.getFollowing(user.email)
            _followersList.value = list
            _isLoadingFollowers.value = false
            if (user.email.equals(currentUser.value.email, ignoreCase = true)) {
                authManager.updateFollowCounts(currentUser.value.followersCount, list.size)
            }
        }
    }

    fun refreshUserFollowCounts() {
        viewModelScope.launch {
            val user = currentUser.value
            if (user.isLoggedIn || user.email.isNotBlank()) {
                val followers = repository.getFollowers(user.email, user.agencyName.ifBlank { user.fullName })
                val following = repository.getFollowing(user.email)
                authManager.updateFollowCounts(followers.size, following.size)
            }
        }
    }

    fun closeFollowersList() {
        _showFollowersDialog.value = false
        _isFollowingDialogMode.value = false
        _followersDialogOrganizer.value = null
        _followersList.value = emptyList()
    }

    fun switchFollowersTab(isFollowing: Boolean, targetUser: UserProfile? = null) {
        val user = targetUser ?: _followersDialogOrganizer.value ?: currentUser.value
        if (isFollowing) {
            openFollowingList(user)
        } else {
            openFollowersList(user)
        }
    }

    fun unfollowFromFollowingList(follower: FollowerRecord) {
        viewModelScope.launch {
            val user = currentUser.value
            val orgEmail = follower.organizerEmail
            val orgName = follower.organizerName.ifBlank { "وكالة معتمدة" }
            val result = repository.toggleFollowOrganizer(user, orgEmail, orgName, 0)
            if (result.isSuccess) {
                // Immediately remove from currently displayed list
                _followersList.value = _followersList.value.filterNot { item ->
                    (orgEmail.isNotBlank() && item.organizerEmail.equals(orgEmail, ignoreCase = true)) ||
                    (orgName.isNotBlank() && item.organizerName.equals(orgName, ignoreCase = true))
                }
                refreshUserFollowCounts()
                _toastMessage.value = if (currentLanguage.value == AppLanguage.ARABIC) "تم إلغاء الاشتراك بنجاح" else "Abonnement annulé avec succès"
            }
        }
    }

    fun toggleFollowersPrivacy(showPublicly: Boolean) {
        viewModelScope.launch {
            val user = currentUser.value
            repository.toggleFollowersPrivacy(user.email, showPublicly)
            val notes = if (showPublicly) "show_followers:true" else "hide_followers:true"
            val updated = user.copy(verificationNotes = notes)
            authManager.updateProfile(updated)
            _toastMessage.value = if (showPublicly) "تم تفعيل إظهار المتابعين للعامة" else "تم إخفاء قائمة المتابعين عن الزوار"
        }
    }

    fun toggleFollowOrganizer(organizerEmail: String, organizerName: String) {
        viewModelScope.launch {
            val currentProfile = _selectedOrganizerProfile.value ?: return@launch
            val follower = currentUser.value
            val result = repository.toggleFollowOrganizer(follower, organizerEmail, organizerName, currentProfile.followersCount)
            if (result.isSuccess) {
                val (isFollowing, newCount) = result.getOrThrow()
                _selectedOrganizerProfile.value = currentProfile.copy(followersCount = newCount)
                _toastMessage.value = if (isFollowing) "تمت المتابعة بنجاح 👍" else "تم إلغاء المتابعة"
                refreshUserFollowCounts()
            }
        }
    }

    fun isFollowingOrganizer(organizerEmail: String, organizerName: String): Boolean {
        return repository.isFollowingOrganizer(organizerEmail, organizerName)
    }

    fun closeOrganizerProfile() {
        _selectedOrganizerProfile.value = null
    }

    fun updateNotificationPreferences(prefs: NotificationPreferences) {
        viewModelScope.launch {
            repository.updateNotificationPreferences(prefs)
            _toastMessage.value = "تم حفظ إعدادات الإشعارات"
        }
    }

    fun testNotificationPreferences(prefs: NotificationPreferences) {
        viewModelScope.launch {
            com.example.util.Ra7alatNotificationManager.sendTestNotification(getApplication(), prefs)
            val dep = if (prefs.departureWilayas.isEmpty()) "كل الولايات" else prefs.departureWilayas.joinToString("، ")
            val type = if (prefs.tripTypes.isEmpty()) "كافة الأنواع" else prefs.tripTypes.joinToString("، ")
            val budget = if (prefs.maxPrice == null || prefs.maxPrice <= 0.0) "أي ميزانية" else "حتى ${prefs.maxPrice.toInt()} دج"
            _toastMessage.value = "🔔 تم إرسال إشعار تجريبي يطابق تفضيلاتك ($dep • $type • $budget)"
        }
    }

    fun sendTestNotification() {
        testNotificationPreferences(notificationPreferences.value)
    }

    fun checkForAppUpdates(isManual: Boolean = false) {
        viewModelScope.launch {
            repository.checkForUpdates()
            if (isManual) {
                _toastMessage.value = "أنت تستخدم أحدث إصدار من تطبيق رحلات الجزائر"
            }
        }
    }

    fun dismissOptionalUpdate() {
        _updateState.value = null
    }

    fun simulateForceUpdate(enable: Boolean) {
        if (enable) {
            _updateState.value = AppUpdateInfo(isForceUpdate = true)
        } else {
            _updateState.value = null
        }
    }

    fun handleDeepLink(tripId: Long) {
        viewModelScope.launch {
            val trip = allTrips.value.firstOrNull { it.id == tripId }
            if (trip != null) {
                _selectedTrip.value = trip
            }
        }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            repository.refreshData()
            _toastMessage.value = "تم تحديث البيانات"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
