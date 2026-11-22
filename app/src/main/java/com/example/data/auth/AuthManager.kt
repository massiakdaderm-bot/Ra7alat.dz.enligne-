package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.SupabaseNetwork
import com.example.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.util.UUID

class AuthManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow(loadLocalProfile())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    val currentUserId: String
        get() = _currentUser.value.id

    val currentUserToken: String?
        get() = prefs.getString(KEY_TOKEN, null)

    fun getLocalProfile(): UserProfile = _currentUser.value

    fun saveLocalProfile(profile: UserProfile) {
        val updated = profile.copy(isLoggedIn = true)
        saveUserToCache(updated)
        _currentUser.value = updated
    }

    fun clearLocalProfile() {
        prefs.edit().clear().apply()
        _currentUser.value = UserProfile(
            id = UUID.randomUUID().toString(),
            isLoggedIn = false
        )
    }

    private fun loadLocalProfile(): UserProfile {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) {
            return UserProfile(id = "", isLoggedIn = false)
        }
        val id = prefs.getString(KEY_USER_ID, null) ?: return UserProfile(id = "", isLoggedIn = false)
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val name = prefs.getString(KEY_USER_NAME, "") ?: ""
        val phone = prefs.getString(KEY_USER_PHONE, "") ?: ""
        val role = prefs.getString(KEY_USER_ROLE, "INDIVIDUAL") ?: "INDIVIDUAL"
        val agencyName = prefs.getString(KEY_AGENCY_NAME, "") ?: ""
        val agencyWilaya = prefs.getString(KEY_AGENCY_WILAYA, "") ?: ""
        val bio = prefs.getString(KEY_BIO, "") ?: ""
        val avatarUrl = prefs.getString(KEY_AVATAR_URL, "") ?: ""
        val isVerified = prefs.getBoolean(KEY_IS_VERIFIED, false)
        val isGoldenCol = prefs.getBoolean(KEY_IS_GOLDEN_VERIFIED_COL, false)
        val badge = prefs.getString(KEY_VERIFICATION_BADGE, "") ?: ""
        val isAgencyVerified = prefs.getBoolean(KEY_IS_VERIFIED_AGENCY, false)
        val followers = prefs.getInt(KEY_FOLLOWERS_COUNT, 0)
        val following = prefs.getInt(KEY_FOLLOWING_COUNT, 0)
        val verificationNotes = prefs.getString(KEY_VERIFICATION_NOTES, "") ?: ""

        return UserProfile(
            id = id,
            fullName = name,
            email = email,
            phone = phone,
            role = role,
            agencyName = agencyName,
            agencyWilaya = agencyWilaya,
            bio = bio,
            avatarUrl = avatarUrl,
            isVerified = isVerified,
            isGoldenVerifiedColumn = isGoldenCol,
            verificationBadge = badge,
            isVerifiedAgency = isAgencyVerified,
            verificationNotes = verificationNotes,
            followersCount = followers,
            followingCount = following,
            isLoggedIn = true
        )
    }

    suspend fun checkSession(): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = SupabaseNetwork.client.auth.currentUserOrNull()
            if (user != null) {
                val profile = loadLocalProfile().copy(isLoggedIn = true)
                _currentUser.value = profile
                syncRemoteProfile(profile.email)
                return@withContext true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Session check error: ${e.message}")
        }
        val current = _currentUser.value
        if (current.isLoggedIn && current.email.isNotBlank()) {
            syncRemoteProfile(current.email)
        }
        _currentUser.value.isLoggedIn
    }

    suspend fun syncRemoteProfile(email: String) = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) return@withContext
        try {
            val remote = SupabaseNetwork.client.from("profiles")
                .select(columns = Columns.ALL) {
                    filter { eq("email", cleanEmail) }
                }.decodeSingleOrNull<UserProfile>()
            if (remote != null) {
                val current = _currentUser.value
                val updated = remote.copy(
                    isLoggedIn = true,
                    followersCount = if (remote.followersCount > 0) remote.followersCount else current.followersCount,
                    followingCount = if (remote.followingCount > 0) remote.followingCount else current.followingCount
                )
                saveUserToCache(updated)
                _currentUser.value = updated
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync remote profile: ${e.message}")
        }
    }

    fun updateFollowCounts(followers: Int, following: Int) {
        val current = _currentUser.value
        val updated = current.copy(followersCount = followers, followingCount = following)
        _currentUser.value = updated
        saveUserToCache(updated)
    }

    suspend fun registerWithEmail(
        email: String,
        pass: String,
        fullName: String,
        phone: String,
        role: String,
        agencyName: String,
        agencyWilaya: String
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            val cleanPass = pass.trim()
            val mappedRole = if (role.equals("AGENCY", ignoreCase = true)) "AGENCY" else "INDIVIDUAL"

            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                return@withContext Result.failure(Exception("يرجى إدخال بريد إلكتروني صحيح"))
            }
            if (cleanPass.length < 6) {
                return@withContext Result.failure(Exception("كلمة المرور يجب ألا تقل عن 6 أحرف أو أرقام"))
            }

            var authUserId: String? = null
            var needLoginFallback = false

            try {
                SupabaseNetwork.client.auth.signUpWith(Email) {
                    this.email = cleanEmail
                    this.password = cleanPass
                }
                authUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id
            } catch (signUpEx: Exception) {
                Log.w(TAG, "Auth signUp note: ${signUpEx.message}")
                val err = signUpEx.message.orEmpty()
                if (err.contains("already", ignoreCase = true) || err.contains("exists", ignoreCase = true)) {
                    needLoginFallback = true
                } else if (err.contains("Password", ignoreCase = true) && err.contains("least", ignoreCase = true)) {
                    return@withContext Result.failure(Exception("كلمة المرور يجب ألا تقل عن 6 أحرف أو أرقام"))
                } else {
                    needLoginFallback = true
                }
            }

            if (needLoginFallback || authUserId == null) {
                try {
                    SupabaseNetwork.client.auth.signInWith(Email) {
                        this.email = cleanEmail
                        this.password = cleanPass
                    }
                    authUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id
                } catch (signInEx: Exception) {
                    Log.w(TAG, "Auth fallback signIn error: ${signInEx.message}")
                    if (signInEx.message?.contains("Invalid login", ignoreCase = true) == true ||
                        signInEx.message?.contains("invalid_grant", ignoreCase = true) == true) {
                        return@withContext Result.failure(Exception("البريد الإلكتروني مسجل مسبقاً، يرجى تسجيل الدخول"))
                    }
                }
            }

            val finalUserId = authUserId
                ?: SupabaseNetwork.client.auth.currentUserOrNull()?.id
                ?: UUID.randomUUID().toString()

            val userProfile = UserProfile(
                id = finalUserId,
                fullName = fullName.trim().ifBlank { cleanEmail.substringBefore("@") },
                email = cleanEmail,
                phone = phone.trim(),
                role = mappedRole,
                agencyName = if (mappedRole == "AGENCY") agencyName.trim() else "",
                agencyWilaya = if (mappedRole == "AGENCY") agencyWilaya.trim() else "",
                isLoggedIn = true
            )

            try {
                SupabaseNetwork.client.from("profiles").upsert(userProfile)
                Log.d(TAG, "Profile saved to Supabase successfully for: $cleanEmail")
            } catch (upsertEx: Exception) {
                Log.w(TAG, "profiles upsert warning: ${upsertEx.message}, trying update")
                try {
                    SupabaseNetwork.client.from("profiles").update(
                        mapOf(
                            "full_name" to userProfile.fullName,
                            "phone" to userProfile.phone,
                            "role" to userProfile.role,
                            "agency_name" to userProfile.agencyName,
                            "agency_wilaya" to userProfile.agencyWilaya
                        )
                    ) {
                        filter { eq("email", cleanEmail) }
                    }
                } catch (updateEx: Exception) {
                    Log.w(TAG, "profiles update error: ${updateEx.message}")
                }
            }

            saveUserToCache(userProfile)
            _currentUser.value = userProfile
            Result.success(userProfile)
        } catch (e: Exception) {
            Log.e(TAG, "Registration error: ${e.message}", e)
            val friendlyMsg = when {
                e.message?.contains("User already registered", ignoreCase = true) == true ->
                    "البريد الإلكتروني مسجل مسبقاً، يرجى تسجيل الدخول"
                e.message?.contains("Password should be at least", ignoreCase = true) == true ->
                    "كلمة المرور يجب ألا تقل عن 6 أحرف أو أرقام"
                else -> e.message ?: "حدث خطأ أثناء إنشاء الحساب"
            }
            Result.failure(Exception(friendlyMsg))
        }
    }

    suspend fun loginWithEmail(email: String, pass: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            val cleanPass = pass.trim()

            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                return@withContext Result.failure(Exception("يرجى إدخال بريد إلكتروني صحيح"))
            }
            if (cleanPass.isBlank()) {
                return@withContext Result.failure(Exception("يرجى إدخال كلمة المرور"))
            }

            try {
                SupabaseNetwork.client.auth.signInWith(Email) {
                    this.email = cleanEmail
                    this.password = cleanPass
                }
            } catch (e: Exception) {
                Log.e(TAG, "Auth signIn error: ${e.message}")
                val friendlyMsg = when {
                    e.message?.contains("Invalid login credentials", ignoreCase = true) == true ||
                    e.message?.contains("invalid_grant", ignoreCase = true) == true ->
                        "البريد الإلكتروني أو كلمة المرور غير صحيحة"
                    e.message?.contains("Email not confirmed", ignoreCase = true) == true ->
                        "يرجى تأكيد البريد الإلكتروني"
                    else -> "فشل تسجيل الدخول: ${e.message ?: "تأكد من صحة البيانات والاتصال"}"
                }
                return@withContext Result.failure(Exception(friendlyMsg))
            }

            val user = SupabaseNetwork.client.auth.currentUserOrNull()
            val userId = user?.id ?: UUID.randomUUID().toString()

            var userProfile = UserProfile(
                id = userId,
                email = cleanEmail,
                fullName = cleanEmail.substringBefore("@"),
                isLoggedIn = true
            )

            try {
                val remoteProfile = SupabaseNetwork.client.from("profiles")
                    .select(columns = Columns.ALL) {
                        filter { eq("email", cleanEmail) }
                    }.decodeSingleOrNull<UserProfile>()
                if (remoteProfile != null) {
                    userProfile = remoteProfile.copy(isLoggedIn = true)
                } else {
                    SupabaseNetwork.client.from("profiles").upsert(userProfile)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load remote profile: ${e.message}")
            }

            saveUserToCache(userProfile)
            _currentUser.value = userProfile
            Result.success(userProfile)
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.message}", e)
            val friendlyMsg = when {
                e.message?.contains("Invalid login credentials", ignoreCase = true) == true ->
                    "البريد الإلكتروني أو كلمة المرور غير صحيحة"
                else -> e.message ?: "فشل تسجيل الدخول، تأكد من البيانات والاتصال"
            }
            Result.failure(Exception(friendlyMsg))
        }
    }

    suspend fun sendPasswordResetEmail(
        email: String,
        redirectUrl: String? = "https://akdaderm-bot.github.io"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                return@withContext Result.failure(Exception("يرجى إدخال بريد إلكتروني صحيح"))
            }

            try {
                if (!redirectUrl.isNullOrBlank()) {
                    SupabaseNetwork.client.auth.resetPasswordForEmail(
                        email = cleanEmail,
                        redirectUrl = redirectUrl.trim()
                    )
                } else {
                    SupabaseNetwork.client.auth.resetPasswordForEmail(cleanEmail)
                }
            } catch (redirEx: Exception) {
                Log.w(TAG, "Reset with redirectUrl note: ${redirEx.message}, falling back to default reset")
                SupabaseNetwork.client.auth.resetPasswordForEmail(cleanEmail)
            }

            Log.d(TAG, "Password reset email dispatched for: $cleanEmail")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Reset password error: ${e.message}", e)
            val friendly = when {
                e.message?.contains("rate limit", ignoreCase = true) == true ->
                    "تم إرسال عدة طلبات مؤخراً. يرجى الانتظار بضع دقائق والمحاولة مجدداً."
                e.message?.contains("not found", ignoreCase = true) == true ->
                    "البريد الإلكتروني غير مسجل."
                else -> e.message ?: "فشل إرسال رابط استعادة كلمة المرور، يرجى المحاولة لاحقاً."
            }
            Result.failure(Exception(friendly))
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> = sendPasswordResetEmail(email)

    suspend fun signInWithGoogle(activityContext: Context, serverClientId: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val dummyProfile = loadLocalProfile().copy(isLoggedIn = true)
        saveUserToCache(dummyProfile)
        _currentUser.value = dummyProfile
        Result.success(dummyProfile)
    }

    suspend fun deleteAccount(userId: String, userEmail: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = userEmail.trim().lowercase()
            if (cleanEmail.isNotBlank()) {
                try {
                    SupabaseNetwork.client.from("profiles").delete {
                        filter { eq("email", cleanEmail) }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Delete profile by email warning: ${e.message}")
                }
                try {
                    SupabaseNetwork.client.from("bookings").delete {
                        filter { eq("user_email", cleanEmail) }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Delete user bookings warning: ${e.message}")
                }
                try {
                    SupabaseNetwork.client.from("trips").delete {
                        filter { eq("organizer_email", cleanEmail) }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Delete user trips warning: ${e.message}")
                }
                try {
                    SupabaseNetwork.client.from("notification_preferences").delete {
                        filter { eq("user_email", cleanEmail) }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Delete notification_preferences warning: ${e.message}")
                }
            }
            if (userId.isNotBlank()) {
                try {
                    SupabaseNetwork.client.from("profiles").delete {
                        filter { eq("id", userId) }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Delete profile by id warning: ${e.message}")
                }
            }
            signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Account deletion error: ${e.message}", e)
            signOut()
            Result.failure(e)
        }
    }

    suspend fun updateProfile(profile: UserProfile): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val updated = profile.copy(isLoggedIn = true)
            val cleanEmail = updated.email.trim().lowercase()
            val updateFields = mutableMapOf<String, Any>(
                "full_name" to updated.fullName,
                "phone" to updated.phone,
                "role" to updated.role,
                "agency_name" to updated.agencyName,
                "agency_wilaya" to updated.agencyWilaya,
                "bio" to updated.bio,
                "avatar_url" to updated.avatarUrl
            )
            if (updated.verificationNotes.isNotBlank()) {
                updateFields["verification_notes"] = updated.verificationNotes
            }
            if (updated.verificationBadge.isNotBlank()) {
                updateFields["verification_badge"] = updated.verificationBadge
            }

            var updatedRemote = false
            if (cleanEmail.isNotBlank()) {
                try {
                    SupabaseNetwork.client.from("profiles").update(updateFields) {
                        filter { eq("email", cleanEmail) }
                    }
                    updatedRemote = true
                } catch (eMail: Exception) {
                    Log.w(TAG, "Failed update profile by email: ${eMail.message}")
                }
            }
            if (!updatedRemote && updated.id.isNotBlank()) {
                try {
                    SupabaseNetwork.client.from("profiles").update(updateFields) {
                        filter { eq("id", updated.id) }
                    }
                    updatedRemote = true
                } catch (eId: Exception) {
                    Log.w(TAG, "Failed update profile by ID: ${eId.message}")
                }
            }
            if (!updatedRemote) {
                try {
                    SupabaseNetwork.client.from("profiles").upsert(updated)
                } catch (eUpsert: Exception) {
                    Log.w(TAG, "Failed upsert profile: ${eUpsert.message}")
                }
            }

            saveUserToCache(updated)
            _currentUser.value = updated
            Result.success(updated)
        } catch (e: Exception) {
            Log.e(TAG, "Update profile error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun upgradeToAgency(agencyName: String, agencyWilaya: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val current = _currentUser.value
        val upgraded = current.copy(
            role = "AGENCY",
            agencyName = agencyName.trim(),
            agencyWilaya = agencyWilaya.trim(),
            isVerified = current.isVerified,
            isVerifiedAgency = current.isVerifiedAgency,
            verificationBadge = current.verificationBadge
        )
        updateProfile(upgraded)
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            SupabaseNetwork.client.auth.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "SignOut error: ${e.message}")
        }
        clearLocalProfile()
    }

    suspend fun logout() = signOut()

    private fun saveUserToCache(user: UserProfile) {
        prefs.edit()
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_NAME, user.fullName)
            .putString(KEY_USER_PHONE, user.phone)
            .putString(KEY_USER_ROLE, user.role)
            .putString(KEY_AGENCY_NAME, user.agencyName)
            .putString(KEY_AGENCY_WILAYA, user.agencyWilaya)
            .putString(KEY_BIO, user.bio)
            .putString(KEY_AVATAR_URL, user.avatarUrl)
            .putBoolean(KEY_IS_VERIFIED, user.isVerified)
            .putBoolean(KEY_IS_GOLDEN_VERIFIED_COL, user.isGoldenVerifiedColumn)
            .putString(KEY_VERIFICATION_BADGE, user.verificationBadge)
            .putBoolean(KEY_IS_VERIFIED_AGENCY, user.isVerifiedAgency)
            .putString(KEY_VERIFICATION_NOTES, user.verificationNotes)
            .putInt(KEY_FOLLOWERS_COUNT, user.followersCount)
            .putInt(KEY_FOLLOWING_COUNT, user.followingCount)
            .putBoolean(KEY_IS_LOGGED_IN, user.isLoggedIn)
            .apply()
    }

    companion object {
        private const val TAG = "AuthManager"
        private const val PREFS_NAME = "ra7alat_auth_prefs"
        private const val KEY_TOKEN = "user_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_AGENCY_NAME = "agency_name"
        private const val KEY_AGENCY_WILAYA = "agency_wilaya"
        private const val KEY_BIO = "user_bio"
        private const val KEY_AVATAR_URL = "avatar_url"
        private const val KEY_IS_VERIFIED = "is_verified"
        private const val KEY_IS_GOLDEN_VERIFIED_COL = "is_golden_verified"
        private const val KEY_VERIFICATION_BADGE = "verification_badge"
        private const val KEY_IS_VERIFIED_AGENCY = "is_verified_agency"
        private const val KEY_VERIFICATION_NOTES = "verification_notes"
        private const val KEY_FOLLOWERS_COUNT = "followers_count"
        private const val KEY_FOLLOWING_COUNT = "following_count"
    }
}
