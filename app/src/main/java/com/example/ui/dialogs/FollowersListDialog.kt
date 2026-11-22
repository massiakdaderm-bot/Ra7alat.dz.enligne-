package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.FollowerRecord
import com.example.ui.components.AppAvatarImage
import com.example.ui.components.AppLottieEmptyState
import com.example.ui.components.AppLottieLoadingView

@Composable
fun FollowersListDialog(
    organizerName: String,
    followers: List<FollowerRecord>,
    isLoading: Boolean = false,
    isOwner: Boolean = false,
    showFollowersPublicly: Boolean = true,
    isFollowingMode: Boolean = false,
    isAr: Boolean = true,
    onSelectOrganizer: ((name: String, email: String) -> Unit)? = null,
    onUnfollow: ((follower: FollowerRecord) -> Unit)? = null,
    onTabChange: ((isFollowing: Boolean) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val canView = isOwner || showFollowersPublicly || isFollowingMode
    val palette = com.example.ui.theme.LocalAppThemePalette.current
    val surfaceColor = if (palette.isLight) Color.White else Color(0xFF0F1E17)
    val borderColor = if (palette.isLight) Color(0xFFE2E8F0) else Color(0xFF1F4331)
    val textColor = palette.textPrimary

    var searchQuery by remember { mutableStateOf("") }
    var itemToUnfollow by remember { mutableStateOf<FollowerRecord?>(null) }

    val filteredList = remember(followers, searchQuery, isFollowingMode) {
        if (searchQuery.isBlank()) followers
        else {
            val q = searchQuery.trim().lowercase()
            followers.filter { item ->
                if (isFollowingMode) {
                    item.organizerName.lowercase().contains(q) ||
                    item.organizerEmail.lowercase().contains(q)
                } else {
                    item.followerName.lowercase().contains(q) ||
                    item.followerEmail.lowercase().contains(q)
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("followers_list_dialog"),
            color = surfaceColor,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (palette.isLight) palette.primaryLight.copy(alpha = 0.15f) else Color(0xFF1B3D2B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFollowingMode) Icons.Default.PersonSearch else Icons.Default.People,
                                contentDescription = null,
                                tint = palette.accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isFollowingMode) {
                                    if (isAr) "قائمة الاشتراكات" else "Liste des Abonnements"
                                } else {
                                    if (isAr) "قائمة المتابعين" else "Liste des Abonnés"
                                },
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = if (isFollowingMode) {
                                    if (isAr) "الحسابات والوكالات المشترك بها (${followers.size})" else "Comptes suivis (${followers.size})"
                                } else {
                                    if (isAr) "$organizerName (${followers.size} متابع)" else "$organizerName (${followers.size} abonnés)"
                                },
                                color = palette.accentColor,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF182D22))
                            .clickable { onDismiss() }
                            .testTag("followers_dialog_close"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 2. Tab Switcher (Followers vs Following) if onTabChange provided
                if (onTabChange != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF14271D))
                            .padding(4.dp)
                    ) {
                        // Tab 1: Followers
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (!isFollowingMode) (if (palette.isLight) Color.White else Color(0xFF224835))
                                    else Color.Transparent
                                )
                                .clickable {
                                    if (isFollowingMode) onTabChange(false)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isAr) "👥 المتابعون" else "👥 Abonnés",
                                color = if (!isFollowingMode) palette.accentColor else palette.mutedTextColor,
                                fontWeight = if (!isFollowingMode) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }

                        // Tab 2: Subscriptions (Following)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isFollowingMode) (if (palette.isLight) Color.White else Color(0xFF224835))
                                    else Color.Transparent
                                )
                                .clickable {
                                    if (!isFollowingMode) onTabChange(true)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isAr) "⭐ الاشتراكات" else "⭐ Abonnements",
                                color = if (isFollowingMode) palette.accentColor else palette.mutedTextColor,
                                fontWeight = if (isFollowingMode) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // 3. Search Bar
                if (canView && followers.size > 2) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = if (isFollowingMode) {
                                    if (isAr) "بحث في الاشتراكات..." else "Rechercher une agence..."
                                } else {
                                    if (isAr) "بحث في المتابعين..." else "Rechercher un abonné..."
                                },
                                color = palette.mutedTextColor,
                                fontSize = 12.5.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = palette.mutedTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = palette.mutedTextColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = if (palette.isLight) Color(0xFFF8FAFC) else Color(0xFF0B1711),
                            unfocusedContainerColor = if (palette.isLight) Color(0xFFF8FAFC) else Color(0xFF0B1711),
                            focusedBorderColor = palette.accentColor,
                            unfocusedBorderColor = borderColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = borderColor, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                if (!canView) {
                    // Privacy state: followers hidden by user
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF1B3D2B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isAr) "قائمة المتابعين خاصة" else "Liste d'abonnés privée",
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAr)
                                "قام صاحب هذا الحساب بتعيين قائمة المتابعين كخاصة."
                            else
                                "Le propriétaire a rendu sa liste d'abonnés privée.",
                            color = palette.mutedTextColor,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (isLoading) {
                    AppLottieLoadingView(
                        title = if (isAr) "جاري تحميل البيانات..." else "Chargement...",
                        animationSize = 100.dp,
                        testTag = "followers_lottie_loading"
                    )
                } else if (filteredList.isEmpty()) {
                    val emptyTitle = if (searchQuery.isNotEmpty()) {
                        if (isAr) "لا توجد نتائج مطابقة" else "Aucun résultat"
                    } else if (isFollowingMode) {
                        if (isAr) "لا توجد اشتراكات حتى الآن" else "Aucun abonnement pour le moment"
                    } else {
                        if (isAr) "لا يوجد متابعون حتى الآن" else "Aucun abonné pour le moment"
                    }
                    val emptyDesc = if (searchQuery.isNotEmpty()) {
                        if (isAr) "جرّب البحث باسم آخر أو بريد إلكتروني." else "Essayez un autre nom ou email."
                    } else if (isFollowingMode) {
                        if (isAr) "استكشف الوكالات السياحية والمنظمين واشترك في صفحاتهم للتوصل بأحدث الرحلات!" else "Explorez les agences et suivez leurs voyages !"
                    } else {
                        if (isAr) "كن أول من يتابع هذا الحساب واكتشف أحدث رحلاته!" else "Soyez le premier à vous abonner !"
                    }
                    AppLottieEmptyState(
                        rawRes = if (isFollowingMode) R.raw.lottie_empty_favorites else R.raw.lottie_empty_trips,
                        badgeText = if (isFollowingMode) (if (isAr) "اشتراكات" else "Abonnements") else (if (isAr) "متابعون" else "Abonnés"),
                        title = emptyTitle,
                        description = emptyDesc,
                        animationSize = 120.dp,
                        testTag = "followers_empty_lottie"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            filteredList,
                            key = { if (it.id != 0L) it.id else "${it.followerEmail}_${it.organizerEmail}_${it.organizerName}" }
                        ) { follower ->
                            FollowerItemRow(
                                follower = follower,
                                isFollowingMode = isFollowingMode,
                                isAr = isAr,
                                onOpenProfile = {
                                    val targetName = if (isFollowingMode) {
                                        follower.organizerName.ifBlank { follower.organizerEmail }
                                    } else {
                                        follower.followerName.ifBlank { follower.followerEmail }
                                    }
                                    val targetEmail = if (isFollowingMode) follower.organizerEmail else follower.followerEmail
                                    onSelectOrganizer?.invoke(targetName, targetEmail)
                                    onDismiss()
                                },
                                onUnfollow = if (isFollowingMode && onUnfollow != null) {
                                    {
                                        itemToUnfollow = follower
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Unsubscribing / Unfollowing
    if (itemToUnfollow != null) {
        val target = itemToUnfollow!!
        val name = target.organizerName.ifBlank { target.organizerEmail }
        Dialog(
            onDismissRequest = { itemToUnfollow = null }
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonRemove,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isAr) "إلغاء الاشتراك؟" else "Se désabonner ?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isAr)
                            "هل أنت متأكد من إلغاء متابعة \"$name\"؟ لن تصلك إشعارات رحلاتهم الجديدة."
                        else
                            "Voulez-vous vraiment vous désabonner de \"$name\" ?",
                        fontSize = 13.sp,
                        color = palette.mutedTextColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { itemToUnfollow = null },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (palette.isLight) Color(0xFFE2E8F0) else Color(0xFF1B3D2B),
                                contentColor = textColor
                            )
                        ) {
                            Text(if (isAr) "تراجع" else "Annuler", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                onUnfollow?.invoke(target)
                                itemToUnfollow = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626),
                                contentColor = Color.White
                            )
                        ) {
                            Text(if (isAr) "إلغاء الاشتراك" else "Désabonner", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FollowerItemRow(
    follower: FollowerRecord,
    isFollowingMode: Boolean = false,
    isAr: Boolean,
    onOpenProfile: () -> Unit,
    onUnfollow: (() -> Unit)? = null
) {
    val palette = com.example.ui.theme.LocalAppThemePalette.current
    val cardColor = if (palette.isLight) Color.White else Color(0xFF14271D)
    val borderColor = if (palette.isLight) Color(0xFFE2E8F0) else Color(0xFF224835)
    val textColor = palette.textPrimary

    val displayName = if (isFollowingMode) {
        if (follower.organizerName.isNotBlank() &&
            !follower.organizerName.equals("وكالة سياحية", ignoreCase = true) &&
            !follower.organizerName.equals("ra7alat", ignoreCase = true)
        ) {
            follower.organizerName
        } else if (follower.organizerEmail.isNotBlank()) {
            follower.organizerEmail.substringBefore("@")
                .replace(".", " ")
                .replace("_", " ")
                .split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
        } else {
            if (isAr) "وكالة معتمدة" else "Agence Partenaire"
        }
    } else {
        if (follower.followerName.isNotBlank() &&
            !follower.followerName.equals("وكالة سياحية", ignoreCase = true) &&
            !follower.followerName.equals("ra7alat", ignoreCase = true)
        ) {
            follower.followerName
        } else if (follower.followerEmail.isNotBlank()) {
            follower.followerEmail.substringBefore("@")
                .replace(".", " ")
                .replace("_", " ")
                .split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
        } else {
            if (isAr) "متابع رحلات" else "Voyageur Ra7alat"
        }
    }

    val displayEmail = if (isFollowingMode) follower.organizerEmail else follower.followerEmail
    val avatarUrl = if (isFollowingMode) {
        follower.organizerAvatar.ifBlank { follower.followerAvatar }
    } else {
        follower.followerAvatar
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenProfile() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with profile click
                Box(
                    modifier = Modifier.clickable { onOpenProfile() }
                ) {
                    AppAvatarImage(
                        avatarUrl = avatarUrl,
                        fullName = displayName,
                        size = 46.dp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Email Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenProfile() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayName,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isFollowingMode) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    if (displayEmail.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = displayEmail,
                            color = palette.mutedTextColor,
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: "عرض البروفايل" (View Profile) + "إلغاء الاشتراك" (Unfollow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button 1: View Profile (عرض البروفايل)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenProfile() },
                    shape = RoundedCornerShape(10.dp),
                    color = if (palette.isLight) palette.primaryLight.copy(alpha = 0.12f) else Color(0xFF183827),
                    border = BorderStroke(1.dp, if (palette.isLight) palette.primaryLight.copy(alpha = 0.3f) else Color(0xFF265A3F))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = palette.accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isAr) "عرض البروفايل" else "Voir Profil",
                            color = palette.accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Button 2: Unfollow / إلغاء الاشتراك (Only in Following mode)
                if (isFollowingMode && onUnfollow != null) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onUnfollow() },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isAr) "إلغاء الاشتراك" else "Désabonner",
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
