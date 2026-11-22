package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAppThemePalette

enum class AppScreen {
    HOME, MY_TRIPS, ADD_TRIP, FAVORITES, PROFILE
}

private data class NavItemData(
    val screen: AppScreen,
    val title: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

@Composable
fun AppBottomNav(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    isAr: Boolean = false,
    isAgency: Boolean = false
) {
    val palette = LocalAppThemePalette.current

    // Dynamically reactive colors connected to the active theme palette
    val navBackground by animateColorAsState(
        targetValue = if (palette.isLight) {
            Color.White.copy(alpha = 0.96f)
        } else {
            palette.darkCardBg.copy(alpha = 0.94f)
        },
        label = "nav_bg"
    )

    val barBorderColor by animateColorAsState(
        targetValue = if (palette.isLight) {
            palette.darkBorderColor
        } else {
            palette.darkBorderColor.copy(alpha = 0.65f)
        },
        label = "bar_border"
    )

    val selectedPillBg by animateColorAsState(
        targetValue = if (palette.isLight) {
            palette.primaryColor.copy(alpha = 0.14f)
        } else {
            palette.accentColor.copy(alpha = 0.28f)
        },
        label = "pill_bg"
    )

    val selectedPillBorder by animateColorAsState(
        targetValue = if (palette.isLight) {
            palette.primaryColor.copy(alpha = 0.28f)
        } else {
            palette.accentColor.copy(alpha = 0.42f)
        },
        label = "pill_border"
    )

    val selectedContentColor by animateColorAsState(
        targetValue = if (palette.isLight) {
            palette.primaryColor
        } else {
            Color.White
        },
        label = "selected_content"
    )

    val unselectedColor by animateColorAsState(
        targetValue = if (palette.isLight) {
            Color(0xFF64748B)
        } else {
            palette.mutedTextColor
        },
        label = "unselected_content"
    )

    val fabColorStart by animateColorAsState(
        targetValue = palette.primaryLight,
        label = "fab_start"
    )

    val fabColorEnd by animateColorAsState(
        targetValue = palette.accentColor,
        label = "fab_end"
    )

    // Outer Box allowing the center button to rise above the floating bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .height(76.dp)
            .testTag("app_bottom_navigation"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Pill Bar Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            color = navBackground,
            shadowElevation = if (palette.isLight) 6.dp else 12.dp,
            border = BorderStroke(1.dp, barBorderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. HOME
                val isHomeSelected = currentScreen == AppScreen.HOME
                NavTabItem(
                    title = if (isAr) "الرئيسية" else "Accueil",
                    icon = Icons.Default.Home,
                    isSelected = isHomeSelected,
                    selectedBg = selectedPillBg,
                    selectedBorder = selectedPillBorder,
                    selectedContent = selectedContentColor,
                    unselectedContent = unselectedColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onScreenSelected(AppScreen.HOME) },
                    testTag = "bottom_nav_item_home"
                )

                // 2. MY TRIPS
                val isTripsSelected = currentScreen == AppScreen.MY_TRIPS
                NavTabItem(
                    title = if (isAr) "رحلاتي" else "Voyages",
                    icon = Icons.Default.ConfirmationNumber,
                    isSelected = isTripsSelected,
                    selectedBg = selectedPillBg,
                    selectedBorder = selectedPillBorder,
                    selectedContent = selectedContentColor,
                    unselectedContent = unselectedColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onScreenSelected(AppScreen.MY_TRIPS) },
                    testTag = "bottom_nav_item_my_trips"
                )

                // 3. CENTER SPACER (Leaves room for the elevated floating + button)
                Spacer(modifier = Modifier.weight(1.1f))

                // 4. FAVORITES
                val isFavSelected = currentScreen == AppScreen.FAVORITES
                NavTabItem(
                    title = if (isAr) "المفضلة" else "Favoris",
                    icon = if (isFavSelected) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    isSelected = isFavSelected,
                    selectedBg = selectedPillBg,
                    selectedBorder = selectedPillBorder,
                    selectedContent = selectedContentColor,
                    unselectedContent = unselectedColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onScreenSelected(AppScreen.FAVORITES) },
                    testTag = "bottom_nav_item_favorites"
                )

                // 5. PROFILE
                val isProfileSelected = currentScreen == AppScreen.PROFILE
                NavTabItem(
                    title = if (isAr) "حسابي" else "Profil",
                    icon = if (isProfileSelected) Icons.Default.Person else Icons.Default.PersonOutline,
                    isSelected = isProfileSelected,
                    selectedBg = selectedPillBg,
                    selectedBorder = selectedPillBorder,
                    selectedContent = selectedContentColor,
                    unselectedContent = unselectedColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onScreenSelected(AppScreen.PROFILE) },
                    testTag = "bottom_nav_item_profile"
                )
            }
        }

        // The Overlapping Glowing Center (+) Button Rising Above the Bar
        val fabInteractionSource = remember { MutableInteractionSource() }
        val isFabPressed by fabInteractionSource.collectIsPressedAsState()
        val fabScale by animateFloatAsState(
            targetValue = if (isFabPressed) 0.88f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "fab_scale"
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-2).dp)
                .scale(fabScale)
                .clickable(
                    interactionSource = fabInteractionSource,
                    indication = null
                ) {
                    onScreenSelected(AppScreen.ADD_TRIP)
                }
                .testTag("bottom_nav_item_add_trip"),
            contentAlignment = Alignment.Center
        ) {
            // Glowing halo layer
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(fabColorEnd.copy(alpha = 0.26f))
            )

            // Center circular button with gradient and white border
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = fabColorEnd,
                        ambientColor = fabColorEnd
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(fabColorStart, fabColorEnd))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isAr) "نشر رحلة" else "Publier",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedBg: Color,
    selectedBorder: Color,
    selectedContent: Color,
    unselectedContent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Active Pill Capsule
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(selectedBg)
                    .border(1.dp, selectedBorder, RoundedCornerShape(27.dp))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = selectedContent,
                        modifier = Modifier.size(21.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedContent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            // Inactive state with icon & title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = unselectedContent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = unselectedContent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
