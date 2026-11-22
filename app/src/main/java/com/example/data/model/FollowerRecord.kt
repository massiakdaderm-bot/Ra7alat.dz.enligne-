package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowerRecord(
    val id: Long = 0,
    @SerialName("follower_email")
    val followerEmail: String = "",
    @SerialName("follower_name")
    val followerName: String = "",
    @SerialName("follower_avatar")
    val followerAvatar: String = "",
    @SerialName("organizer_email")
    val organizerEmail: String = "",
    @SerialName("organizer_name")
    val organizerName: String = "",
    @SerialName("organizer_avatar")
    val organizerAvatar: String = "",
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class FollowerInsertDto(
    @SerialName("follower_email")
    val followerEmail: String = "",
    @SerialName("follower_name")
    val followerName: String = "",
    @SerialName("follower_avatar")
    val followerAvatar: String = "",
    @SerialName("organizer_email")
    val organizerEmail: String = "",
    @SerialName("organizer_name")
    val organizerName: String = "",
    @SerialName("organizer_avatar")
    val organizerAvatar: String = ""
)
