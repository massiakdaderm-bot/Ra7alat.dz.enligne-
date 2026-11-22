package com.example.ui.dialogs

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Trip
import com.example.data.model.UserProfile

@Composable
fun BookingDialog(
    trip: Trip,
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onConfirmBooking: (Int, String) -> Unit,
    isAr: Boolean = true
) {
    var seats by remember { mutableIntStateOf(1) }
    var passengerName by remember { mutableStateOf(userProfile.fullName.ifBlank { "" }) }
    var passengerPhone by remember { mutableStateOf(userProfile.phone.ifBlank { "" }) }
    var notes by remember { mutableStateOf("") }

    val totalPrice = trip.priceDzd * seats
    val maxSeats = trip.availableSeats.coerceAtLeast(1)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("booking_dialog"),
            color = Color(0xFF0F1E17),
            border = BorderStroke(1.dp, Color(0xFF1F4331))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Bar: Title with Ticket Icon and Close 'X' button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "حجز رحلة سياحية 🎫" else "Réservation de Voyage 🎫",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF182D22))
                            .clickable { onDismiss() }
                            .testTag("cancel_booking_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trip Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14271D)),
                    border = BorderStroke(1.dp, Color(0xFF224835))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = trip.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "📍 ${trip.departureWilaya} ➔ ${trip.destinationWilaya}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "📅 ${trip.departureDate}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "منظم الرحلة: ${trip.organizerName}" else "Organisateur: ${trip.organizerName}",
                                color = Color(0xFF81C784),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${trip.priceDzd.toInt()} د.ج / مقعد",
                                color = Color(0xFFFFB300),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Passenger Info Section
                Text(
                    text = if (isAr) "بيانات المسافر الرئيسي :" else "Informations du voyageur :",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Name Input
                OutlinedTextField(
                    value = passengerName,
                    onValueChange = { passengerName = it },
                    placeholder = { Text(if (isAr) "الاسم واللقب" else "Nom et Prénom", color = Color(0xFF64748B)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF81C784)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0C1B13),
                        unfocusedContainerColor = Color(0xFF0C1B13),
                        focusedBorderColor = Color(0xFF2E6548),
                        unfocusedBorderColor = Color(0xFF1E3D2C)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone Input
                OutlinedTextField(
                    value = passengerPhone,
                    onValueChange = { passengerPhone = it },
                    placeholder = { Text(if (isAr) "رقم الهاتف للتواصل" else "Numéro de téléphone", color = Color(0xFF64748B)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF81C784)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_phone_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0C1B13),
                        unfocusedContainerColor = Color(0xFF0C1B13),
                        focusedBorderColor = Color(0xFF2E6548),
                        unfocusedBorderColor = Color(0xFF1E3D2C)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Seats Stepper Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14271D)),
                    border = BorderStroke(1.dp, Color(0xFF224835))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EventSeat, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "عدد المقاعد المطلوبة" else "Nombre de places",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (isAr) "المتبقي: ${trip.availableSeats} مقعد" else "Disponibles: ${trip.availableSeats} places",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.5.sp
                                )
                            }

                            // Stepper (- [seats] +)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (seats > 1) Color(0xFF1E3D2C) else Color(0xFF112217))
                                        .clickable(enabled = seats > 1) { seats-- }
                                        .testTag("booking_decrement_seats"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = if (seats > 1) Color.White else Color(0xFF4B5563), modifier = Modifier.size(18.dp))
                                }

                                Text(
                                    text = "$seats",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (seats < maxSeats) Color(0xFF257B52) else Color(0xFF112217))
                                        .clickable(enabled = seats < maxSeats) { seats++ }
                                        .testTag("booking_increment_seats"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = if (seats < maxSeats) Color.White else Color(0xFF4B5563), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Summary Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1B13)),
                    border = BorderStroke(1.dp, Color(0xFF1D3E2D))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "سعر التذكرة للفرد:" else "Prix unitaire :",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.5.sp
                            )
                            Text(
                                text = "${trip.priceDzd.toInt()} د.ج",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "عدد المقاعد:" else "Places :",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.5.sp
                            )
                            Text(
                                text = "× $seats",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF1E3D2C))
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "المبلغ الإجمالي المستحق:" else "Total à payer :",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${totalPrice.toInt()} د.ج",
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text(if (isAr) "ملاحظات إضافية للمنظم (اختياري)" else "Notes pour l'organisateur (optionnel)", color = Color(0xFF64748B), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0C1B13),
                        unfocusedContainerColor = Color(0xFF0C1B13),
                        focusedBorderColor = Color(0xFF2E6548),
                        unfocusedBorderColor = Color(0xFF1E3D2C)
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Confirm Booking Button
                Button(
                    onClick = {
                        val finalNotes = buildString {
                            if (passengerName.isNotBlank()) append("المسافر: $passengerName ")
                            if (passengerPhone.isNotBlank()) append("هاتف: $passengerPhone ")
                            if (notes.isNotBlank()) append("ملاحظات: $notes")
                        }.trim()
                        onConfirmBooking(seats, finalNotes)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_booking_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF257B52))
                ) {
                    Text(
                        text = if (isAr) "🎫 تأكيد الحجز فوراً" else "🎫 Confirmer la Réservation",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
