package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Booking
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.ui.components.AppLottieEmptyState
import com.example.ui.components.TripCard
import com.example.ui.theme.LocalAppThemePalette

@Composable
fun MyTripsScreen(
    userProfile: UserProfile?,
    bookings: List<Booking>,
    agencyTrips: List<Trip>,
    onBookingClick: (Booking) -> Unit,
    onTripClick: (Trip) -> Unit,
    onEditTripClick: (Trip) -> Unit,
    onDeleteTripClick: (Trip) -> Unit,
    onCancelBooking: (Booking) -> Unit = {},
    onOpenDashboard: () -> Unit = {},
    onChatClick: (Trip) -> Unit = {},
    isAr: Boolean = true
) {
    val context = LocalContext.current
    val isAgency = userProfile?.role == "AGENCY"
    var selectedTab by remember { mutableIntStateOf(0) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }

    val palette = LocalAppThemePalette.current
    val goldAccent = palette.accentColor
    val mutedTabColor = palette.mutedTextColor
    val darkScreenBg = palette.darkCanvasBg
    val cardBg = palette.darkCardBg
    val cardBorder = palette.darkBorderColor
    val userTargetPhone = "0673085612"

    val upcomingBookings = bookings.filter {
        it.status.equals("UPCOMING", ignoreCase = true) ||
                it.status.equals("CONFIRMED", ignoreCase = true) ||
                it.status.isBlank()
    }
    val completedBookings = bookings.filter { it.status.equals("COMPLETED", ignoreCase = true) }
    val cancelledBookings = bookings.filter {
        it.status.equals("CANCELLED", ignoreCase = true) ||
                it.status.equals("ANNULÉ", ignoreCase = true)
    }

    if (bookingToCancel != null) {
        val b = bookingToCancel!!
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = { Text(if (isAr) "إلغاء الحجز" else "Annuler la réservation") },
            text = {
                Text(
                    if (isAr) "هل أنت متأكد من رغبتك في إلغاء حجز الرحلة (${b.tripTitle})؟ رقم التذكرة: ${b.bookingReference}"
                    else "Voulez-vous vraiment annuler votre réservation pour (${b.tripTitle}) ? N° Billet: ${b.bookingReference}"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelBooking(b)
                        bookingToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (isAr) "تأكيد الإلغاء" else "Confirmer l'annulation")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text(if (isAr) "تراجع" else "Retour")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkScreenBg)
            .testTag("my_trips_screen")
    ) {
        // Agency Quick Banner if the user is an agency
        if (isAgency && agencyTrips.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onOpenDashboard() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132B20))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(goldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isAr) "لوحة تحكم الوكالة 🏢 (${agencyTrips.size} رحلة)" else "Tableau de Bord (${agencyTrips.size} voyages)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = if (isAr) "اضغط لإدارة الرحلات ومتابعة الحجوزات" else "Gérer vos offres et passagers",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = goldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Tabs: 3 standard passenger tabs + 4th tab for Published Trips if user is an agency / organizer
        val showAgencyTab = isAgency || agencyTrips.isNotEmpty()
        val tabs = buildList {
            add(if (isAr) "القادمة (${upcomingBookings.size})" else "À venir (${upcomingBookings.size})")
            add(if (isAr) "المكتملة (${completedBookings.size})" else "Terminées (${completedBookings.size})")
            add(if (isAr) "الملغاة (${cancelledBookings.size})" else "Annulées (${cancelledBookings.size})")
            if (showAgencyTab) {
                add(if (isAr) "رحلاتي المعروضة (${agencyTrips.size})" else "Mes Offres (${agencyTrips.size})")
            }
        }

        TabRow(
            selectedTabIndex = selectedTab.coerceIn(0, tabs.size - 1),
            containerColor = darkScreenBg,
            contentColor = goldAccent,
            indicator = { tabPositions ->
                val currentPos = tabPositions.getOrNull(selectedTab.coerceIn(0, tabPositions.size - 1))
                if (currentPos != null) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(currentPos),
                        color = goldAccent,
                        height = 3.dp
                    )
                }
            },
            divider = {
                HorizontalDivider(color = Color(0xFF1E3A2B), thickness = 1.dp)
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (isSelected) goldAccent else mutedTabColor,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        val isPublishedTripsTab = showAgencyTab && selectedTab == 3
        val activeList = when (selectedTab) {
            0 -> upcomingBookings
            1 -> completedBookings
            2 -> cancelledBookings
            else -> emptyList()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            if (isPublishedTripsTab) {
                if (agencyTrips.isEmpty()) {
                    item {
                        AppLottieEmptyState(
                            rawRes = R.raw.lottie_empty_trips,
                            badgeText = if (isAr) "عروض الوكالة" else "Offres d'agence",
                            title = if (isAr) "لم تنشر أي رحلات حتى الآن" else "Aucun voyage publié",
                            description = if (isAr)
                                "ابدأ بنشر أول رحلة سياحية لك، وحدد برنامج الرحلة، السعر، وعدد المقاعد لاستقبال المسجلين."
                            else
                                "Commencez par publier votre premier voyage pour accueillir des voyageurs.",
                            animationSize = 140.dp,
                            testTag = "my_trips_agency_empty_lottie"
                        )
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clickable { onOpenDashboard() },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF132B20)),
                            border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(goldAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isAr) "لوحة تحكم الوكالة وإدارة الحجوزات 🏢" else "Gérer mon voyage & Tableau de Bord",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isAr) "فتح الداشبورد لمتابعة المقاعد والركاب والإيرادات" else "Ouvrir le tableau de bord pour suivre les passagers",
                                            fontSize = 11.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = goldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(agencyTrips, key = { it.id }) { trip ->
                        TripCard(
                            trip = trip,
                            onClick = { onTripClick(trip) },
                            onFavoriteToggle = {},
                            onChatClick = { onChatClick(trip) },
                            isOwner = true,
                            isAr = isAr,
                            onEditClick = { onEditTripClick(trip) },
                            onDeleteClick = { onDeleteTripClick(trip) },
                            onManageClick = { onOpenDashboard() },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            } else if (activeList.isEmpty()) {
                item {
                    val emptyTitle = when (selectedTab) {
                        0 -> if (isAr) "لا توجد حجوزات قادمة" else "Aucune réservation à venir"
                        1 -> if (isAr) "لا توجد رحلات سابقة مكتملة" else "Aucun voyage terminé"
                        else -> if (isAr) "لا توجد حجوزات ملغاة" else "Aucune réservation annulée"
                    }
                    val emptyBadge = when (selectedTab) {
                        0 -> if (isAr) "تذاكر قادمة" else "À venir"
                        1 -> if (isAr) "أرشيف الرحلات" else "Historique"
                        else -> if (isAr) "ملغاة" else "Annulées"
                    }
                    AppLottieEmptyState(
                        rawRes = R.raw.lottie_empty_bookings,
                        badgeText = emptyBadge,
                        title = emptyTitle,
                        description = if (isAr)
                            "احجز مقعدك في أفضل الرحلات الاستكشافية في الجزائر واحصل على تذكرة صعودك فوراً مع باركود الصعود."
                        else
                            "Explorez nos circuits en Algérie et réservez votre place dès maintenant.",
                        animationSize = 140.dp,
                        testTag = "my_trips_bookings_empty_lottie"
                    )
                }
            } else {
                items(activeList, key = { it.id }) { booking ->
                    val displayPhone = if (booking.organizerPhone.isNotBlank()) booking.organizerPhone else userTargetPhone

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("booking_card_${booking.id}"),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, cardBorder),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row: Status badge on the Right, Ticket number on the Left (Screenshot 3)
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left: Ticket Reference DZ-611220
                                    Text(
                                        text = "رقم التذكرة: ${booking.bookingReference}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = goldAccent
                                    )

                                    // Right: Status Badge "⏳ حجز مؤكد • قادمة"
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF132B20))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "⏳",
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = when (booking.status.uppercase()) {
                                                    "COMPLETED" -> "رحلة مكتملة"
                                                    "CANCELLED" -> "حجز ملغى"
                                                    else -> "حجز مؤكد • قادمة"
                                                },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFF5E6CC)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Middle Info: Right-aligned (Screenshot 3)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                // Trip Title: voyage
                                Text(
                                    text = booking.tripTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1E5E41)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Destination
                                val dest = if (booking.destinationWilaya.isNotBlank()) booking.destinationWilaya else booking.destinationPlace
                                Text(
                                    text = "الوجهة: $dest",
                                    fontSize = 13.sp,
                                    color = Color(0xFF286548)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Departure Date
                                Text(
                                    text = "تاريخ الانطلاق: ${booking.departureDate}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF286548)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Thin white divider line (Screenshot 3)
                            HorizontalDivider(
                                color = if (palette.isLight) palette.darkBorderColor else Color.White.copy(alpha = 0.5f),
                                thickness = 0.8.dp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Bottom Row: Actions on the Left, Price/Seats on the Right (Screenshot 3)
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                // Left Actions Row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Red outline button: "إلغاء الحجز"
                                    Box(
                                        modifier = Modifier
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.Black)
                                            .border(BorderStroke(1.dp, Color(0xFFE53935)), RoundedCornerShape(12.dp))
                                            .clickable { bookingToCancel = booking }
                                            .padding(horizontal = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "إلغاء الحجز",
                                            color = Color(0xFFEF5350),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // 2. White button: Call phone number 0673085612
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .clickable {
                                                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$displayPhone"))
                                                context.startActivity(callIntent)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Call",
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // 3. Mint green button: Messages -> directly to WhatsApp for 0673085612
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFD4F5E2))
                                            .clickable {
                                                val clean = displayPhone.replace(" ", "").replace("-", "")
                                                val intlPhone = if (clean.startsWith("0")) "213" + clean.substring(1) else clean
                                                try {
                                                    val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$intlPhone"))
                                                    waIntent.setPackage("com.whatsapp")
                                                    context.startActivity(waIntent)
                                                } catch (e: Exception) {
                                                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$intlPhone"))
                                                    context.startActivity(webIntent)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Chat,
                                            contentDescription = "WhatsApp",
                                            tint = palette.primaryLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // 4. Dark QR Code button: opens boarding pass
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(palette.darkSurfaceBg)
                                            .clickable { onBookingClick(booking) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode,
                                            contentDescription = "QR Ticket",
                                            tint = goldAccent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                // Right Column: Seats & Price in Theme Accent
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "المقاعد: ${booking.seatsCount}",
                                        fontSize = 12.sp,
                                        color = palette.mutedTextColor
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "السعر: ${"%,d".format(booking.totalPriceDzd.toLong())} دج",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = goldAccent
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
}

