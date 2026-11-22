package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Trip
import com.example.ui.components.AppLottieEmptyState
import com.example.ui.components.TripCard

@Composable
fun FavoritesScreen(
    favoriteTrips: List<Trip>,
    onTripClick: (Trip) -> Unit,
    onRemoveFavorite: (Trip) -> Unit,
    onChatClick: (Trip) -> Unit = {},
    isAr: Boolean = true
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("favorites_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = if (isAr) "رحلاتك المفضلة ❤️ (${favoriteTrips.size})" else "Mes Favoris ❤️ (${favoriteTrips.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (favoriteTrips.isEmpty()) {
            item {
                AppLottieEmptyState(
                    rawRes = R.raw.lottie_empty_favorites,
                    badgeText = if (isAr) "قائمتك المفضلة فارغة" else "Favoris vides",
                    title = if (isAr) "لم تضف أي رحلة للمفضلة بعد" else "Votre liste de favoris est vide",
                    description = if (isAr)
                        "اضغط على أيقونة القلب ❤️ في بطاقات الرحلات لحفظ الرحلات التي تعجبك والعودة إليها في أي وقت."
                    else
                        "Cliquez sur le coeur ❤️ pour enregistrer vos voyages préférés et les retrouver facilement.",
                    animationSize = 140.dp,
                    testTag = "favorites_empty_lottie"
                )
            }
        } else {
            items(favoriteTrips, key = { it.id }) { trip ->
                TripCard(
                    trip = trip,
                    onClick = { onTripClick(trip) },
                    onFavoriteToggle = { onRemoveFavorite(trip) },
                    onChatClick = { onChatClick(trip) },
                    isAr = isAr,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
