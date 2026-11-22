package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAppThemePalette

@Composable
fun AppTopBar(
    title: String = "RA7ALAT DZ 🇩🇿",
    subtitle: String = "EXPLORER L'ALGÉRIE",
    userName: String = "ra7alt",
    isAr: Boolean = false,
    isAgency: Boolean = true,
    onFilterClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onLangClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onNotificationPrefsClick: () -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    showFilters: Boolean = true,
    showDashboard: Boolean = true
) {
    val palette = LocalAppThemePalette.current
    val emeraldAccent = palette.accentColor
    val darkButtonBg = if (palette.isLight) Color(0xFFF1F5F9) else palette.darkSurfaceBg
    val buttonBorderColor = if (palette.isLight) palette.darkBorderColor else Color.Transparent
    val subtitleColor = palette.accentColor

    CompositionLocalProvider(LocalLayoutDirection provides (if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .heightIn(max = 56.dp)
                .testTag("app_top_bar"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title & Subtitle column on the Start (Weighted to allow space for actions)
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = if (isAr) "استكشف الجزائر" else "EXPLORER L'ALGÉRIE",
                    color = subtitleColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isAr) "🇩🇿 RA7ALAT DZ" else "RA7ALAT DZ 🇩🇿",
                    color = palette.textPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Circular Actions Row on the End (Consistent 34dp circular buttons)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // 1. Theme Button (Palette)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(darkButtonBg)
                        .border(1.dp, buttonBorderColor, CircleShape)
                        .clickable { onThemeClick() }
                        .testTag("top_bar_theme_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = "Themes",
                        tint = emeraldAccent,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 2. Language Toggle Button (AR / FR)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(darkButtonBg)
                        .border(1.dp, buttonBorderColor, CircleShape)
                        .clickable { onLangClick() }
                        .testTag("top_bar_language_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAr) "FR" else "AR",
                        color = emeraldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 3. Refresh Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(darkButtonBg)
                        .border(1.dp, buttonBorderColor, CircleShape)
                        .clickable { onRefreshClick() }
                        .testTag("top_bar_refresh_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = emeraldAccent,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 4. Dashboard Grid Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(darkButtonBg)
                        .border(1.dp, buttonBorderColor, CircleShape)
                        .clickable { onDashboardClick() }
                        .testTag("top_bar_dashboard_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Dashboard",
                        tint = emeraldAccent,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 5. User Profile Button (Consistent 34dp circular button - never wraps or elongates)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isAgency) emeraldAccent.copy(alpha = 0.18f) else darkButtonBg)
                        .border(
                            width = if (isAgency) 1.5.dp else if (palette.isLight) 1.dp else 0.dp,
                            color = if (isAgency) emeraldAccent else buttonBorderColor,
                            shape = CircleShape
                        )
                        .clickable { onProfileClick() }
                        .testTag("top_bar_user_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = if (isAgency) "Agence" else "User",
                        tint = emeraldAccent,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

