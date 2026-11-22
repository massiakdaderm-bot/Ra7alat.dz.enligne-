package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationPreferences
import com.example.util.AlgeriaConstants

private val SheetBgColor = Color(0xFF0F1E17)
private val CardBgColor = Color(0xFF14241C)
private val CardBorderColor = Color(0xFF1E3A2B)
private val EmeraldColor = Color(0xFF26A65B)
private val TextMutedColor = Color(0xFFA0AEC0)
private val GoldBadgeBg = Color(0xFFFFF3CD)
private val GoldBadgeText = Color(0xFF856404)

private val DefaultWilayasList = listOf(
    "سطيف", "وهران", "الجزائر العاصمة", "بجاية", "تلمسان", "البليدة",
    "تيزي وزو", "عنابة", "قسنطينة", "البويرة", "بومرداس", "باتنة",
    "جيجل", "بسكرة", "مستغانم", "تيبازة", "برج بوعريريج", "غرداية"
)

private val TripCategoriesList = listOf(
    "🏔️ جبلية / استكشاف",
    "🏜️ صحراوية",
    "🏖️ شاطئية / بحرية",
    "⛺ تخييم ومغامرات",
    "🏛️ تاريخية وثقافية"
)

private val QuickPrices = listOf(5000, 10000, 15000, 25000, 50000)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NotificationPreferencesDialog(
    currentPrefs: NotificationPreferences,
    onSave: (NotificationPreferences) -> Unit,
    onDismiss: () -> Unit,
    onTest: (NotificationPreferences) -> Unit = {},
    isAr: Boolean = true
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var notificationsEnabled by remember { mutableStateOf(currentPrefs.notificationsEnabled) }
    var notifyOrganizers by remember { mutableStateOf(currentPrefs.notifyOrganizersSubscribed) }
    var selectedWilayas by remember { mutableStateOf(currentPrefs.departureWilayas.toSet()) }
    var selectedTripTypes by remember { mutableStateOf(currentPrefs.tripTypes.toSet()) }
    var maxPriceText by remember {
        mutableStateOf(
            if (currentPrefs.maxPrice != null && currentPrefs.maxPrice > 0)
                currentPrefs.maxPrice.toLong().toString()
            else ""
        )
    }

    var showOtherWilayaPicker by remember { mutableStateOf(false) }

    fun buildCurrentPrefs(): NotificationPreferences {
        val parsedMaxPrice = maxPriceText.toDoubleOrNull()?.takeIf { it > 0 }
        return currentPrefs.copy(
            notificationsEnabled = notificationsEnabled,
            notifyOrganizersSubscribed = notifyOrganizers,
            departureWilayas = selectedWilayas.toList(),
            tripTypes = selectedTripTypes.toList(),
            maxPrice = parsedMaxPrice,
            notifyNewTrips = notificationsEnabled
        )
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val current = buildCurrentPrefs()
        if (isGranted) {
            onTest(current)
        } else {
            android.widget.Toast.makeText(
                context,
                if (isAr) "يرجى منح إذن الإشعارات من إعدادات الهاتف لتلقي التنبيهات 🔔" else "Veuillez autoriser les notifications dans les paramètres",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    fun triggerTestNotification() {
        val current = buildCurrentPrefs()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onTest(current)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetBgColor,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("notification_preferences_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Row matching Screenshot 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B3828)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alerts",
                        tint = EmeraldColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Personnaliser les alertes 🔔",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Recevez uniquement les voyages qui vous intéressent",
                        fontSize = 12.sp,
                        color = TextMutedColor
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onDismiss() }
                        .testTag("close_notif_sheet_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.White.copy(alpha = 0.1f)
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                // Block 1: Activer les notifications
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBgColor),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Activer les notifications",
                                color = EmeraldColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (notificationsEnabled) "Les alertes sont actives" else "Les alertes sont désactivées",
                                color = TextMutedColor,
                                fontSize = 12.sp
                            )
                        }

                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { isChecked ->
                                notificationsEnabled = isChecked
                                if (isChecked && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                                    androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                                ) {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldColor,
                                uncheckedTrackColor = Color(0xFF243B2E)
                            ),
                            modifier = Modifier.testTag("toggle_enable_notifications")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Block 2: Alertes abonnements & organisateurs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBgColor),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Alertes abonnements & organisateurs",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "👥", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Alertes quand quelqu'un vous suit ou qu'un organisateur publie",
                                color = TextMutedColor,
                                fontSize = 12.sp
                            )
                        }

                        Switch(
                            checked = notifyOrganizers,
                            onCheckedChange = { notifyOrganizers = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldColor,
                                uncheckedTrackColor = Color(0xFF243B2E)
                            ),
                            modifier = Modifier.testTag("toggle_notify_organizers")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Block 3: Wilayas de départ souhaitées
                Text(
                    text = "📍 Wilayas de départ souhaitées",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choisissez vos points de départ. Si rien n'est sélectionné, vous recevrez les alertes de toutes les wilayas.",
                    color = TextMutedColor,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mode actuel banner (Screenshot 1)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldBadgeBg)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = if (selectedWilayas.isEmpty())
                            "✨ Mode actuel: toutes les wilayas"
                        else
                            "✨ Mode actuel: ${selectedWilayas.size} wilaya(s) sélectionnée(s)",
                        color = GoldBadgeText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wilayas Pills
                val allDisplayedWilayas = remember(selectedWilayas) {
                    (DefaultWilayasList + selectedWilayas.filter { it !in DefaultWilayasList }).distinct()
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allDisplayedWilayas.forEach { wilaya ->
                        val isSelected = selectedWilayas.contains(wilaya)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) EmeraldColor else CardBgColor)
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldColor else Color(0xFF244233),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedWilayas = if (isSelected) {
                                        selectedWilayas - wilaya
                                    } else {
                                        selectedWilayas + wilaya
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = wilaya,
                                color = if (isSelected) Color.White else Color(0xFFD4E8DC),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Button: + Autre wilaya de départ... (Screenshot 1)
                OutlinedButton(
                    onClick = { showOtherWilayaPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("add_other_wilaya_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EmeraldColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = EmeraldColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Autre wilaya de départ...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Block 4: Types de voyages souhaités (Screenshot 2)
                Text(
                    text = "🌲 Types de voyages souhaités",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sélectionnez vos catégories préférées (ou toutes par défaut).",
                    color = TextMutedColor,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TripCategoriesList.forEach { category ->
                        val isSelected = selectedTripTypes.contains(category)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) EmeraldColor else CardBgColor)
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldColor else Color(0xFF244233),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedTripTypes = if (isSelected) {
                                        selectedTripTypes - category
                                    } else {
                                        selectedTripTypes + category
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = category,
                                    color = if (isSelected) Color.White else Color(0xFFD4E8DC),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Block 5: Budget maximum (DZD) (Screenshot 2)
                Text(
                    text = "💵 Budget maximum (DZD)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Alertes uniquement pour les voyages ne dépassant pas ce montant.",
                    color = TextMutedColor,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Budget Input Box matching Screenshot 2
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBgColor)
                        .border(1.dp, Color(0xFF244233), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column {
                        Text(
                            text = "Prix max (ex: 15000)",
                            color = Color(0xFF8B9B90),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BasicTextField(
                                value = maxPriceText,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() }) {
                                        maxPriceText = input
                                    }
                                },
                                textStyle = TextStyle(
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                cursorBrush = SolidColor(EmeraldColor),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("max_price_input"),
                                decorationBox = { innerTextField ->
                                    if (maxPriceText.isEmpty()) {
                                        Text(
                                            text = "بدون حد أقصى (جميع الأسعار)",
                                            color = TextMutedColor.copy(alpha = 0.5f),
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            Text(
                                text = "دج",
                                color = EmeraldColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Chips Row (Screenshot 2)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(QuickPrices) { price ->
                        val isSelected = maxPriceText == price.toString()
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) EmeraldColor else CardBgColor)
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldColor else Color(0xFF244233),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    maxPriceText = if (isSelected) "" else price.toString()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "حتى $price دج",
                                color = if (isSelected) Color.White else Color(0xFFD4E8DC),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color.White.copy(alpha = 0.1f)
            )

            // Bottom Action Bar matching Screenshot 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Test Button
                Button(
                    onClick = {
                        triggerTestNotification()
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("test_notif_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, EmeraldColor.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardBgColor,
                        contentColor = EmeraldColor
                    )
                ) {
                    Text(
                        text = "🔔 Test 🔔",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Save Button
                Button(
                    onClick = {
                        val current = buildCurrentPrefs()
                        onSave(current)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("save_notif_prefs_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "✓ Enregistrer ✓",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    // Modal to search and select any of the 58 Algerian Wilayas
    if (showOtherWilayaPicker) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredWilayas = remember(searchQuery) {
            if (searchQuery.isBlank()) AlgeriaConstants.WILAYAS
            else AlgeriaConstants.WILAYAS.filter {
                it.contains(searchQuery.trim(), ignoreCase = true)
            }
        }

        AlertDialog(
            onDismissRequest = { showOtherWilayaPicker = false },
            containerColor = CardBgColor,
            title = {
                Text(
                    text = "اختر ولاية الانطلاق",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("بحث عن ولاية...", color = TextMutedColor) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldColor)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldColor,
                            unfocusedBorderColor = Color(0xFF244233),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        items(filteredWilayas) { w ->
                            val cleanName = AlgeriaConstants.cleanWilayaName(w)
                            val isSelected = selectedWilayas.contains(cleanName)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedWilayas = if (isSelected) {
                                            selectedWilayas - cleanName
                                        } else {
                                            selectedWilayas + cleanName
                                        }
                                    }
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = w,
                                    color = if (isSelected) EmeraldColor else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = EmeraldColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showOtherWilayaPicker = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldColor)
                ) {
                    Text("تم", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
