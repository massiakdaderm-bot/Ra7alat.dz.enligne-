package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.ui.components.TripDatePickerDialog
import com.example.ui.theme.LocalAppThemePalette
import com.example.util.AlgeriaConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class CategoryOption(
    val id: String,
    val titleAr: String,
    val titleFr: String,
    val emoji: String,
    val descAr: String,
    val descFr: String
)

private data class AmenityOption(
    val id: String,
    val labelAr: String,
    val labelFr: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTripScreen(
    userProfile: UserProfile?,
    onTripAdded: (Trip) -> Unit,
    onRequireLogin: () -> Unit,
    isPublishing: Boolean = false,
    onPublishTripWithImages: ((Trip, List<Uri>) -> Unit)? = null,
    isAr: Boolean = false
) {
    val palette = LocalAppThemePalette.current
    val emeraldAccent = palette.accentColor
    val darkCardBg = palette.darkCardBg
    val darkInputBg = palette.darkSurfaceBg
    val darkCanvasBg = palette.darkCanvasBg
    val borderColor = if (palette.isLight) palette.darkBorderColor else Color(0xFF1C3A2C)
    val labelColor = palette.mutedTextColor
    val textPrimary = palette.textPrimary
    val textSecondary = palette.textSecondary
    val subSurfaceBg = if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF0A1811)
    val unselectedCardBg = if (palette.isLight) Color(0xFFF8FAFC) else Color(0xFF0E1A14)

    // Current Step index: 0 to 5 (6 steps total)
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 6

    // Handle back button for multi-step navigation
    BackHandler(enabled = currentStep > 0) {
        currentStep--
    }

    if (userProfile == null || !userProfile.isLoggedIn) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkCanvasBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🔒", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (isAr) "يجب تسجيل الدخول لنشر رحلة سياحية" else "Connexion requise pour publier un voyage",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAr) "سجل الدخول كوكالة سياحية أو منظم رحلات لإضافة عروضك" else "Connectez-vous en tant qu'agence pour publier",
                    color = textSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRequireLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                    modifier = Modifier.testTag("add_trip_login_button")
                ) {
                    Text(if (isAr) "تسجيل الدخول / إنشاء حساب" else "Se connecter / S'inscrire", color = Color.White)
                }
            }
        }
        return
    }

    // ========================================================
    // Form States
    // ========================================================
    var organizerType by remember {
        mutableStateOf(if (userProfile.role == "AGENCY") "AGENCY" else "INDIVIDUAL")
    }
    var organizerName by remember {
        mutableStateOf(
            userProfile.agencyName.ifBlank {
                userProfile.fullName.ifBlank { "منظم رحلات" }
            }
        )
    }
    var phone by remember {
        mutableStateOf(userProfile.phone.ifBlank { "0550123456" })
    }

    // Step 1: Category
    val categories = remember {
        listOf(
            CategoryOption("sahara", "صحراء وسفاري", "Désert & Safari", "🏜️", "استكشاف الواحات، تاغيت، جانت والتاسيلي", "Oasis, dunes du Sahara, Tassili et Hoggar"),
            CategoryOption("beach", "شواطئ وبحرية", "Plages & Mer", "🏖️", "رحلات ساحلية، جولات بالقوارب وأنشطة بحرية", "Côtes algériennes, balades en bateau et plongée"),
            CategoryOption("mountain", "جبال واستكشاف", "Montagne & Rando", "🏔️", "تسلق ومسارات جرجرة، الشريعة والأوراس", "Randonnées Djurdjura, Chréa et sommets"),
            CategoryOption("culture", "ثقافة وتاريخ", "Culture & Histoire", "🏛️", "معالم أثرية، قصبة الجزائر، جميلة وتيمقاد", "Monuments historiques, Casbah et musées"),
            CategoryOption("camping", "تخييم ومغامرة", "Camping & Aventure", "⛺", "مبيت تحت النجوم، نار المخيم وأجواء البرية", "Bivouac sous les étoiles et vie sauvage"),
            CategoryOption("spa", "حمامات واستجمام", "Thermes & Détente", "♨️", "حمامات معدنية ساخنة وراحة واسترخاء", "Eaux thermales, détente et bien-être"),
            CategoryOption("weekend", "عطلات الأسبوع", "Escapades Week-end", "🌲", "رحلات سريعة يوم أو يومين لتجديد النشاط", "Sorties courtes de 1 ou 2 jours"),
            CategoryOption("omra", "عمرة وزيارات", "Omra & Pèlerinage", "🕋", "رحلات البقاع المقدسة والرحلات الإيمانية", "Voyages Omra et services de pèlerinage")
        )
    }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    // Step 2: General info & Transport
    var tripCategory by remember { mutableStateOf("صحراوية 🏜️") }
    var transportType by remember { mutableStateOf("حافلة سياحية VIP 🚌") }
    var title by remember { mutableStateOf("") }
    var programDetails by remember { mutableStateOf("") }
    var expandedTransport by remember { mutableStateOf(false) }

    // Step 3: Departure, Destination & Map Zone
    var departureWilaya by remember { mutableStateOf("16 - الجزائر العاصمة") }
    var departureCity by remember { mutableStateOf("الجزائر العاصمة (ساحة أول ماي)") }
    var departureTime by remember { mutableStateOf("06:00 صباحاً") }
    var destinationWilaya by remember { mutableStateOf("47 - غرداية") }
    var destinationPlace by remember { mutableStateOf("وادي ميزاب والواحات القديمة") }
    var showMapPrecisePoint by remember { mutableStateOf(true) }
    var mapLatitude by remember { mutableStateOf("36.7538") }
    var mapLongitude by remember { mutableStateOf("3.0588") }
    var itinerary by remember { mutableStateOf("") }
    var googleMapsUrl by remember { mutableStateOf("") }

    var expandedDeptWilaya by remember { mutableStateOf(false) }
    var expandedDeptCity by remember { mutableStateOf(false) }
    var expandedDestWilaya by remember { mutableStateOf(false) }

    // Step 4: Dates & Duration
    var departureDateIso by remember { mutableStateOf("2026-10-15") }
    var returnDateIso by remember { mutableStateOf("2026-10-18") }
    var showDepartureDatePicker by remember { mutableStateOf(false) }
    var showReturnDatePicker by remember { mutableStateOf(false) }

    // Step 5: Pricing, Seats & Amenities
    var totalSeats by remember { mutableStateOf("30") }
    var pricePerPerson by remember { mutableStateOf("18500") }
    val amenitiesList = remember {
        listOf(
            AmenityOption("hotel", "فندق وإقامة مريحة", "Hébergement hôtel", "🏨"),
            AmenityOption("vip_bus", "نقل بحافلة VIP", "Transport bus VIP", "🚌"),
            AmenityOption("meals", "وجبات طعام كاملة", "Repas complets", "🍽️"),
            AmenityOption("guide", "مرشد سياحي مرافق", "Guide touristique", "👨‍💼"),
            AmenityOption("music", "سهرات فنية وموسيقية", "Soirées musicales", "🎵"),
            AmenityOption("camping_kit", "خيم ومعدات تخييم", "Matériel camping", "⛺"),
            AmenityOption("ac", "تكييف وتدفئة", "Climatisation", "❄️"),
            AmenityOption("wifi", "واي فاي وشاحن هواتف", "Wifi & Chargeurs", "📶"),
            AmenityOption("photo", "تصوير فوتوغرافي تذكاري", "Séance photo", "📷"),
            AmenityOption("insurance", "تأمين وأمان المسافرين", "Sécurité & Assurance", "🛡️")
        )
    }
    val selectedAmenities = remember {
        mutableStateListOf("hotel", "vip_bus", "meals", "guide", "ac")
    }
    var customInclusions by remember { mutableStateOf("") }

    // Step 6: Photos & Verification
    val selectedImages = remember { mutableStateListOf<Uri>() }
    var videoUrl by remember { mutableStateOf("") }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImages.addAll(uris.take(15 - selectedImages.size))
        }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showDepartureDatePicker) {
        TripDatePickerDialog(
            onDateSelected = { departureDateIso = it },
            onDismiss = { showDepartureDatePicker = false },
            isAr = isAr
        )
    }

    if (showReturnDatePicker) {
        TripDatePickerDialog(
            onDateSelected = { returnDateIso = it },
            onDismiss = { showReturnDatePicker = false },
            isAr = isAr
        )
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = darkInputBg,
        unfocusedContainerColor = darkInputBg,
        focusedBorderColor = emeraldAccent,
        unfocusedBorderColor = borderColor,
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        focusedLabelColor = emeraldAccent,
        unfocusedLabelColor = labelColor
    )

    val stepTitlesAr = listOf(
        "تصنيف ونوع الرحلة",
        "المعلومات العامة",
        "الانطلاق والمسار والخريطة",
        "الرزنامة ومواعيد الرحلة",
        "المقاعد والأسعار والخدمات",
        "الصور وتأكيد النشر"
    )

    val stepTitlesFr = listOf(
        "Choix de la catégorie",
        "Informations générales",
        "Départ, destination & carte",
        "Calendrier & dates",
        "Tarification & équipements",
        "Galerie & confirmation"
    )

    val progressPercent = ((currentStep + 1) * 100) / totalSteps
    val animatedProgress by animateFloatAsState(
        targetValue = (currentStep + 1) / totalSteps.toFloat(),
        label = "progress"
    )

    // Validation per step
    fun validateCurrentStep(): Boolean {
        errorMessage = null
        when (currentStep) {
            0 -> {
                // Category step
                return true
            }
            1 -> {
                if (title.trim().length < 4) {
                    errorMessage = if (isAr) "يرجى كتابة عنوان الرحلة (4 أحرف على الأقل)" else "Veuillez renseigner le titre de l'annonce"
                    return false
                }
                if (programDetails.trim().length < 10) {
                    errorMessage = if (isAr) "يرجى إضافة وصف تفصيلي للرحلة (10 أحرف على الأقل)" else "La description doit contenir au moins 10 caractères"
                    return false
                }
            }
            2 -> {
                if (departureWilaya.isBlank() || departureCity.isBlank()) {
                    errorMessage = if (isAr) "يرجى تحديد ولاية ومدينة الانطلاق" else "Veuillez préciser le lieu de départ"
                    return false
                }
                if (destinationWilaya.isBlank()) {
                    errorMessage = if (isAr) "يرجى تحديد ولاية الوجهة" else "Veuillez préciser la wilaya de destination"
                    return false
                }
            }
            3 -> {
                if (departureDateIso.isBlank() || returnDateIso.isBlank()) {
                    errorMessage = if (isAr) "يرجى تحديد تاريخي الانطلاق والعودة" else "Veuillez définir les dates du voyage"
                    return false
                }
            }
            4 -> {
                val priceVal = pricePerPerson.toDoubleOrNull()
                val seatsVal = totalSeats.toIntOrNull()
                if (priceVal == null || priceVal <= 0) {
                    errorMessage = if (isAr) "يرجى تحديد سعر صالح للشخص بالدينار الجزائري" else "Veuillez définir un tarif valide en DZD"
                    return false
                }
                if (seatsVal == null || seatsVal <= 0) {
                    errorMessage = if (isAr) "يرجى تحديد عدد المقاعد المتاحة" else "Veuillez indiquer le nombre de places"
                    return false
                }
            }
            5 -> {
                if (phone.trim().length < 8) {
                    errorMessage = if (isAr) "يرجى إدخال رقم هاتف صحيح للتواصل والحجز" else "Numéro de téléphone requis pour les réservations"
                    return false
                }
            }
        }
        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkCanvasBg)
            .testTag("add_trip_screen")
    ) {
        // ========================================================
        // TOP PROGRESS HEADER (Inspired by user screenshots)
        // ========================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkCardBg)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) {
                        "المرحلة ${currentStep + 1}/$totalSteps : ${stepTitlesAr[currentStep]}"
                    } else {
                        "Étape ${currentStep + 1}/$totalSteps : ${stepTitlesFr[currentStep]}"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = emeraldAccent
                )
                Text(
                    text = if (isAr) "$progressPercent% مكتمل" else "$progressPercent% complété",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = labelColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sleek animated Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (palette.isLight) Color(0xFFE2E8F0) else Color(0xFF1E2E25))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(emeraldAccent)
                )
            }
        }

        // ========================================================
        // SCROLLABLE STEP CONTENT
        // ========================================================
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (currentStep) {
                // ----------------------------------------------------
                // STEP 1: CATEGORY SELECTION (Étape 1/6)
                // ----------------------------------------------------
                0 -> {
                    Text(
                        text = if (isAr) "اختر تصنيف رحلتك السياحية 🇩🇿" else "Choisissez la catégorie de votre voyage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr)
                            "التصنيف يحدد طبيعة الرحلة ويساعد المسافرين في العثور عليها بسهولة."
                        else
                            "La catégorie définit les informations et aide les voyageurs à trouver votre annonce.",
                        fontSize = 13.sp,
                        color = labelColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2-Column Grid of Category Cards (Inspired by screenshots)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (i in categories.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (j in 0 until 2) {
                                    val index = i + j
                                    if (index < categories.size) {
                                        val cat = categories[index]
                                        val isSelected = selectedCategory.id == cat.id

                                        val cardBorderColor by animateColorAsState(
                                            if (isSelected) emeraldAccent else borderColor,
                                            label = "cardBorder"
                                        )
                                        val cardBackground by animateColorAsState(
                                            if (isSelected) emeraldAccent.copy(alpha = 0.12f) else unselectedCardBg,
                                            label = "cardBg"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(130.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(cardBackground)
                                                .border(if (isSelected) 2.dp else 1.dp, cardBorderColor, RoundedCornerShape(14.dp))
                                                .clickable {
                                                    selectedCategory = cat
                                                    tripCategory = "${cat.titleAr} ${cat.emoji}"
                                                }
                                                .padding(12.dp)
                                                .testTag("category_card_${cat.id}")
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Text(text = cat.emoji, fontSize = 28.sp)
                                                    if (isSelected) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(22.dp)
                                                                .clip(CircleShape)
                                                                .background(emeraldAccent),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Selected",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                        }
                                                    }
                                                }

                                                Column {
                                                    Text(
                                                        text = if (isAr) cat.titleAr else cat.titleFr,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = textPrimary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = if (isAr) cat.descAr else cat.descFr,
                                                        fontSize = 10.sp,
                                                        color = labelColor,
                                                        maxLines = 2,
                                                        lineHeight = 13.sp,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Organizer Mode Selection (Individual vs Agency)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = emeraldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "صفة ناشر الرحلة" else "Statut de l'organisateur",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val isIndiv = organizerType == "INDIVIDUAL"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isIndiv) emeraldAccent.copy(alpha = 0.15f) else unselectedCardBg)
                                        .border(if (isIndiv) 1.5.dp else 1.dp, if (isIndiv) emeraldAccent else borderColor, RoundedCornerShape(10.dp))
                                        .clickable { organizerType = "INDIVIDUAL" }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isAr) "منظم فردي / هاوٍ" else "Organisateur individuel",
                                        fontSize = 12.sp,
                                        fontWeight = if (isIndiv) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isIndiv) emeraldAccent else textSecondary
                                    )
                                }

                                val isAg = organizerType == "AGENCY"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isAg) emeraldAccent.copy(alpha = 0.15f) else unselectedCardBg)
                                        .border(if (isAg) 1.5.dp else 1.dp, if (isAg) emeraldAccent else borderColor, RoundedCornerShape(10.dp))
                                        .clickable { organizerType = "AGENCY" }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isAr) "وكالة سياحية معتمدة ★" else "Agence certifiée ★",
                                        fontSize = 12.sp,
                                        fontWeight = if (isAg) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isAg) emeraldAccent else textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // ----------------------------------------------------
                // STEP 2: GENERAL INFORMATION (Étape 2/6)
                // ----------------------------------------------------
                1 -> {
                    Text(
                        text = if (isAr) "أكمل تفاصيل ومعلومات الرحلة 📝" else "Complétez les détails de votre voyage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr)
                            "اكتب عنواناً جذاباً ووصفاً شاملاً لبرنامج ومميزات الرحلة."
                        else
                            "Remplissez soigneusement les champs suivants pour permettre aux clients de mieux comprendre votre offre.",
                        fontSize = 13.sp,
                        color = labelColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(emeraldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = emeraldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "المعلومات العامة" else "Information générale",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Transport Type Dropdown
                            val transports = listOf(
                                "حافلة سياحية VIP 🚌",
                                "حافلة صغيرة (Minibus) 🚐",
                                "سيارات دفع رباعي 4x4 🚙",
                                "طائرة ✈️",
                                "قطار 🚆",
                                "سيارة خاصة / فردي 🚗",
                                "باخرة / قارب بحري 🚢"
                            )
                            ExposedDropdownMenuBox(
                                expanded = expandedTransport,
                                onExpandedChange = { expandedTransport = !expandedTransport },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = transportType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (isAr) "نوع وسيلة النقل" else "Type de transport") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTransport) },
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedTransport,
                                    onDismissRequest = { expandedTransport = false },
                                    modifier = Modifier.background(darkCardBg)
                                ) {
                                    transports.forEach { tr ->
                                        DropdownMenuItem(
                                            text = { Text(tr, color = textPrimary) },
                                            onClick = {
                                                transportType = tr
                                                expandedTransport = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Trip Title with length counter
                            OutlinedTextField(
                                value = title,
                                onValueChange = { if (it.length <= 60) title = it },
                                label = { Text(if (isAr) "عنوان الرحلة *" else "Titre de l'annonce *") },
                                placeholder = {
                                    Text(if (isAr) "مثال: رحلة سياحية إلى تاغيت وبني عباس 🏜️" else "Ex: Échappée magique à Taghit")
                                },
                                supportingText = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text("${title.length}/60", fontSize = 11.sp, color = labelColor)
                                    }
                                },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_trip_title_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Description & Program
                            OutlinedTextField(
                                value = programDetails,
                                onValueChange = { programDetails = it },
                                label = { Text(if (isAr) "برنامج ووصف الرحلة المفصل *" else "Description détaillée *") },
                                placeholder = {
                                    Text(
                                        if (isAr)
                                            "صف بالتفصيل المحطات، الأنشطة، الإقامة، وأهم مميزات الرحلة..."
                                        else
                                            "Décrivez votre voyage en détail (programme, hébergement, ambiance)..."
                                    )
                                },
                                minLines = 4,
                                maxLines = 8,
                                supportingText = {
                                    Text(
                                        text = if (isAr) "الحد الأدنى 10 أحرف (${programDetails.length})" else "Minimum 10 caractères (${programDetails.length})",
                                        fontSize = 11.sp,
                                        color = if (programDetails.length < 10) Color(0xFFFF9800) else emeraldAccent
                                    )
                                },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_trip_desc_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick structure chips to help user format their description
                            Text(
                                text = if (isAr) "إضافات سريعة للوصف:" else "Suggestions de structure :",
                                fontSize = 11.sp,
                                color = labelColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val suggestions = listOf(
                                    if (isAr) "📍 خط السير" else "📍 Itinéraire",
                                    if (isAr) "🏨 الإقامة" else "🏨 Hébergement",
                                    if (isAr) "🍽️ الوجبات" else "🍽️ Restauration",
                                    if (isAr) "🎒 نصائح" else "🎒 Conseils"
                                )
                                suggestions.forEach { sug ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(subSurfaceBg)
                                            .clickable {
                                                programDetails = if (programDetails.isBlank()) {
                                                    "$sug: "
                                                } else {
                                                    "$programDetails\n$sug: "
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = sug, fontSize = 10.sp, color = emeraldAccent)
                                    }
                                }
                            }
                        }
                    }
                }

                // ----------------------------------------------------
                // STEP 3: DEPARTURE & DESTINATION + MAP ZONE (Étape 3/6)
                // ----------------------------------------------------
                2 -> {
                    Text(
                        text = if (isAr) "الانطلاق والوجهة ومسار الرحلة 📍" else "Lieu de départ, destination & carte",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr)
                            "حدد مكان لقاء وانطلاق المسافرين، والوجهة السياحية المقصودة مع الخريطة."
                        else
                            "Indiquez la localisation exacte de départ et d'arrivée pour vos voyageurs.",
                        fontSize = 13.sp,
                        color = labelColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Departure Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(emeraldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBus,
                                        contentDescription = null,
                                        tint = emeraldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "نقطة وتوقيت الانطلاق 🚌" else "Point et heure de départ 🚌",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Wilaya de départ
                            ExposedDropdownMenuBox(
                                expanded = expandedDeptWilaya,
                                onExpandedChange = { expandedDeptWilaya = !expandedDeptWilaya },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = departureWilaya,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (isAr) "ولاية الانطلاق *" else "Wilaya de départ *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDeptWilaya) },
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedDeptWilaya,
                                    onDismissRequest = { expandedDeptWilaya = false },
                                    modifier = Modifier.background(darkCardBg)
                                ) {
                                    AlgeriaConstants.WILAYAS.forEach { w ->
                                        DropdownMenuItem(
                                            text = { Text(w, color = textPrimary) },
                                            onClick = {
                                                departureWilaya = w
                                                expandedDeptWilaya = false
                                                val clean = w.substringAfter("-").trim()
                                                val cities = AlgeriaConstants.WILAYA_CITIES[clean]
                                                if (!cities.isNullOrEmpty()) {
                                                    departureCity = cities.first()
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Departure City / Gathering spot
                            ExposedDropdownMenuBox(
                                expanded = expandedDeptCity,
                                onExpandedChange = { expandedDeptCity = !expandedDeptCity },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = departureCity,
                                    onValueChange = { departureCity = it },
                                    label = { Text(if (isAr) "المدينة / نقطة التجمع الدقيقة *" else "Ville / Point de rendez-vous *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDeptCity) },
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedDeptCity,
                                    onDismissRequest = { expandedDeptCity = false },
                                    modifier = Modifier.background(darkCardBg)
                                ) {
                                    val currentClean = departureWilaya.substringAfter("-").trim()
                                    val cities = AlgeriaConstants.WILAYA_CITIES[currentClean] ?: listOf(
                                        currentClean,
                                        "ساحة أول ماي",
                                        "محطة الخروبة",
                                        "الجزائر الوسطى"
                                    )
                                    cities.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c, color = textPrimary) },
                                            onClick = {
                                                departureCity = c
                                                expandedDeptCity = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Departure Time
                            OutlinedTextField(
                                value = departureTime,
                                onValueChange = { departureTime = it },
                                label = { Text(if (isAr) "ساعة وتوقيت الانطلاق" else "Heure de départ") },
                                placeholder = { Text("06:00 صباحاً") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Destination Card with Map (Inspired by screenshots 5, 6, 7)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(emeraldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = emeraldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "وجهة الوصول والخريطة 📍" else "Zone d'activité et destination 📍",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Wilaya de destination
                            ExposedDropdownMenuBox(
                                expanded = expandedDestWilaya,
                                onExpandedChange = { expandedDestWilaya = !expandedDestWilaya },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = destinationWilaya,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (isAr) "ولاية الوجهة *" else "Wilaya de destination *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDestWilaya) },
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedDestWilaya,
                                    onDismissRequest = { expandedDestWilaya = false },
                                    modifier = Modifier.background(darkCardBg)
                                ) {
                                    AlgeriaConstants.WILAYAS.forEach { w ->
                                        DropdownMenuItem(
                                            text = { Text(w, color = textPrimary) },
                                            onClick = {
                                                destinationWilaya = w
                                                expandedDestWilaya = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Destination Place / Site
                            OutlinedTextField(
                                value = destinationPlace,
                                onValueChange = { destinationPlace = it },
                                label = { Text(if (isAr) "المعلم / المكان المقصود بالتفصيل" else "Lieu / Endroit précis") },
                                placeholder = { Text(if (isAr) "مثلاً: واحة تغيث، الكثبان الذهبية، القصر القديم" else "Ex: Tassili n'Ajjer") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_trip_destination_place_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Switch to add precise location on map (Inspired by screenshot 5)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(subSurfaceBg)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = emeraldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = if (isAr) "تثبيت موقع الوجهة على الخريطة" else "Ajouter une localisation précise sur la carte",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = if (isAr) "يساعد المسافرين في رؤية مسار الرحلة على الخريطة" else "Permet aux voyageurs de voir la zone sur la carte",
                                            fontSize = 10.sp,
                                            color = labelColor
                                        )
                                    }
                                }
                                Switch(
                                    checked = showMapPrecisePoint,
                                    onCheckedChange = { showMapPrecisePoint = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = emeraldAccent
                                    )
                                )
                            }

                            if (showMapPrecisePoint) {
                                Spacer(modifier = Modifier.height(10.dp))

                                // Interactive stylized Map card (Inspired by screenshots 6 & 7)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (palette.isLight) Color(0xFFE9F3EB) else Color(0xFF142A1E))
                                        .border(1.dp, emeraldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                ) {
                                    // Custom map grid & decorative streets canvas
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val w = size.width
                                        val h = size.height

                                        // Subtle grid lines
                                        val gridColor = Color(0xFF22C55E).copy(alpha = 0.08f)
                                        for (x in 0..w.toInt() step 50) {
                                            drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), h), 1f)
                                        }
                                        for (y in 0..h.toInt() step 50) {
                                            drawLine(gridColor, Offset(0f, y.toFloat()), Offset(w, y.toFloat()), 1f)
                                        }

                                        // Winding road / route path
                                        val roadPath = Path().apply {
                                            moveTo(w * 0.1f, h * 0.85f)
                                            cubicTo(w * 0.3f, h * 0.7f, w * 0.4f, h * 0.9f, w * 0.5f, h * 0.5f)
                                            cubicTo(w * 0.6f, h * 0.3f, w * 0.8f, h * 0.4f, w * 0.9f, h * 0.2f)
                                        }
                                        drawPath(
                                            path = roadPath,
                                            color = Color(0xFF22C55E).copy(alpha = 0.45f),
                                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                                        )
                                    }

                                    // Zoom controls on top-left (like screenshot)
                                    Column(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .align(Alignment.TopStart)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.White.copy(alpha = 0.9f))
                                                .clickable { /* zoom in */ },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("+", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.White.copy(alpha = 0.9f))
                                                .clickable { /* zoom out */ },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("-", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                                        }
                                    }

                                    // Pin marker at center
                                    Column(
                                        modifier = Modifier.align(Alignment.Center),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = Color(0xFFE53935),
                                            modifier = Modifier.size(34.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.Black.copy(alpha = 0.75f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = destinationPlace.ifBlank { destinationWilaya.substringAfter("-").trim() },
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // OpenStreetMap attribution badge (like in screenshot)
                                    Text(
                                        text = "🗺️ الجزائر السياحية",
                                        fontSize = 9.sp,
                                        color = labelColor,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Lat / Lng inputs
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = "$mapLatitude, $mapLongitude",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Latitude, Longitude") },
                                        colors = textFieldColors,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Button(
                                        onClick = {
                                            // Mock coordinates update for preview
                                            mapLatitude = "36.7632"
                                            mapLongitude = "3.0577"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(52.dp)
                                    ) {
                                        Text(if (isAr) "تحديث" else "Chercher", fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Itinéraire text & Google Maps
                            OutlinedTextField(
                                value = itinerary,
                                onValueChange = { itinerary = it },
                                label = { Text(if (isAr) "المسار والمحطات التوقفية" else "Itinéraire et étapes du voyage") },
                                placeholder = { Text(if (isAr) "الجزائر -> بوفاريك -> عين وسارة -> الأغواط -> غرداية" else "Alger -> Ghardaïa") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = googleMapsUrl,
                                onValueChange = { googleMapsUrl = it },
                                label = { Text(if (isAr) "رابط خرائط جوجل للموقع (اختياري)" else "Lien Google Maps (Facultatif)") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // ----------------------------------------------------
                // STEP 4: CALENDAR, DATES & DURATION (Étape 4/6)
                // ----------------------------------------------------
                3 -> {
                    Text(
                        text = if (isAr) "رزنامة وتواريخ الرحلة 📅" else "Calendrier et dates du voyage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr)
                            "حدد تاريخ الانطلاق والعودة وحساب مدة الإقامة تلقائياً."
                        else
                            "Précisez la date de début et de fin du séjour pour vos clients.",
                        fontSize = 13.sp,
                        color = labelColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(emeraldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = emeraldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "تواريخ الانطلاق والعودة 🗓️" else "Dates du séjour (Du / Au) 🗓️",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Date selectors row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Departure Date (Du)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(darkInputBg)
                                        .border(1.5.dp, emeraldAccent, RoundedCornerShape(12.dp))
                                        .clickable { showDepartureDatePicker = true }
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarMonth,
                                                contentDescription = null,
                                                tint = emeraldAccent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isAr) "تاريخ الانطلاق (من)" else "Date de départ (Du)",
                                                fontSize = 11.sp,
                                                color = emeraldAccent
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = formatDisplayDate(departureDateIso, isAr),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = departureDateIso,
                                            fontSize = 11.sp,
                                            color = emeraldAccent
                                        )
                                    }
                                }

                                // Return Date (Au)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(darkInputBg)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { showReturnDatePicker = true }
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarMonth,
                                                contentDescription = null,
                                                tint = emeraldAccent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isAr) "تاريخ العودة (إلى)" else "Date de retour (Au)",
                                                fontSize = 11.sp,
                                                color = emeraldAccent
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = formatDisplayDate(returnDateIso, isAr),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = returnDateIso,
                                            fontSize = 11.sp,
                                            color = emeraldAccent
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Computed duration badge
                            val durationText = computeDuration(departureDateIso, returnDateIso, isAr)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(subSurfaceBg)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = emeraldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "المدة الإجمالية المقدرة:" else "Durée estimée :",
                                        fontSize = 13.sp,
                                        color = textPrimary
                                    )
                                }
                                Text(
                                    text = durationText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = emeraldAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick preset pills
                            Text(
                                text = if (isAr) "تحديد مدة سريعة:" else "Raccourcis de durée :",
                                fontSize = 12.sp,
                                color = labelColor
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DurationPill(
                                    label = if (isAr) "يوم واحد ☀️" else "1 Jour ☀️",
                                    modifier = Modifier.weight(1f),
                                    onClick = { returnDateIso = departureDateIso }
                                )
                                DurationPill(
                                    label = if (isAr) "عطلة أسبوع 🏕️" else "Week-end 🏕️",
                                    modifier = Modifier.weight(1.1f),
                                    onClick = { returnDateIso = addDaysToDate(departureDateIso, 2) }
                                )
                                DurationPill(
                                    label = if (isAr) "3 أيام 🎒" else "3 Jours 🎒",
                                    modifier = Modifier.weight(1f),
                                    onClick = { returnDateIso = addDaysToDate(departureDateIso, 3) }
                                )
                                DurationPill(
                                    label = if (isAr) "أسبوع كامل 🌴" else "1 Semaine 🌴",
                                    modifier = Modifier.weight(1.1f),
                                    onClick = { returnDateIso = addDaysToDate(departureDateIso, 6) }
                                )
                            }
                        }
                    }
                }

                // ----------------------------------------------------
                // STEP 5: PRICING, SEATS & AMENITIES (Étape 5/6)
                // ----------------------------------------------------
                4 -> {
                    Text(
                        text = if (isAr) "الأسعار، المقاعد والخدمات المضمنة 💰" else "Tarification et équipements",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr)
                            "حدد تكلفة المشاركة وعدد المقاعد والمزايا المتضمنة في العرض."
                        else
                            "Indiquez le tarif appliqué et les équipements inclus dans votre offre.",
                        fontSize = 13.sp,
                        color = labelColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pricing & Seats Card (Inspired by Screenshot 9)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(emeraldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("💰", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "التسعير والمقاعد" else "Tarifs & Disponibilité",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Devise tag
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(subSurfaceBg)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isAr) "العملة الرسمية:" else "Devise :",
                                    fontSize = 12.sp,
                                    color = labelColor
                                )
                                Text(
                                    text = "الدينار الجزائري (DA / دج)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = emeraldAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Price per person
                                OutlinedTextField(
                                    value = pricePerPerson,
                                    onValueChange = { pricePerPerson = it },
                                    label = { Text(if (isAr) "السعر للشخص *" else "Prix par personne *") },
                                    trailingIcon = { Text("DA ", fontSize = 12.sp, color = emeraldAccent, fontWeight = FontWeight.Bold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("add_trip_price_input")
                                )

                                // Number of seats
                                OutlinedTextField(
                                    value = totalSeats,
                                    onValueChange = { totalSeats = it },
                                    label = { Text(if (isAr) "عدد المقاعد *" else "Nombre de places *") },
                                    trailingIcon = { Text("👤 ", fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("add_trip_seats_input")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Amenities & Equipment Chips (Inspired by Screenshot 8)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(emeraldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✨", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "الخدمات والمزايا المتضمنة" else "Équipements & Services inclus",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                amenitiesList.forEach { am ->
                                    val isSelected = selectedAmenities.contains(am.id)
                                    val chipBg by animateColorAsState(
                                        if (isSelected) emeraldAccent.copy(alpha = 0.18f) else unselectedCardBg,
                                        label = "chipBg"
                                    )
                                    val chipBorder by animateColorAsState(
                                        if (isSelected) emeraldAccent else borderColor,
                                        label = "chipBorder"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(chipBg)
                                            .border(1.dp, chipBorder, RoundedCornerShape(20.dp))
                                            .clickable {
                                                if (isSelected) {
                                                    selectedAmenities.remove(am.id)
                                                } else {
                                                    selectedAmenities.add(am.id)
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(am.emoji, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isAr) am.labelAr else am.labelFr,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) textPrimary else labelColor
                                            )
                                            if (isSelected) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = emeraldAccent,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = customInclusions,
                                onValueChange = { customInclusions = it },
                                label = { Text(if (isAr) "ملاحظات إضافية حول الخدمات المضمنة" else "Autres inclusions ou remarques") },
                                placeholder = { Text(if (isAr) "مثال: مياه معدنية مجانية طوال الرحلة، هدايا تذكارية" else "Ex: Eau minérale offerte") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // ----------------------------------------------------
                // STEP 6: PHOTO GALLERY & CONTACT CONFIRMATION (Étape 6/6)
                // ----------------------------------------------------
                5 -> {
                    Text(
                        text = if (isAr) "ألبوم الصور ومراجعة النشر 📸" else "Galerie photos et confirmation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr)
                            "أضف صور الرحلة الجذابة وتحقق من معلومات التواصل لتلقي الحجوزات."
                        else
                            "Ajoutez des photos et vérifiez vos informations avant publication.",
                        fontSize = 13.sp,
                        color = labelColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Photo Gallery Card (Inspired by Screenshot 4)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(emeraldAccent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = emeraldAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isAr) "معرض الصور (حتى 15 صورة)" else "Galerie photos (max 15)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = textPrimary
                                    )
                                }
                                Text(
                                    text = "${selectedImages.size}/15",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = emeraldAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Large dashed upload box (Inspired by Screenshot 4)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(subSurfaceBg)
                                    .border(1.5.dp, emeraldAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .padding(vertical = 20.dp, horizontal = 16.dp)
                                    .testTag("pick_images_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(emeraldAccent.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudUpload,
                                            contentDescription = null,
                                            tint = emeraldAccent,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (isAr) "إضافة صور للرحلة" else "Ajouter des photos du voyage",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isAr)
                                            "JPG, PNG, WEBP • تصفح الصور من الهاتف"
                                        else
                                            "PNG, JPG, WEBP • Parcourir les fichiers",
                                        fontSize = 11.sp,
                                        color = labelColor
                                    )
                                }
                            }

                            // Thumbnails Preview
                            if (selectedImages.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isAr) "معاينة الصور (الصورة الأولى ستكون الغلاف الرئيسي) :" else "Aperçu (la première sera l'image principale) :",
                                    fontSize = 11.sp,
                                    color = emeraldAccent
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(selectedImages) { uri ->
                                        Box(
                                            modifier = Modifier
                                                .size(90.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                        ) {
                                            AsyncImage(
                                                model = uri,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            IconButton(
                                                onClick = { selectedImages.remove(uri) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(24.dp)
                                                    .background(Color.Black.copy(alpha = 0.65f))
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Video URL option
                            OutlinedTextField(
                                value = videoUrl,
                                onValueChange = { videoUrl = it },
                                label = { Text(if (isAr) "رابط فيديو ترويجي (YouTube / TikTok / IG)" else "Lien vidéo (Optionnel)") },
                                placeholder = { Text("https://www.youtube.com/watch?v=...") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact Details Card (Inspired by Screenshots 10 & 11)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(emeraldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = emeraldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "بيانات الاتصال والتأكيد 📞" else "Détails de contact 📞",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = organizerName,
                                onValueChange = { organizerName = it },
                                label = { Text(if (isAr) "اسم المنظم أو الوكالة" else "Nom de l'organisateur") },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_trip_organizer_name")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text(if (isAr) "رقم الهاتف / واتساب للحجز *" else "Téléphone / WhatsApp *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_trip_phone_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Summary recap box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(subSurfaceBg)
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isAr) "📋 ملخص الرحلة:" else "📋 Récapitulatif :",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = emeraldAccent
                                    )
                                    Text(
                                        text = "• ${title.ifBlank { if (isAr) "رحلة سياحية" else "Voyage" }} ($tripCategory)",
                                        fontSize = 11.sp,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "• ${if (isAr) "من" else "De"} $departureCity ${if (isAr) "إلى" else "vers"} $destinationWilaya",
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        text = "• ${computeDuration(departureDateIso, returnDateIso, isAr)} (${formatDisplayDate(departureDateIso, isAr)})",
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        text = "• $pricePerPerson DA • $totalSeats ${if (isAr) "مقعد متوفر" else "places"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = emeraldAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // ========================================================
        // FLOATING ERROR BANNER (Inspired by user screenshot 3)
        // ========================================================
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF450A0A))
                    .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFFCA5A5),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFEE2E2),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = { errorMessage = null },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color(0xFFFCA5A5),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // ========================================================
        // BOTTOM ACTION BAR (Retour / Continuer / Publier)
        // (Inspired directly by user screenshots)
        // ========================================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = darkCardBg,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Retour Button
                OutlinedButton(
                    onClick = {
                        if (currentStep > 0) {
                            errorMessage = null
                            currentStep--
                        }
                    },
                    enabled = currentStep > 0,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textPrimary,
                        disabledContentColor = labelColor.copy(alpha = 0.5f)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (currentStep > 0) borderColor else borderColor.copy(alpha = 0.3f))
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("step_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "السابق" else "Retour",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Continuer / Publier Button
                val isLastStep = currentStep == totalSteps - 1

                Button(
                    onClick = {
                        if (validateCurrentStep()) {
                            if (!isLastStep) {
                                currentStep++
                            } else {
                                // Final publication trigger
                                if (isPublishing) return@Button

                                val finalBadge = when {
                                    userProfile.isGoldenVerified -> "GOLDEN_VERIFIED"
                                    userProfile.isAgency || organizerType == "AGENCY" -> "AGENCY_BLUE"
                                    else -> ""
                                }

                                val finalInclusions = buildString {
                                    append(selectedAmenities.joinToString(" + ") { amId ->
                                        amenitiesList.find { it.id == amId }?.let { if (isAr) it.labelAr else it.labelFr } ?: ""
                                    })
                                    if (customInclusions.isNotBlank()) {
                                        if (isNotEmpty()) append(" • ")
                                        append(customInclusions.trim())
                                    }
                                }

                                val trip = Trip(
                                    title = title.trim(),
                                    description = programDetails.trim().ifBlank { "رحلة استكشافية ممتعة وبرنامج شيق" },
                                    departureWilaya = departureWilaya,
                                    departureCity = departureCity,
                                    destinationWilaya = destinationWilaya,
                                    destinationPlace = destinationPlace.trim().ifBlank { destinationWilaya.substringAfter("-").trim() },
                                    departureDate = departureDateIso,
                                    returnDate = returnDateIso,
                                    departureTime = departureTime,
                                    tripType = tripCategory,
                                    transportType = transportType,
                                    priceDzd = pricePerPerson.toDoubleOrNull() ?: 18000.0,
                                    totalSeats = totalSeats.toIntOrNull() ?: 30,
                                    availableSeats = totalSeats.toIntOrNull() ?: 30,
                                    organizerType = organizerType,
                                    organizerName = organizerName.trim().ifBlank { "منظم رحلات" },
                                    organizerPhone = phone.trim().ifBlank { "0550123456" },
                                    organizerEmail = userProfile.email,
                                    organizerAvatarUrl = userProfile.avatarUrl,
                                    organizerBadge = finalBadge,
                                    inclusions = finalInclusions,
                                    routeItinerary = itinerary,
                                    googleMapsUrl = googleMapsUrl,
                                    videoUrl = videoUrl,
                                    imageUris = selectedImages.joinToString(",") { it.toString() }
                                )

                                if (onPublishTripWithImages != null) {
                                    onPublishTripWithImages(trip, selectedImages.toList())
                                } else {
                                    onTripAdded(trip)
                                }
                            }
                        }
                    },
                    enabled = !isPublishing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = emeraldAccent,
                        disabledContainerColor = emeraldAccent.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(50.dp)
                        .testTag(if (isLastStep) "publish_trip_button" else "step_next_button")
                ) {
                    if (isPublishing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "جاري النشر..." else "Publication...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else if (isLastStep) {
                        Icon(
                            imageVector = Icons.Default.Publish,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "نشر الرحلة الآن 🚀" else "Publier le voyage",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = if (isAr) "التالي" else "Continuer",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DurationPill(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = LocalAppThemePalette.current
    val pillBg = if (palette.isLight) Color(0xFFF1F5F9) else Color(0xFF0E1F17)
    val pillBorder = if (palette.isLight) palette.darkBorderColor else Color(0xFF1C3A2C)
    val pillText = if (palette.isLight) palette.textPrimary else Color(0xFFD2E2D9)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(pillBg)
            .border(1.dp, pillBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = pillText,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

private fun formatDisplayDate(isoDate: String, isAr: Boolean): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = parser.parse(isoDate) ?: return isoDate
        val locale = if (isAr) Locale("ar") else Locale.FRENCH
        val formatter = SimpleDateFormat("dd MMMM yyyy", locale)
        formatter.format(date)
    } catch (e: Exception) {
        isoDate
    }
}

private fun computeDuration(startIso: String, endIso: String, isAr: Boolean): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val d1 = parser.parse(startIso)
        val d2 = parser.parse(endIso)
        if (d1 != null && d2 != null) {
            val diffDays = ((d2.time - d1.time) / (1000 * 60 * 60 * 24)).toInt()
            val days = (diffDays + 1).coerceAtLeast(1)
            val nights = (days - 1).coerceAtLeast(0)
            if (isAr) "$days أيام / $nights ليالٍ" else "$days jours / $nights nuits"
        } else {
            if (isAr) "4 أيام / 3 ليالٍ" else "4 jours / 3 nuits"
        }
    } catch (e: Exception) {
        if (isAr) "4 أيام / 3 ليالٍ" else "4 jours / 3 nuits"
    }
}

private fun addDaysToDate(baseIso: String, daysToAdd: Int): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = parser.parse(baseIso) ?: return baseIso
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, daysToAdd)
        parser.format(cal.time)
    } catch (e: Exception) {
        baseIso
    }
}
