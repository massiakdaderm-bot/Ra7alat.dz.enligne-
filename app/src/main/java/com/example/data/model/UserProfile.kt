package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserProfile(
    val id: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "INDIVIDUAL", // "INDIVIDUAL" or "AGENCY"
    @SerialName("agency_name")
    val agencyName: String = "",
    @SerialName("agency_wilaya")
    val agencyWilaya: String = "",
    val bio: String = "",
    @SerialName("avatar_url")
    val avatarUrl: String = "",
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("rating")
    val rating: Double = 0.0,
    @SerialName("review_count")
    val reviewCount: Int = 0,
    @SerialName("is_verified")
    val isVerified: Boolean = false,
    @Transient
    val isGoldenVerifiedColumn: Boolean = false,
    @SerialName("verification_badge")
    val verificationBadge: String = "", // "GOLDEN_VERIFIED"
    @Transient val isLoggedIn: Boolean = false,
    @SerialName("is_verified_agency")
    val isVerifiedAgency: Boolean = false,
    @SerialName("verification_document_url")
    val verificationDocumentUrl: String = "",
    @SerialName("verification_notes")
    val verificationNotes: String = "",
    @Transient
    val followersCount: Int = 0,
    @Transient
    val followingCount: Int = 0
) {
    val isAgency: Boolean get() = role.equals("AGENCY", ignoreCase = true) || agencyName.isNotBlank() || isVerifiedAgency || isVerified

    val computedFollowersCount: Int
        get() {
            if (followersCount > 0) return followersCount
            val match = Regex("followers:(\\d+)").find(verificationNotes)
            if (match != null) {
                return match.groupValues[1].toIntOrNull() ?: 0
            }
            return 0
        }

    val computedFollowingCount: Int
        get() {
            if (followingCount > 0) return followingCount
            val match = Regex("following:(\\d+)").find(verificationNotes)
            if (match != null) {
                return match.groupValues[1].toIntOrNull() ?: 0
            }
            return 0
        }

    val showFollowersPublicly: Boolean
        get() = !verificationNotes.contains("hide_followers:true", ignoreCase = true) &&
                !verificationNotes.contains("show_followers:false", ignoreCase = true)

    val displayNameAr: String get() = if (role == "AGENCY" && agencyName.isNotBlank()) agencyName else if (fullName.isNotBlank()) fullName else "مستخدم رحلات"
    val displayNameFr: String get() = if (role == "AGENCY" && agencyName.isNotBlank()) agencyName else if (fullName.isNotBlank()) fullName else "Utilisateur Ra7alat"
    val verificationStatus: String get() = if (isVerified || isVerifiedAgency || isGoldenVerifiedColumn || isGoldenVerified) "APPROVED" else "PENDING"

    // Golden Verified Badge: Triggered if is_verified or is_verified_agency is true, or verification_badge is GOLDEN_VERIFIED
    val isGoldenVerified: Boolean
        get() = isVerified || isVerifiedAgency || verificationBadge.equals("GOLDEN_VERIFIED", ignoreCase = true) || isGoldenVerifiedColumn || verificationNotes.contains("gold", ignoreCase = true)

    // Blue Agency Badge: Strictly for AGENCY accounts that are not gold-verified
    val isAgencyBlueVerified: Boolean
        get() = isAgency && !isGoldenVerified

    fun isOwnerOf(trip: Trip): Boolean {
        val userEmail = email.trim().lowercase()
        val tripEmail = trip.organizerEmail.trim().lowercase()
        if (userEmail.isNotBlank()) {
            if (tripEmail.isNotBlank()) {
                return userEmail == tripEmail
            }
            if (role == "AGENCY" && agencyName.isNotBlank() && !agencyName.equals("وكالة سياحية", ignoreCase = true)) {
                return trip.organizerName.trim().equals(agencyName.trim(), ignoreCase = true)
            }
            if (phone.isNotBlank() && phone != "0550123456" && trip.organizerPhone.isNotBlank()) {
                return trip.organizerPhone.trim() == phone.trim()
            }
            return false
        }
        if (id.isNotBlank() && tripEmail.isNotBlank()) {
            return tripEmail.contains(id.trim(), ignoreCase = true)
        }
        return false
    }
}
