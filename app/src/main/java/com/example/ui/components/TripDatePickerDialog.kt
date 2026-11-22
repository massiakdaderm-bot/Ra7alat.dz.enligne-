package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isAr: Boolean = true
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        onDateSelected(formatter.format(Date(millis)))
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("date_picker_confirm_button")
            ) {
                Text(if (isAr) "تأكيد التاريخ" else "Confirmer")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("date_picker_cancel_button")
            ) {
                Text(if (isAr) "إلغاء" else "Annuler")
            }
        },
        modifier = Modifier.testTag("trip_date_picker_dialog")
    ) {
        DatePicker(state = datePickerState)
    }
}
