package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Booking
import com.example.data.model.NotificationPreferences
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.ui.components.AppAvatarImage
import com.example.ui.components.AppLottieEmptyState
import com.example.ui.components.GoldenVerifiedBadge
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.LocalAppThemePalette
import com.example.util.AppLanguage

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    bookingsCount: Int,
    favoritesCount: Int,
    publishedTripsCount: Int,
    language: AppLanguage,
    currentPalette: AppThemePalette,
    notificationPrefs: NotificationPreferences,
    userTrips: List<Trip> = emptyList(),
    userBookings: List<Booking> = emptyList(),
    onTripClick: (Trip) -> Unit = {},
    onAddTrip: () -> Unit = {},
    onUpdateProfile: (name: String, phone: String, bio: String, agencyName: String, agencyWilaya: String, agencyAddress: String, cr: String) -> Unit,
    onUploadAvatar: (ByteArray, String) -> Unit,
    onUpdateNotifications: (NotificationPreferences) -> Unit,
    onOpenNotificationsDialog: () -> Unit,
    onOpenThemeSelector: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenVerificationDialog: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenAboutDialog: () -> Unit,
    onOpenShareApp: () -> Unit,
    onOpenRateApp: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onSwitchToAgency: () -> Unit,
    onRequireLogin: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onOpenDashboard: () -> Unit = {},
    onOpenFollowers: () -> Unit = {},
    onOpenFollowing: () -> Unit = {},
    onToggleFollowersPrivacy: (Boolean) -> Unit = {},
    isAr: Boolean = false,
    onSelectPalette: ((AppThemePalette) -> Unit)? = null
) {
    val context = LocalContext.current
    var showSettingsScreen by remember { mutableStateOf(false) }

    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Editable profile fields
    var editName by remember(userProfile.fullName) { mutableStateOf(userProfile.fullName) }
    var editPhone by remember(userProfile.phone) { mutableStateOf(userProfile.phone) }
    var editBio by remember(userProfile.bio) { mutableStateOf(userProfile.bio) }
    var editAgencyName by remember(userProfile.agencyName) { mutableStateOf(userProfile.agencyName) }
    var editAgencyWilaya by remember(userProfile.agencyWilaya) { mutableStateOf(userProfile.agencyWilaya) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    onUploadAvatar(bytes, "avatar_${System.currentTimeMillis()}.jpg")
                    Toast.makeText(context, if (isAr) "تم تحديث الصورة الشخصية بنجاح 📸" else "Photo de profil mise à jour 📸", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, if (isAr) "فشل تحميل الصورة" else "Erreur de chargement", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle Android system back gesture: return to Instagram profile view
    BackHandler(enabled = showSettingsScreen) {
        showSettingsScreen = false
    }

    Crossfade(targetState = showSettingsScreen, label = "ProfileScreenTransition") { inSettings ->
        if (inSettings) {
            // =========================================================================
            // SUB-SCREEN B: SETTINGS SCREEN (Exact match to Screenshots 1 & 2)
            // =========================================================================
            SettingsSubScreen(
                userProfile = userProfile,
                currentPalette = currentPalette,
                notificationPrefs = notificationPrefs,
                isAr = isAr,
                onBack = { showSettingsScreen = false },
                onEditProfileClick = { showEditProfileDialog = true },
                onUploadAvatarClick = { photoPickerLauncher.launch("image/*") },
                onOpenThemeSelector = onOpenThemeSelector,
                onSelectPalette = onSelectPalette,
                onToggleLanguage = onToggleLanguage,
                onOpenNotificationsDialog = onOpenNotificationsDialog,
                onUpdateNotifications = onUpdateNotifications,
                onOpenVerificationDialog = onOpenVerificationDialog,
                onOpenAboutDialog = onOpenAboutDialog,
                onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                onLogoutClick = { showLogoutConfirm = true },
                onDeleteAccountClick = { showDeleteConfirm = true }
            )
        } else {
            // =========================================================================
            // SUB-SCREEN A: INSTAGRAM PROFILE (Exact match to Screenshot 3)
            // =========================================================================
            InstagramProfileScreen(
                userProfile = userProfile,
                userTrips = userTrips,
                userBookings = userBookings,
                publishedTripsCount = publishedTripsCount,
                isAr = isAr,
                onOpenSettings = { showSettingsScreen = true },
                onAddTrip = onAddTrip,
                onTripClick = onTripClick,
                onEditProfileClick = { showEditProfileDialog = true },
                onUploadAvatarClick = { photoPickerLauncher.launch("image/*") },
                onShareProfileClick = onOpenShareApp,
                onOpenFollowers = onOpenFollowers,
                onOpenFollowing = onOpenFollowing,
                onOpenDashboard = onOpenDashboard
            )
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                title = {
                    Text(
                        text = if (isAr) "تعديل الملف الشخصي ✏️" else "Modifier le profil ✏️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text(if (isAr) "الاسم الكامل" else "Nom complet") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_name")
                        )

                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text(if (isAr) "رقم الهاتف" else "Téléphone") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_phone")
                        )

                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text(if (isAr) "النبذة التعريفية (Bio)" else "Biographie") },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_bio")
                        )

                        if (userProfile.isAgency) {
                            OutlinedTextField(
                                value = editAgencyName,
                                onValueChange = { editAgencyName = it },
                                label = { Text(if (isAr) "اسم الوكالة السياحية" else "Nom de l'agence") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_agency_name")
                            )

                            OutlinedTextField(
                                value = editAgencyWilaya,
                                onValueChange = { editAgencyWilaya = it },
                                label = { Text(if (isAr) "ولاية المقر" else "Wilaya du siège") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_agency_wilaya")
                            )
                        }

                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = LocalAppThemePalette.current.accentColor.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = LocalAppThemePalette.current.accentColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isAr) "تغيير الصورة الشخصية 📸" else "Changer la photo de profil 📸", color = LocalAppThemePalette.current.accentColor)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUpdateProfile(editName, editPhone, editBio, editAgencyName, editAgencyWilaya, "", "")
                            showEditProfileDialog = false
                            Toast.makeText(context, if (isAr) "تم حفظ التعديلات بنجاح ✅" else "Modifications enregistrées ✅", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LocalAppThemePalette.current.accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isAr) "حفظ التعديلات" else "Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfileDialog = false }) {
                        Text(if (isAr) "إلغاء" else "Annuler")
                    }
                }
            )
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirm = false },
                title = { Text(if (isAr) "تسجيل الخروج" else "Déconnexion", fontWeight = FontWeight.Bold) },
                text = { Text(if (isAr) "هل أنت متأكد من رغبتك في تسجيل الخروج من حسابك؟" else "Êtes-vous sûr de vouloir vous déconnecter ?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutConfirm = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isAr) "نعم، تسجيل الخروج" else "Oui, Déconnexion", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirm = false }) {
                        Text(if (isAr) "إلغاء" else "Annuler")
                    }
                }
            )
        }
    }

    // Delete Account Confirmation Dialog
    if (showDeleteConfirm) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text(if (isAr) "حذف الحساب نهائياً ⚠️" else "Supprimer le compte ⚠️", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444)) },
                text = { Text(if (isAr) "هذا الإجراء نهائي ولا يمكن التراجع عنه. سيتم حذف جميع بياناتك وحجوزاتك ورحلاتك المنشورة من خوادمنا." else "Cette action est irréversible. Toutes vos données seront définitivement effacées.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirm = false
                            onDeleteAccount()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isAr) "حذف الحساب الآن" else "Supprimer définitivement", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text(if (isAr) "تراجع" else "Annuler")
                    }
                }
            )
        }
    }
}

