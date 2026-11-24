package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.firebase.attemptAutoSignIn
import com.example.data.firebase.onGoogleSignInClicked
import com.example.data.firebase.sendPhoneVerificationOtp
import com.example.data.firebase.verifyPhoneOtpCode
import com.example.data.model.AppLanguage
import com.example.ui.components.BilingualToggleChip
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.SaffronGoldBright
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var language by remember { mutableStateOf(AppLanguage.EN) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var isPhoneLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    // Phone Auth State (Defaults to +91 for APPSC / AP Police aspirants in India)
    var countryCode by remember { mutableStateOf("+91") }
    var phoneDigits by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var otpCode by remember { mutableStateOf("") }
    var resendCountdown by remember { mutableIntStateOf(0) }

    val isTe = language == AppLanguage.TE
    val isOtpStep = verificationId != null
    val isAnyLoading = isGoogleLoading || isPhoneLoading

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = onAuthSuccess,
            onUnauthenticated = { /* Interactive login screen remains visible */ },
            scope = scope
        )
    }

    LaunchedEffect(resendCountdown) {
        if (resendCountdown > 0) {
            delay(1000L)
            resendCountdown--
        }
    }

    fun buildE164Phone(): String? {
        val cleanCode = countryCode.trim().let { if (it.startsWith("+")) it else "+$it" }
        val digitsOnly = phoneDigits.filter(Char::isDigit)
        if (!cleanCode.matches(Regex("^\\+[0-9]{1,4}$"))) return null
        if (cleanCode == "+91" && digitsOnly.length != 10) return null
        if (digitsOnly.length !in 7..15) return null
        return "$cleanCode$digitsOnly"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(RoyalNavyDark, RoyalNavy, MaterialTheme.colorScheme.background)
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Brand & Instant Bilingual EN/తెలుగు Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SaffronGoldBright
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Vijeta AP crest",
                            tint = RoyalNavyDark,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Vijeta AP",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (isTe) "APPSC • ఏపీ పోలీస్ విద్యార్థి లాగిన్" else "APPSC • AP Police Student Login",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronGoldBright
                        )
                    }
                }
                BilingualToggleChip(
                    language = language,
                    onToggle = { language = if (isTe) AppLanguage.EN else AppLanguage.TE }
                )
            }

            // Main Bilingual Login Card
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    // Hero Banner Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_coaching_1795427352670),
                            contentDescription = "APPSC and AP Police coaching banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            RoyalNavyDark.copy(alpha = 0.25f),
                                            RoyalNavyDark.copy(alpha = 0.9f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SaffronGoldBright
                            ) {
                                Text(
                                    text = if (isTe) "APPSC గ్రూప్ 1, 2, 3, 4 & పోలీస్" else "APPSC GROUP 1–4 & AP POLICE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = RoyalNavyDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isTe) {
                                    "విద్యార్థి పోర్టల్‌లోకి లాగిన్ అవ్వండి"
                                } else {
                                    "Sign in to Your Aspirant Portal"
                                },
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Error & Info Banners
                        if (errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                        .testTag("auth_error_banner")
                                )
                            }
                        }

                        if (infoMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldTeal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = infoMessage!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }

                        // 1. Primary Google Sign-In (Jetpack Credential Manager)
                        Button(
                            onClick = {
                                errorMessage = null
                                infoMessage = null
                                isGoogleLoading = true
                                onGoogleSignInClicked(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = {
                                        isGoogleLoading = false
                                        onAuthSuccess()
                                    },
                                    onAuthError = { msg ->
                                        isGoogleLoading = false
                                        errorMessage = msg
                                    },
                                    scope = scope,
                                    onAuthCancelled = {
                                        isGoogleLoading = false
                                    }
                                )
                            },
                            enabled = !isAnyLoading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("google_sign_in_button")
                        ) {
                            if (isGoogleLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isTe) "Google తో సైన్ ఇన్ అవుతోంది..." else "Signing in with Google...",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isTe) "Google తో కొనసాగండి" else "Continue with Google",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f))
                            Text(
                                text = if (isTe) "లేదా మొబైల్ OTP లాగిన్" else "OR MOBILE OTP LOGIN",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f))
                        }

                        // 2. Firebase Phone Authentication Flow (2-step Animated)
                        AnimatedContent(
                            targetState = isOtpStep,
                            label = "phone_auth_step_transition"
                        ) { showingOtp ->
                            if (!showingOtp) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = if (isTe) {
                                            "మీ 10 అంకెల మొబైల్ నంబర్ నమోదు చేసి SMS OTP పొందండి"
                                        } else {
                                            "Enter your 10-digit mobile number to receive a Firebase SMS OTP"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        OutlinedTextField(
                                            value = countryCode,
                                            onValueChange = { input ->
                                                val sanitized = input.filter { it == '+' || it.isDigit() }.take(5)
                                                countryCode = sanitized
                                            },
                                            label = { Text(if (isTe) "కోడ్" else "Code") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier
                                                .width(92.dp)
                                                .testTag("country_code_input")
                                        )
                                        OutlinedTextField(
                                            value = phoneDigits,
                                            onValueChange = { input ->
                                                phoneDigits = input.filter(Char::isDigit).take(10)
                                            },
                                            label = { Text(if (isTe) "మొబైల్ నంబర్" else "Mobile Number") },
                                            placeholder = { Text("9876543210") },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.PhoneAndroid,
                                                    contentDescription = null
                                                )
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("phone_number_input")
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            errorMessage = null
                                            infoMessage = null
                                            val formatted = buildE164Phone()
                                            if (formatted == null) {
                                                errorMessage = if (isTe) {
                                                    "దయచేసి చెల్లుబాటు అయ్యే 10 అంకెల మొబైల్ నంబర్ నమోదు చేయండి."
                                                } else {
                                                    "Please enter a valid 10-digit mobile number."
                                                }
                                                return@OutlinedButton
                                            }
                                            isPhoneLoading = true
                                            sendPhoneVerificationOtp(
                                                context = context,
                                                phoneNumberE164 = formatted,
                                                languageCode = if (isTe) "te" else "en",
                                                resendingToken = null,
                                                scope = scope,
                                                onCodeSent = { vId, token ->
                                                    isPhoneLoading = false
                                                    verificationId = vId
                                                    resendToken = token
                                                    otpCode = ""
                                                    resendCountdown = 60
                                                    infoMessage = if (isTe) {
                                                        "$formatted కు 6 అంకెల OTP పంపబడింది."
                                                    } else {
                                                        "6-digit OTP sent to $formatted."
                                                    }
                                                },
                                                onAuthSuccess = {
                                                    isPhoneLoading = false
                                                    onAuthSuccess()
                                                },
                                                onAuthError = { msg ->
                                                    isPhoneLoading = false
                                                    errorMessage = msg
                                                }
                                            )
                                        },
                                        enabled = !isAnyLoading,
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("send_otp_button")
                                    ) {
                                        if (isPhoneLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.5.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(if (isTe) "OTP పంపుతోంది..." else "Sending OTP...")
                                        } else {
                                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isTe) "మొబైల్ OTP పంపండి" else "Send Mobile OTP",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Step 2: 6-Digit OTP Entry & Verification
                                val formattedDisplay = "${countryCode.trim()} $phoneDigits"
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isTe) "OTP ధృవీకరణ" else "Verify 6-Digit OTP",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = formattedDisplay,
                                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                                                color = EmeraldTeal
                                            )
                                        }
                                        TextButton(
                                            onClick = {
                                                verificationId = null
                                                otpCode = ""
                                                errorMessage = null
                                                infoMessage = null
                                            },
                                            modifier = Modifier.testTag("change_phone_button")
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isTe) "మార్చండి" else "Change")
                                        }
                                    }

                                    OutlinedTextField(
                                        value = otpCode,
                                        onValueChange = { input ->
                                            otpCode = input.filter(Char::isDigit).take(6)
                                        },
                                        label = { Text(if (isTe) "6 అంకెల OTP కోడ్" else "6-Digit OTP Code") },
                                        placeholder = { Text("123456") },
                                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("otp_code_input")
                                    )

                                    Button(
                                        onClick = {
                                            errorMessage = null
                                            val vId = verificationId ?: return@Button
                                            if (otpCode.length != 6) {
                                                errorMessage = if (isTe) {
                                                    "దయచేసి 6 అంకెల OTP కోడ్ నమోదు చేయండి."
                                                } else {
                                                    "Please enter the complete 6-digit OTP code."
                                                }
                                                return@Button
                                            }
                                            isPhoneLoading = true
                                            verifyPhoneOtpCode(
                                                verificationId = vId,
                                                otpCode = otpCode,
                                                isTelugu = isTe,
                                                scope = scope,
                                                onAuthSuccess = {
                                                    isPhoneLoading = false
                                                    onAuthSuccess()
                                                },
                                                onAuthError = { msg ->
                                                    isPhoneLoading = false
                                                    errorMessage = msg
                                                }
                                            )
                                        },
                                        enabled = !isAnyLoading && otpCode.length == 6,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EmeraldTeal,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("verify_otp_button")
                                    ) {
                                        if (isPhoneLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color.White,
                                                strokeWidth = 2.5.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(if (isTe) "ధృవీకరిస్తోంది..." else "Verifying OTP...")
                                        } else {
                                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isTe) "OTP ధృవీకరించి లాగిన్ అవ్వండి" else "Verify OTP & Sign In",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                val formatted = buildE164Phone() ?: return@TextButton
                                                errorMessage = null
                                                isPhoneLoading = true
                                                sendPhoneVerificationOtp(
                                                    context = context,
                                                    phoneNumberE164 = formatted,
                                                    languageCode = if (isTe) "te" else "en",
                                                    resendingToken = resendToken,
                                                    scope = scope,
                                                    onCodeSent = { vId, token ->
                                                        isPhoneLoading = false
                                                        verificationId = vId
                                                        resendToken = token
                                                        resendCountdown = 60
                                                        infoMessage = if (isTe) "కొత్త OTP మళ్లీ పంపబడింది." else "A new OTP has been resent."
                                                    },
                                                    onAuthSuccess = {
                                                        isPhoneLoading = false
                                                        onAuthSuccess()
                                                    },
                                                    onAuthError = { msg ->
                                                        isPhoneLoading = false
                                                        errorMessage = msg
                                                    }
                                                )
                                            },
                                            enabled = resendCountdown == 0 && !isAnyLoading,
                                            modifier = Modifier.testTag("resend_otp_button")
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (resendCountdown > 0) {
                                                    if (isTe) "${resendCountdown}సె లో మళ్లీ పంపండి" else "Resend OTP in ${resendCountdown}s"
                                                } else {
                                                    if (isTe) "OTP మళ్లీ పంపండి" else "Resend OTP"
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Compact Student Trust Footer
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isTe) {
                                    "మీ CBT మాక్ టెస్ట్ స్కోర్‌కార్డ్‌లు, ఎన్‌రోల్ చేసిన కోర్సులు & బుక్‌మార్క్‌లు మీ ఖాతాతో సురక్షితంగా సింక్ అవుతాయి."
                                } else {
                                    "Your CBT mock test scorecards, enrolled APPSC/AP Police batches, and saved study notes sync securely to your account."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
