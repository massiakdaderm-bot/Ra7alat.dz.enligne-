package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.AppDatabase
import com.example.data.SupabaseNetwork
import com.example.data.model.Booking
import com.example.data.model.FollowerInsertDto
import com.example.data.model.FollowerRecord
import com.example.data.model.NotificationPreferences
import com.example.data.model.Trip
import com.example.data.model.TripInsertDto
import com.example.data.model.TripMessage
import com.example.data.model.TripReview
import com.example.data.model.UserProfile
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Serializable
data class FavoriteItem(
    val id: Long? = null,
    @SerialName("user_email") val userEmail: String = "",
    @SerialName("trip_id") val tripId: Long = 0L
)

class TripRepository(private val context: Context) {

    private val tripDao = AppDatabase.getInstance(context).tripDao()
    private val bookingDao = AppDatabase.getInstance(context).bookingDao()
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("ra7alat_repo_prefs", Context.MODE_PRIVATE)

    private var currentUserKey: String = "guest"
    private var currentUserEmail: String = ""

    val allTrips: Flow<List<Trip>> = tripDao.getAllTrips()
    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings()
    val favoriteTrips: Flow<List<Trip>> = tripDao.getFavoriteTrips()

    private val _allReviews = MutableStateFlow<List<TripReview>>(emptyList())
    val allReviews: Flow<List<TripReview>> = _allReviews.asStateFlow()

    private val _organizerAvatars = MutableStateFlow<Map<String, String>>(emptyMap())
    val organizerAvatars: StateFlow<Map<String, String>> = _organizerAvatars.asStateFlow()

    private val _notificationPrefs = MutableStateFlow(NotificationPreferences())
    val notificationPreferences: Flow<NotificationPreferences> = _notificationPrefs.asStateFlow()

    init {
        try {
            val savedJson = sharedPrefs.getString("cached_notification_prefs", null)
            if (savedJson != null) {
                _notificationPrefs.value = kotlinx.serialization.json.Json.decodeFromString<NotificationPreferences>(savedJson)
            }
        } catch (ignored: Exception) {}
    }

    fun setCurrentUser(userEmail: String?, userId: String?) {
        currentUserEmail = userEmail.orEmpty()
        currentUserKey = if (!userEmail.isNullOrBlank()) userEmail.trim().lowercase() else userId.orEmpty().ifBlank { "guest" }
    }

