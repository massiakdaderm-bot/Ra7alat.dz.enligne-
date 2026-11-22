package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferences(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("user_email")
    val userEmail: String = "",
    @SerialName("notifications_enabled")
    val notificationsEnabled: Boolean = true,
    @SerialName("departure_wilayas")
    val departureWilayas: List<String> = emptyList(),
    @SerialName("trip_types")
    val tripTypes: List<String> = emptyList(),
    @SerialName("max_price")
    val maxPrice: Double? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val notifyNewTrips: Boolean = true,
    val notifyDiscountsAndOffers: Boolean = true,
    val notifyTripUpdates: Boolean = true,
    val notifyOrganizersSubscribed: Boolean = true
)
