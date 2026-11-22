package com.example.ui.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserSurveyDialog(
    onComplete: (gender: String, ageGroup: String, heardFrom: String, interests: Set<String>, termsAccepted: Boolean) -> Unit,
    onDismiss: () -> Unit,
    isAr: Boolean = true
) {
    var step by remember { mutableIntStateOf(0) } // 0, 1, 2, 3 (4 steps total)

    var selectedGender by remember { mutableStateOf("ذكر") }
    var selectedAgeGroup by remember { mutableStateOf("18 - 24 سنة") }
    var selectedHeardFrom by remember { mutableStateOf("فيسبوك") }
    var selectedInterests by remember { mutableStateOf(setOf("تخييم وسفاري صحراوي", "مغامرات وتسلق جبال")) }
    var termsAccepted by remember { mutableStateOf(true) }

    val emeraldPrimary = Color(0xFF0D9488)
    val totalSteps = 4
    val progress by animateFloatAsState(
        targetValue = (step + 1) / totalSteps.toFloat(),
        animationSpec = tween(300),
        label = "surveyProgress"
    )

    Dialog(
        onDismissRequest = { /* Must be completed or closed */ },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("user_survey_dialog"),
            color = Color(0xFFFFFFFF), // Pure White Theme
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar: Step counter, progress, close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "خطوة ${step + 1} من $totalSteps" else "Étape ${step + 1} sur $totalSteps",
                        color = emeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Linear Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = emeraldPrimary,
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Dynamic Step Content with smooth slide transitions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    AnimatedContent(
                        targetState = step,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn() with
                                        slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut()
                            } else {
                                slideInHorizontally(animationSpec = tween(300)) { -it } + fadeIn() with
                                        slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut()
                            }
                        }
                    ) { currentStep ->
                        when (currentStep) {
                            0 -> StepAgeAndGender(
                                selectedGender = selectedGender,
                                onSelectGender = { selectedGender = it },
                                selectedAgeGroup = selectedAgeGroup,
                                onSelectAgeGroup = { selectedAgeGroup = it },
                                isAr = isAr,
                                accentColor = emeraldPrimary
                            )
                            1 -> StepHeardFrom(
                                selectedHeardFrom = selectedHeardFrom,
                                onSelectHeardFrom = { selectedHeardFrom = it },
                                isAr = isAr,
                                accentColor = emeraldPrimary
                            )
                            2 -> StepInterests(
                                selectedInterests = selectedInterests,
                                onToggleInterest = { interest ->
                                    selectedInterests = if (selectedInterests.contains(interest)) {
                                        if (selectedInterests.size > 1) selectedInterests - interest else selectedInterests
                                    } else {
                                        selectedInterests + interest
                                    }
                                },
                                isAr = isAr,
                                accentColor = emeraldPrimary
                            )
                            3 -> StepCharterCommitment(
                                termsAccepted = termsAccepted,
                                onToggleAccepted = { termsAccepted = it },
                                isAr = isAr,
                                accentColor = emeraldPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Action Buttons: Back & Continue/Finish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 0) {
                        Surface(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { step-- },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFF334155),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (step < totalSteps - 1) {
                                step++
                            } else {
                                onComplete(
                                    selectedGender,
                                    selectedAgeGroup,
                                    selectedHeardFrom,
                                    selectedInterests,
                                    termsAccepted
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("survey_continue_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = emeraldPrimary,
                            contentColor = Color.White
                        ),
                        enabled = if (step == 3) termsAccepted else true
                    ) {
                        Text(
                            text = if (step == totalSteps - 1) {
                                if (isAr) "إتمام والانطلاق 🚀" else "Terminer et Explorer 🚀"
                            } else {
                                if (isAr) "متابعة" else "Continuer"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 1: Age & Gender
// -------------------------------------------------------------
@Composable
private fun StepAgeAndGender(
    selectedGender: String,
    onSelectGender: (String) -> Unit,
    selectedAgeGroup: String,
    onSelectAgeGroup: (String) -> Unit,
    isAr: Boolean,
    accentColor: Color
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = if (isAr) "من أنت؟ 👤" else "Qui êtes-vous ? 👤",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = if (isAr) "لنخصص تجربة الرحلات الأنسب لك تماماً" else "Pour personnaliser les voyages selon votre profil",
            fontSize = 12.5.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Gender Selection (Cards)
        Text(
            text = if (isAr) "الجنس:" else "Genre :",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val genders = listOf(
                Pair("ذكر", "Homme 👨"),
                Pair("أنثى", "Femme 👩")
            )
            genders.forEach { (code, label) ->
                val isSelected = selectedGender == code
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectGender(code) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) accentColor.copy(alpha = 0.12f) else Color(0xFFF8FAFC)
                    ),
                    border = BorderStroke(1.dp, if (isSelected) accentColor else Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) (if (code == "ذكر") "ذكر 👨" else "أنثى 👩") else label,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) accentColor else Color(0xFF334155)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Age Group Selection
        Text(
            text = if (isAr) "الفئة العمرية:" else "Tranche d'âge :",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(6.dp))

        val ageGroups = listOf(
            Pair("18 - 24 سنة", "18 - 24 ans (Jeunesse & Études) 🎒"),
            Pair("25 - 34 سنة", "25 - 34 ans (Actifs & Aventure) 🧗"),
            Pair("35 - 49 سنة", "35 - 49 ans (Famille & Découverte) 👨‍👩‍👧‍👦"),
            Pair("+50 سنة", "+50 ans (Détente & Sérénité) 🌿")
        )

        ageGroups.forEach { (code, label) ->
            val isSelected = selectedAgeGroup == code
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelectAgeGroup(code) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) accentColor.copy(alpha = 0.10f) else Color(0xFFF8FAFC)
                ),
                border = BorderStroke(1.dp, if (isSelected) accentColor else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) code else label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) accentColor else Color(0xFF334155)
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 2: How Did You Hear About Us?
// -------------------------------------------------------------
@Composable
private fun StepHeardFrom(
    selectedHeardFrom: String,
    onSelectHeardFrom: (String) -> Unit,
    isAr: Boolean,
    accentColor: Color
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = if (isAr) "كيف سمعت عنا؟ 📢" else "Comment nous avez-vous connu ? 📢",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = if (isAr) "يسعدنا معرفة كيف وصلت إلى مجتمع رحلات الجزائر" else "Nous aimerions savoir comment vous nous avez découvert",
            fontSize = 12.5.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        val channels = listOf(
            Triple("فيسبوك", "Facebook 🔵", "مجموعات السفر وصفحات السياحة"),
            Triple("تيك توك", "TikTok 🎵", "فيديوهات واستكشافات الجزائر"),
            Triple("إنستغرام", "Instagram 📸", "ستوريات وصور الرحلات"),
            Triple("توصية صديق", "Ami / Proche 👥", "توصية من مسافرين أو أصدقاء"),
            Triple("محرك بحث / إعلانات", "Google / Web 🌐", "بحث عن رحلات أو إعلانات")
        )

        channels.forEach { (code, title, subtitle) ->
            val isSelected = selectedHeardFrom == code
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectHeardFrom(code) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) accentColor.copy(alpha = 0.12f) else Color(0xFFF8FAFC)
                ),
                border = BorderStroke(1.dp, if (isSelected) accentColor else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAr) code else title,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) accentColor else Color(0xFF1E293B)
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 3: Travel Interests
// -------------------------------------------------------------
@Composable
private fun StepInterests(
    selectedInterests: Set<String>,
    onToggleInterest: (String) -> Unit,
    isAr: Boolean,
    accentColor: Color
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = if (isAr) "ما هي اهتماماتك المفضلة؟ 🧭" else "Vos centres d'intérêt 🧭",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = if (isAr) "اختر واحدة أو أكثر لتخصيص الرحلات المقترحة لك" else "Choisissez vos styles de voyage préférés",
            fontSize = 12.5.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        val interestOptions = listOf(
            Pair("تخييم وسفاري صحراوي", "⛺ تخييم وسفاري صحراوي (Désert & Bivouac)"),
            Pair("مغامرات وتسلق جبال", "🏔️ تسلق ومغامرات جبلية (Montagne & Trek)"),
            Pair("شواطئ واستجمام بحري", "🏖️ شواطئ واستجمام بحري (Plages & Mer)"),
            Pair("رحلات عائلية واسترخاء", "👨‍👩‍👧‍👦 رحلات عائلية مريحة (Voyages Famille)"),
            Pair("سياحة تاريخية وتصوير", "🏛️ آثار وتصوير فوتوغرافي (Patrimoine & Photo)")
        )

        interestOptions.forEach { (code, title) ->
            val isSelected = selectedInterests.contains(code)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleInterest(code) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) accentColor.copy(alpha = 0.12f) else Color(0xFFF8FAFC)
                ),
                border = BorderStroke(1.dp, if (isSelected) accentColor else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) accentColor else Color(0xFF334155),
                        modifier = Modifier.weight(1f)
                    )

                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleInterest(code) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = accentColor,
                            checkmarkColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 4: Community Charter & Commitment (No Spam / Ban)
// -------------------------------------------------------------
@Composable
private fun StepCharterCommitment(
    termsAccepted: Boolean,
    onToggleAccepted: (Boolean) -> Unit,
    isAr: Boolean,
    accentColor: Color
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.12f))
                .border(1.5.dp, accentColor.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (isAr) "ميثاق الأمان ومصداقية الحجوزات 🛡️" else "Charte de Confiance & Sécurité 🛡️",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isAr)
                "مجتمع رحلات الجزائر مبني على الاحترام والمصداقية لضمان نجاح كل رحلة."
            else
                "La communauté Ra7alat repose sur la confiance et l'engagement mutuel.",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Warning Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
            border = BorderStroke(1.dp, Color(0xFFFDE68A))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "تنبيه صارم ضد الحجوزات الوهمية:" else "Avertissement contre les abus :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color(0xFF92400E)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isAr)
                        "• الحجز في أي رحلة يعني التزامك بالحضور وتأكيد مكانك.\n• الحجوزات العشوائية أو إلغاء الحجز في اللحظات الأخيرة دون سبب قاهر يعرّض حسابك للحظر النهائي (BAN) من التطبيق حفاظاً على حقوق المنظمين وبقية المسافرين."
                    else
                        "• Réserver un voyage engage votre présence.\n• Les réservations abusives ou désistements injustifiés entraînent le bannissement définitif (BAN) de votre compte.",
                    fontSize = 11.5.sp,
                    color = Color(0xFF78350F),
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Commitment Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { onToggleAccepted(!termsAccepted) }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = onToggleAccepted,
                colors = CheckboxDefaults.colors(
                    checkedColor = accentColor,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isAr)
                    "أقرّ بموافقتي على شروط الاستخدام وأتعهد بالالتزام بالقوانين 🇩🇿"
                else
                    "J'accepte la charte et m'engage à respecter les règles 🇩🇿",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }
    }
}