    suspend fun refreshData() = withContext(Dispatchers.IO) {
        val hasPurgedSamples = sharedPrefs.getBoolean("purged_sample_trips_online_v1", false)
        if (!hasPurgedSamples) {
            tripDao.deleteAllTrips()
            sharedPrefs.edit().putBoolean("purged_sample_trips_online_v1", true).apply()
        }

        val existingTripIds = try {
            tripDao.getAllTripsDirect().map { it.id }.toSet()
        } catch (e: Exception) {
            emptySet()
        }

        try {
            val remoteTrips = SupabaseNetwork.client.from("trips")
                .select(columns = Columns.ALL) {
                    order("created_at", Order.DESCENDING)
                }.decodeList<Trip>()

            // Check if any new trip matches user notification preferences!
            if (existingTripIds.isNotEmpty()) {
                val newTrips = remoteTrips.filter { it.id !in existingTripIds }
                val currentPrefs = _notificationPrefs.value
                newTrips.forEach { trip ->
                    if (com.example.util.Ra7alatNotificationManager.matchesPreferences(trip, currentPrefs, currentUserEmail)) {
                        com.example.util.Ra7alatNotificationManager.sendTripNotification(context, trip)
                    }
                }
            }

            val userFavTripIds = mutableSetOf<Long>()
            if (currentUserEmail.isNotBlank()) {
                try {
                    val favs = SupabaseNetwork.client.from("favorites")
                        .select(columns = Columns.ALL) {
                            filter {
                                eq("user_email", currentUserEmail.trim().lowercase())
                            }
                        }.decodeList<FavoriteItem>()
                    userFavTripIds.addAll(favs.map { it.tripId })
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load favorites from Supabase: ${e.message}")
                }
            }

            val syncedTrips = remoteTrips.map { trip ->
                if (userFavTripIds.contains(trip.id)) trip.copy(isFavorite = true) else trip
            }

            tripDao.deleteAllTrips()
            if (syncedTrips.isNotEmpty()) {
                tripDao.insertTrips(syncedTrips)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to refresh trips from Supabase: ${e.message}")
        }

        try {
            val remoteBookings = SupabaseNetwork.client.from("bookings")
                .select(columns = Columns.ALL) {
                    order("booking_date", Order.DESCENDING)
                }.decodeList<Booking>()

            if (remoteBookings.isNotEmpty()) {
                bookingDao.insertBookings(remoteBookings)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to refresh bookings from Supabase: ${e.message}")
        }

        try {
            val remoteReviews = SupabaseNetwork.client.from("reviews")
                .select(columns = Columns.ALL) {
                    order("created_at", Order.DESCENDING)
                }.decodeList<TripReview>()
            _allReviews.value = remoteReviews
        } catch (e: Exception) {
            Log.w(TAG, "Failed to refresh reviews: ${e.message}")
        }
    }

    suspend fun refreshTrips() = refreshData()
    suspend fun refreshBookings() = refreshData()

    suspend fun insertTrip(trip: Trip): Result<Trip> = withContext(Dispatchers.IO) {
        try {
            val insertDto = trip.toInsertDto()
            var createdTrip: Trip? = null
            try {
                val insertedList = SupabaseNetwork.client.from("trips")
                    .insert(insertDto) {
                        select()
                    }.decodeList<Trip>()
                if (insertedList.isNotEmpty()) {
                    createdTrip = insertedList.first()
                    Log.d(TAG, "Trip inserted to Supabase successfully with id: ${createdTrip.id}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert trip to Supabase: ${e.message}", e)
            }

            val finalTripToSave = createdTrip ?: trip
            val localId = tripDao.insertTrip(finalTripToSave)
            val finalSavedTrip = finalTripToSave.copy(id = if (finalTripToSave.id != 0L) finalTripToSave.id else localId)

            refreshTrips()
            Result.success(finalSavedTrip)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting trip: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateTrip(trip: Trip): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            tripDao.updateTrip(trip)
            try {
                SupabaseNetwork.client.from("trips").update(trip.toInsertDto()) {
                    filter { eq("id", trip.id) }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to update trip on Supabase: ${e.message}")
            }
            refreshTrips()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating trip: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTrip(trip: Trip): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            tripDao.deleteTripById(trip.id)
            try {
                SupabaseNetwork.client.from("trips").delete {
                    filter { eq("id", trip.id) }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete trip on Supabase: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting trip: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTripByOrganizer(tripId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            tripDao.deleteTripById(tripId)
            try {
                SupabaseNetwork.client.from("trips").delete {
                    filter { eq("id", tripId) }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete trip by organizer on Supabase: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting trip by organizer: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun cancelTripByOrganizer(tripId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val trip = tripDao.findTripById(tripId)
            if (trip != null) {
                val updated = trip.copy(
                    availableSeats = 0,
                    status = "CANCELLED",
                    description = "[ملغاة من المنظم] " + trip.description
                )
                updateTrip(updated)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling trip: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(id: Long, specificEmail: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val trip = tripDao.findTripById(id)
            if (trip != null) {
                val newFav = !trip.isFavorite
                tripDao.toggleFavorite(id, newFav)
                val email = specificEmail ?: currentUserEmail
                if (email.isNotBlank()) {
                    try {
                        if (newFav) {
                            SupabaseNetwork.client.from("favorites").insert(
                                FavoriteItem(userEmail = email.trim().lowercase(), tripId = id)
                            )
                        } else {
                            SupabaseNetwork.client.from("favorites").delete {
                                filter {
                                    eq("user_email", email.trim().lowercase())
                                    eq("trip_id", id)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to sync favorite to Supabase: ${e.message}")
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling favorite: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(trip: Trip): Result<Unit> = toggleFavorite(trip.id)

    suspend fun bookTrip(
        trip: Trip,
        userName: String,
        userPhone: String,
        userEmail: String,
        seatsCount: Int,
        specialNotes: String
    ): Result<Booking> = withContext(Dispatchers.IO) {
        try {
            if (trip.availableSeats < seatsCount) {
                return@withContext Result.failure(Exception("عدد المقاعد المتاحة غير كافٍ"))
            }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val bookingRef = "DZ-" + (100000..999999).random()
            val booking = Booking(
                tripId = trip.id,
                tripTitle = trip.title,
                destinationWilaya = trip.destinationWilaya,
                destinationPlace = trip.destinationPlace,
                departureDate = trip.departureDate,
                organizerName = trip.organizerName,
                organizerPhone = trip.organizerPhone,
                userEmail = userEmail.ifBlank { "guest@ra7alat.dz" },
                userName = userName.ifBlank { "مسافر جزائري" },
                userPhone = userPhone.ifBlank { "0550123456" },
                seatsCount = seatsCount,
                totalPriceDzd = trip.priceDzd * seatsCount,
                bookingReference = bookingRef,
                bookingDate = dateFormat.format(Date()),
                status = "UPCOMING",
                specialNotes = specialNotes
            )
            val localBookingId = bookingDao.insertBooking(booking)
            val finalBooking = booking.copy(id = localBookingId)

            try {
                SupabaseNetwork.client.from("bookings").insert(finalBooking)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync booking to Supabase: ${e.message}")
            }

            val newAvailableSeats = (trip.availableSeats - seatsCount).coerceAtLeast(0)
            tripDao.updateAvailableSeats(trip.id, newAvailableSeats)
            try {
                SupabaseNetwork.client.from("trips")
                    .update(mapOf("available_seats" to newAvailableSeats)) {
                        filter { eq("id", trip.id) }
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to update remaining seats on Supabase: ${e.message}")
            }

            Result.success(finalBooking)
        } catch (e: Exception) {
            Log.e(TAG, "Error booking seats: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun cancelBooking(booking: Booking): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val wasActive = !booking.status.equals("CANCELLED", ignoreCase = true)
            val updated = booking.copy(status = "CANCELLED")
            bookingDao.updateBooking(updated)
            try {
                SupabaseNetwork.client.from("bookings").update(updated) {
                    filter { eq("id", booking.id) }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to cancel booking on Supabase: ${e.message}")
            }

            // Restore seats on the trip
            if (wasActive) {
                try {
                    val trip = tripDao.findTripById(booking.tripId)
                    if (trip != null) {
                        val newSeats = (trip.availableSeats + booking.seatsCount).coerceAtMost(trip.totalSeats)
                        val updatedTrip = trip.copy(availableSeats = newSeats)
                        tripDao.updateTrip(updatedTrip)
                        try {
                            SupabaseNetwork.client.from("trips").update(updatedTrip) {
                                filter { eq("id", trip.id) }
                            }
                        } catch (e2: Exception) {
                            Log.w(TAG, "Failed to update trip seats on Supabase: ${e2.message}")
                        }
                    }
                } catch (e3: Exception) {
                    Log.w(TAG, "Failed to restore seats locally: ${e3.message}")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling booking: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun uploadTripImage(bytes: ByteArray, fileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bucket = SupabaseNetwork.client.storage.from("avatars")
            val sanitized = fileName.filter { it.isLetterOrDigit() || it == '.' || it == '_' }.ifBlank { "trip_img.jpg" }
            val path = "trip_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}_$sanitized"
            bucket.upload(path, bytes) {
                upsert = true
            }
            val publicUrl = "https://abzxeetfxnejlurqixun.supabase.co/storage/v1/object/public/avatars/$path"
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading trip image: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getFollowing(userEmail: String): List<FollowerRecord> = withContext(Dispatchers.IO) {
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isBlank()) return@withContext emptyList()
        val localKey = "following_list_${cleanEmail}"
        val cachedJson = sharedPrefs.getString(localKey, null)
        val cachedList: List<FollowerRecord> = if (!cachedJson.isNullOrBlank()) {
            try {
                Json.decodeFromString(cachedJson)
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()

        try {
            val remote = SupabaseNetwork.client.from("followers")
                .select(columns = Columns.ALL) {
                    filter { eq("follower_email", cleanEmail) }
                }.decodeList<FollowerRecord>()

            if (remote.isNotEmpty()) {
                val cachedTrips = try { tripDao.getAllTripsDirect() } catch (_: Exception) { emptyList() }

                // Enrich each record with real organizer name and avatar if missing or generic
                val enriched = remote.map { record ->
                    val orgEmail = record.organizerEmail.trim().lowercase()
                    var resolvedName = record.organizerName.trim()
                    var resolvedAvatar = record.organizerAvatar.trim()

                    val isGeneric = resolvedName.isBlank() ||
                            resolvedName.equals("وكالة سياحية", ignoreCase = true) ||
                            resolvedName.equals("ra7alat", ignoreCase = true)

                    // 1. Try finding from cached trips
                    val tripOrg = cachedTrips.firstOrNull {
                        it.organizerEmail.trim().equals(orgEmail, ignoreCase = true) &&
                                it.organizerName.isNotBlank() &&
                                !it.organizerName.equals("وكالة سياحية", ignoreCase = true) &&
                                !it.organizerName.equals("ra7alat", ignoreCase = true)
                    }?.organizerName

                    if (tripOrg != null) {
                        resolvedName = tripOrg
                    }

                    // 2. If still generic or avatar is blank, resolve from profiles table
                    if ((isGeneric || resolvedAvatar.isBlank()) && orgEmail.isNotBlank()) {
                        try {
                            val orgProfile = SupabaseNetwork.client.from("profiles")
                                .select(columns = Columns.ALL) {
                                    filter { eq("email", orgEmail) }
                                }.decodeSingleOrNull<UserProfile>()

                            if (orgProfile != null) {
                                val profName = if (orgProfile.role == "AGENCY" && orgProfile.agencyName.isNotBlank() && !orgProfile.agencyName.equals("وكالة سياحية", ignoreCase = true)) {
                                    orgProfile.agencyName
                                } else if (orgProfile.fullName.isNotBlank() && !orgProfile.fullName.equals("وكالة سياحية", ignoreCase = true)) {
                                    orgProfile.fullName
                                } else {
                                    null
                                }
                                if (!profName.isNullOrBlank()) {
                                    resolvedName = profName
                                }
                                if (resolvedAvatar.isBlank() && orgProfile.avatarUrl.isNotBlank()) {
                                    resolvedAvatar = orgProfile.avatarUrl
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Could not resolve organizer profile in getFollowing: ${e.message}")
                        }
                    }

                    // 3. If still generic, derive from email name
                    if (resolvedName.isBlank() || resolvedName.equals("وكالة سياحية", ignoreCase = true) || resolvedName.equals("ra7alat", ignoreCase = true)) {
                        if (orgEmail.contains("@")) {
                            val prefix = orgEmail.substringBefore("@")
                            resolvedName = prefix.replace(".", " ").replace("_", " ")
                                .split(" ")
                                .filter { it.isNotBlank() }
                                .joinToString(" ") { part -> part.replaceFirstChar { char -> char.titlecase() } }
                        }
                    }

                    record.copy(
                        organizerName = resolvedName.ifBlank { "وكالة معتمدة" },
                        organizerAvatar = resolvedAvatar
                    )
                }

                val json = Json.encodeToString(enriched)
                sharedPrefs.edit().putString(localKey, json).apply()
                return@withContext enriched
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not fetch following from Supabase: ${e.message}")
        }
        cachedList
    }

    suspend fun getTripReviews(tripId: Long): List<TripReview> = withContext(Dispatchers.IO) {
        try {
            val reviews = SupabaseNetwork.client.from("reviews")
                .select(columns = Columns.ALL) {
                    filter { eq("trip_id", tripId.toString()) }
                    order("created_at", Order.DESCENDING)
                }.decodeList<TripReview>()
            reviews
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch reviews: ${e.message}")
            emptyList()
        }
    }

    suspend fun addTripReview(review: TripReview): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            SupabaseNetwork.client.from("reviews").insert(review)
            refreshData()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add review: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getTripMessages(tripId: String): List<TripMessage> = withContext(Dispatchers.IO) {
        try {
            val messages = SupabaseNetwork.client.from("messages")
                .select(columns = Columns.ALL) {
                    filter { eq("trip_id", tripId) }
                    order("created_at", Order.ASCENDING)
                }.decodeList<TripMessage>()
            messages
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch messages: ${e.message}")
            emptyList()
        }
    }

    suspend fun loadTripMessages(tripId: Long): List<TripMessage> = getTripMessages(tripId.toString())

    suspend fun sendTripMessage(message: TripMessage): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            SupabaseNetwork.client.from("messages").insert(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send trip message: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            SupabaseNetwork.client.from("profiles").upsert(profile)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user profile: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun fetchOrganizerProfile(name: String, email: String?): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email?.trim()?.lowercase() ?: ""
            val cleanName = name.trim()
            var profile: UserProfile? = null
            if (cleanEmail.isNotBlank()) {
                profile = SupabaseNetwork.client.from("profiles")
                    .select(columns = Columns.ALL) {
                        filter { eq("email", cleanEmail) }
                    }.decodeSingleOrNull<UserProfile>()
            }
            if (profile == null && cleanName.isNotBlank()) {
                profile = SupabaseNetwork.client.from("profiles")
                    .select(columns = Columns.ALL) {
                        filter { eq("agency_name", cleanName) }
                    }.decodeSingleOrNull<UserProfile>()
            }
            if (profile == null && cleanName.isNotBlank()) {
                profile = SupabaseNetwork.client.from("profiles")
                    .select(columns = Columns.ALL) {
                        filter { eq("full_name", cleanName) }
                    }.decodeSingleOrNull<UserProfile>()
            }

            val followers = getFollowers(cleanEmail, cleanName)
            if (profile != null) {
                profile.copy(followersCount = followers.size)
            } else if (cleanName.isNotBlank() || cleanEmail.isNotBlank()) {
                UserProfile(
                    fullName = cleanName.ifBlank { "وكالة سياحية" },
                    agencyName = cleanName,
                    email = cleanEmail,
                    role = "AGENCY",
                    followersCount = followers.size
                )
            } else null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch organizer profile: ${e.message}")
            null
        }
    }

    suspend fun getFollowers(organizerEmail: String, organizerName: String): List<FollowerRecord> = withContext(Dispatchers.IO) {
        val cleanEmail = organizerEmail.trim().lowercase()
        val cleanName = organizerName.trim()
        val localKey = "followers_list_${cleanEmail.ifBlank { cleanName.lowercase() }}"
        val cachedJson = sharedPrefs.getString(localKey, null)
        val cachedList = if (!cachedJson.isNullOrBlank()) {
            try {
                Json.decodeFromString<List<FollowerRecord>>(cachedJson)
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()

        try {
            val byEmail = if (cleanEmail.isNotBlank()) {
                SupabaseNetwork.client.from("followers")
                    .select(columns = Columns.ALL) {
                        filter { eq("organizer_email", cleanEmail) }
                    }.decodeList<FollowerRecord>()
            } else emptyList()

            val byName = if (cleanName.isNotBlank()) {
                SupabaseNetwork.client.from("followers")
                    .select(columns = Columns.ALL) {
                        filter { eq("organizer_name", cleanName) }
                    }.decodeList<FollowerRecord>()
            } else emptyList()

            val remoteFollowers = (byEmail + byName).distinctBy { it.followerEmail.lowercase() }

            if (remoteFollowers.isNotEmpty() || cachedList.isEmpty()) {
                val json = Json.encodeToString(remoteFollowers)
                sharedPrefs.edit().putString(localKey, json).apply()
                return@withContext remoteFollowers
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not fetch followers from Supabase: ${e.message}")
        }
        cachedList
    }

    suspend fun toggleFollowOrganizer(
        follower: UserProfile,
        organizerEmail: String,
        organizerName: String,
        currentFollowersCount: Int
    ): Result<Pair<Boolean, Int>> = withContext(Dispatchers.IO) {
        try {
            val cleanOrgEmail = organizerEmail.trim().lowercase()
            var cleanOrgName = organizerName.trim()
            var cleanOrgAvatar = ""
            val cleanFollowerEmail = follower.email.trim().lowercase().ifBlank { follower.id }

            // Ensure we have the real organizer name and avatar
            val isGenericName = cleanOrgName.isBlank() ||
                    cleanOrgName.equals("وكالة سياحية", ignoreCase = true) ||
                    cleanOrgName.equals("ra7alat", ignoreCase = true)

            // Try from cached trips
            val cachedTrips = try { tripDao.getAllTripsDirect() } catch (_: Exception) { emptyList() }
            val tripOrg = cachedTrips.firstOrNull {
                it.organizerEmail.trim().equals(cleanOrgEmail, ignoreCase = true) &&
                        it.organizerName.isNotBlank() &&
                        !it.organizerName.equals("وكالة سياحية", ignoreCase = true) &&
                        !it.organizerName.equals("ra7alat", ignoreCase = true)
            }
            if (tripOrg != null) {
                cleanOrgName = tripOrg.organizerName
            }

            // If still generic or avatar missing, try profiles
            if ((isGenericName || cleanOrgAvatar.isBlank()) && cleanOrgEmail.isNotBlank()) {
                try {
                    val orgProfile = SupabaseNetwork.client.from("profiles")
                        .select(columns = Columns.ALL) {
                            filter { eq("email", cleanOrgEmail) }
                        }.decodeSingleOrNull<UserProfile>()
                    if (orgProfile != null) {
                        if (isGenericName) {
                            val profName = if (orgProfile.role == "AGENCY" && orgProfile.agencyName.isNotBlank() && !orgProfile.agencyName.equals("وكالة سياحية", ignoreCase = true)) {
                                orgProfile.agencyName
                            } else if (orgProfile.fullName.isNotBlank() && !orgProfile.fullName.equals("وكالة سياحية", ignoreCase = true)) {
                                orgProfile.fullName
                            } else null
                            if (!profName.isNullOrBlank()) {
                                cleanOrgName = profName
                            }
                        }
                        if (orgProfile.avatarUrl.isNotBlank()) {
                            cleanOrgAvatar = orgProfile.avatarUrl
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error resolving profile in toggleFollow: ${e.message}")
                }
            }

            if (cleanOrgName.isBlank() || cleanOrgName.equals("وكالة سياحية", ignoreCase = true)) {
                if (cleanOrgEmail.contains("@")) {
                    cleanOrgName = cleanOrgEmail.substringBefore("@")
                        .replace(".", " ")
                        .replace("_", " ")
                        .split(" ")
                        .filter { it.isNotBlank() }
                        .joinToString(" ") { part -> part.replaceFirstChar { char -> char.titlecase() } }
                }
            }

            val followKey = "following_${cleanOrgEmail.ifBlank { cleanOrgName.lowercase() }}"
            val isCurrentlyFollowing = sharedPrefs.getBoolean(followKey, false)
            val newFollowState = !isCurrentlyFollowing

            sharedPrefs.edit().putBoolean(followKey, newFollowState).apply()

            if (newFollowState) {
                val insertDto = FollowerInsertDto(
                    followerEmail = cleanFollowerEmail,
                    followerName = follower.fullName.ifBlank { follower.displayNameAr },
                    followerAvatar = follower.avatarUrl,
                    organizerEmail = cleanOrgEmail,
                    organizerName = cleanOrgName.ifBlank { "وكالة معتمدة" },
                    organizerAvatar = cleanOrgAvatar
                )
                try {
                    SupabaseNetwork.client.from("followers").insert(insertDto)
                } catch (e: Exception) {
                    Log.w(TAG, "Insert to Supabase followers error: ${e.message}")
                }
            } else {
                try {
                    if (cleanOrgEmail.isNotBlank()) {
                        SupabaseNetwork.client.from("followers").delete {
                            filter {
                                eq("follower_email", cleanFollowerEmail)
                                eq("organizer_email", cleanOrgEmail)
                            }
                        }
                    }
                    if (cleanOrgName.isNotBlank()) {
                        SupabaseNetwork.client.from("followers").delete {
                            filter {
                                eq("follower_email", cleanFollowerEmail)
                                eq("organizer_name", cleanOrgName)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Delete from Supabase followers error: ${e.message}")
                }
            }

            // Immediately query Supabase for updated followers list
            val remoteFollowers = getFollowers(cleanOrgEmail, cleanOrgName)
            val actualCount = if (remoteFollowers.isNotEmpty() || !newFollowState) {
                remoteFollowers.size
            } else {
                if (newFollowState) (currentFollowersCount + 1) else (currentFollowersCount - 1).coerceAtLeast(0)
            }

            // Refresh following list for current user
            if (cleanFollowerEmail.isNotBlank()) {
                getFollowing(cleanFollowerEmail)
            }

            // Sync followers count to profiles table in Supabase
            try {
                if (cleanOrgEmail.isNotBlank()) {
                    SupabaseNetwork.client.from("profiles")
                        .update(mapOf("verification_notes" to "followers:$actualCount")) {
                            filter { eq("email", cleanOrgEmail) }
                        }
                } else if (cleanOrgName.isNotBlank()) {
                    SupabaseNetwork.client.from("profiles")
                        .update(mapOf("verification_notes" to "followers:$actualCount")) {
                            filter { eq("agency_name", cleanOrgName) }
                        }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to update followers in verification_notes: ${e.message}")
            }

            Result.success(Pair(newFollowState, actualCount))
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling follow: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun toggleFollowersPrivacy(userEmail: String, showPublicly: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = userEmail.trim().lowercase()
            val flag = if (showPublicly) "show_followers:true" else "hide_followers:true"
            sharedPrefs.edit().putBoolean("show_followers_publicly_$cleanEmail", showPublicly).apply()

            if (cleanEmail.isNotBlank()) {
                try {
                    SupabaseNetwork.client.from("profiles")
                        .update(mapOf("verification_notes" to flag)) {
                            filter { eq("email", cleanEmail) }
                        }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to update followers privacy in profiles: ${e.message}")
                }
            }
            Result.success(showPublicly)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isFollowingOrganizer(organizerEmail: String, organizerName: String): Boolean {
        val cleanEmail = organizerEmail.trim().lowercase()
        val followKey = "following_${cleanEmail.ifBlank { organizerName.trim().lowercase() }}"
        return sharedPrefs.getBoolean(followKey, false)
    }

    suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bucket = SupabaseNetwork.client.storage.from("avatars")
            val sanitized = fileName.filter { it.isLetterOrDigit() || it == '.' || it == '_' }.ifBlank { "avatar.jpg" }
            val path = "avatar_${System.currentTimeMillis()}_$sanitized"
            bucket.upload(path, imageBytes) {
                upsert = true
            }
            val publicUrl = "https://abzxeetfxnejlurqixun.supabase.co/storage/v1/object/public/avatars/$path"
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Avatar upload error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun uploadChatImage(imageBytes: ByteArray, fileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bucket = SupabaseNetwork.client.storage.from("chat_images")
            val path = "chat_${System.currentTimeMillis()}_$fileName"
            bucket.upload(path, imageBytes)
            val publicUrl = bucket.publicUrl(path)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Chat image upload error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateNotificationPreferences(prefs: NotificationPreferences): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _notificationPrefs.value = prefs
            sharedPrefs.edit().putString("cached_notification_prefs", kotlinx.serialization.json.Json.encodeToString(prefs)).apply()
            val prefsToSave = if (currentUserEmail.isNotBlank()) prefs.copy(userEmail = currentUserEmail.trim().lowercase()) else prefs
            SupabaseNetwork.client.from("notification_preferences").upsert(prefsToSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update notification preferences: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun dismissUpdate() {}
    fun simulateUpdate() {}
    suspend fun checkForUpdates() {}

    companion object {
        private const val TAG = "TripRepository"
    }
}
