package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserProfile

@Composable
fun AgencyVerificationDialog(
    userProfile: UserProfile,
    publishedTripsCount: Int = 0,
    onSwitchToAgency: () -> Unit = {},
    onDismiss: () -> Unit,
    isAr: Boolean = false
) {
    val context = LocalContext.current
    val isAgency = userProfile.role == "AGENCY" || userProfile.agencyName.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("agency_verification_dialog"),
            color = Color(0xFF0F1E17),
            border = BorderStroke(1.dp, Color(0xFF1F4331))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Bar: Title with Golden Star and Close 'X' button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "توثيق الحساب بالباج الذهبي ★" else "Vérification Compte Or ★",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    // Circular Close Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF182D22))
                            .clickable { onDismiss() },
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

                if (userProfile.isGoldenVerified) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            com.example.ui.components.AppLottieSuccessBadge(size = 80.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isAr) "وكالتك موثقة رسمياً بالشارة الذهبية! 👑✨" else "Votre agence est officiellement certifiée Badge Or ! 👑✨",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Golden Top Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                    border = BorderStroke(1.2.dp, Color(0xFFF0CB69))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Centered Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFE67E22))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) "باج معتمد ذهبي براق ✨" else "Badge Certifié Or Brillant ✨",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAr)
                                "الباج الذهبي المعتمد يمنح وكالتك أقصى درجات الثقة والمصداقية لدى جميع المسافرين في الجزائر 🇩🇿."
                            else
                                "Le badge Or certifié donne à votre agence une crédibilité maximale auprès de tous les voyageurs.",
                            color = Color(0xFF5D4037),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section Title: Conditions d'obtention du Badge Or
                Text(
                    text = if (isAr) "شروط الحصول على الباج الذهبي :" else "Conditions d'obtention du Badge Or :",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Step 1: Compte enregistré en tant qu'Agence
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14271D)),
                    border = BorderStroke(1.dp, Color(0xFF204834))
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                NumberBadge(number = "1")
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isAr) "حساب مسجل كـ وكالة سياحية" else "Compte enregistré en tant qu'Agence",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            if (isAgency) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isAgency) {
                            Text(
                                text = if (isAr) "✅ تم تسجيل الحساب كوكالة سياحية معتمدة." else "✅ Compte enregistré en tant qu'Agence.",
                                color = Color(0xFF81C784),
                                fontSize = 12.5.sp
                            )
                        } else {
                            Text(
                                text = if (isAr) "⚠️ حساب فردي. يرجى التحويل إلى وكالة." else "⚠️ Compte individuel. Veuillez passer en Agence.",
                                color = Color(0xFFFFB74D),
                                fontSize = 12.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onSwitchToAgency,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D3E2D)),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF2E6548)),
                                modifier = Modifier.testTag("switch_to_agency_button")
                            ) {
                                Text(
                                    text = if (isAr) "التحويل إلى وكالة 🏢" else "Changer en Agence 🏢",
                                    color = Color(0xFF81C784),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Step 2: Organiser au moins 3 voyages
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14271D)),
                    border = BorderStroke(1.dp, Color(0xFF204834))
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                NumberBadge(number = "2")
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isAr) "تنظيم 3 رحلات على الأقل" else "Organiser au moins 3 voyages",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            if (publishedTripsCount >= 3) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = "...",
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val progress = (publishedTripsCount / 3f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (publishedTripsCount >= 3) Color(0xFF4CAF50) else Color(0xFFFFB300),
                            trackColor = Color(0xFF0C1B13)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "$publishedTripsCount / 3 رحلات منظمة" else "$publishedTripsCount / 3 voyages organisés",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                            val remaining = (3 - publishedTripsCount).coerceAtLeast(0)
                            Text(
                                text = if (remaining == 0) {
                                    if (isAr) "الشرط مكتمل ✅" else "Condition remplie ✅"
                                } else {
                                    if (isAr) "المتبقي $remaining" else "Restant $remaining"
                                },
                                color = if (remaining == 0) Color(0xFF81C784) else Color(0xFFFFB74D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Step 3: Envoyer les preuves via WhatsApp
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14271D)),
                    border = BorderStroke(1.dp, Color(0xFF204834))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NumberBadge(number = "3")
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isAr) "إرسال الإثباتات عبر WhatsApp 💬" else "Envoyer les preuves via WhatsApp 💬",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAr) "يرجى إرسال هذه العناصر عبر WhatsApp :" else "Veuillez envoyer ces éléments sur WhatsApp :",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = if (isAr) "🏢 1. صور لمقر وواجهة الوكالة" else "🏢 1. Photos du local et façade de l'agence", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                            Text(text = if (isAr) "🤳 2. سيلفي أمام الواجهة الخارجية للوكالة" else "🤳 2. Selfie devant l'extérieur de l'agence", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                            Text(text = if (isAr) "📸 3. صورة للمكتب من الداخل" else "📸 3. Photo de l'intérieur du bureau", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                            Text(text = if (isAr) "🎥 4. فيديو يثبت أنك المسير أو صاحب الوكالة" else "🎥 4. Vidéo prouvant que vous êtes le gérant", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big WhatsApp Button
                Button(
                    onClick = {
                        val agencyName = userProfile.agencyName.ifBlank { userProfile.fullName.ifBlank { "وكالة سياحية" } }
                        val message = if (isAr) {
                            "السلام عليكم، أرغب في توثيق حساب وكالتي ($agencyName) بالباج الذهبي في تطبيق رحلات الجزائر 🇩🇿. هذه إثباتاتي والوثائق المطلوبة:"
                        } else {
                            "Bonjour, je souhaite certifier mon compte agence ($agencyName) avec le Badge Or sur l'application Ra7alat DZ 🇩🇿. Voici mes justificatifs:"
                        }
                        val phone = "213670085612"
                        val url = "https://wa.me/$phone?text=${Uri.encode(message)}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"))
                            context.startActivity(webIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("whatsapp_verification_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Text(
                        text = if (isAr) "💬 التواصل عبر WhatsApp لطلب التوثيق 💬" else "💬 Contacter sur WhatsApp pour vérification 💬",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Disclaimer Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF14241B))
                        .border(1.dp, Color(0xFF1E3A2B), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr)
                            "تتم مراجعة طلب التوثيق وتفعيل الشارة الذهبية يدوياً من طرف إدارة المنصة بعد التأكد من الوثائق والإثباتات عبر قاعدة البيانات."
                        else
                            "La vérification et l'attribution du badge Or sont traitées manuellement par l'administration après réception des justificatifs.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberBadge(number: String) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFF1D0)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            color = Color(0xFF1A1A1A),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
