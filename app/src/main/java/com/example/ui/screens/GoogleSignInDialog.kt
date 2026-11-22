package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.auth.AuthManager
import com.example.data.model.UserProfile
import com.example.util.AlgeriaConstants
import kotlinx.coroutines.launch

@Composable
fun GoogleSignInDialog(
    onDismiss: () -> Unit,
    onSuccess: (UserProfile) -> Unit,
    isAr: Boolean = false,
    allowDismiss: Boolean = true,
    authManager: AuthManager? = null
) {
    val context = LocalContext.current
    val effectiveAuthManager = authManager ?: remember { AuthManager(context) }
    val scope = rememberCoroutineScope()

    var isArabicLanguage by remember { mutableStateOf(isAr) }
    var isLoginTab by remember { mutableStateOf(false) } // Default matches Screenshot 1: Créer un compte, or switches to Connexion
    var accountType by remember { mutableStateOf("VOYAGEUR") } // "VOYAGEUR" or "AGENCY"

    // Form fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var agencyName by remember { mutableStateOf("") }
    var agencyWilaya by remember { mutableStateOf("01 - أدرار") }

    var showWilayaDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    // Forgot Password Bottom Sheet State (Screenshot 1)
    var showForgotPasswordSheet by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var isForgotSending by remember { mutableStateOf(false) }
    var forgotErrorMsg by remember { mutableStateOf<String?>(null) }
    var forgotSuccessMsg by remember { mutableStateOf<String?>(null) }

    val layoutDirection = if (isArabicLanguage) LayoutDirection.Rtl else LayoutDirection.Ltr

    Dialog(
        onDismissRequest = { if (allowDismiss) onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = allowDismiss,
            dismissOnClickOutside = false
        )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("auth_screen")
            ) {
                // 1. Clean Light Background (Fullscreen)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF8FAFC),
                                    Color(0xFFF1F5F9),
                                    Color(0xFFE2E8F0)
                                )
                            )
                        )
                )

                // 2. Top Bar: Language Toggle Pill + Close Button (if allowed)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pill Language Selector: "🇩🇿 العربية" or "🇫🇷 Français"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                            .clickable { isArabicLanguage = !isArabicLanguage }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("auth_language_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabicLanguage) "🇫🇷 Français" else "🇩🇿 العربية",
                            color = Color(0xFF0F766E),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (allowDismiss) {
                        // Discreet Close Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(34.dp))
                    }
                }

                // 3. Floating Auth Card (Centered)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(top = 56.dp, bottom = 12.dp, start = 14.dp, end = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .widthIn(max = 420.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Compass Icon inside Circular Green Badge
                            CompassCircularBadge()

                            Spacer(modifier = Modifier.height(10.dp))

                            // App Title: RA7ALAT DZ 🇩🇿
                            Text(
                                text = "RA7ALAT DZ 🇩🇿",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Subtitle
                            Text(
                                text = if (isArabicLanguage) "منصة الرحلات السياحية في الجزائر"
                                else "Plateforme des voyages touristiques en Algérie",
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Segmented Switch: [ Connexion ] [ ✨ Créer un compte ]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(4.dp)
                            ) {
                                // Connexion Tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isLoginTab) Color(0xFF0D9488) else Color.Transparent)
                                        .clickable {
                                            isLoginTab = true
                                            errorMessage = null
                                            infoMessage = null
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isArabicLanguage) "تسجيل الدخول" else "Connexion",
                                        color = if (isLoginTab) Color.White else Color(0xFF475569),
                                        fontWeight = if (isLoginTab) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.5.sp
                                    )
                                }

                                // Créer un compte Tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (!isLoginTab) Color(0xFF0D9488) else Color.Transparent)
                                        .clickable {
                                            isLoginTab = false
                                            errorMessage = null
                                            infoMessage = null
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isArabicLanguage) "✨ إنشاء حساب" else "✨ Créer un compte",
                                        color = if (!isLoginTab) Color.White else Color(0xFF475569),
                                        fontWeight = if (!isLoginTab) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.5.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Error / Info Messages
                            if (errorMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFEE2E2))
                                        .border(1.dp, Color(0xFFF87171), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = errorMessage ?: "",
                                        color = Color(0xFF991B1B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (infoMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = infoMessage ?: "",
                                        color = Color(0xFF166534),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (isLoginTab) {
                                // -------------------------------------------------------------
                                // TAB 1: CONNEXION
                                // -------------------------------------------------------------

                                // Yellow Notice Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                                        .clickable { isLoginTab = false }
                                        .padding(vertical = 11.dp, horizontal = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isArabicLanguage) "👇 جديد؟ انقر هنا لإنشاء حساب 👈"
                                        else "👇 Nouveau ? Cliquez ici pour créer un compte 👈",
                                        color = Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Subheading
                                Text(
                                    text = if (isArabicLanguage) "أدخل بريدك الإلكتروني وكلمة المرور"
                                    else "Entrez votre email et mot de passe",
                                    color = Color(0xFF334155),
                                    fontSize = 13.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = if (isArabicLanguage) TextAlign.Right else TextAlign.Left
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Email Input
                                DarkOutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    placeholder = if (isArabicLanguage) "البريد الإلكتروني" else "Adresse email",
                                    leadingIcon = {
                                        Icon(Icons.Default.Mail, contentDescription = null, tint = Color(0xFF64748B))
                                    },
                                    keyboardType = KeyboardType.Email,
                                    isFocusedBorderActive = true,
                                    testTag = "auth_email_input"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Password Input
                                DarkOutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = if (isArabicLanguage) "كلمة المرور" else "Mot de passe",
                                    isPassword = true,
                                    passwordVisible = passwordVisible,
                                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                                    testTag = "auth_password_input"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Mot de passe oublié ? 🔑
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = if (isArabicLanguage) Alignment.CenterStart else Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = if (isArabicLanguage) "نسيت كلمة المرور؟ 🔑" else "Mot de passe oublié ? 🔑",
                                        color = Color(0xFF0F766E),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable {
                                            forgotEmail = email.trim()
                                            forgotErrorMsg = null
                                            forgotSuccessMsg = null
                                            showForgotPasswordSheet = true
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Se connecter Button
                                Button(
                                    onClick = {
                                        if (email.isBlank() || password.isBlank()) {
                                            errorMessage = if (isArabicLanguage) "يرجى إدخال البريد الإلكتروني وكلمة المرور"
                                            else "Veuillez entrer votre email et mot de passe"
                                            return@Button
                                        }
                                        isLoading = true
                                        errorMessage = null
                                        scope.launch {
                                            val result = effectiveAuthManager.loginWithEmail(email.trim(), password.trim())
                                            isLoading = false
                                            result.fold(
                                                onSuccess = { profile ->
                                                    onSuccess(profile)
                                                },
                                                onFailure = { err ->
                                                    errorMessage = err.message ?: if (isArabicLanguage) "فشل تسجيل الدخول" else "Échec de connexion"
                                                }
                                            )
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("auth_login_button"),
                                    shape = RoundedCornerShape(26.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = if (isArabicLanguage) "➔ تسجيل الدخول 🚀" else "➔ Se connecter 🚀",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Yellow Bottom Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(18.dp))
                                        .clickable { isLoginTab = false }
                                        .padding(vertical = 12.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isArabicLanguage) "👉 جديد؟ إنشاء حساب هنا 👈"
                                        else "👉 Nouveau ? Créer un compte ici 👈",
                                        color = Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Bottom Footer Text
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isArabicLanguage) "ليس لديك حساب ؟ " else "Vous n'avez pas de compte ? ",
                                        color = Color(0xFF64748B),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isArabicLanguage) "إنشاء حساب" else "Créer un compte",
                                        color = Color(0xFF0F766E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.clickable { isLoginTab = false }
                                    )
                                }
                            } else {
                                // -------------------------------------------------------------
                                // TAB 2: CRÉER UN COMPTE
                                // -------------------------------------------------------------

                                // 1. Type de compte :
                                Text(
                                    text = if (isArabicLanguage) "1. نوع الحساب :" else "1. Type de compte :",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = if (isArabicLanguage) TextAlign.Right else TextAlign.Left
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Account Type Selector: [ Voyageur 👦 ] [ Agence certifiée ⭐ ]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Voyageur
                                    val isVoyageur = accountType == "VOYAGEUR"
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(if (isVoyageur) Color(0xFF0D9488).copy(alpha = 0.12f) else Color(0xFFF8FAFC))
                                            .border(
                                                if (isVoyageur) BorderStroke(1.5.dp, Color(0xFF0D9488))
                                                else BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                                RoundedCornerShape(18.dp)
                                            )
                                            .clickable { accountType = "VOYAGEUR" },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isArabicLanguage) "مسافر 👦" else "Voyageur 👦",
                                            color = if (isVoyageur) Color(0xFF0D9488) else Color(0xFF475569),
                                            fontWeight = if (isVoyageur) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.5.sp
                                        )
                                    }

                                    // Agence certifiée
                                    val isAgency = accountType == "AGENCY"
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(if (isAgency) Color(0xFF0D9488).copy(alpha = 0.12f) else Color(0xFFF8FAFC))
                                            .border(
                                                if (isAgency) BorderStroke(1.5.dp, Color(0xFF0D9488))
                                                else BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                                RoundedCornerShape(18.dp)
                                            )
                                            .clickable { accountType = "AGENCY" },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isArabicLanguage) "وكالة معتمدة ⭐" else "Agence certifiée ⭐",
                                            color = if (isAgency) Color(0xFF0D9488) else Color(0xFF475569),
                                            fontWeight = if (isAgency) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.5.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Agency-Specific Fields
                                if (accountType == "AGENCY") {
                                    // Nom de l'agence de voyage
                                    DarkOutlinedTextField(
                                        value = agencyName,
                                        onValueChange = { agencyName = it },
                                        placeholder = if (isArabicLanguage) "اسم وكالة السياحة والأسفار" else "Nom de l'agence de voyage",
                                        leadingIcon = {
                                            Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF64748B))
                                        },
                                        testTag = "auth_agency_name_input"
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Wilaya de l'agence (Dropdown selector)
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = if (isArabicLanguage) "ولاية مقر الوكالة" else "Wilaya de l'agence",
                                            color = Color(0xFF64748B),
                                            fontSize = 11.5.sp,
                                            modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(54.dp)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(Color(0xFFF8FAFC))
                                                .border(BorderStroke(1.dp, Color(0xFFCBD5E1)), RoundedCornerShape(18.dp))
                                                .clickable { showWilayaDialog = true }
                                                .padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = agencyWilaya,
                                                    color = Color(0xFF0F172A),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDropDown,
                                                    contentDescription = "Sélectionner la wilaya",
                                                    tint = Color(0xFF64748B)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Nom et Prénom complet
                                DarkOutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    placeholder = if (isArabicLanguage) "الاسم واللقب بالكامل" else "Nom et Prénom complet",
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B))
                                    },
                                    testTag = "auth_fullname_input"
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Adresse email
                                DarkOutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    placeholder = if (isArabicLanguage) "البريد الإلكتروني" else "Adresse email",
                                    leadingIcon = {
                                        Icon(Icons.Default.Mail, contentDescription = null, tint = Color(0xFF64748B))
                                    },
                                    keyboardType = KeyboardType.Email,
                                    testTag = "auth_reg_email_input"
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Numéro de téléphone
                                DarkOutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    placeholder = if (isArabicLanguage) "رقم الهاتف" else "Numéro de téléphone",
                                    leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64748B))
                                    },
                                    keyboardType = KeyboardType.Phone,
                                    testTag = "auth_phone_input"
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Mot de passe (min 6 caractères)
                                DarkOutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = if (isArabicLanguage) "كلمة المرور (6 أحرف على الأقل)" else "Mot de passe (min 6 caractères)",
                                    isPassword = true,
                                    passwordVisible = passwordVisible,
                                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                                    testTag = "auth_reg_password_input"
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Button: Créer mon compte ✨
                                Button(
                                    onClick = {
                                        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
                                            errorMessage = if (isArabicLanguage) "يرجى ملء جميع الحقول المطلوبة"
                                            else "Veuillez remplir tous les champs obligatoires"
                                            return@Button
                                        }
                                        if (password.length < 6) {
                                            errorMessage = if (isArabicLanguage) "كلمة المرور يجب أن تتكون من 6 أحرف على الأقل"
                                            else "Le mot de passe doit contenir au moins 6 caractères"
                                            return@Button
                                        }
                                        if (accountType == "AGENCY" && agencyName.isBlank()) {
                                            errorMessage = if (isArabicLanguage) "يرجى إدخال اسم وكالة الأسفار"
                                            else "Veuillez entrer le nom de l'agence de voyage"
                                            return@Button
                                        }
                                        isLoading = true
                                        errorMessage = null
                                        scope.launch {
                                            val result = effectiveAuthManager.registerWithEmail(
                                                email = email.trim(),
                                                pass = password.trim(),
                                                fullName = fullName.trim(),
                                                phone = phone.trim(),
                                                role = accountType,
                                                agencyName = if (accountType == "AGENCY") agencyName.trim() else "",
                                                agencyWilaya = if (accountType == "AGENCY") agencyWilaya.trim() else ""
                                            )
                                            isLoading = false
                                            result.fold(
                                                onSuccess = { profile ->
                                                    onSuccess(profile)
                                                },
                                                onFailure = { err ->
                                                    errorMessage = err.message ?: if (isArabicLanguage) "فشل إنشاء الحساب" else "Échec de création du compte"
                                                }
                                            )
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("auth_register_button"),
                                    shape = RoundedCornerShape(26.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = if (isArabicLanguage) "👤+ إنشاء حسابي ✨" else "👤+ Créer mon compte ✨",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Bottom Footer Text: Vous avez déjà un compte ? Se connecter
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isArabicLanguage) "لديك حساب بالفعل ؟ " else "Vous avez déjà un compte ? ",
                                        color = Color(0xFF64748B),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isArabicLanguage) "تسجيل الدخول" else "Se connecter",
                                        color = Color(0xFF0F766E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.clickable { isLoginTab = true }
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Modal Forgot Password Sheet (Screenshot 1)
                if (showForgotPasswordSheet) {
                    ForgotPasswordBottomSheet(
                        isAr = isArabicLanguage,
                        initialEmail = forgotEmail,
                        isSending = isForgotSending,
                        errorMessage = forgotErrorMsg,
                        successMessage = forgotSuccessMsg,
                        onDismiss = {
                            showForgotPasswordSheet = false
                            forgotErrorMsg = null
                            forgotSuccessMsg = null
                        },
                        onSend = { targetEmail ->
                            if (targetEmail.isBlank() || !targetEmail.contains("@")) {
                                forgotErrorMsg = if (isArabicLanguage) "يرجى إدخال بريد إلكتروني صحيح"
                                else "Veuillez entrer une adresse email valide"
                                return@ForgotPasswordBottomSheet
                            }
                            isForgotSending = true
                            forgotErrorMsg = null
                            forgotSuccessMsg = null
                            scope.launch {
                                val result = effectiveAuthManager.sendPasswordResetEmail(targetEmail.trim())
                                isForgotSending = false
                                result.fold(
                                    onSuccess = {
                                        forgotSuccessMsg = if (isArabicLanguage)
                                            "تم إرسال رابط استعادة كلمة المرور بنجاح إلى بريدك الإلكتروني! تفقد بريدك (بما في ذلك مجلد الرسائل غير المرغوب فيها Spam)."
                                        else
                                            "Un lien de réinitialisation a été envoyé avec succès à votre adresse email ! Vérifiez votre boîte de réception (et les spams)."
                                    },
                                    onFailure = { err ->
                                        forgotErrorMsg = err.message ?: if (isArabicLanguage)
                                            "فشل إرسال الرابط، يرجى التأكد من صحة البريد والمحاولة لاحقاً"
                                        else
                                            "Échec de l'envoi du lien de réinitialisation"
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    // Wilaya Selector Dialog
    if (showWilayaDialog) {
        WilayaPickerDialog(
            selectedWilaya = agencyWilaya,
            onWilayaSelected = {
                agencyWilaya = it
                showWilayaDialog = false
            },
            onDismiss = { showWilayaDialog = false },
            isAr = isArabicLanguage
        )
    }
}

/**
 * Custom dark outlined text field matching the screenshots exactly
 */
@Composable
private fun DarkOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isFocusedBorderActive: Boolean = false,
    testTag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF94A3B8),
                fontSize = 13.5.sp
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
            {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onTogglePasswordVisibility() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF8FAFC),
            focusedBorderColor = Color(0xFF0D9488),
            unfocusedBorderColor = if (isFocusedBorderActive) Color(0xFF0D9488) else Color(0xFFCBD5E1),
            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),
            cursorColor = Color(0xFF0D9488)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

/**
 * Compass icon inside a circular green badge matching Screenshot 1 & 3
 */
@Composable
private fun CompassCircularBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF0D9488)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val needleLength = size.width * 0.40f
            val needleWidth = size.width * 0.16f

            val needleDark = Color.White
            val needleLight = Color(0xCCFFFFFF)

            // Compass tilted at ~45 degrees
            rotate(45f, pivot = Offset(cx, cy)) {
                // North needle - right half
                val northRight = Path().apply {
                    moveTo(cx, cy - needleLength)
                    lineTo(cx + needleWidth, cy)
                    lineTo(cx, cy)
                    close()
                }
                drawPath(northRight, needleDark)

                // North needle - left half
                val northLeft = Path().apply {
                    moveTo(cx, cy - needleLength)
                    lineTo(cx - needleWidth, cy)
                    lineTo(cx, cy)
                    close()
                }
                drawPath(northLeft, needleLight)

                // South needle - right half
                val southRight = Path().apply {
                    moveTo(cx, cy + needleLength)
                    lineTo(cx + needleWidth, cy)
                    lineTo(cx, cy)
                    close()
                }
                drawPath(southRight, needleLight)

                // South needle - left half
                val southLeft = Path().apply {
                    moveTo(cx, cy + needleLength)
                    lineTo(cx - needleWidth, cy)
                    lineTo(cx, cy)
                    close()
                }
                drawPath(southLeft, needleDark)
            }
        }
    }
}

/**
 * Wilaya Picker Dialog
 */
@Composable
private fun WilayaPickerDialog(
    selectedWilaya: String,
    onWilayaSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isAr: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredWilayas = remember(searchQuery) {
        if (searchQuery.isBlank()) AlgeriaConstants.WILAYAS
        else AlgeriaConstants.WILAYAS.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isAr) "اختر ولاية مقر الوكالة" else "Sélectionner la wilaya de l'agence",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(if (isAr) "ابحث عن الولاية..." else "Rechercher une wilaya...")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFF0D9488),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(filteredWilayas) { wilaya ->
                        val isSelected = wilaya == selectedWilaya
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF0D9488).copy(alpha = 0.12f) else Color.Transparent)
                                .clickable { onWilayaSelected(wilaya) }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = wilaya,
                                color = if (isSelected) Color(0xFF0D9488) else Color(0xFF1E293B),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "إلغاء" else "Annuler", color = Color(0xFF0D9488))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ForgotPasswordBottomSheet(
    isAr: Boolean,
    initialEmail: String,
    isSending: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var emailInput by remember { mutableStateOf(initialEmail) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) { /* prevent click through */ }
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .imePadding(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Icon Badge: Blue circular badge with lock icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF))
                        .border(1.5.dp, Color(0xFF93C5FD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = if (isAr) "استعادة كلمة المرور 🔑" else "Récupération du mot de passe 🔑",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = if (isAr)
                        "أدخل بريدك الإلكتروني وسنرسل لك رابطاً لإعادة تعيين كلمة المرور فوراً."
                    else
                        "Entrez votre adresse email et nous vous enverrons un lien pour réinitialiser votre mot de passe immédiatement.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Email Input
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = {
                        Text(
                            text = if (isAr) "البريد الإلكتروني (مثال: user@mail.com)" else "Adresse email (ex: user@mail.com)",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.5.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = null,
                            tint = Color(0xFF64748B)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!isSending) onSend(emailInput)
                        }
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF0F172A),
                        cursorColor = Color(0xFF2563EB)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error message
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        border = BorderStroke(1.dp, Color(0xFFF87171)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFF991B1B),
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Success message
                if (successMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = successMessage,
                            color = Color(0xFF166534),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons (Row: Send Link + Cancel)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Cancel Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = Color(0xFF334155)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Text(
                            text = if (isAr) "إلغاء" else "Annuler",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    // Send Link Button
                    Button(
                        onClick = { onSend(emailInput) },
                        enabled = !isSending && emailInput.isNotBlank(),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF2563EB).copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isAr) "إرسال الرابط 📩" else "Envoyer le lien 📩",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
