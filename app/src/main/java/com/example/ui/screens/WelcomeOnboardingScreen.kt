package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * World-Class Onboarding Experience for Ra7alat DZ
 * Crafted with the modern Halalbooking visual benchmark:
 * - Deep midnight navy canvas with ambient lighting and curved horizon
 * - Elegant branding with dynamic highlighted typography
 * - Verified feature checkmark capsules
 * - 3D perspective card deck with gentle floating physics animation
 * - Verified traveler review cards with amber rating badges
 * - Smooth gesture-driven HorizontalPager & morphing pill indicators
 */
@Composable
fun WelcomeOnboardingScreen(
    onFinish: () -> Unit,
    isAr: Boolean = true,
    onToggleLanguage: (() -> Unit)? = null
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Smooth floating animation for cards
    val infiniteTransition = rememberInfiniteTransition(label = "floatingDeck")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "floatAngle"
    )
    val floatYOffset = (sin(floatAnim.toDouble()) * 7.0).dp

    Dialog(
        onDismissRequest = { /* Must use action button or skip */ },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("welcome_onboarding_screen"),
            color = Color(0xFF071426)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Layer 1: Atmospheric Deep Midnight Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF051120),
                                    Color(0xFF092036),
                                    Color(0xFF0F314D),
                                    Color(0xFFE2E8F0),
                                    Color(0xFFFFFFFF)
                                ),
                                startY = 0f,
                                endY = 2200f
                            )
                        )
                )

                // Layer 2: Curved White Base Horizon at the bottom (Exactly like Halalbooking)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x00FFFFFF),
                                    Color(0xCCFFFFFF),
                                    Color(0xFFFFFFFF)
                                )
                            )
                        )
                )

                // Main Content Column
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar: Brand Logo & Language Switcher & Skip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Language Switcher Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0x2BFFFFFF),
                            border = BorderStroke(1.dp, Color(0x40FFFFFF)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onToggleLanguage?.invoke() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isAr) "🇫🇷 Français" else "🇩🇿 العربية",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Logo in center
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Ra7alat",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00D2B4),
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "DZ",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFA726),
                                letterSpacing = (-0.5).sp
                            )
                        }

                        // Skip Action
                        TextButton(
                            onClick = onFinish,
                            modifier = Modifier.testTag("onboarding_skip_button")
                        ) {
                            Text(
                                text = if (isAr) "تخطي" else "Passer",
                                color = Color(0xCCFFFFFF),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Swipeable Pager for the 3 slides
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) { pageIndex ->
                        when (pageIndex) {
                            0 -> DestinationDeckSlide(
                                isAr = isAr,
                                floatYOffset = floatYOffset
                            )
                            1 -> TrustedAgenciesSlide(
                                isAr = isAr,
                                floatYOffset = floatYOffset
                            )
                            2 -> CommunityReviewsSlide(
                                isAr = isAr
                            )
                        }
                    }

                    // Bottom Navigation Bar: Morphing Pill Indicators + "Get Started" CTA Button
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Smooth Morphing Indicators (Exact Halalbooking shape: pill for active, dots for inactive)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) { index ->
                                val isSelected = pagerState.currentPage == index
                                val pillWidth by animateDpAsState(
                                    targetValue = if (isSelected) 30.dp else 7.dp,
                                    animationSpec = tween(durationMillis = 300),
                                    label = "pillWidth"
                                )
                                Box(
                                    modifier = Modifier
                                        .height(7.dp)
                                        .width(pillWidth)
                                        .clip(RoundedCornerShape(3.5.dp))
                                        .background(
                                            if (isSelected) Color(0xFF00A699) else Color(0xFFCBD5E1)
                                        )
                                        .clickable {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Large Action Button (Deep Emerald Teal Pill like Halalbooking)
                        Button(
                            onClick = {
                                if (pagerState.currentPage < 2) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                } else {
                                    onFinish()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .shadow(8.dp, RoundedCornerShape(27.dp), spotColor = Color(0xFF00A699).copy(alpha = 0.4f))
                                .testTag("onboarding_next_button"),
                            shape = RoundedCornerShape(27.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00A699),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (pagerState.currentPage == 2) {
                                    if (isAr) "ابدأ رحلتك الآن 🇩🇿" else "Commencer maintenant 🇩🇿"
                                } else {
                                    if (isAr) "استكشف المزيد" else "Get started"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// SLIDE 1: NATURAL WONDERS & 3D PHOTO DECK
// -------------------------------------------------------------------------
@Composable
private fun DestinationDeckSlide(
    isAr: Boolean,
    floatYOffset: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Frosted Pill Tag
        FrostedHeaderBadge(
            text = if (isAr) "• عِش سحر الجزائر الحقيقي" else "• HOLIDAYS WITH PEACE OF MIND"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Headline with italicized accent word
        Text(
            text = buildAnnotatedString {
                if (isAr) {
                    append("استكشف أروع ")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF00D2B4))) {
                        append("المغامرات")
                    }
                    append(" في 58 ولاية")
                } else {
                    append("Taking care of your ")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF00D2B4))) {
                        append("travel")
                    }
                    append(" in Algeria")
                }
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 31.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = if (isAr)
                "رحلات صحراوية، شواطئ عذراء، قمم ثلجية وجولات عائلية - بنقرة واحدة."
            else
                "Sahara dunes, turquoise Mediterranean coves and mountain peaks - found with a tap.",
            fontSize = 13.5.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Verified Feature Chips (White capsules with teal checkmark)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeatureCheckPill(if (isAr) "صحراء طاسيلي وجانت" else "Sahara & Tassili")
                FeatureCheckPill(if (isAr) "شواطئ فيروزية عذراء" else "Turquoise beaches")
            }
            FeatureCheckPill(if (isAr) "قمم جرجرة وتيكجدة الثلجية" else "Djurdjura & Tikjda peaks")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3D Perspective Card Deck with realistic photos & bottom ribbon labels
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val cardWidth = (maxWidth * 0.58f).coerceIn(190.dp, 240.dp)
            val cardHeight = (maxHeight * 0.90f).coerceIn(210.dp, 290.dp)

            // Left Tilted Card (Mediterranean Coast)
            PerspectiveCard(
                imageRes = R.drawable.algerian_turquoise_coast_1789330536079,
                label = if (isAr) "• شواطئ البحر الأبيض" else "• Turquoise coast",
                modifier = Modifier
                    .offset(x = (-cardWidth * 0.55f), y = 14.dp)
                    .width(cardWidth * 0.88f)
                    .height(cardHeight * 0.88f)
                    .graphicsLayer {
                        rotationZ = -6.5f
                        rotationY = 12f
                        alpha = 0.88f
                    }
            )

            // Right Tilted Card (Djurdjura Snow Peaks)
            PerspectiveCard(
                imageRes = R.drawable.tikjda_mountain_trip_1787149263505,
                label = if (isAr) "• قمم جرجرة وتيكجدة" else "• Tikjda snow peaks",
                modifier = Modifier
                    .offset(x = (cardWidth * 0.55f), y = 14.dp)
                    .width(cardWidth * 0.88f)
                    .height(cardHeight * 0.88f)
                    .graphicsLayer {
                        rotationZ = 6.5f
                        rotationY = -12f
                        alpha = 0.88f
                    }
            )

            // Center Elevated Floating Card (Sahara Taghit Sunset)
            PerspectiveCard(
                imageRes = R.drawable.sahara_taghit_trip_1787149248284,
                label = if (isAr) "• سحر صحراء طاسيلي وتاغيت" else "• Sahara Taghit dunes",
                modifier = Modifier
                    .offset(y = floatYOffset)
                    .width(cardWidth)
                    .height(cardHeight)
                    .shadow(20.dp, RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.45f))
                    .graphicsLayer {
                        scaleX = 1.04f
                        scaleY = 1.04f
                    }
            )
        }
    }
}

