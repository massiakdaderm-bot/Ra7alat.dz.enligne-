package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Booking
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.ui.components.AppLottieEmptyState
import com.example.ui.theme.LocalAppThemePalette
import com.example.util.AppLanguage

@Composable
fun DashboardScreen(
    userProfile: UserProfile,
    trips: List<Trip>,
    bookings: List<Booking>,
    language: AppLanguage = AppLanguage.ARABIC,
    isAr: Boolean = true,
    onRefresh: () -> Unit,
    onTripClick: (Trip) -> Unit,
    onEditTrip: (Trip) -> Unit,
    onCancelTrip: (Trip) -> Unit = {},
    onDeleteTrip: (Trip) -> Unit,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    val palette = LocalAppThemePalette.current
    val emeraldAccent = palette.accentColor
    val darkCanvasBg = palette.darkCanvasBg
    val darkCardBg = palette.darkCardBg

    val agencyTrips = trips.filter { trip ->
        (userProfile.email.isNotBlank() && trip.organizerEmail.equals(userProfile.email, ignoreCase = true)) ||
        (userProfile.agencyName.isNotBlank() && trip.organizerName.equals(userProfile.agencyName, ignoreCase = true)) ||
        (userProfile.fullName.isNotBlank() && trip.organizerName.equals(userProfile.fullName, ignoreCase = true))
    }

    val tripIds = agencyTrips.map { it.id }.toSet()
    val agencyBookings = bookings.filter { tripIds.contains(it.tripId) }

    val totalBookings = agencyBookings.size
    val totalRevenue = agencyBookings.sumOf { it.totalPriceDzd }.toInt()
    val totalSeats = agencyTrips.sumOf { it.availableSeats }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkCanvasBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("dashboard_screen")
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF132B1E))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = emeraldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = null,
                    tint = emeraldAccent,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = if (isAr) "لوحة تحكم المنظم والرحلات" else "Tableau de Bord Organisateur",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = emeraldAccent
                    )
                    Text(
                        text = if (isAr) "إدارة رحلاتك وقائمة المسجلين" else "Gérez vos voyages et la liste des inscrits",
                        fontSize = 11.sp,
                        color = Color(0xFF8CA397)
                    )
                }
            }

            // Actualiser Button (Clean pill button on right matching screenshots)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .clickable { onRefresh() }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("dashboard_refresh_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualiser",
                        tint = Color(0xFF1B4D31),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAr) "تحديث" else "Actualiser",
                        color = Color(0xFF1B4D31),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4 Stat Cards in a Single Row (Exact match to Screenshot 1, 2, 3)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Voyages
            DashboardStatCard(
                icon = Icons.Default.Explore,
                count = "${agencyTrips.size}",
                label = if (isAr) "رحلات" else "Voyages",
                modifier = Modifier.weight(1f)
            )

            // 2. Inscrits
            DashboardStatCard(
                icon = Icons.Default.Group,
                count = "$totalBookings",
                label = if (isAr) "مسجلين" else "Inscrits",
                modifier = Modifier.weight(1f)
            )

            // 3. Places
            DashboardStatCard(
                icon = Icons.Default.EventSeat,
                count = "$totalSeats",
                label = if (isAr) "مقاعد" else "Places",
                modifier = Modifier.weight(1f)
            )

            // 4. Revenus
            val revenueDisplay = if (totalRevenue >= 1000) "${totalRevenue / 1000}k" else "$totalRevenue"
            DashboardStatCard(
                icon = Icons.Default.AttachMoney,
                count = revenueDisplay,
                label = if (isAr) "مداخيل" else "Revenus",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Trips List or Empty State
        if (agencyTrips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AppLottieEmptyState(
                    rawRes = R.raw.lottie_empty_trips,
                    badgeText = if (isAr) "لوحة الوكالة فارغة" else "Tableau de bord vide",
                    title = if (isAr) "لم تقم بإنشاء أي رحلة حتى الآن" else "Vous n'avez créé aucun voyage pour l'instant",
                    description = if (isAr)
                        "أنشئ رحلتك الأولى وتابع عدد المقاعد المحجوزة، المسجلين، والأرباح في الوقت الفعلي."
                    else
                        "Créez votre première expédition pour suivre vos passagers et réservations.",
                    actionLabel = if (isAr) "تحديث البيانات 🔄" else "Actualiser les données 🔄",
                    onActionClick = onRefresh,
                    animationSize = 140.dp,
                    testTag = "dashboard_empty_trips_lottie"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(agencyTrips, key = { it.id }) { trip ->
                    val tripBookings = bookings.filter { it.tripId == trip.id }
                    val reservedSeats = tripBookings.sumOf { it.seatsCount }

                    AgencyTripItemCard(
                        trip = trip,
                        tripBookings = tripBookings,
                        reservedSeats = reservedSeats,
                        isAr = isAr,
                        onTripClick = { onTripClick(trip) },
                        onEditTrip = { onEditTrip(trip) },
                        onCancelTrip = { onCancelTrip(trip) },
                        onDeleteTrip = { onDeleteTrip(trip) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardStatCard(
    icon: ImageVector,
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3B2B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF37474F)
            )
        }
    }
}

@Composable
private fun AgencyTripItemCard(
    trip: Trip,
    tripBookings: List<Booking>,
    reservedSeats: Int,
    isAr: Boolean,
    onTripClick: () -> Unit,
    onEditTrip: () -> Unit,
    onCancelTrip: () -> Unit,
    onDeleteTrip: () -> Unit
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_trip_card_${trip.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2217)),
        border = BorderStroke(1.dp, Color(0xFF1F4A32))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title & Places Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF163E28))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isAr) "${trip.availableSeats} مقعد" else "${trip.availableSeats} places",
                        color = Color(0xFF4EBA77),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${trip.departureWilaya} • ${trip.destinationWilaya}",
                fontSize = 12.sp,
                color = Color(0xFF8CA397)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Inscrits Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF132B1E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = Color(0xFF4EBA77),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr)
                                "${tripBookings.size} مسجلين ($reservedSeats مقاعد محجوزة)"
                            else
                                "${tripBookings.size} Inscrits ($reservedSeats places réservées)",
                            color = Color(0xFF4EBA77),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = if (isExpanded)
                            (if (isAr) "إخفاء ▲" else "Masquer ▲")
                        else
                            (if (isAr) "عرض المسجلين ▼" else "Voir inscrits ▼"),
                        color = Color(0xFF4EBA77),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (isExpanded) {
                    if (tripBookings.isEmpty()) {
                        Text(
                            text = if (isAr) "لا يوجد مسجلين حتى الآن." else "Aucun inscrit pour le moment.",
                            color = Color(0xFF8CA397),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    } else {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            tripBookings.forEach { booking ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = booking.passengerName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${booking.seatsCount} ${if (isAr) "مقاعد" else "places"} • ${booking.totalPriceDzd.toInt()} DZD",
                                            fontSize = 11.sp,
                                            color = Color(0xFF8CA397)
                                        )
                                    }

                                    if (booking.passengerPhone.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF1B4D31))
                                                .clickable {
                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.passengerPhone}"))
                                                    context.startActivity(intent)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = "Appeler",
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = Color(0xFF1F4A32), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row: [ Modifier (BLUE) ] [ Annuler (RED OUTLINE) ] [ Supprimer (GREY OUTLINE) ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Modifier Button (Solid Vibrant Blue)
                Button(
                    onClick = onEditTrip,
                    modifier = Modifier
                        .weight(1.15f)
                        .height(40.dp)
                        .testTag("dashboard_edit_trip_${trip.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0088FF),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAr) "تعديل" else "Modifier",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 2. Annuler Button (Outlined Red)
                OutlinedButton(
                    onClick = onCancelTrip,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("dashboard_cancel_trip_${trip.id}"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE53935)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53935)
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAr) "إلغاء" else "Annuler",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // 3. Supprimer Button (Outlined Grey/Red)
                OutlinedButton(
                    onClick = onDeleteTrip,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(40.dp)
                        .testTag("dashboard_delete_trip_${trip.id}"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF3F634E)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF90A4AE)
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAr) "حذف" else "Supprimer",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
