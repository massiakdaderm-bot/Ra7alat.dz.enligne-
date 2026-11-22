package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ads.AdMobManager
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.ui.components.AppLottieEmptyState
import com.example.ui.components.MiniBadgeType
import com.example.ui.components.MiniTripCard
import com.example.ui.theme.AppThemePalette

/**
 * Single distinct trip type category with its own unique color and icon.
 */
data class HomeTripTypeCategory(
    val id: String,
    val nameAr: String,
    val nameFr: String,
    val icon: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    trips: List<Trip>,
    onTripClick: (Trip) -> Unit,
    onFavoriteToggle: (Trip) -> Unit,
    onChatClick: (Trip) -> Unit = {},
    onOpenFilters: () -> Unit,
    onRefresh: () -> Unit = {},
    onEditTrip: ((Trip) -> Unit)? = null,
    onDeleteTrip: ((Trip) -> Unit)? = null,
    onManageTrip: ((Trip) -> Unit)? = null,
    userProfile: UserProfile = UserProfile(),
    userBookings: List<com.example.data.model.Booking> = emptyList(),
    selectedWilaya: String = "الكل",
    onWilayaSelected: (String) -> Unit = {},
    selectedType: String = "الكل",
    onTypeSelected: (String) -> Unit = {},
    selectedPrice: String = "الكل",
    themePalette: AppThemePalette = AppThemePalette.WHITE_ROYAL,
    isAr: Boolean = false
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val emeraldAccent = themePalette.accentColor
    val searchBarBg = if (themePalette.isLight) Color(0xFFF8FAFC) else themePalette.darkSurfaceBg
    val textPrimary = themePalette.textPrimary
    val subTextColor = if (themePalette.isLight) Color(0xFF64748B) else Color(0xFF94A3B8)
    val cardBorderColor = if (themePalette.isLight) Color(0xFFE2E8F0) else themePalette.darkBorderColor

    // Single consolidated bar of trip types, each with its dedicated signature color
    val categories = remember(emeraldAccent) {
        listOf(
            HomeTripTypeCategory(
                id = "ALL",
                nameAr = "الكل",
                nameFr = "Tous",
                icon = "🧭",
                color = emeraldAccent
            ),
            HomeTripTypeCategory(
                id = "CAMPING",
                nameAr = "خيامة",
                nameFr = "Camping",
                icon = "⛺",
                color = Color(0xFFEA580C) // Warm terracotta orange
            ),
            HomeTripTypeCategory(
                id = "TOURISME",
                nameAr = "سياحية",
                nameFr = "Tourisme",
                icon = "🧳",
                color = Color(0xFF0D9488) // Deep teal
            ),
            HomeTripTypeCategory(
                id = "PLAGE",
                nameAr = "بحرية",
                nameFr = "Bord de mer",
                icon = "🏖️",
                color = Color(0xFF0284C7) // Ocean azure blue
            ),
            HomeTripTypeCategory(
                id = "EXPLORATION",
                nameAr = "استكشافية",
                nameFr = "Exploration",
                icon = "🧭",
                color = Color(0xFFE11D48) // Crimson ruby
            ),
            HomeTripTypeCategory(
                id = "MONTAGNE",
                nameAr = "جبلية",
                nameFr = "Montagne",
                icon = "🏔️",
                color = Color(0xFF16A34A) // Alpine pine green
            ),
            HomeTripTypeCategory(
                id = "SAHARA",
                nameAr = "صحراوية",
                nameFr = "Sahara",
                icon = "🏜️",
                color = Color(0xFFD97706) // Desert sand amber
            ),
            HomeTripTypeCategory(
                id = "CULTURE",
                nameAr = "ثقافية",
                nameFr = "Culture",
                icon = "🏛️",
                color = Color(0xFF7C3AED) // Royal violet
            )
        )
    }

    // 1. General Filters & Search
    val filteredTrips = trips.filter { trip ->
        val matchesWilaya = selectedWilaya == "الكل" ||
                trip.departureWilaya.contains(selectedWilaya, ignoreCase = true) ||
                trip.destinationWilaya.contains(selectedWilaya, ignoreCase = true)

        val matchesType = selectedType == "الكل" ||
                trip.tripType.contains(selectedType.substringBefore(" "), ignoreCase = true)

        val matchesPrice = when (selectedPrice) {
            "أقل من 15,000 دج" -> trip.priceDzd < 15000
            "15,000 - 30,000 دج" -> trip.priceDzd in 15000.0..30000.0
            "أكثر من 30,000 دج" -> trip.priceDzd > 30000
            else -> true
        }

        val matchesSearch = searchQuery.isBlank() ||
                trip.title.contains(searchQuery, ignoreCase = true) ||
                trip.destinationWilaya.contains(searchQuery, ignoreCase = true) ||
                trip.destinationPlace.contains(searchQuery, ignoreCase = true) ||
                trip.organizerName.contains(searchQuery, ignoreCase = true)

        val matchesCategory = when (selectedCategory) {
            "ALL" -> true
            "CAMPING" -> trip.tripType.contains("خيام", ignoreCase = true) ||
                    trip.tripType.contains("تخييم", ignoreCase = true) ||
                    trip.description.contains("camp", ignoreCase = true) ||
                    trip.description.contains("bivouac", ignoreCase = true) ||
                    trip.description.contains("خيام", ignoreCase = true)
            "TOURISME" -> trip.tripType.contains("سياح", ignoreCase = true) ||
                    trip.tripType.contains("touris", ignoreCase = true) ||
                    trip.tripType.contains("زيارة", ignoreCase = true)
            "PLAGE" -> trip.tripType.contains("بحر", ignoreCase = true) ||
                    trip.tripType.contains("شاطئ", ignoreCase = true) ||
                    trip.tripType.contains("plage", ignoreCase = true) ||
                    trip.destinationWilaya.contains("جيجل") ||
                    trip.destinationWilaya.contains("عنابة") ||
                    trip.destinationWilaya.contains("وهران") ||
                    trip.destinationWilaya.contains("سكيكدة") ||
                    trip.destinationWilaya.contains("مستغانم")
            "EXPLORATION" -> trip.tripType.contains("استكشاف", ignoreCase = true) ||
                    trip.tripType.contains("مغامر", ignoreCase = true) ||
                    trip.tripType.contains("explor", ignoreCase = true) ||
                    trip.tripType.contains("aventure", ignoreCase = true) ||
                    trip.description.contains("استكشاف", ignoreCase = true)
            "MONTAGNE" -> trip.tripType.contains("جبل", ignoreCase = true) ||
                    trip.tripType.contains("montagne", ignoreCase = true) ||
                    trip.destinationWilaya.contains("تيكجدة") ||
                    trip.destinationWilaya.contains("البويرة") ||
                    trip.destinationWilaya.contains("تيزي وزو") ||
                    trip.destinationWilaya.contains("بجاية") ||
                    trip.destinationWilaya.contains("البليدة")
            "SAHARA" -> trip.tripType.contains("صحرا", ignoreCase = true) ||
                    trip.tripType.contains("sahara", ignoreCase = true) ||
                    trip.destinationWilaya.contains("تمنراست") ||
                    trip.destinationWilaya.contains("بشار") ||
                    trip.destinationWilaya.contains("غرداية") ||
                    trip.destinationWilaya.contains("تيميمون") ||
                    trip.destinationWilaya.contains("جانت") ||
                    trip.destinationWilaya.contains("طاسيلي")
            "CULTURE" -> trip.tripType.contains("ثقاف", ignoreCase = true) ||
                    trip.tripType.contains("تاريخ", ignoreCase = true) ||
                    trip.tripType.contains("culture", ignoreCase = true) ||
                    trip.tripType.contains("patrimoine", ignoreCase = true) ||
                    trip.description.contains("قصبة", ignoreCase = true) ||
                    trip.description.contains("تاريخ", ignoreCase = true)
            else -> true
        }

        matchesWilaya && matchesType && matchesPrice && matchesSearch && matchesCategory
    }

    // 2. Partition into the two visual sections: Sponsorisé & Prochainement (Mini trips 2-per-row)
    val explicitlySponsored = filteredTrips.filter {
        it.isFeatured || it.organizerBadge.contains("SPONSOR", ignoreCase = true) || it.isOrganizerGoldenVerified
    }

    val (sponsoredTrips, prochainementTrips) = if (explicitlySponsored.isNotEmpty()) {
        Pair(explicitlySponsored, filteredTrips.filter { it !in explicitlySponsored })
    } else if (filteredTrips.size >= 2) {
        // Design prototype mode (as requested: "هذا مجرد تصميم فقط الان"): highlight first trip as sponsored demo
        Pair(filteredTrips.take(1), filteredTrips.drop(1))
    } else {
        Pair(filteredTrips, emptyList())
    }

    val activeCategoryItem = categories.find { it.id == selectedCategory } ?: categories.first()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_trip_list"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // =========================================================================
        // 1. MODERN TRAVEL SEARCH BAR (Inspired by user screenshot & modern travel apps)
        // =========================================================================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pill Container with Search Input and Integrated Filter Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(searchBarBg)
                        .border(1.dp, cardBorderColor, RoundedCornerShape(28.dp))
                        .padding(start = 12.dp, end = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search icon in soft circular container
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(emeraldAccent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = emeraldAccent,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Text input field with travel inspiration hints
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Column(verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = if (isAr) "أين وجهتك القادمة؟" else "Où voulez-vous aller ?",
                                        color = textPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = if (isAr) "وجهة، نشاط، أو مدينة..." else "Destination, activité, wilaya...",
                                        color = subTextColor,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = textPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(emeraldAccent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("home_search_input")
                            )
                        }

                        // Clear search query button
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = subTextColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                        }

                        // Integrated Filter Button inside Search Bar
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (themePalette.isLight) Color(0xFFFFFFFF) else themePalette.darkCardBg)
                                .border(1.dp, cardBorderColor, CircleShape)
                                .clickable { onOpenFilters() }
                                .testTag("home_search_filter_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = emeraldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Circular Refresh Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(searchBarBg)
                        .border(1.dp, cardBorderColor, CircleShape)
                        .clickable { onRefresh() }
                        .testTag("home_search_refresh_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = emeraldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // =========================================================================
        // 2. SINGLE HORIZONTAL TRIP TYPES BAR (شريط واحد لأنواع الرحلات وكل واحد بلونه)
        // =========================================================================
        item {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat.id
                    val itemColor = cat.color

                    val chipBg by animateColorAsState(
                        targetValue = if (isSelected) itemColor else itemColor.copy(alpha = if (themePalette.isLight) 0.08f else 0.16f),
                        label = "cat_chip_bg_${cat.id}"
                    )
                    val chipTextColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else (if (themePalette.isLight) itemColor else itemColor.copy(alpha = 0.95f)),
                        label = "cat_chip_text_${cat.id}"
                    )
                    val borderColor = if (isSelected) itemColor else itemColor.copy(alpha = if (themePalette.isLight) 0.35f else 0.45f)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(chipBg)
                            .border(BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor), RoundedCornerShape(20.dp))
                            .clickable {
                                selectedCategory = if (selectedCategory == cat.id && cat.id != "ALL") "ALL" else cat.id
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("category_chip_${cat.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Mini circular badge for the icon
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color.White.copy(alpha = 0.22f)
                                        else itemColor.copy(alpha = 0.16f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat.icon,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(7.dp))

                            Text(
                                text = if (isAr) cat.nameAr else cat.nameFr,
                                color = chipTextColor,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                            )

                            // Small dot indicator when selected
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // =========================================================================
        // 3. ACTIVE FILTER SUMMARY ROW
        // =========================================================================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(activeCategoryItem.color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedCategory == "ALL") {
                            if (isAr) "كافة الرحلات المتوفرة (${filteredTrips.size})"
                            else "Tous les voyages (${filteredTrips.size})"
                        } else {
                            if (isAr) "رحلات ${activeCategoryItem.nameAr} ${activeCategoryItem.icon} (${filteredTrips.size})"
                            else "Voyages ${activeCategoryItem.nameFr} ${activeCategoryItem.icon} (${filteredTrips.size})"
                        },
                        color = subTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Quick clear filter button if category or search is active
                if (selectedCategory != "ALL" || searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(activeCategoryItem.color.copy(alpha = 0.12f))
                            .clickable {
                                selectedCategory = "ALL"
                                searchQuery = ""
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isAr) "إلغاء التصفية ✕" else "Réinitialiser ✕",
                            color = activeCategoryItem.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // =========================================================================
        // 4. EMPTY STATE
        // =========================================================================
        if (filteredTrips.isEmpty()) {
            item {
                AppLottieEmptyState(
                    rawRes = R.raw.lottie_empty_trips,
                    badgeText = if (isAr) "0 رحلات متوفرة" else "0 voyage trouvé",
                    title = if (isAr) "لا توجد رحلات مطابقة لمعايير البحث" else "Aucun voyage ne correspond à vos critères",
                    description = if (isAr)
                        "جرّب اختيار نوع رحلة آخر، تغيير البحث، أو إعادة ضبط الفلاتر للاطلاع على كل العروض."
                    else
                        "Essayez de choisir une autre catégorie ou de réinitialiser la recherche pour découvrir nos aventures.",
                    actionLabel = if (searchQuery.isNotBlank() || selectedCategory != "ALL" || selectedWilaya != "الكل") {
                        if (isAr) "عرض جميع الرحلات 🔄" else "Afficher tous les voyages 🔄"
                    } else null,
                    onActionClick = {
                        searchQuery = ""
                        selectedCategory = "ALL"
                        onWilayaSelected("الكل")
                        onTypeSelected("الكل")
                    },
                    testTag = "home_empty_trips_lottie"
                )
            }
        }

        // =========================================================================
        // SECTION 1: SPONSORISÉ ⭐ (رحلتين في سطر واحد - Mini Trip Cards)
        // =========================================================================
        if (sponsoredTrips.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SectionHeaderRow(
                    title = if (isAr) "رحلات ممولة (Sponsorisé) ⭐" else "Voyages Sponsorisés ⭐",
                    subtitle = if (isAr) "عروض مميزة وحصرية برعاية شركاء راكالات" else "Offres exclusives et partenaires officiels",
                    count = sponsoredTrips.size,
                    accentColor = Color(0xFFFFB300),
                    textPrimary = textPrimary,
                    subTextColor = subTextColor,
                    isAr = isAr
                )
            }

            // Render Sponsored trips 2-per-row
            val sponsoredPairs = sponsoredTrips.chunked(2)
            items(sponsoredPairs, key = { "sponsor_${it[0].id}_${it.getOrNull(1)?.id ?: 0}" }) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1
                    Box(modifier = Modifier.weight(1f)) {
                        MiniTripCard(
                            trip = pair[0],
                            onClick = {
                                AdMobManager.incrementTripInteraction(context)
                                onTripClick(pair[0])
                            },
                            onFavoriteToggle = { onFavoriteToggle(pair[0]) },
                            isAr = isAr,
                            isOwner = userProfile.isOwnerOf(pair[0]),
                            badgeType = MiniBadgeType.SPONSORED,
                            onEditClick = if (userProfile.isOwnerOf(pair[0]) && onEditTrip != null) { { onEditTrip(pair[0]) } } else null,
                            onDeleteClick = if (userProfile.isOwnerOf(pair[0]) && onDeleteTrip != null) { { onDeleteTrip(pair[0]) } } else null
                        )
                    }

                    // Card 2 (or Spacer to maintain exact 50% width)
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            MiniTripCard(
                                trip = pair[1],
                                onClick = {
                                    AdMobManager.incrementTripInteraction(context)
                                    onTripClick(pair[1])
                                },
                                onFavoriteToggle = { onFavoriteToggle(pair[1]) },
                                isAr = isAr,
                                isOwner = userProfile.isOwnerOf(pair[1]),
                                badgeType = MiniBadgeType.SPONSORED,
                                onEditClick = if (userProfile.isOwnerOf(pair[1]) && onEditTrip != null) { { onEditTrip(pair[1]) } } else null,
                                onDeleteClick = if (userProfile.isOwnerOf(pair[1]) && onDeleteTrip != null) { { onDeleteTrip(pair[1]) } } else null
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 2: PROCHAINEMENT ⏳ (رحلتين في سطر واحد - Mini Trip Cards)
        // =========================================================================
        if (prochainementTrips.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeaderRow(
                    title = if (isAr) "الرحلات القادمة (Prochainement) ⏳" else "Départs Prochains ⏳",
                    subtitle = if (isAr) "رحلات تنطلق قريباً، احجز مقعدك الآن" else "Départs confirmés bientôt, réservez vos places",
                    count = prochainementTrips.size,
                    accentColor = Color(0xFF6366F1),
                    textPrimary = textPrimary,
                    subTextColor = subTextColor,
                    isAr = isAr
                )
            }

            // Render Upcoming trips 2-per-row
            val prochainementPairs = prochainementTrips.chunked(2)
            items(prochainementPairs, key = { "soon_${it[0].id}_${it.getOrNull(1)?.id ?: 0}" }) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1
                    Box(modifier = Modifier.weight(1f)) {
                        MiniTripCard(
                            trip = pair[0],
                            onClick = {
                                AdMobManager.incrementTripInteraction(context)
                                onTripClick(pair[0])
                            },
                            onFavoriteToggle = { onFavoriteToggle(pair[0]) },
                            isAr = isAr,
                            isOwner = userProfile.isOwnerOf(pair[0]),
                            badgeType = MiniBadgeType.PROCHAINEMENT,
                            onEditClick = if (userProfile.isOwnerOf(pair[0]) && onEditTrip != null) { { onEditTrip(pair[0]) } } else null,
                            onDeleteClick = if (userProfile.isOwnerOf(pair[0]) && onDeleteTrip != null) { { onDeleteTrip(pair[0]) } } else null
                        )
                    }

                    // Card 2
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            MiniTripCard(
                                trip = pair[1],
                                onClick = {
                                    AdMobManager.incrementTripInteraction(context)
                                    onTripClick(pair[1])
                                },
                                onFavoriteToggle = { onFavoriteToggle(pair[1]) },
                                isAr = isAr,
                                isOwner = userProfile.isOwnerOf(pair[1]),
                                badgeType = MiniBadgeType.PROCHAINEMENT,
                                onEditClick = if (userProfile.isOwnerOf(pair[1]) && onEditTrip != null) { { onEditTrip(pair[1]) } } else null,
                                onDeleteClick = if (userProfile.isOwnerOf(pair[1]) && onDeleteTrip != null) { { onDeleteTrip(pair[1]) } } else null
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // =========================================================================
        // 5. THEME FOOTER BADGE
        // =========================================================================
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (themePalette.isLight) Color(0xFFF1F5F9) else Color(0xFF282E2B).copy(alpha = 0.95f))
                        .then(if (themePalette.isLight) Modifier.border(1.dp, themePalette.darkBorderColor, RoundedCornerShape(20.dp)) else Modifier)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ra7alat_logo_icon_1787149237815),
                            contentDescription = "Theme Icon",
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "السمة المطبقة : ${themePalette.nameAr} ${themePalette.emoji}"
                            else "Thème appliqué : ${themePalette.nameFr} ${themePalette.emoji}",
                            color = textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    subtitle: String,
    count: Int,
    accentColor: Color,
    textPrimary: Color,
    subTextColor: Color,
    isAr: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isAr) "$count رحلة" else "$count voyages",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            color = subTextColor,
            fontSize = 11.sp
        )
    }
}