// -------------------------------------------------------------------------
// SLIDE 2: TRUSTED AGENCIES & VIP TOURISM
// -------------------------------------------------------------------------
@Composable
private fun TrustedAgenciesSlide(
    isAr: Boolean,
    floatYOffset: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Frosted Pill Tag
        FrostedHeaderBadge(
            text = if (isAr) "• تنظيم احترافي وأمان تام" else "• MAKING TRAVEL SAFE & EASY"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Headline
        Text(
            text = buildAnnotatedString {
                if (isAr) {
                    append("وكالات سياحية ")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF00D2B4))) {
                        append("معتمدة")
                    }
                    append(" ومرافقة محترفة")
                } else {
                    append("Certified ")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF00D2B4))) {
                        append("agencies")
                    }
                    append(" & instant booking")
                }
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 31.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = if (isAr)
                "تواصل مباشر عبر الواتساب والمكالمات مع وكالات مرخصة رسمياً ونقل سياحي VIP."
            else
                "Direct contact via WhatsApp, licensed guides and comfortable air-conditioned VIP transport.",
            fontSize = 13.5.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Feature pills
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeatureCheckPill(if (isAr) "نقل سياحي مكيف VIP" else "VIP Tourism coach")
                FeatureCheckPill(if (isAr) "تأكيد حجز فوري" else "Instant confirmation")
            }
            FeatureCheckPill(if (isAr) "مرشدون سياحيون محليون" else "Certified local guides")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3D Deck of Agency & Camp & Mountain tour
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val cardWidth = (maxWidth * 0.58f).coerceIn(190.dp, 240.dp)
            val cardHeight = (maxHeight * 0.90f).coerceIn(210.dp, 290.dp)

            // Left Card (Mountain Tour)
            PerspectiveCard(
                imageRes = R.drawable.tikjda_mountain_trip_1787149263505,
                label = if (isAr) "• مسارات مشي جبلية" else "• Mountain hiking trails",
                modifier = Modifier
                    .offset(x = (-cardWidth * 0.55f), y = 14.dp)
                    .width(cardWidth * 0.88f)
                    .height(cardHeight * 0.88f)
                    .graphicsLayer {
                        rotationZ = -6.5f
                        rotationY = 12f
                        alpha = 0.88f
                    }
            )

            // Right Card (Coast Bay)
            PerspectiveCard(
                imageRes = R.drawable.algerian_turquoise_coast_1789330536079,
                label = if (isAr) "• رحلات بحرية وشاطئية" else "• Boat & beach tours",
                modifier = Modifier
                    .offset(x = (cardWidth * 0.55f), y = 14.dp)
                    .width(cardWidth * 0.88f)
                    .height(cardHeight * 0.88f)
                    .graphicsLayer {
                        rotationZ = 6.5f
                        rotationY = -12f
                        alpha = 0.88f
                    }
            )

            // Center Card (Oasis Camp with Guide)
            PerspectiveCard(
                imageRes = R.drawable.algeria_tour_guide_camp_1789330550122,
                label = if (isAr) "• مخيمات وأنشطة صحراوية" else "• Sahara camp & guides",
                modifier = Modifier
                    .offset(y = floatYOffset)
                    .width(cardWidth)
                    .height(cardHeight)
                    .shadow(20.dp, RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.45f))
                    .graphicsLayer {
                        scaleX = 1.04f
                        scaleY = 1.04f
                    }
            )
        }
    }
}

