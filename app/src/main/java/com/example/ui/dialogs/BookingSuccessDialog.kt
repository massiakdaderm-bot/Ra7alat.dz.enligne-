package com.example.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.ui.components.AppLottieSuccessBadge

@Composable
fun BookingSuccessDialog(
    booking: Booking,
    onViewTicket: () -> Unit,
    onDismiss: () -> Unit,
    isAr: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            AppLottieSuccessBadge(size = 72.dp)
        },
        title = {
            Text(
                text = if (isAr) "تم تأكيد حجزك بنجاح! 🇩🇿" else "Réservation confirmée avec succès ! 🇩🇿",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isAr) {
                        "تهانينا! تم حجز مقاعدك في رحلة (${booking.tripTitle}). يمكنك الآن عرض تذكرة الصعود الإلكترونية أو التواصل مع المنظم عبر رقم الهاتف: ${booking.organizerPhone}"
                    } else {
                        "Félicitations ! Vos places pour (${booking.tripTitle}) sont réservées. Vous pouvez consulter votre billet ou contacter l'organisateur au ${booking.organizerPhone}"
                    },
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onViewTicket()
                    onDismiss()
                },
                modifier = Modifier.testTag("view_boarding_pass_button")
            ) {
                Text(if (isAr) "عرض التذكرة الإلكترونية" else "Voir la Carte d'embarquement")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("booking_success_dismiss_button")
            ) {
                Text(if (isAr) "حسناً" else "D'accord")
            }
        },
        modifier = Modifier.testTag("booking_success_dialog")
    )
}
