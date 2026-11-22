package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Trip
import com.example.ui.components.AppAvatarImage
import com.example.ui.components.GoldenVerifiedBadge
import com.example.util.TripEquipmentManager

@Composable
fun TripDetailDialog(
    trip: Trip,
    onBookClick: () -> Unit,
    onOrganizerClick: () -> Unit,
    onReviewClick: () -> Unit,
    onChatClick: () -> Unit,
    onDismiss: () -> Unit,
    isAr: Boolean = true,
    isOwner: Boolean = false,
    isBooked: Boolean = false,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onManageClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val palette = com.example.ui.theme.LocalAppThemePalette.current
    val dialogBg = if (palette.isLight) palette.darkCanvasBg else Color(0xFF0F1E17)
    val cardContainerBg = if (palette.isLight) palette.darkCardBg else Color(0xFF142B20)
    val dialogBorder = if (palette.isLight) palette.darkBorderColor else Color(0xFF1F4331)
    val cardBorderColor = if (palette.isLight) palette.darkBorderColor else Color(0xFF234B37)
    val bannerBg = if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF163325)
    val bannerBorder = if (palette.isLight) palette.darkBorderColor else Color(0xFF285C42)
    val textPrimary = if (palette.isLight) palette.textPrimary else Color.White
    val textSecondary = if (palette.isLight) palette.textSecondary else Color(0xFFB0D5C1)
    val textMuted = if (palette.isLight) Color(0xFF334155) else Color(0xFFD4E8DC)
    val closeBtnBorder = if (palette.isLight) palette.darkBorderColor else Color(0xFF2E6349)
    val closeBtnText = if (palette.isLight) palette.textPrimary else Color(0xFFB0D5C1)
    val orgBtnBg = if (palette.isLight) Color(0xFFE2E8F0) else Color(0xFF1F4331)
    val orgBtnText = if (palette.isLight) palette.textPrimary else Color.White

    val images = trip.allImages
    var selectedImageIndex by remember { mutableStateOf(0) }

    val currentImage = if (images.isNotEmpty() && selectedImageIndex in images.indices) {
        images[selectedImageIndex]
    } else if (trip.imageResName.isNotBlank()) {
        trip.imageResName
    } else {
        R.drawable.sahara_taghit_trip_1787149248284
    }

    val equipments = TripEquipmentManager.getEquipmentForTrip(trip.tripType, 3, isAr)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("trip_detail_dialog"),
            color = dialogBg,
            border = BorderStroke(1.dp, dialogBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 1. Main Header Image Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    val resolvedModel = when {
                        currentImage is Int -> currentImage
                        currentImage.toString().contains("tikjda") -> R.drawable.tikjda_mountain_trip_1787149263505
                        currentImage.toString().contains("sahara") || currentImage.toString().contains("taghit") -> R.drawable.sahara_taghit_trip_1787149248284
                        else -> currentImage
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(resolvedModel)
                            .crossfade(true)
                            .error(R.drawable.sahara_taghit_trip_1787149248284)
                            .build(),
                        contentDescription = trip.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    // Gradient overlay at top and bottom
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )

                    // Top Action Bar inside Image
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Close button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .size(36.dp)
                        ) {
                            Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        // Share button
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "اكتشف رحلة (${trip.title}) إلى ${trip.destinationWilaya} عبر تطبيق رحلات الجزائر 🇩🇿: https://ra7alat.dz/trip/${trip.id}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "مشاركة الرحلة"))
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Bottom tag on image
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${trip.destinationWilaya} • ${trip.destinationPlace.ifBlank { trip.title }}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 2. Multi-image thumbnails (if more than 1 image)
                if (images.size > 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        images.forEachIndexed { index, imgUri ->
                            val isSelected = index == selectedImageIndex
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF81C784) else Color(0xFF2C4A3A),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedImageIndex = index }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(imgUri)
                                        .crossfade(true)
                                        .error(R.drawable.sahara_taghit_trip_1787149248284)
                                        .build(),
                                    contentDescription = "Photo ${index + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Trip Title & Status Badge
                Text(
                    text = trip.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = textPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Status and Type tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (palette.isLight) Color(0xFFE0F2FE) else Color(0xFF1B3D2B))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = trip.tripType.ifBlank { if (isAr) "سياحة واستكشاف" else "Tourisme" },
                            color = if (palette.isLight) Color(0xFF0369A1) else Color(0xFF81C784),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Publication Time Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF14271D))
                            .then(if (palette.isLight) Modifier.border(1.dp, dialogBorder, RoundedCornerShape(8.dp)) else Modifier)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🕒 ${trip.getRelativePublishTime(isAr)}",
                            color = if (palette.isLight) Color(0xFF0F766E) else Color(0xFFA5D6A7),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Lifecycle Status Tag
                    if (trip.isExpired()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF333333))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(if (isAr) "الرحلة منتهية 🏁" else "Voyage terminé 🏁", color = Color.LightGray, fontSize = 12.sp)
                        }
                    } else if (trip.isOngoing()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4A3800))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(if (isAr) "الرحلة جارية وفي طريقها 🚌" else "Voyage en cours 🚌", color = Color(0xFFFFD54F), fontSize = 12.sp)
                        }
                    } else if (trip.availableSeats <= 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4A1A1A))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(if (isAr) "المقاعد مكتملة 🛑" else "Complet 🛑", color = Color(0xFFFF8A80), fontSize = 12.sp)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF133827))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(if (isAr) "متاحة للحجز ✨" else "Disponible ✨", color = Color(0xFF81C784), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Organizer Profile Card (Enhanced with Avatar, Badge, and Profile button)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOrganizerClick() },
                    colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, cardBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppAvatarImage(
                            avatarUrl = trip.organizerAvatarUrl,
                            fullName = trip.organizerName,
                            size = 48.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = trip.organizerName.ifBlank { if (isAr) "وكالة رحلات الجزائر" else "Agence Ra7alat" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = textPrimary
                                )
                                if (trip.isOrganizerAgency) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    if (trip.isOrganizerGoldenVerified) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFFB300)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = "Golden", tint = Color(0xFF5D3800), modifier = Modifier.size(11.dp))
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF1976D2)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = "Verified", tint = Color.White, modifier = Modifier.size(11.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (trip.isOrganizerGoldenVerified) {
                                    if (isAr) "وكالة سياحية موثقة ذهبياً ★" else "Agence Certifiée Or ★"
                                } else if (trip.isOrganizerAgency) {
                                    if (isAr) "وكالة سياحية معتمدة ★" else "Agence Certifiée ★"
                                } else {
                                    if (isAr) "منظم رحلات مستقل" else "Organisateur Indépendant"
                                },
                                fontSize = 11.sp,
                                color = if (trip.isOrganizerGoldenVerified) Color(0xFFFFD54F) else Color(0xFF81C784)
                            )
                        }

                        // Button to view profile
                        Button(
                            onClick = onOrganizerClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = orgBtnBg),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = if (isAr) "البروفايل 👤" else "Profil 👤",
                                fontSize = 11.sp,
                                color = orgBtnText,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Price and Seats Overview Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(bannerBg)
                        .border(1.dp, bannerBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAr) "سعر التذكرة / للشخص" else "Prix du billet / pers.",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${"%,d".format(trip.priceDzd.toLong())} ${if (isAr) "دج" else "DZD"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF0F766E)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isAr) "المقاعد المتبقية" else "Places restantes",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${trip.availableSeats} / ${if (trip.totalSeats > 0) trip.totalSeats else 30}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (trip.availableSeats > 5) textPrimary else Color(0xFFFF8A80)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Itinerary & Exact Location with Google Maps Integration
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, cardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isAr) "📍 مسار وموقع الرحلة" else "📍 Itinéraire et Localisation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = textPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Departure point
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isAr) "نقطة الانطلاق:" else "Point de départ :",
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                                val depDetails = buildString {
                                    append(trip.departureWilaya)
                                    if (trip.departureCity.isNotBlank() && trip.departureCity != trip.departureWilaya) {
                                        append(" - ${trip.departureCity}")
                                    }
                                }
                                Text(
                                    text = depDetails,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = textPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Destination point
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isAr) "الوجهة المقصودة:" else "Destination :",
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                                val destDetails = buildString {
                                    append(trip.destinationWilaya)
                                    if (trip.destinationPlace.isNotBlank()) {
                                        append(" - ${trip.destinationPlace}")
                                    }
                                }
                                Text(
                                    text = destDetails,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = textPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive Google Maps Button
                        OutlinedButton(
                            onClick = {
                                val query = if (trip.departureCity.isNotBlank()) {
                                    "${trip.departureCity}, ${trip.departureWilaya}, Algeria"
                                } else {
                                    "${trip.departureWilaya}, Algeria"
                                }
                                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                try {
                                    context.startActivity(mapIntent)
                                } catch (_: Exception) {
                                    // Fallback to web browser maps
                                    val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF0F766E))
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "فتح موقع الانطلاق على Google Maps 🗺️" else "Ouvrir le départ sur Google Maps 🗺️",
                                color = Color(0xFF0F766E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7. Dates & Transport Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Departure Date
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (isAr) "تاريخ الذهاب" else "Départ", fontSize = 11.sp, color = textSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = trip.departureDate, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = textPrimary)
                        }
                    }

                    // Return Date
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (isAr) "تاريخ العودة" else "Retour", fontSize = 11.sp, color = textSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = trip.returnDate.ifBlank { if (isAr) "نفس اليوم" else "Même jour" }, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = textPrimary)
                        }
                    }

                    // Transport Type
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (isAr) "وسيلة النقل" else "Transport", fontSize = 11.sp, color = textSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = trip.transportType.ifBlank { if (isAr) "حافلة" else "Bus" }, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = textPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 8. Description Section
                if (trip.description.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "📝 تفاصيل ووصف البرنامج" else "📝 Description & Programme",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = trip.description,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = textMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 9. Inclusions / What's Included (if available)
                if (trip.inclusions.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "🎁 ما تشمله الرحلة" else "🎁 Inclus dans le voyage",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = trip.inclusions,
                                fontSize = 13.sp,
                                color = if (palette.isLight) Color(0xFF0F766E) else Color(0xFF81C784)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 10. Suggested Equipments
                if (equipments.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "🎒 المستلزمات المقترحة للرحلة:" else "🎒 Équipements recommandés :",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            equipments.forEach { eq ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = eq, fontSize = 12.sp, color = textMuted)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 11. Direct Chat & Review Shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onChatClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trip_detail_chat_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF2E6349))
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isAr) "شات الرحلة 💬" else "Discussion 💬", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReviewClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trip_detail_review_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF2E6349))
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isAr) "التقييمات ⭐" else "Avis ⭐", color = Color(0xFFFFD54F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 12. Primary Action Buttons at Bottom
                if (isOwner) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onManageClick != null) {
                            Button(
                                onClick = {
                                    onDismiss()
                                    onManageClick()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1B3D2B),
                                    contentColor = Color(0xFF81C784)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("trip_detail_manage_button")
                            ) {
                                Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isAr) "إدارة الرحلة والحجوزات ⚙️" else "Gérer mon voyage & réservations ⚙️", fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (onEditClick != null) {
                                Button(
                                    onClick = {
                                        onDismiss()
                                        onEditClick()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("trip_detail_edit_button")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isAr) "تعديل ✏️" else "Modifier ✏️", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (onDeleteClick != null) {
                                OutlinedButton(
                                    onClick = {
                                        onDismiss()
                                        onDeleteClick()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                                    border = BorderStroke(1.dp, Color(0xFF5C2525)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("trip_detail_delete_button")
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isAr) "حذف الرحلة" else "Supprimer", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    if (isBooked) {
                        Button(
                            onClick = { /* Already booked */ },
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFF1B3D2B),
                                disabledContentColor = Color(0xFF81C784)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("trip_detail_already_booked_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isAr) "لقد حجزت في هذه الرحلة بنجاح ✓" else "Vous êtes inscrit à ce voyage ✓", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    } else if (trip.isExpired()) {
                        Button(
                            onClick = { /* Expired */ },
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFF2C2C2C),
                                disabledContentColor = Color.LightGray
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(if (isAr) "الرحلة منتهية 🏁" else "Voyage terminé 🏁", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    } else if (trip.isOngoing()) {
                        Button(
                            onClick = { /* Ongoing */ },
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFF4A3800),
                                disabledContentColor = Color(0xFFFFD54F)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(if (isAr) "الرحلة جارية وفي طريقها 🚌" else "Voyage en cours 🚌", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    } else if (trip.availableSeats <= 0) {
                        Button(
                            onClick = { /* Sold out */ },
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFF4A1A1A),
                                disabledContentColor = Color(0xFFFF8A80)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(if (isAr) "المقاعد مكتملة 🛑" else "Complet 🛑", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                onBookClick()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("trip_detail_book_button")
                        ) {
                            Icon(Icons.Default.EventSeat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isAr) "احجز مقعدك الآن 🎟️" else "Réserver ma place 🎟️", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("trip_detail_close_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, closeBtnBorder)
                ) {
                    Text(if (isAr) "إغلاق" else "Fermer", color = closeBtnText, fontSize = 13.sp)
                }
            }
        }
    }
}
