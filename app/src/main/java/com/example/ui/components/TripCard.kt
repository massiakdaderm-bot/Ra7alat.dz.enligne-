package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Trip
import com.example.ui.theme.LocalAppThemePalette

@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onChatClick: () -> Unit = onClick,
    isAr: Boolean = true,
    isOwner: Boolean = false,
    isBooked: Boolean = false,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onManageClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val palette = LocalAppThemePalette.current
    val emeraldAccent = palette.accentColor
    val cardBackground = if (palette.isLight) Color.White else palette.darkCardBg
    val innerDarkBg = if (palette.isLight) Color(0xFFF8FAFC) else palette.darkCanvasBg
    val buttonBg = if (palette.isLight) Color(0xFFF1F5F9) else palette.darkSurfaceBg
    val subTextColor = if (palette.isLight) Color(0xFF64748B) else Color.Gray
    val cardBorder = if (palette.isLight) BorderStroke(1.dp, Color(0xFFE2E8F0)) else BorderStroke(1.dp, Color.White.copy(alpha = 0.85f))

    val imageRes = when {
        trip.imageResName.contains("tikjda") -> R.drawable.tikjda_mountain_trip_1787149263505
        trip.imageResName.contains("sahara") || trip.imageResName.contains("taghit") -> R.drawable.sahara_taghit_trip_1787149248284
        trip.imageUris.isNotBlank() -> trip.imageUris.split(",").firstOrNull()?.trim()
        else -> R.drawable.sahara_taghit_trip_1787149248284
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("trip_card_${trip.id}"),
        shape = RoundedCornerShape(24.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = if (palette.isLight) 3.dp else 2.dp)
    ) {
        Column {
            // Upper Image with Badges and Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
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
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                )

                // Dark Gradient Scrim
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                startY = 80f
                            )
                        )
                )

                // Top Badges Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Top Left Badge
                    if (isOwner) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E2421).copy(alpha = 0.9f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isAr) "👑 رحلتي المنشورة 🇩🇿" else "👑 Mon voyage publié 🇩🇿",
                                color = emeraldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE65100))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isAr) "🔥 انطلاق اليوم !" else "🔥 Départ aujourd'hui !",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Top Right Items (Time Ago + Heart Favorite)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "🕒 ${trip.getRelativePublishTime(isAr)}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                                .testTag("favorite_button_${trip.id}")
                        ) {
                            Icon(
                                imageVector = if (trip.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (trip.isFavorite) Color(0xFFFF4081) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Bottom Content on Image (Title + Organizer Info + Golden Badge)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = trip.title.ifBlank { "voyage" },
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ra7alat_logo_icon_1787149237815),
                            contentDescription = "Organizer Logo",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        if (trip.isOrganizerGoldenVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "👑", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${trip.organizerName.ifBlank { "ra7alat" }} • ${trip.departureWilaya}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (trip.isOrganizerAgency) {
                            Spacer(modifier = Modifier.width(6.dp))
                            if (trip.isOrganizerGoldenVerified) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFB300)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Golden Verified",
                                        tint = Color(0xFF5D3800),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1976D2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Agency Verified",
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Lower Section (Itinerary + Specs + Price + Actions)
            Column(modifier = Modifier.padding(14.dp)) {
                // Route Bar (📍 Wilaya ➔ 🎯 Wilaya)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(innerDarkBg)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 ${trip.departureWilaya}",
                            color = emeraldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "➔",
                            color = emeraldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🎯 ${trip.destinationWilaya}",
                            color = emeraldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = trip.destinationPlace.ifBlank { trip.destinationWilaya },
                            color = palette.mutedTextColor,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Specs & Price Row (Bus, Date, Passengers, Price)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Transport
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = "Transport",
                            tint = emeraldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = trip.transportType.ifBlank { if (isAr) "حافلة" else "Bus" },
                            fontSize = 11.sp,
                            color = subTextColor
                        )
                    }

                    // Date
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Date",
                            tint = emeraldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = trip.departureDate,
                            fontSize = 11.sp,
                            color = subTextColor
                        )
                    }

                    // People / Seats (0/30)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Seats",
                            tint = emeraldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val booked = (trip.totalSeats - trip.availableSeats).coerceAtLeast(0)
                        val total = if (trip.totalSeats > 0) trip.totalSeats else 30
                        Text(
                            text = "$booked/$total",
                            fontSize = 11.sp,
                            color = subTextColor
                        )
                    }

                    // Price (Right aligned)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "%,d".format(trip.priceDzd.toLong()),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = emeraldAccent
                        )
                        Text(
                            text = if (isAr) "دج / للشخص" else "DZD / personne",
                            fontSize = 11.sp,
                            color = subTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Row (Réserver 🎟️ / Gérer ⚙️ + Chat 💬)
                if (isOwner) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onManageClick?.invoke() ?: onEditClick?.invoke() ?: onClick() },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("manage_trip_button_${trip.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonBg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = emeraldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "إدارة رحلتي ⚙️" else "Gérer mon voyage ⚙️",
                                color = emeraldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        if (onEditClick != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0088FF))
                                    .clickable { onEditClick() }
                                    .testTag("edit_trip_button_${trip.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Trip",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (onDeleteClick != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF3B1E1E))
                                    .clickable { onDeleteClick() }
                                    .testTag("delete_trip_button_${trip.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Trip",
                                    tint = Color(0xFFFF6B6B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(buttonBg)
                                .clickable { onChatClick() }
                                .testTag("trip_chat_button_${trip.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Chat",
                                tint = emeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isBooked) {
                            Button(
                                onClick = onClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("booked_trip_button_${trip.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2B))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "تم الحجز بنجاح ✓" else "Réservé avec succès ✓",
                                    color = Color(0xFF81C784),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else if (trip.isExpired()) {
                            Button(
                                onClick = onClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C))
                            ) {
                                Text(
                                    text = if (isAr) "الرحلة منتهية 🏁" else "Voyage terminé 🏁",
                                    color = Color.LightGray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else if (trip.isOngoing()) {
                            Button(
                                onClick = onClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF332A15))
                            ) {
                                Text(
                                    text = if (isAr) "الرحلة جارية 🚌" else "Voyage en cours 🚌",
                                    color = Color(0xFFFFD54F),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else if (trip.availableSeats <= 0) {
                            Button(
                                onClick = onClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1E1E))
                            ) {
                                Text(
                                    text = if (isAr) "مكتملة 🛑" else "Complet 🛑",
                                    color = Color(0xFFFF8A80),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = onClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("reserve_trip_button_${trip.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = buttonBg)
                            ) {
                                Text(
                                    text = if (isAr) "حجز 🎟️" else "Réserver 🎟️",
                                    color = emeraldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(buttonBg)
                                .clickable { onChatClick() }
                                .testTag("trip_chat_button_${trip.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Chat",
                                tint = emeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

