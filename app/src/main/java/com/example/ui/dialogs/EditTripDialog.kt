package com.example.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Trip
import com.example.ui.components.TripDatePickerDialog
import com.example.ui.theme.LocalAppThemePalette
import com.example.util.AlgeriaConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripDialog(
    trip: Trip,
    onDismiss: () -> Unit,
    onSaveTrip: (Trip) -> Unit,
    isAr: Boolean = false
) {
    val palette = LocalAppThemePalette.current
    val emeraldAccent = palette.accentColor
    val darkCanvasBg = palette.darkCanvasBg
    val darkCardBg = palette.darkCardBg
    val darkInputBg = palette.darkSurfaceBg
    val borderColor = Color(0xFF1F4A32)
    val labelColor = Color(0xFF8CA397)

    // Form fields pre-filled from trip
    var title by remember { mutableStateOf(trip.title) }
    var destinationWilaya by remember { mutableStateOf(trip.destinationWilaya.ifBlank { "16 - الجزائر العاصمة" }) }
    var destinationPlace by remember { mutableStateOf(trip.destinationPlace) }
    var tripType by remember { mutableStateOf(trip.tripType.ifBlank { "صحراوية 🏜️" }) }
    var transportType by remember { mutableStateOf(trip.transportType.ifBlank { "حافلة سياحية VIP 🚌" }) }
    var departureWilaya by remember { mutableStateOf(trip.departureWilaya.ifBlank { "16 - الجزائر العاصمة" }) }
    var departureCity by remember { mutableStateOf(trip.departureCity) }

    var departureDateIso by remember { mutableStateOf(trip.departureDate) }
    var returnDateIso by remember { mutableStateOf(trip.returnDate) }
    var departureTime by remember { mutableStateOf(trip.departureTime.ifBlank { "06:00" }) }
    var durationDays by remember { mutableStateOf(trip.durationDays.toString()) }

    var totalSeats by remember { mutableStateOf(trip.totalSeats.toString()) }
    var availableSeats by remember { mutableStateOf(trip.availableSeats.toString()) }
    var priceDzd by remember { mutableStateOf(trip.priceDzd.toInt().toString()) }
    var status by remember { mutableStateOf(trip.status.ifBlank { "AVAILABLE" }) }

    var description by remember { mutableStateOf(trip.description) }
    var inclusions by remember { mutableStateOf(trip.inclusions.ifBlank { "Transport + Hébergement + Repas + Guide" }) }
    var organizerName by remember { mutableStateOf(trip.organizerName) }
    var organizerPhone by remember { mutableStateOf(trip.organizerPhone) }
    var videoUrl by remember { mutableStateOf(trip.videoUrl) }
    var googleMapsUrl by remember { mutableStateOf(trip.googleMapsUrl) }

    // Photos list
    val photoUris = remember {
        mutableStateListOf<String>().apply {
            if (trip.imageUris.isNotBlank()) {
                addAll(trip.imageUris.split(",").map { it.trim() }.filter { it.isNotBlank() })
            } else if (trip.imageResName.isNotBlank()) {
                add(trip.imageResName)
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val uriStr = uri.toString()
            if (!photoUris.contains(uriStr)) {
                photoUris.add(uriStr)
            }
        }
    }

    var showDepartureDatePicker by remember { mutableStateOf(false) }
    var showReturnDatePicker by remember { mutableStateOf(false) }

    var expandedDestWilaya by remember { mutableStateOf(false) }
    var expandedTripType by remember { mutableStateOf(false) }
    var expandedTransport by remember { mutableStateOf(false) }
    var expandedDeptWilaya by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

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
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = emeraldAccent,
        unfocusedLabelColor = labelColor
    )

    val tripCategories = listOf(
        "صحراوية 🏜️",
        "شاطئية وصيفية 🏖️",
        "ثقافية وتاريخية 🏛️",
        "جبلية ومغامرة ⛰️",
        "عائلية وترفيهية 🎡",
        "تسوق وجولات 🛍️"
    )

    val transportOptions = listOf(
        "حافلة سياحية VIP 🚌",
        "حافلة صغيرة Minibus 🚐",
        "سيارة دفع رباعي 4x4 🚙",
        "طائرة Air Algérie ✈️",
        "قطار SNTF 🚆",
        "باخرة Ferries 🚢"
    )

    val statusOptions = listOf(
        "AVAILABLE" to if (isAr) "متاحة للحجز 🟢" else "Disponible 🟢",
        "LAST_SEATS" to if (isAr) "آخر المقاعد المتبقية 🟠" else "Dernières places 🟠",
        "COMPLETED" to if (isAr) "مكتملة الحجز 🔴" else "Complet 🔴",
        "CANCELLED" to if (isAr) "ملغاة من المنظم ⚪" else "Annulé ⚪"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = darkCanvasBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header (Close circle [X] + Title)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF132B1E))
                            .clickable { onDismiss() }
                            .testTag("edit_trip_dialog_close_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = emeraldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (isAr) "تعديل بيانات الرحلة ✏️" else "Modifier le voyage ✏️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = emeraldAccent
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 16.dp)
                ) {
                    // 1. Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(if (isAr) "عنوان الرحلة" else "Titre du voyage") },
                        leadingIcon = {
                            Icon(Icons.Default.Title, contentDescription = null, tint = emeraldAccent)
                        },
                        colors = textFieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_trip_title_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Destination Wilaya
                    ExposedDropdownMenuBox(
                        expanded = expandedDestWilaya,
                        onExpandedChange = { expandedDestWilaya = !expandedDestWilaya }
                    ) {
                        OutlinedTextField(
                            value = destinationWilaya,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isAr) "ولاية الوجهة" else "Wilaya de destination") },
                            leadingIcon = {
                                Icon(Icons.Default.Place, contentDescription = null, tint = emeraldAccent)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDestWilaya) },
                            colors = textFieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDestWilaya,
                            onDismissRequest = { expandedDestWilaya = false },
                            modifier = Modifier.background(darkCardBg)
                        ) {
                            AlgeriaConstants.WILAYAS.forEach { wilaya ->
                                DropdownMenuItem(
                                    text = { Text(wilaya, color = Color.White) },
                                    onClick = {
                                        destinationWilaya = wilaya
                                        expandedDestWilaya = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Exact destination place
                    OutlinedTextField(
                        value = destinationPlace,
                        onValueChange = { destinationPlace = it },
                        label = { Text(if (isAr) "المكان أو المعلم المحدد للوجهة" else "Lieu exact / Point d'intérêt") },
                        placeholder = { Text(if (isAr) "مثال: تمنراست أسكرام، تيكجدة..." else "Ex: Assekrem, Tikjda...", color = labelColor) },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = emeraldAccent)
                        },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Trip Category
                    ExposedDropdownMenuBox(
                        expanded = expandedTripType,
                        onExpandedChange = { expandedTripType = !expandedTripType }
                    ) {
                        OutlinedTextField(
                            value = tripType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isAr) "تصنيف الرحلة" else "Catégorie de voyage") },
                            leadingIcon = {
                                Icon(Icons.Default.Category, contentDescription = null, tint = emeraldAccent)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTripType) },
                            colors = textFieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTripType,
                            onDismissRequest = { expandedTripType = false },
                            modifier = Modifier.background(darkCardBg)
                        ) {
                            tripCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = Color.White) },
                                    onClick = {
                                        tripType = cat
                                        expandedTripType = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 5. Transportation
                    ExposedDropdownMenuBox(
                        expanded = expandedTransport,
                        onExpandedChange = { expandedTransport = !expandedTransport }
                    ) {
                        OutlinedTextField(
                            value = transportType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isAr) "وسيلة النقل" else "Moyen de transport") },
                            leadingIcon = {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = emeraldAccent)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTransport) },
                            colors = textFieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTransport,
                            onDismissRequest = { expandedTransport = false },
                            modifier = Modifier.background(darkCardBg)
                        ) {
                            transportOptions.forEach { trans ->
                                DropdownMenuItem(
                                    text = { Text(trans, color = Color.White) },
                                    onClick = {
                                        transportType = trans
                                        expandedTransport = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6. Departure Wilaya & City / Meeting Point
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expandedDeptWilaya,
                            onExpandedChange = { expandedDeptWilaya = !expandedDeptWilaya },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = departureWilaya,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isAr) "الانطلاق" else "Wilaya départ") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDeptWilaya) },
                                colors = textFieldColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDeptWilaya,
                                onDismissRequest = { expandedDeptWilaya = false },
                                modifier = Modifier.background(darkCardBg)
                            ) {
                                AlgeriaConstants.WILAYAS.forEach { wilaya ->
                                    DropdownMenuItem(
                                        text = { Text(wilaya, color = Color.White) },
                                        onClick = {
                                            departureWilaya = wilaya
                                            expandedDeptWilaya = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = departureCity,
                            onValueChange = { departureCity = it },
                            label = { Text(if (isAr) "مكان التجمع" else "Ville / Point rdv") },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7. Dates and Calendar Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = emeraldAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "جدول ومواعيد الرحلة (من / إلى) 📅" else "Calendrier et dates du voyage (Du / Au) 📅",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Start Date
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(darkInputBg)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { showDepartureDatePicker = true }
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(if (isAr) "تاريخ الانطلاق (من)" else "Date de départ (Du)", fontSize = 11.sp, color = labelColor)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = formatDisplayDate(departureDateIso, isAr),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = departureDateIso,
                                            fontSize = 10.sp,
                                            color = emeraldAccent
                                        )
                                    }
                                }

                                // Return Date
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(darkInputBg)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { showReturnDatePicker = true }
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(if (isAr) "تاريخ العودة (إلى)" else "Date de retour (Au)", fontSize = 11.sp, color = labelColor)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (returnDateIso.isNotBlank()) formatDisplayDate(returnDateIso, isAr) else (if (isAr) "حدد التاريخ" else "Sélectionner"),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        if (returnDateIso.isNotBlank()) {
                                            Text(
                                                text = returnDateIso,
                                                fontSize = 10.sp,
                                                color = emeraldAccent
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Estimated duration
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = emeraldAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${if (isAr) "المدة المقدرة:" else "Durée estimée:"} ${computeDuration(departureDateIso, returnDateIso, isAr)}",
                                    color = emeraldAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val presets = listOf(
                                    (if (isAr) "يوم واحد ☀️" else "1 Jour ☀️") to 1,
                                    (if (isAr) "عطلة أسبوع 🏕️" else "Week-end 🏕️") to 2,
                                    (if (isAr) "3 أيام 🎒" else "3 Jours 🎒") to 3,
                                    (if (isAr) "أسبوع 🌴" else "1 Semaine 🌴") to 7
                                )
                                presets.forEach { (label, days) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1B3D2B))
                                            .clickable {
                                                durationDays = days.toString()
                                                returnDateIso = addDaysToDate(departureDateIso, days - 1)
                                            }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 8. Departure Time & Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = departureTime,
                            onValueChange = { departureTime = it },
                            label = { Text(if (isAr) "ساعة الانطلاق" else "Heure départ") },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = durationDays,
                            onValueChange = { durationDays = it },
                            label = { Text(if (isAr) "المدة (أيام)" else "Durée (jours)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 9. Seats & Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = totalSeats,
                            onValueChange = { totalSeats = it },
                            label = { Text(if (isAr) "إجمالي المقاعد" else "Places totales") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = availableSeats,
                            onValueChange = { availableSeats = it },
                            label = { Text(if (isAr) "المقاعد المتبقية" else "Places restantes") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = priceDzd,
                            onValueChange = { priceDzd = it },
                            label = { Text(if (isAr) "السعر (دج)" else "Prix (DZD)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 10. Status dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedStatus,
                        onExpandedChange = { expandedStatus = !expandedStatus }
                    ) {
                        val currentStatusLabel = statusOptions.firstOrNull { it.first == status }?.second ?: status
                        OutlinedTextField(
                            value = currentStatusLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isAr) "حالة الرحلة" else "Statut du voyage") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                            colors = textFieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStatus,
                            onDismissRequest = { expandedStatus = false },
                            modifier = Modifier.background(darkCardBg)
                        ) {
                            statusOptions.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, color = Color.White) },
                                    onClick = {
                                        status = key
                                        expandedStatus = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 11. Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isAr) "وصف وتفاصيل الرحلة" else "Description détaillée du voyage") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 12. Inclusions
                    OutlinedTextField(
                        value = inclusions,
                        onValueChange = { inclusions = it },
                        label = { Text(if (isAr) "الخدمات المشمولة في السعر" else "Inclus dans le tarif (Services compris)") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 13. Organizer Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = organizerName,
                            onValueChange = { organizerName = it },
                            label = { Text(if (isAr) "اسم المنظم / الوكالة" else "Nom organisateur") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = emeraldAccent) },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1.1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = organizerPhone,
                            onValueChange = { organizerPhone = it },
                            label = { Text(if (isAr) "الهاتف" else "Téléphone") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = emeraldAccent) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 14. Video URL
                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text(if (isAr) "رابط الفيديو (YouTube / TikTok)" else "Lien vidéo (YouTube / TikTok)") },
                        leadingIcon = { Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = emeraldAccent) },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 15. Maps URL
                    OutlinedTextField(
                        value = googleMapsUrl,
                        onValueChange = { googleMapsUrl = it },
                        label = { Text(if (isAr) "رابط الموقع على الخريطة (Google Maps)" else "Lien localisation (Google Maps)") },
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = emeraldAccent) },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 16. Photos Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(darkCardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "صور الرحلة (${photoUris.size}) 🖼️" else "Photos du voyage (${photoUris.size}) 🖼️",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D31)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isAr) "+ إضافة صور" else "+ Ajouter", color = Color.White, fontSize = 12.sp)
                            }
                        }

                        if (photoUris.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(photoUris) { uriStr ->
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                    ) {
                                        AsyncImage(
                                            model = uriStr,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.7f))
                                                .clickable { photoUris.remove(uriStr) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Supprimer", tint = Color.White, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Save Button
                Button(
                    onClick = {
                        val parsedPrice = priceDzd.toDoubleOrNull() ?: trip.priceDzd
                        val parsedTotalSeats = totalSeats.toIntOrNull() ?: trip.totalSeats
                        val parsedAvailSeats = availableSeats.toIntOrNull() ?: trip.availableSeats
                        val parsedDuration = durationDays.toIntOrNull() ?: trip.durationDays

                        val updated = trip.copy(
                            title = title.trim().ifBlank { trip.title },
                            destinationWilaya = destinationWilaya.trim(),
                            destinationPlace = destinationPlace.trim(),
                            tripType = tripType.trim(),
                            transportType = transportType.trim(),
                            departureWilaya = departureWilaya.trim(),
                            departureCity = departureCity.trim(),
                            departureDate = departureDateIso.trim(),
                            returnDate = returnDateIso.trim(),
                            departureTime = departureTime.trim(),
                            durationDays = parsedDuration,
                            totalSeats = parsedTotalSeats,
                            availableSeats = parsedAvailSeats,
                            priceDzd = parsedPrice,
                            status = status,
                            description = description.trim(),
                            inclusions = inclusions.trim(),
                            organizerName = organizerName.trim(),
                            organizerPhone = organizerPhone.trim(),
                            videoUrl = videoUrl.trim(),
                            googleMapsUrl = googleMapsUrl.trim(),
                            imageUris = photoUris.joinToString(",")
                        )
                        onSaveTrip(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("edit_trip_save_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = emeraldAccent,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "حفظ التعديلات على الرحلة ✨" else "Enregistrer les modifications ✨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
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
