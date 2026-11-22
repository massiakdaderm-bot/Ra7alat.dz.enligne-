package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class CoachmarkStep(
    val titleAr: String,
    val titleFr: String,
    val descriptionAr: String,
    val descriptionFr: String,
    val icon: ImageVector,
    val isPointingUp: Boolean, // true = points to top bar, false = points to bottom bar
    val arrowHorizontalBias: Float // -1f = left, 0f = center, 1f = right
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppCoachmarksOverlay(
    onDismiss: () -> Unit,
    isAr: Boolean = true
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        CoachmarkStep(
            titleAr = "فلاتر الولايات ونوع الرحلة 🧭",
            titleFr = "Filtres & Wilayas 🧭",
            descriptionAr = "من هنا في الأعلى، يمكنك تصفية الرحلات حسب ولاية الانطلاق أو الوجهة، واختيار نوع المغامرة (تخييم صحراوي، جبال، شواطئ).",
            descriptionFr = "En haut, filtrez rapidement les voyages par wilaya de départ/destination et par type de voyage.",
            icon = Icons.Default.Explore,
            isPointingUp = true,
            arrowHorizontalBias = 0f
        ),
        CoachmarkStep(
            titleAr = "حجوزاتك وبطاقة الصعود الرقمية 🎫",
            titleFr = "Mes Réservations & Billet 🎫",
            descriptionAr = "في الشريط السفلي، تجد جميع حجوزاتك المؤكدة مع بطاقة الصعود الرقمية (QR Code) وتفاصيل التواصل المباشر مع الوكالة.",
            descriptionFr = "En bas, accédez à vos voyages réservés, votre carte d'embarquement numérique et les contacts directs.",
            icon = Icons.Default.ConfirmationNumber,
            isPointingUp = false,
            arrowHorizontalBias = 0f
        ),
        CoachmarkStep(
            titleAr = "حسابك، الاشتراكات والمظهر 👤",
            titleFr = "Profil & Abonnements 👤",
            descriptionAr = "من هنا يمكنك تخصيص بروفايلك، متابعة الوكالات المفضلة لديك لمعرفة جديدها، وتغيير ثيمات التطبيق ولغته.",
            descriptionFr = "Gérez votre profil, suivez vos organisateurs favoris et personnalisez le thème et la langue de l'application.",
            icon = Icons.Default.Person,
            isPointingUp = false,
            arrowHorizontalBias = 0.6f
        )
    )

    val stepData = steps[currentStep]
    val isLast = currentStep == steps.size - 1

    // Infinite bouncing animation for the arrow pointer
    val infiniteTransition = rememberInfiniteTransition(label = "coachmarkBounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .testTag("coachmarks_overlay")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // If pointing UP (e.g. Step 1: Top bar filters)
                if (stepData.isPointingUp) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        // Bouncing Arrow pointing UP
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(0, -bounceOffset.toInt()) }
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D9488))
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Pointer Up",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = if (isAr) "👇 انظر هنا بالأعلى" else "👇 Regardez ici en haut",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(40.dp))
                }

                // Middle: Informative Spotlight Card
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        slideInHorizontally(animationSpec = tween(300)) { if (targetState > initialState) it else -it } + fadeIn() with
                                slideOutHorizontally(animationSpec = tween(300)) { if (targetState > initialState) -it else it } + fadeOut()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) { targetStepIndex ->
                    val cur = steps[targetStepIndex]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, Color(0xFF0D9488)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Step Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0D9488).copy(alpha = 0.12f))
                                    .padding(horizontal = 14.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isAr) "تلميح ذكي ${targetStepIndex + 1} / ${steps.size}" else "Astuce ${targetStepIndex + 1} / ${steps.size}",
                                    color = Color(0xFF0D9488),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Step Icon
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0D9488)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cur.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Step Title
                            Text(
                                text = if (isAr) cur.titleAr else cur.titleFr,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Step Description
                            Text(
                                text = if (isAr) cur.descriptionAr else cur.descriptionFr,
                                fontSize = 14.sp,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center,
                                lineHeight = 21.sp
                            )
                        }
                    }
                }

                // If pointing DOWN (e.g. Step 2 & 3: Bottom Navigation)
                if (!stepData.isPointingUp) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = if (isAr) "👆 انظر هنا بالأسفل" else "👆 Regardez ici en bas",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Bouncing Arrow pointing DOWN
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(0, bounceOffset.toInt()) }
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D9488))
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Pointer Down",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(40.dp))
                }

                // Bottom Controls: Skip, Dots, Next/Finish
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Skip Button
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("coachmarks_skip_btn")
                        ) {
                            Text(
                                text = if (isAr) "تخطي" else "Passer",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Step Dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            steps.indices.forEach { idx ->
                                Box(
                                    modifier = Modifier
                                        .size(if (idx == currentStep) 10.dp else 7.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (idx == currentStep) Color(0xFF0D9488) else Color(0xFF64748B)
                                        )
                                )
                            }
                        }

                        // Continue / Finish Button
                        Button(
                            onClick = {
                                if (isLast) {
                                    onDismiss()
                                } else {
                                    currentStep++
                                }
                            },
                            modifier = Modifier.testTag("coachmarks_next_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0D9488),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (isLast) {
                                    if (isAr) "فهمت وانطلق 🚀" else "Compris 🚀"
                                } else {
                                    if (isAr) "متابعة" else "Continuer"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