// -------------------------------------------------------------------------
// SLIDE 3: STACKED TRAVELER REVIEWS (EXACT HALALBOOKING SCREENSHOT 3)
// -------------------------------------------------------------------------
@Composable
private fun CommunityReviewsSlide(
    isAr: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Frosted Pill Tag
        FrostedHeaderBadge(
            text = if (isAr) "• من مجتمع المسافرين الجزائريين" else "• FROM LIKE-MINDED TRAVELLERS"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Headline
        Text(
            text = buildAnnotatedString {
                if (isAr) {
                    append("تجارب وتقييمات من ")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF00D2B4))) {
                        append("مجتمعنا")
                    }
                } else {
                    append("Reviews from your ")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF00D2B4))) {
                        append("community")
                    }
                    append(".")
                }
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 31.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = if (isAr)
                "تقييمات حقيقية موثقة من مسافرين وعائلات استكشفوا الجزائر معنا."
            else
                "Genuine reviews from verified Algerian travellers who share your passion for discovery.",
            fontSize = 13.5.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 3 Stacked Luxury Review Cards with Amber Rating Badges
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            ReviewCardItem(
                name = if (isAr) "أمين بوعلام" else "Amine Boualam",
                tripInfo = if (isAr) "وكالة الأطلس السياحية • تاغيت، بشار" else "Atlas Voyages • Taghit, Bechar",
                rating = "10",
                review = if (isAr)
                    "تنظيم في القمة ومرشدين يعرفون كل زاوية في الكثبان. مخيم راقي، سهرة شاي حول النار لا تنسى، وراحة بال تامة طيلة 4 أيام."
                else
                    "Flawless organization and friendly guides who know every dune. Authentic desert camp and peaceful atmosphere."
            )

            ReviewCardItem(
                name = if (isAr) "فاطمة الزهراء ع." else "Fatima Zohra",
                tripInfo = if (isAr) "نادي المغامرة • تيكجدة وجرجرة" else "Club Aventure • Tikjda & Djurdjura",
                rating = "9.8",
                review = if (isAr)
                    "أجواء عائلية آمنة ومريحة جداً، النقل السياحي كان جديد ومكيف، والوجبات التقليدية على قمة الجبل ممتازة. تطبيق رقم 1 في الجزائر!"
                else
                    "Safe and welcoming family atmosphere. Brand new comfortable transport and unforgettable traditional meals on the peaks."
            )

            ReviewCardItem(
                name = if (isAr) "كريم مرواني" else "Karim Merouani",
                tripInfo = if (isAr) "سياحة الشرق • شواطئ القل وجيجل" else "Tourisme Est • Collo & Jijel Coast",
                rating = "9.6",
                review = if (isAr)
                    "حجزت مقعدين وتواصلت فوراً مع الوكالة عبر الواتساب. كل شيء كان مطابقاً تماماً للوصف بدون أي مفاجآت. تجربة موثوقة تستحق التكرار."
                else
                    "Instant booking confirmation and quick WhatsApp contact. Everything was exactly as described with complete transparency."
            )
        }
    }
}

// -------------------------------------------------------------------------
// REUSABLE COMPONENTS: FROSTED BADGE, CHECK PILL, 3D CARD, REVIEW CARD
// -------------------------------------------------------------------------

@Composable
private fun FrostedHeaderBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0x33FFFFFF),
        border = BorderStroke(1.dp, Color(0x38FFFFFF))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00D2B4))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text.removePrefix("• ").trim(),
                color = Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun FeatureCheckPill(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00A699)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = text,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
private fun PerspectiveCard(
    imageRes: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // High Resolution Photo
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            )

            // Bottom White Ribbon Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00A699))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label.removePrefix("• ").trim(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewCardItem(
    name: String,
    tripInfo: String,
    rating: String,
    review: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = tripInfo,
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Vibrant Orange/Amber Rating Badge (Exactly as in Halalbooking screenshot 3)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF97316)) // Amber-Orange #F97316
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rating,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = review,
                fontSize = 12.sp,
                color = Color(0xFF334155),
                lineHeight = 17.5.sp
            )
        }
    }
}
