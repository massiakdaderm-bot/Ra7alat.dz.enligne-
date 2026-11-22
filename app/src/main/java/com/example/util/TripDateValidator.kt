package com.example.util

import com.example.data.model.Trip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TripDateValidator {
    private val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
        SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH),
        SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH),
        SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    )

    fun isTripExpired(trip: Trip): Boolean {
        val dateStr = trip.returnDate.ifBlank { trip.departureDate }.trim()
        if (dateStr.isBlank() || dateStr.contains("قريباً") || dateStr.contains("Bientôt", ignoreCase = true)) {
            return false
        }

        // Try parsing
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        for (format in dateFormats) {
            try {
                val parsedDate = format.parse(dateStr)
                if (parsedDate != null) {
                    return parsedDate.before(today)
                }
            } catch (_: Exception) {}
        }
        return false
    }
}
