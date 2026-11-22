package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Serializable
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    @SerialName("destination_wilaya")
    val destinationWilaya: String = "",
    @SerialName("destination_place")
    val destinationPlace: String = "",
    @SerialName("organizer_type")
    val organizerType: String = "INDIVIDUAL", // "AGENCY" or "INDIVIDUAL"
    @SerialName("organizer_name")
    val organizerName: String = "",
    @SerialName("organizer_phone")
    val organizerPhone: String = "",
    @SerialName("organizer_badge")
    val organizerBadge: String = "",
    @SerialName("trip_type")
    val tripType: String = "صحراوية 🏜️",
    @SerialName("transport_type")
    val transportType: String = "حافلة سياحية VIP 🚌",
    @SerialName("departure_wilaya")
    val departureWilaya: String = "16 - الجزائر العاصمة",
    @SerialName("departure_city")
    val departureCity: String = "الجزائر الوسطى",
    @SerialName("route_itinerary")
    val routeItinerary: String = "",
    @SerialName("departure_date")
    val departureDate: String = "قريباً",
    @SerialName("return_date")
    val returnDate: String = "",
    @SerialName("departure_time")
    val departureTime: String = "06:00 صباحاً",
    @SerialName("duration_days")
    val durationDays: Int = 1,
    @SerialName("total_seats")
    val totalSeats: Int = 20,
    @SerialName("available_seats")
    val availableSeats: Int = 20,
    @SerialName("price_dzd")
    val priceDzd: Double = 0.0,
    val description: String = "",
    val inclusions: String = "",
    @SerialName("image_res_name")
    val imageResName: String = "",
    @SerialName("image_uris")
    val imageUris: String = "",
    @SerialName("video_url")
    val videoUrl: String = "",
    @SerialName("google_maps_url")
    val googleMapsUrl: String = "",
    @SerialName("is_favorite")
    val isFavorite: Boolean = false,
    @SerialName("is_featured")
    val isFeatured: Boolean = false,
    val status: String = "AVAILABLE", // "AVAILABLE", "COMPLETED", "CANCELLED"
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("organizer_email")
    val organizerEmail: String = "",
    @Transient
    val organizerAvatarUrl: String = ""
) {
    val isOrganizerAgency: Boolean
        get() = organizerType.equals("AGENCY", ignoreCase = true)

    val isOrganizerGoldenVerified: Boolean
        get() = organizerBadge.contains("GOLDEN", ignoreCase = true) ||
                organizerBadge.contains("ذهبي", ignoreCase = true) ||
                organizerBadge.equals("GOLDEN_VERIFIED", ignoreCase = true)

    val isOrganizerBlueVerified: Boolean
        get() = (isOrganizerAgency || organizerBadge.contains("AGENCY", ignoreCase = true) || organizerBadge.contains("BLUE", ignoreCase = true)) && !isOrganizerGoldenVerified

    val allImages: List<String>
        get() {
            val list = mutableListOf<String>()
            if (imageUris.isNotBlank()) {
                list.addAll(imageUris.split(",").map { it.trim() }.filter { it.isNotBlank() })
            }
            if (list.isEmpty() && imageResName.isNotBlank()) {
                list.add(imageResName)
            }
            return list
        }

    fun isOngoing(): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dep = sdf.parse(departureDate) ?: return false
            val now = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val ret = if (returnDate.isNotBlank()) sdf.parse(returnDate) else dep
            val retDate = ret ?: dep

            !now.before(dep) && !now.after(retDate)
        } catch (_: Exception) {
            false
        }
    }

    fun isExpired(): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val target = if (returnDate.isNotBlank()) {
                sdf.parse(returnDate) ?: sdf.parse(departureDate)
            } else {
                sdf.parse(departureDate)
            } ?: return false

            val now = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time

            now.after(target) && !isOngoing()
        } catch (_: Exception) {
            false
        }
    }

    fun getRelativePublishTime(isAr: Boolean = true): String {
        val dateStr = createdAt
        val publishMillis: Long = if (!dateStr.isNullOrBlank()) {
            try {
                java.time.OffsetDateTime.parse(dateStr).toInstant().toEpochMilli()
            } catch (_: Exception) {
                try {
                    java.time.Instant.parse(dateStr).toEpochMilli()
                } catch (_: Exception) {
                    try {
                        val patterns = listOf(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX",
                            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                            "yyyy-MM-dd'T'HH:mm:ssX",
                            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                            "yyyy-MM-dd'T'HH:mm:ssZ",
                            "yyyy-MM-dd'T'HH:mm:ss",
                            "yyyy-MM-dd HH:mm:ss",
                            "yyyy-MM-dd"
                        )
                        var parsedDate: Date? = null
                        for (pattern in patterns) {
                            try {
                                val sdf = SimpleDateFormat(pattern, Locale.US)
                                parsedDate = sdf.parse(dateStr)
                                if (parsedDate != null) break
                            } catch (_: Exception) {}
                        }
                        parsedDate?.time ?: 0L
                    } catch (_: Exception) {
                        0L
                    }
                }
            }
        } else {
            0L
        }

        if (publishMillis <= 0L) {
            return if (isAr) "تم النشر الآن" else "Publié à l'instant"
        }

        val now = System.currentTimeMillis()
        val diffMillis = (now - publishMillis).coerceAtLeast(0L)
        val diffSec = diffMillis / 1000
        val diffMin = diffSec / 60
        val diffHours = diffMin / 60
        val diffDays = diffHours / 24
        val diffWeeks = diffDays / 7
        val diffMonths = diffDays / 30

        return if (isAr) {
            when {
                diffMin < 1 -> "تم النشر الآن"
                diffMin == 1L -> "تم النشر منذ دقيقة"
                diffMin == 2L -> "تم النشر منذ دقيقتين"
                diffMin in 3..10 -> "تم النشر منذ $diffMin دقائق"
                diffMin < 60 -> "تم النشر منذ $diffMin دقيقة"
                diffHours == 1L -> "تم النشر منذ ساعة"
                diffHours == 2L -> "تم النشر منذ ساعتين"
                diffHours in 3..10 -> "تم النشر منذ $diffHours ساعات"
                diffHours < 24 -> "تم النشر منذ $diffHours ساعة"
                diffDays == 1L -> "تم النشر منذ يوم"
                diffDays == 2L -> "تم النشر منذ يومين"
                diffDays in 3..10 -> "تم النشر منذ $diffDays أيام"
                diffDays < 7 -> "تم النشر منذ $diffDays يوم"
                diffWeeks == 1L -> "تم النشر منذ أسبوع"
                diffWeeks == 2L -> "تم النشر منذ أسبوعين"
                diffWeeks in 3..4 -> "تم النشر منذ $diffWeeks أسابيع"
                diffMonths == 1L -> "تم النشر منذ شهر"
                diffMonths == 2L -> "تم النشر منذ شهرين"
                diffMonths in 3..10 -> "تم النشر منذ $diffMonths أشهر"
                else -> "تم النشر منذ $diffDays يوم"
            }
        } else {
            when {
                diffMin < 1 -> "Publié à l'instant"
                diffMin == 1L -> "Publié il y a 1 min"
                diffMin < 60 -> "Publié il y a ${diffMin} min"
                diffHours == 1L -> "Publié il y a 1h"
                diffHours < 24 -> "Publié il y a ${diffHours}h"
                diffDays == 1L -> "Publié il y a 1 jour"
                diffDays < 7 -> "Publié il y a ${diffDays} jours"
                diffWeeks == 1L -> "Publié il y a 1 semaine"
                diffWeeks < 4 -> "Publié il y a ${diffWeeks} semaines"
                diffMonths == 1L -> "Publié il y a 1 mois"
                else -> "Publié il y a ${diffMonths} mois"
            }
        }
    }

    fun isBookingClosed(): Boolean {
        return isExpired() || isOngoing() || availableSeats <= 0
    }

    fun toInsertDto() = TripInsertDto(
        title = title,
        destinationWilaya = destinationWilaya,
        destinationPlace = destinationPlace,
        organizerType = organizerType,
        organizerName = organizerName,
        organizerPhone = organizerPhone,
        organizerBadge = organizerBadge,
        tripType = tripType,
        transportType = transportType,
        departureWilaya = departureWilaya,
        departureCity = departureCity,
        routeItinerary = routeItinerary,
        departureDate = departureDate,
        returnDate = returnDate,
        departureTime = departureTime,
        durationDays = durationDays,
        totalSeats = totalSeats,
        availableSeats = availableSeats,
        priceDzd = priceDzd,
        description = description,
        inclusions = inclusions,
        imageResName = imageResName,
        imageUris = imageUris,
        videoUrl = videoUrl,
        googleMapsUrl = googleMapsUrl,
        isFavorite = isFavorite,
        isFeatured = isFeatured,
        status = status,
        organizerEmail = organizerEmail
    )
}

