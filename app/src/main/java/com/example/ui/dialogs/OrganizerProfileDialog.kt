package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.ui.components.AppAvatarImage

@Composable
fun OrganizerProfileDialog(
    organizer: UserProfile,
    organizerTrips: List<Trip> = emptyList(),
    isFollowing: Boolean = false,
    isSelfProfile: Boolean = false,
    onToggleFollow: () -> Unit = {},
    onOpenFollowers: () -> Unit = {},
    onTripClick: (Trip) -> Unit = {},
    onDismiss: () -> Unit,
    isAr: Boolean = true
) {
    val context = LocalContext.current
    val displayName = organizer.agencyName.ifBlank { organizer.fullName.ifBlank { "وكالة سياحية" } }
    val displayWilaya = organizer.agencyWilaya.ifBlank { "الجزائر العاصمة" }
    val isAgency = organizer.isAgency
    val isGolden = organizer.isGoldenVerified
    val isAgencyBlue = organizer.isAgencyBlueVerified

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("organizer_profile_dialog"),
            color = Color(0xFF0F1E17),
            border = BorderStroke(1.dp, Color(0xFF1F4331))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Header Bar: Title and Close button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "الملف التعريفي للمنظم 🇩🇿" else "Profil de l'Organisateur 🇩🇿",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF182D22))
                                .clickable { onDismiss() }
                                .testTag("organizer_dialog_close"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. Main Profile Info Card (Screenshot 1 Layout)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF14271D)),
                        border = BorderStroke(1.dp, Color(0xFF224835))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Centered Large Avatar
                            AppAvatarImage(
                                avatarUrl = organizer.avatarUrl,
                                fullName = displayName,
                                size = 96.dp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name with Golden Verified Badge if applicable
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = displayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                                if (isAgency) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = if (isGolden) "Vérifié Or" else "Vérifié Agence",
                                        tint = if (isGolden) Color(0xFFFFB300) else Color(0xFF1976D2),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Golden Pill Badge or Regular Agency Pill or Traveler Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isGolden) Brush.horizontalGradient(
                                            listOf(Color(0xFFD48806), Color(0xFFF59E0B), Color(0xFFFBBF24))
                                        )
                                        else if (isAgency) Brush.horizontalGradient(
                                            listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
                                        )
                                        else Brush.horizontalGradient(
                                            listOf(Color(0xFF1B4332), Color(0xFF2D6A4F))
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isGolden) {
                                        if (isAr) "★ وكالة موثقة ذهبياً ★" else "★ Agence Certifiée Or ★"
                                    } else if (isAgency) {
                                        if (isAr) "★ وكالة سياحية معتمدة ★" else "★ Agence Certifiée ★"
                                    } else {
                                        if (isAr) "★ منظم رحلات ★" else "★ Organisateur ★"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Location
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) "ولاية $displayWilaya" else "Wilaya de $displayWilaya",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stats row: Rating & Followers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Rating Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0C1B13))
                                        .border(1.dp, Color(0xFF1E3D2C), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "★ 4.9 (12)",
                                        color = Color(0xFFFFB300),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Followers Pill (Clickable to view followers)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0C1B13))
                                        .border(1.dp, Color(0xFF1E3D2C), RoundedCornerShape(12.dp))
                                        .clickable { onOpenFollowers() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isAr) "👥 ${organizer.computedFollowersCount} متابع" else "👥 ${organizer.computedFollowersCount} Abonnés",
                                        color = Color(0xFF81C784),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (isSelfProfile) {
                                Surface(
                                    shape = RoundedCornerShape(21.dp),
                                    color = Color(0xFF1B3D2B),
                                    border = BorderStroke(1.dp, Color(0xFF2E6548)),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color(0xFF81C784),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isAr) "👑 حسابك الشخصي (أنت المنظم)" else "👑 Votre profil (Organisateur)",
                                            color = Color(0xFFA5D6A7),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            } else {
                                // Follow Button (+ Suivre / ✓ Suivi(e))
                                Button(
                                    onClick = onToggleFollow,
                                    modifier = Modifier
                                        .fillMaxWidth(0.65f)
                                        .height(42.dp)
                                        .testTag("organizer_follow_button"),
                                    shape = RoundedCornerShape(21.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFollowing) Color(0xFF1E3D2C) else Color(0xFF257B52)
                                    ),
                                    border = if (isFollowing) BorderStroke(1.dp, Color(0xFF2E6548)) else null
                                ) {
                                    Icon(
                                        imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isFollowing) {
                                            if (isAr) "✓ تمت المتابعة" else "✓ Suivi(e)"
                                        } else {
                                            if (isAr) "+ متابعة" else "+ Suivre"
                                        },
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Bio Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0C1B13))
                                    .border(1.dp, Color(0xFF193726), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (organizer.bio.isNotBlank()) {
                                        organizer.bio
                                    } else if (isAr) {
                                        "نحن وكالة ومنظمو رحلات جزائرية متخصصة في تنظيم الرحلات السياحية، الاستكشافية، والتخييم عبر أجمل معالم الجزائر الطبيعية والتاريخية 🇩🇿."
                                    } else {
                                        "Nous sommes une agence organisatrice de voyages spécialisée dans les circuits touristiques, randonnées et bivouacs à travers l'Algérie 🇩🇿."
                                    },
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Contact Buttons Row (Appeler + WhatsApp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Call Button
                                Button(
                                    onClick = {
                                        if (organizer.phone.isNotBlank()) {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${organizer.phone}"))
                                            context.startActivity(intent)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("organizer_call_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4331))
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "اتصال" else "Appeler",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                // WhatsApp Button
                                Button(
                                    onClick = {
                                        val cleanPhone = organizer.phone.replace("+", "").replace(" ", "")
                                        val phone = if (cleanPhone.startsWith("0")) "213" + cleanPhone.substring(1) else cleanPhone
                                        val message = if (isAr) "مرحباً، أود الاستفسار بخصوص رحلاتكم السياحية المعروضة في تطبيق رحلات الجزائر 🇩🇿"
                                        else "Bonjour, je souhaite me renseigner sur vos voyages sur l'application Ra7alat DZ 🇩🇿"
                                        val url = "https://wa.me/$phone?text=${Uri.encode(message)}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"))
                                            context.startActivity(webIntent)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("organizer_whatsapp_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                ) {
                                    Text(
                                        text = if (isAr) "💬 واتساب" else "💬 WhatsApp",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Section Title: Trips of this organizer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "🧭 رحلات هذا المنظم (${organizerTrips.size})" else "🧭 Voyages de cet organisateur (${organizerTrips.size})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 3. Organizer Trips List
                if (organizerTrips.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF14271D))
                        ) {
                            Text(
                                text = if (isAr) "لا توجد رحلات معلنة حالياً لهذا المنظم" else "Aucun voyage actif pour cet organisateur",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(organizerTrips) { trip ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onTripClick(trip) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF14271D)),
                            border = BorderStroke(1.dp, Color(0xFF1F4331))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = trip.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📍 ${trip.destinationWilaya} • 📅 ${trip.departureDate}",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.5.sp
                                    )
                                }
                                Text(
                                    text = "${trip.priceDzd.toInt()} د.ج",
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
