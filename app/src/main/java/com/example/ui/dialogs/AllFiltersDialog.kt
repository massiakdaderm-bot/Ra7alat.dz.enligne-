package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AlgeriaConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllFiltersDialog(
    selectedWilaya: String,
    selectedTripType: String,
    selectedPriceFilter: String,
    onApply: (String, String, String) -> Unit,
    onDismiss: () -> Unit,
    isAr: Boolean = false
) {
    var curWilaya by remember { mutableStateOf(selectedWilaya) }
    var curType by remember { mutableStateOf(selectedTripType) }
    var curPrice by remember { mutableStateOf(selectedPriceFilter) }
    var onlyAvailable by remember { mutableStateOf(true) }

    var showWilayaPicker by remember { mutableStateOf(false) }
    var showDestinationPicker by remember { mutableStateOf(false) }
    var showPricePicker by remember { mutableStateOf(false) }

    val greenButtonColor = Color(0xFF2E7D32)
    val sheetBackgroundColor = Color(0xFF202226)
    val emeraldGreen = Color(0xFF2ECC71)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            onApply(curWilaya, curType, curPrice)
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = sheetBackgroundColor,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null,
        modifier = Modifier.testTag("all_filters_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header
            Text(
                text = if (isAr) "تصفية وترتيب ⚙️" else "Filtres et tri ⚙️",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Lieu de départ Button
            Button(
                onClick = { showWilayaPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("filter_departure_button"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenButtonColor)
            ) {
                Text(
                    text = if (curWilaya != "الكل") "📍 $curWilaya" else if (isAr) "📍 مكان الانطلاق" else "📍 Lieu de départ",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Destination Button
            Button(
                onClick = { showDestinationPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("filter_destination_button"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenButtonColor)
            ) {
                Text(
                    text = if (curType != "الكل") "🎯 $curType" else if (isAr) "🎯 الوجهة" else "🎯 Destination",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Prix Button
            Button(
                onClick = { showPricePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("filter_price_button"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenButtonColor)
            ) {
                Text(
                    text = if (curPrice != "الكل") "💰 $curPrice" else if (isAr) "💰 السعر" else "💰 Prix",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 4. Row: Voyages disponibles + Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "الرحلات المتاحة" else "Voyages disponibles",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Switch(
                    checked = onlyAvailable,
                    onCheckedChange = { onlyAvailable = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = greenButtonColor,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF2C3036)
                    ),
                    modifier = Modifier.testTag("filter_available_trips_switch")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Fermer Button (Bottom right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onApply(curWilaya, curType, curPrice)
                        onDismiss()
                    },
                    modifier = Modifier.testTag("close_filters_button")
                ) {
                    Text(
                        text = if (isAr) "إغلاق" else "Fermer",
                        color = emeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    // Departure Wilaya Picker Dialog
    if (showWilayaPicker) {
        AlertDialog(
            onDismissRequest = { showWilayaPicker = false },
            title = {
                Text(
                    text = if (isAr) "اختر ولاية الانطلاق" else "Lieu de départ",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    curWilaya = "الكل"
                                    showWilayaPicker = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(text = if (isAr) "جميع الولايات (الكل)" else "Toutes les wilayas", color = Color.White)
                        }
                    }
                    items(AlgeriaConstants.WILAYAS_58) { wilaya ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    curWilaya = wilaya
                                    showWilayaPicker = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(text = wilaya, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWilayaPicker = false }) {
                    Text(if (isAr) "تم" else "OK", color = emeraldGreen)
                }
            },
            containerColor = Color(0xFF202226)
        )
    }

    // Destination / Category Picker Dialog
    if (showDestinationPicker) {
        AlertDialog(
            onDismissRequest = { showDestinationPicker = false },
            title = {
                Text(
                    text = if (isAr) "اختر الوجهة / نوع الرحلة" else "Destination / Type",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    curType = "الكل"
                                    showDestinationPicker = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(text = if (isAr) "جميع الوجهات (الكل)" else "Toutes destinations", color = Color.White)
                        }
                    }
                    items(AlgeriaConstants.TRIP_TYPES) { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    curType = type
                                    showDestinationPicker = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(text = type, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDestinationPicker = false }) {
                    Text(if (isAr) "تم" else "OK", color = emeraldGreen)
                }
            },
            containerColor = Color(0xFF202226)
        )
    }

    // Price Picker Dialog
    if (showPricePicker) {
        AlertDialog(
            onDismissRequest = { showPricePicker = false },
            title = {
                Text(
                    text = if (isAr) "اختر نطاق السعر" else "Fourchette de prix",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    AlgeriaConstants.PRICE_FILTERS.forEach { price ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    curPrice = price
                                    showPricePicker = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(text = price, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPricePicker = false }) {
                    Text(if (isAr) "تم" else "OK", color = emeraldGreen)
                }
            },
            containerColor = Color(0xFF202226)
        )
    }
}