@Serializable
data class TripInsertDto(
    val title: String = "",
    @SerialName("destination_wilaya")
    val destinationWilaya: String = "",
    @SerialName("destination_place")
    val destinationPlace: String = "",
    @SerialName("organizer_type")
    val organizerType: String = "INDIVIDUAL",
    @SerialName("organizer_name")
    val organizerName: String = "",
    @SerialName("organizer_phone")
    val organizerPhone: String = "",
    @SerialName("organizer_badge")
    val organizerBadge: String = "",
    @SerialName("trip_type")
    val tripType: String = "صحراوية 🏜️",
    @SerialName("transport_type")
    val transportType: String = "حافلة سياحية VIP 🚌",
    @SerialName("departure_wilaya")
    val departureWilaya: String = "16 - الجزائر العاصمة",
    @SerialName("departure_city")
    val departureCity: String = "الجزائر الوسطى",
    @SerialName("route_itinerary")
    val routeItinerary: String = "",
    @SerialName("departure_date")
    val departureDate: String = "قريباً",
    @SerialName("return_date")
    val returnDate: String = "",
    @SerialName("departure_time")
    val departureTime: String = "06:00 صباحاً",
    @SerialName("duration_days")
    val durationDays: Int = 1,
    @SerialName("total_seats")
    val totalSeats: Int = 20,
    @SerialName("available_seats")
    val availableSeats: Int = 20,
    @SerialName("price_dzd")
    val priceDzd: Double = 0.0,
    val description: String = "",
    val inclusions: String = "",
    @SerialName("image_res_name")
    val imageResName: String = "",
    @SerialName("image_uris")
    val imageUris: String = "",
    @SerialName("video_url")
    val videoUrl: String = "",
    @SerialName("google_maps_url")
    val googleMapsUrl: String = "",
    @SerialName("is_favorite")
    val isFavorite: Boolean = false,
    @SerialName("is_featured")
    val isFeatured: Boolean = false,
    val status: String = "AVAILABLE",
    @SerialName("organizer_email")
    val organizerEmail: String = ""
)
