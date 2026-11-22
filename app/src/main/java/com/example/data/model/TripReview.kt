package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripReview(
    val id: String = "",
    @SerialName("trip_id")
    val tripId: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("user_name")
    val userName: String = "",
    @SerialName("user_avatar")
    val userAvatar: String = "",
    val rating: Int = 5, // 1 to 5 stars
    val comment: String = "",
    @SerialName("created_at")
    val createdAt: String? = null
)
