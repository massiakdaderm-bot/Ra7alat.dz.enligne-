package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.UpdateCheckResult
import com.example.util.UpdateType

@Composable
fun ForceUpdateDialog(
    updateResult: UpdateCheckResult,
    onDismiss: () -> Unit,
    isAr: Boolean = true
) {
    val context = LocalContext.current
    val info = updateResult.info ?: return

    AlertDialog(
        onDismissRequest = {
            if (updateResult.type != UpdateType.FORCE) onDismiss()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        },
        title = {
            Text(
                text = if (isAr) info.titleAr else info.titleFr,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isAr) info.messageAr else info.messageFr,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${if (isAr) "الإصدار الجديد: " else "Version : "}${info.latestVersionName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(info.downloadUrl))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier.testTag("force_update_confirm_button")
            ) {
                Text(if (isAr) "تحديث الآن" else "Mettre à jour")
            }
        },
        dismissButton = {
            if (updateResult.type == UpdateType.OPTIONAL) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("force_update_later_button")
                ) {
                    Text(if (isAr) "لاحقاً" else "Plus tard")
                }
            }
        },
        modifier = Modifier.testTag("force_update_dialog")
    )
}
