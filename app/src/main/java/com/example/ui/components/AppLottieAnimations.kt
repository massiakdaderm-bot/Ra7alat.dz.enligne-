package com.example.ui.components

import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import com.example.ui.theme.LocalAppThemePalette

/**
 * High-performance Lottie animation wrapper with smooth tap-to-bounce interaction.
 */
@Composable
fun AppLottieAnimation(
    @RawRes rawRes: Int,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever,
    speed: Float = 1.0f,
    isPlaying: Boolean = true,
    interactiveBounce: Boolean = true
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        speed = speed,
        isPlaying = isPlaying
    )

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "lottieScale"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (interactiveBounce) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        isPressed = true
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Universal, beautifully styled Empty State view driven by smooth Lottie vector animations.
 */
@Composable
fun AppLottieEmptyState(
    @RawRes rawRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    animationSize: Dp = 150.dp,
    testTag: String = "lottie_empty_state"
) {
    val palette = LocalAppThemePalette.current
    val accentColor = palette.accentColor
    val textPrimary = palette.textPrimary
    val textSecondary = palette.mutedTextColor

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (badgeText != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = accentColor.copy(alpha = 0.12f),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = badgeText,
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(animationSize)
                .clip(CircleShape)
                .background(if (palette.isLight) Color(0xFFF1F5F9).copy(alpha = 0.8f) else Color(0xFF14271D).copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            AppLottieAnimation(
                rawRes = rawRes,
                modifier = Modifier.size(animationSize * 0.9f),
                iterations = LottieConstants.IterateForever,
                interactiveBounce = true
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = title,
            color = textPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 320.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            color = textSecondary,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 300.dp)
        )

        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("${testTag}_action")
            ) {
                Text(
                    text = actionLabel,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}

/**
 * Fullscreen or embedded Loading View with smooth Lottie travel radar animation.
 */
@Composable
fun AppLottieLoadingView(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    animationSize: Dp = 130.dp,
    testTag: String = "lottie_loading_view"
) {
    val palette = LocalAppThemePalette.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(animationSize),
            contentAlignment = Alignment.Center
        ) {
            AppLottieAnimation(
                rawRes = R.raw.lottie_travel_loading,
                modifier = Modifier.fillMaxSize(),
                iterations = LottieConstants.IterateForever,
                speed = 1.1f,
                interactiveBounce = false
            )
        }

        if (title != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                color = palette.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = palette.mutedTextColor,
                fontSize = 12.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Animated Checkmark / Celebration badge for success dialogs.
 */
@Composable
fun AppLottieSuccessBadge(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        AppLottieAnimation(
            rawRes = R.raw.lottie_success_celebration,
            modifier = Modifier.fillMaxSize(),
            iterations = 1,
            interactiveBounce = true
        )
    }
}
