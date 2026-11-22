package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @SerialName("trip_id")
    val tripId: Long = 0,
    @SerialName("trip_title")
    val tripTitle: String = "",
    @SerialName("destination_wilaya")
    val destinationWilaya: String = "",
    @SerialName("destination_place")
    val destinationPlace: String = "",
    @SerialName("departure_date")
    val departureDate: String = "",
    @SerialName("organizer_name")
    val organizerName: String = "",
    @SerialName("organizer_phone")
    val organizerPhone: String = "",
    @SerialName("user_email")
    val userEmail: String = "",
    @SerialName("user_name")
    val userName: String = "",
    @SerialName("user_phone")
    val userPhone: String = "",
    @SerialName("seats_count")
    val seatsCount: Int = 1,
    @SerialName("total_price_dzd")
    val totalPriceDzd: Double = 0.0,
    @SerialName("booking_reference")
    val bookingReference: String = "",
    @SerialName("booking_date")
    val bookingDate: String? = null,
    val status: String = "UPCOMING", // "UPCOMING", "COMPLETED", "CANCELLED"
    @SerialName("special_notes")
    val specialNotes: String = ""
) {
    val passengerName: String get() = userName
    val passengerPhone: String get() = userPhone
    val destination: String get() = if (destinationPlace.isNotBlank()) "$destinationPlace ($destinationWilaya)" else destinationWilaya
    val startDate: String get() = departureDate
}
