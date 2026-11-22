package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Trip
import com.example.ui.theme.LocalAppThemePalette

enum class MiniBadgeType {
    AUTO,
    SPONSORED,
    PROCHAINEMENT,
    TRANSPORT,
    GUIDE,
    CATEGORY
}

@Composable
fun MiniTripCard(
    trip: Trip,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isAr: Boolean = true,
    isOwner: Boolean = false,
    badgeType: MiniBadgeType = MiniBadgeType.AUTO,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val palette = LocalAppThemePalette.current
    val emeraldAccent = palette.accentColor
    val cardBackground = if (palette.isLight) Color.White else palette.darkCardBg
    val textPrimary = palette.textPrimary
    val subTextColor = if (palette.isLight) Color(0xFF64748B) else Color(0xFF94A3B8)
    val cardBorder = if (palette.isLight) {
        BorderStroke(1.dp, Color(0xFFE2E8F0))
    } else {
        BorderStroke(1.dp, palette.darkBorderColor.copy(alpha = 0.8f))
    }

    val imageRes = when {
        trip.imageResName.contains("tikjda") -> R.drawable.tikjda_mountain_trip_1787149263505
        trip.imageResName.contains("sahara") || trip.imageResName.contains("taghit") -> R.drawable.sahara_taghit_trip_1787149248284
        trip.imageUris.isNotBlank() -> trip.imageUris.split(",").firstOrNull()?.trim()
        else -> R.drawable.sahara_taghit_trip_1787149248284
    }

    val isSponsored = badgeType == MiniBadgeType.SPONSORED ||
            (badgeType == MiniBadgeType.AUTO && (trip.isFeatured || trip.organizerBadge.contains("SPONSOR", ignoreCase = true)))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("mini_trip_card_${trip.id}"),
        shape = RoundedCornerShape(18.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = if (palette.isLight) 2.dp else 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Compact Photo Container (Height 115dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageRes)
                        .crossfade(true)
                        .build(),
                    contentDescription = trip.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                )

                // Subtle Top & Bottom Gradients for Chip Legibility
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.65f)
                                )
                            )
                        )
                )

                // Top Badge (Left/Start) & Favorite Button (Right/End)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge: Sponsorisé ⭐, Prochainement ⏳, Transport 🚌, Guide 🧭 or Category
                    when {
                        isSponsored -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
                                        )
                                    )
                                    .padding(horizontal = 7.dp, vertical = 3.5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Sponsor",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isAr) "ممولة" else "Sponsorisé",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        badgeType == MiniBadgeType.PROCHAINEMENT -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
                                        )
                                    )
                                    .padding(horizontal = 7.dp, vertical = 3.5.dp)
                            ) {
                                Text(
                                    text = if (isAr) "⏳ قريباً" else "⏳ Bientôt",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        badgeType == MiniBadgeType.TRANSPORT || trip.tripType.contains("نقل") -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0284C7).copy(alpha = 0.9f))
                                    .padding(horizontal = 7.dp, vertical = 3.5.dp)
                            ) {
                                Text(
                                    text = if (isAr) "🚌 نقل" else "🚌 Transport",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        badgeType == MiniBadgeType.GUIDE || trip.organizerType == "INDIVIDUAL" -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.9f))
                                    .padding(horizontal = 7.dp, vertical = 3.5.dp)
                            ) {
                                Text(
                                    text = if (isAr) "🧭 مرشد" else "🧭 Guide",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> {
                            val cleanType = trip.tripType.substringBefore(" ").take(8)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 6.dp, vertical = 3.5.dp)
                            ) {
                                Text(
                                    text = cleanType.ifBlank { if (isAr) "رحلة" else "Voyage" },
                                    color = Color.White,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Circular Favorite Button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clickable { onFavoriteToggle() }
                            .testTag("mini_favorite_${trip.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (trip.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (trip.isFavorite) Color(0xFFFF4081) else Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Date Chip on Bottom of Image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 7.dp, bottom = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 6.dp, vertical = 2.5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Date",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = trip.departureDate.ifBlank { if (isAr) "قريباً" else "Bientôt" },
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 2. Info Body (Padded compact layout)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp, vertical = 8.dp)
            ) {
                // Title (1 line with ellipsis)
                Text(
                    text = trip.title.ifBlank { if (isAr) "رحلة سياحية" else "Voyage" },
                    color = textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Location / Wilaya Pin Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = emeraldAccent,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    val locText = if (trip.destinationWilaya.isNotBlank()) {
                        trip.destinationWilaya.replace(Regex("^[0-9]+ - "), "")
                    } else {
                        trip.destinationPlace.ifBlank { "Algérie" }
                    }
                    Text(
                        text = locText,
                        color = subTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Organizer miniature row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ra7alat_logo_icon_1787149237815),
                        contentDescription = "Organizer",
                        modifier = Modifier
                            .size(13.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = trip.organizerName.ifBlank { "Ra7alat" },
                        color = subTextColor,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (trip.isOrganizerGoldenVerified) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "👑", fontSize = 9.sp)
                    } else if (trip.isOrganizerAgency) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "%,d".format(trip.priceDzd.toLong()),
                            color = emeraldAccent,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (isAr) "دج" else "DZD",
                            color = emeraldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (isAr) "/شخص" else "/pers",
                        color = subTextColor,
                        fontSize = 9.5.sp
                    )
                }

                // Owner Controls (if owned by user)
                if (isOwner && (onEditClick != null || onDeleteClick != null)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onEditClick != null) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(emeraldAccent.copy(alpha = 0.15f))
                                    .clickable { onEditClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = emeraldAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        if (onDeleteClick != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.15f))
                                    .clickable { onDeleteClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