// =============================================================================
// SUB-SCREEN A: INSTAGRAM PROFILE VIEW (Matches Screenshot 3)
// =============================================================================
@Composable
private fun InstagramProfileScreen(
    userProfile: UserProfile,
    userTrips: List<Trip>,
    userBookings: List<Booking>,
    publishedTripsCount: Int,
    isAr: Boolean,
    onOpenSettings: () -> Unit,
    onAddTrip: () -> Unit,
    onTripClick: (Trip) -> Unit,
    onEditProfileClick: () -> Unit,
    onUploadAvatarClick: () -> Unit,
    onShareProfileClick: () -> Unit,
    onOpenFollowers: () -> Unit,
    onOpenFollowing: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    val palette = LocalAppThemePalette.current
    val accentColor = palette.accentColor
    val textPrimary = palette.textPrimary
    val textSecondary = palette.mutedTextColor
    val cardBg = palette.darkCardBg
    val borderCol = palette.darkBorderColor

    val handle = remember(userProfile.email, userProfile.agencyName, userProfile.fullName) {
        if (userProfile.agencyName.isNotBlank()) {
            userProfile.agencyName.replace(" ", "").lowercase()
        } else if (userProfile.email.isNotBlank()) {
            userProfile.email.substringBefore("@").lowercase()
        } else {
            userProfile.fullName.replace(" ", "").lowercase().ifBlank { "massiakkk" }
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Trips Grid, 1: Bookings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.darkCanvasBg)
            .testTag("instagram_profile_screen")
    ) {
        // 1. Top Instagram Bar: username handle ▾  |  +  ⚙️
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Username handle with drop arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onEditProfileClick() }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = handle,
                    color = textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "▾",
                    color = textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (userProfile.isGoldenVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    GoldenVerifiedBadge(compact = true, isAr = isAr)
                }
            }

            // Right icons: [+] create trip, and [⚙️] settings
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onAddTrip,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .testTag("profile_add_trip_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter un voyage",
                        tint = textPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .testTag("profile_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Scrollable Profile Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 2. Avatar + 3 Stats row (publications, followers, following) in ONE line
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Large Avatar with Story/Glow Ring & '+' badge
                    Box(
                        modifier = Modifier.size(86.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(86.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.5.dp,
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            accentColor,
                                            Color(0xFFFFB300),
                                            Color(0xFF0D9488),
                                            accentColor
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .padding(3.dp)
                                .clickable { onUploadAvatarClick() }
                        ) {
                            AppAvatarImage(
                                avatarUrl = userProfile.avatarUrl,
                                fullName = userProfile.fullName.ifBlank { userProfile.agencyName },
                                size = 80.dp
                            )
                        }

                        // Small camera/plus button badge on bottom-right of avatar
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                                .border(1.5.dp, palette.darkCanvasBg, CircleShape)
                                .clickable { onUploadAvatarClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Changer la photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right: 3 Stats in one horizontal row (matching Instagram)
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Publications (Trips)
                        InstagramStatColumn(
                            count = "${userTrips.size.coerceAtLeast(publishedTripsCount)}",
                            label = if (isAr) "منشورات" else "publications",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onClick = { selectedTab = 0 }
                        )

                        // 2. Followers (Abonnés)
                        InstagramStatColumn(
                            count = "${userProfile.computedFollowersCount}",
                            label = if (isAr) "متابعون" else "followers",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onClick = onOpenFollowers
                        )

                        // 3. Following (Abonnements)
                        InstagramStatColumn(
                            count = "${userProfile.computedFollowingCount}",
                            label = if (isAr) "اشتراكات" else "suivi(e)s",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onClick = onOpenFollowing
                        )
                    }
                }
            }

            // 3. Bio & Details Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    // Full Name + Badges
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (userProfile.role == "AGENCY" && userProfile.agencyName.isNotBlank()) {
                                userProfile.agencyName
                            } else if (userProfile.fullName.isNotBlank()) {
                                userProfile.fullName
                            } else {
                                if (isAr) "مستخدم رحلات الجزائر" else "Voyageur Ra7alat"
                            },
                            color = textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (userProfile.isGoldenVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            GoldenVerifiedBadge(compact = true, isAr = isAr)
                        }
                    }

                    // Role / Wilaya Category tag
                    Text(
                        text = if (userProfile.isAgency) {
                            if (isAr) "📍 ${userProfile.agencyWilaya.ifBlank { "الجزائر" }} • وكالة سياحية معتمدة 🏢"
                            else "📍 ${userProfile.agencyWilaya.ifBlank { "Algérie" }} • Agence de Tourisme 🏢"
                        } else {
                            if (isAr) "📍 الجزائر • مسافر ومستكشف 🧭" else "📍 Algérie • Voyageur Explorateur 🧭"
                        },
                        color = accentColor,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    // Bio Text
                    val bioText = userProfile.bio.ifBlank {
                        if (isAr) "📍 عاشق للسياحة واكتشاف جمال الجزائر عبر 58 ولاية 🇩🇿\n✨ تنظيم جولات وتخييم في الصحراء والجبال والشواطئ"
                        else "📍 Passionné de voyages et d'aventure à travers les 58 wilayas d'Algérie 🇩🇿\n✨ Organisation d'expéditions et circuits touristiques"
                    }
                    Text(
                        text = bioText,
                        color = textPrimary.copy(alpha = 0.95f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Website & Contact Tags (Pill chips)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Portfolio / Web Link
                        InstagramBioTag(
                            icon = Icons.Default.Link,
                            text = "ra7alat.dz/@$handle",
                            color = Color(0xFF60A5FA)
                        )

                        if (userProfile.phone.isNotBlank()) {
                            InstagramBioTag(
                                icon = Icons.Default.Phone,
                                text = userProfile.phone,
                                color = Color(0xFF34D399)
                            )
                        }

                        if (userProfile.email.isNotBlank()) {
                            InstagramBioTag(
                                icon = Icons.Default.Email,
                                text = userProfile.email,
                                color = Color(0xFFFBBF24)
                            )
                        }
                    }
                }
            }

            // 4. Action Buttons (Modifier le profil | Partager le profil)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Modifier le profil
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF1E2E25))
                            .border(1.dp, borderCol, RoundedCornerShape(8.dp))
                            .clickable { onEditProfileClick() }
                            .testTag("instagram_edit_profile_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAr) "تعديل الملف الشخصي" else "Modifier le profil",
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Partager le profil
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF1E2E25))
                            .border(1.dp, borderCol, RoundedCornerShape(8.dp))
                            .clickable { onShareProfileClick() }
                            .testTag("instagram_share_profile_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAr) "مشاركة الملف" else "Partager le profil",
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 5. "Votre tableau de bord" Shortcut Banner (Screenshot 3)
            if (userProfile.isAgency || publishedTripsCount > 0) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onOpenDashboard() }
                            .testTag("instagram_dashboard_banner"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (palette.isLight) Color(0xFFE2E8F0) else Color(0xFF16251D)
                        ),
                        border = BorderStroke(1.dp, borderCol)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isAr) "لوحة تحكم الوكالة والإحصائيات" else "Votre tableau de bord",
                                    color = textPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAr)
                                    "↗ ${(userTrips.size.coerceAtLeast(publishedTripsCount) * 120 + 45)} مشاهدة خلال آخر 30 يوماً • إدارة الحجوزات والمقاعد"
                                else
                                    "↗ ${(userTrips.size.coerceAtLeast(publishedTripsCount) * 120 + 45)} vues au cours des 30 derniers jours.",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 6. Instagram Tabs Row (▦ Publications | 🎟️ Réservations)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = palette.darkCanvasBg,
                    contentColor = accentColor,
                    indicator = { tabPositions ->
                        val currentPos = tabPositions.getOrNull(selectedTab)
                        if (currentPos != null) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(currentPos),
                                color = accentColor,
                                height = 2.dp
                            )
                        }
                    },
                    divider = {
                        HorizontalDivider(color = borderCol, thickness = 0.8.dp)
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Publications",
                                tint = if (selectedTab == 0) accentColor else textSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = "Réservations",
                                tint = if (selectedTab == 1) accentColor else textSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )
                }
            }

            // 7. Tab Content: Grid of Published Trips or Bookings
            if (selectedTab == 0) {
                // Published Trips Feed/Grid
                if (userTrips.isEmpty()) {
                    item {
                        AppLottieEmptyState(
                            rawRes = R.raw.lottie_empty_trips,
                            badgeText = if (isAr) "0 منشورات" else "0 publication",
                            title = if (isAr) "لم تنشر أي رحلات حتى الآن" else "Aucune publication pour le moment",
                            description = if (isAr)
                                "شارك رحلاتك مع آلاف المسافرين عبر تطبيق رحلات الجزائر واكسب متابعين جدد."
                            else
                                "Partagez vos voyages et organisez vos prochaines expéditions.",
                            actionLabel = if (isAr) "نشر أول رحلة لك ➕" else "Publier un voyage ➕",
                            onActionClick = onAddTrip,
                            animationSize = 130.dp,
                            testTag = "instagram_empty_trips"
                        )
                    }
                } else {
                    // Instagram 3-column square photo grid
                    item {
                        val chunked = userTrips.chunked(3)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            chunked.forEach { rowTrips ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    rowTrips.forEach { trip ->
                                        val tripImageRes: Any = when {
                                            trip.imageResName.contains("tikjda") -> R.drawable.tikjda_mountain_trip_1787149263505
                                            trip.imageResName.contains("sahara") || trip.imageResName.contains("taghit") -> R.drawable.sahara_taghit_trip_1787149248284
                                            trip.imageUris.isNotBlank() -> trip.imageUris.split(",").firstOrNull()?.trim() ?: R.drawable.sahara_taghit_trip_1787149248284
                                            else -> R.drawable.sahara_taghit_trip_1787149248284
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clickable { onTripClick(trip) }
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(tripImageRes)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = trip.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            // Gradient scrim overlay
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                                        )
                                                    )
                                            )

                                            // Bottom Wilaya Tag on grid
                                            Text(
                                                text = trip.destinationWilaya.ifBlank { trip.title },
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(4.dp)
                                            )
                                        }
                                    }

                                    // Fill empty slots if last row has less than 3 items
                                    if (rowTrips.size < 3) {
                                        repeat(3 - rowTrips.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Bookings Tab
                if (userBookings.isEmpty()) {
                    item {
                        AppLottieEmptyState(
                            rawRes = R.raw.lottie_empty_bookings,
                            badgeText = if (isAr) "الحجوزات" else "Réservations",
                            title = if (isAr) "لا توجد تذاكر أو حجوزات نشطة" else "Aucune réservation active",
                            description = if (isAr)
                                "تصفح الرحلات المتاحة في الصفحة الرئيسية واحجز مقعدك للاستمتاع برحلات الجزائر."
                            else
                                "Explorez nos circuits pour réserver votre place dès maintenant.",
                            animationSize = 130.dp,
                            testTag = "instagram_empty_bookings"
                        )
                    }
                } else {
                    items(userBookings, key = { it.id }) { booking ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, borderCol)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = booking.tripTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "🎫 ${booking.seatsCount} ${if (isAr) "مقاعد" else "places"} • ${booking.totalPriceDzd.toInt()} DZD",
                                        fontSize = 12.sp,
                                        color = accentColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = booking.status,
                                        color = Color(0xFF10B981),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstagramStatColumn(
    count: String,
    label: String,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = count,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = textSecondary
        )
    }
}

@Composable
private fun InstagramBioTag(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = text, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// =============================================================================
// SUB-SCREEN B: SETTINGS SCREEN (Exact match to Screenshots 1 & 2)
// =============================================================================
@Composable
private fun SettingsSubScreen(
    userProfile: UserProfile,
    currentPalette: AppThemePalette,
    notificationPrefs: NotificationPreferences,
    isAr: Boolean,
    onBack: () -> Unit,
    onEditProfileClick: () -> Unit,
    onUploadAvatarClick: () -> Unit,
    onOpenThemeSelector: () -> Unit,
    onSelectPalette: ((AppThemePalette) -> Unit)?,
    onToggleLanguage: () -> Unit,
    onOpenNotificationsDialog: () -> Unit,
    onUpdateNotifications: (NotificationPreferences) -> Unit,
    onOpenVerificationDialog: () -> Unit,
    onOpenAboutDialog: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    val context = LocalContext.current
    val palette = LocalAppThemePalette.current
    val accentColor = palette.accentColor
    val textPrimary = palette.textPrimary
    val textSecondary = palette.mutedTextColor
    val cardBg = palette.darkCardBg
    val borderCol = palette.darkBorderColor

    val handle = remember(userProfile.email, userProfile.agencyName, userProfile.fullName) {
        if (userProfile.agencyName.isNotBlank()) {
            "@${userProfile.agencyName.replace(" ", "").lowercase()}"
        } else if (userProfile.email.isNotBlank()) {
            "@${userProfile.email.substringBefore("@").lowercase()}"
        } else {
            "@massiakkk"
        }
    }

    val colorSwatches = listOf(
        AppThemePalette.WHITE_ROYAL to Color(0xFFFFFFFF),
        AppThemePalette.EMERALD to Color(0xFF0D7A57),
        AppThemePalette.MEDITERRANEAN to Color(0xFF0277BD),
        AppThemePalette.SAHARA_GOLD to Color(0xFFD97706),
        AppThemePalette.SUNSET_CORAL to Color(0xFFE11D48),
        AppThemePalette.ROYAL_PURPLE to Color(0xFF7C3AED),
        AppThemePalette.TEAL_OASIS to Color(0xFF00897B),
        AppThemePalette.NIGHT_SAHARA to Color(0xFF455A64)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.darkCanvasBg)
            .testTag("settings_sub_screen")
    ) {
        // 1. Settings Top Bar with Back Arrow and Centered Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (isAr) "الإعدادات" else "Settings",
                color = textPrimary,
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(48.dp)) // balancing space
        }

        // 2. Settings Content List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Profile Card (Avatar + Name + @handle + arrow >) matching Screenshot 1 & 2
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditProfileClick() }
                        .testTag("settings_profile_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderCol)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .clickable { onUploadAvatarClick() }
                        ) {
                            AppAvatarImage(
                                avatarUrl = userProfile.avatarUrl,
                                fullName = userProfile.fullName.ifBlank { userProfile.agencyName },
                                size = 50.dp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (userProfile.role == "AGENCY" && userProfile.agencyName.isNotBlank()) {
                                        userProfile.agencyName
                                    } else if (userProfile.fullName.isNotBlank()) {
                                        userProfile.fullName
                                    } else {
                                        if (isAr) "مستخدم رحلات الجزائر" else "Voyageur Ra7alat"
                                    },
                                    color = textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (userProfile.isGoldenVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    GoldenVerifiedBadge(compact = true, isAr = isAr)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = handle,
                                color = textSecondary,
                                fontSize = 12.5.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Section: Other Settings
            item {
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = if (isAr) "الإعدادات العامة" else "Other Settings",
                    color = textSecondary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderCol)
                ) {
                    Column {
                        // 1. Profile details
                        ModernSettingItem(
                            icon = Icons.Default.Person,
                            title = if (isAr) "تفاصيل الملف الشخصي" else "Profile details",
                            onClick = onEditProfileClick
                        )

                        HorizontalDivider(color = borderCol.copy(alpha = 0.6f), thickness = 0.8.dp)

                        // 2. Password / Sécurité
                        ModernSettingItem(
                            icon = Icons.Default.Lock,
                            title = if (isAr) "كلمة المرور والأمان" else "Password",
                            onClick = onEditProfileClick
                        )

                        HorizontalDivider(color = borderCol.copy(alpha = 0.6f), thickness = 0.8.dp)

                        // 3. Notifications (with Switch)
                        ModernSettingToggleItem(
                            icon = Icons.Default.Notifications,
                            title = if (isAr) "الإشعارات والتنبيهات" else "Notifications",
                            checked = notificationPrefs.notificationsEnabled,
                            onCheckedChange = { isChecked ->
                                onUpdateNotifications(notificationPrefs.copy(notificationsEnabled = isChecked))
                            },
                            onClick = onOpenNotificationsDialog
                        )

                        HorizontalDivider(color = borderCol.copy(alpha = 0.6f), thickness = 0.8.dp)

                        // 4. Dark mode / Theme Swatches (Screenshot 1 & 2)
                        ModernSettingToggleItem(
                            icon = Icons.Default.NightlightRound,
                            title = if (isAr) "الوضع الداكن / السمة 🎨" else "Dark mode",
                            checked = !currentPalette.isLight,
                            onCheckedChange = { isDark ->
                                if (isDark) onSelectPalette?.invoke(AppThemePalette.EMERALD)
                                else onSelectPalette?.invoke(AppThemePalette.WHITE_ROYAL)
                            },
                            onClick = onOpenThemeSelector
                        )

                        // Theme Swatches preview row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            colorSwatches.forEach { (pal, col) ->
                                val isSelected = currentPalette == pal
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(col)
                                        .border(
                                            width = if (isSelected) 2.5.dp else if (pal.isLight) 1.5.dp else 0.dp,
                                            color = if (isSelected) accentColor else Color(0xFF94A3B8),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onSelectPalette?.invoke(pal)
                                            Toast.makeText(context, if (isAr) pal.nameAr else pal.nameFr, Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (pal.isLight) Color(0xFF0F766E) else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = borderCol.copy(alpha = 0.6f), thickness = 0.8.dp)

                        // 5. Language
                        ModernSettingItem(
                            icon = Icons.Default.Language,
                            title = if (isAr) "اللغة (العربية / Français)" else "Language",
                            onClick = onToggleLanguage
                        )

                        HorizontalDivider(color = borderCol.copy(alpha = 0.6f), thickness = 0.8.dp)

                        // 6. Agency Verification Badge Or
                        ModernSettingItem(
                            icon = Icons.Default.Verified,
                            title = if (isAr) "توثيق الوكالة (الشارة الذهبية) ★" else "Certification Agence (Badge Or)",
                            tint = Color(0xFFFFB300),
                            onClick = onOpenVerificationDialog
                        )
                    }
                }
            }

            // Section: About & Help (Screenshot 1 & 2)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderCol)
                ) {
                    Column {
                        // About Application
                        ModernSettingItem(
                            icon = Icons.Default.Info,
                            title = if (isAr) "عن تطبيق رحلات الجزائر" else "About Application",
                            onClick = onOpenAboutDialog
                        )

                        HorizontalDivider(color = borderCol.copy(alpha = 0.6f), thickness = 0.8.dp)

                        // Help / FAQ
                        ModernSettingItem(
                            icon = Icons.Default.Help,
                            title = if (isAr) "المساعدة والأسئلة الشائعة" else "Help / FAQ",
                            onClick = {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:Akekehdwiw@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Support Ra7alat DZ")
                                }
                                try {
                                    context.startActivity(emailIntent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Akekehdwiw@gmail.com", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        HorizontalDivider(color = borderCol.copy(alpha = 0.6f), thickness = 0.8.dp)

                        // Terms of service / Privacy
                        ModernSettingItem(
                            icon = Icons.Default.Security,
                            title = if (isAr) "الشروط وسياسة الخصوصية" else "Terms of service",
                            onClick = onOpenPrivacyPolicy
                        )
                    }
                }
            }

            // Section: Log Out Button (Screenshot 2: wide pill button)
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_logout_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF1E2E25),
                        contentColor = textPrimary
                    ),
                    border = BorderStroke(1.dp, borderCol)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "تسجيل الخروج" else "Log Out",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Section: Deactivate / Delete my account (Screenshot 1 & 2: Red trash button)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeleteAccountClick() }
                        .testTag("settings_delete_account_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEF4444).copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isAr) "حذف أو تعطيل حسابي نهائياً" else "Deactivate my account",
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}

@Composable
private fun ModernSettingItem(
    icon: ImageVector,
    title: String,
    tint: Color = LocalAppThemePalette.current.textPrimary,
    onClick: () -> Unit
) {
    val palette = LocalAppThemePalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = palette.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = palette.mutedTextColor,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ModernSettingToggleItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val palette = LocalAppThemePalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.textPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = palette.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = palette.accentColor,
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF334155)
            )
        )
    }
}
