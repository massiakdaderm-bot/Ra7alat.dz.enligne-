package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripMessage(
    val id: String = "",
    @SerialName("trip_id")
    val tripId: String = "",
    @SerialName("sender_id")
    val senderId: String = "",
    @SerialName("sender_name")
    val senderName: String = "",
    @SerialName("sender_phone")
    val senderPhone: String = "",
    val text: String = "",
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("is_organizer")
    val isOrganizer: Boolean = false,
    @SerialName("created_at")
    val createdAt: String = ""
) {
    val userName: String get() = senderName
    val message: String get() = text
}
