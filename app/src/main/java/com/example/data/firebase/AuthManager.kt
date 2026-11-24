package com.example.data.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val AUTH_TAG = "Auth"

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    if (Firebase.auth.currentUser != null) {
        onAuthSuccess()
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (_: Exception) {
        onUnauthenticated()
        return
    }
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    scope.launch {
        try {
            val credential = credentialManager.getCredential(context, request).credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(credential.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                onAuthSuccess()
            } else {
                onUnauthenticated()
            }
        } catch (_: Exception) {
            onUnauthenticated()
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (_: Exception) {
        onAuthError("Google Sign-In configuration missing: default_web_client_id not found")
        return
    }
    val activity = context as? Activity
    if (activity == null) {
        onAuthError("Unable to launch sign-in outside an Activity context")
        return
    }
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()
    scope.launch {
        try {
            val credential = credentialManager.getCredential(activity, request).credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(credential.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                onAuthSuccess()
            } else {
                onAuthError("Unexpected credential type received")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(AUTH_TAG, "Google Sign-In flow cancelled or dismissed: ${e.message}", e)
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e(AUTH_TAG, "Google Sign-In failed", e)
            onAuthError(e.localizedMessage ?: "Sign in failed. Please try again.")
        }
    }
}

fun sendPhoneVerificationOtp(
    context: Context,
    phoneNumberE164: String,
    languageCode: String,
    resendingToken: PhoneAuthProvider.ForceResendingToken? = null,
    scope: CoroutineScope,
    onCodeSent: (verificationId: String, token: PhoneAuthProvider.ForceResendingToken) -> Unit,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {
    val activity = context as? Activity
    if (activity == null) {
        onAuthError("Unable to start phone verification outside an Activity context.")
        return
    }
    val auth = Firebase.auth
    auth.setLanguageCode(languageCode)

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            scope.launch {
                try {
                    auth.signInWithCredential(credential).await()
                    onAuthSuccess()
                } catch (e: Exception) {
                    Log.e(AUTH_TAG, "Auto phone credential sign-in failed", e)
                    onAuthError(mapPhoneAuthError(e, isTelugu = languageCode == "te"))
                }
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e(AUTH_TAG, "Phone verification failed", e)
            onAuthError(mapPhoneAuthError(e, isTelugu = languageCode == "te"))
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            onCodeSent(verificationId, token)
        }
    }

    val builder = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phoneNumberE164)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(callbacks)
    if (resendingToken != null) {
        builder.setForceResendingToken(resendingToken)
    }

    try {
        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    } catch (e: Exception) {
        Log.e(AUTH_TAG, "Failed to invoke PhoneAuthProvider.verifyPhoneNumber", e)
        onAuthError(mapPhoneAuthError(e, isTelugu = languageCode == "te"))
    }
}

fun verifyPhoneOtpCode(
    verificationId: String,
    otpCode: String,
    isTelugu: Boolean,
    scope: CoroutineScope,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {
    scope.launch {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            Firebase.auth.signInWithCredential(credential).await()
            onAuthSuccess()
        } catch (e: Exception) {
            Log.e(AUTH_TAG, "OTP verification failed", e)
            onAuthError(mapPhoneAuthError(e, isTelugu = isTelugu))
        }
    }
}

private fun mapPhoneAuthError(e: Exception, isTelugu: Boolean): String {
    val code = (e as? FirebaseAuthException)?.errorCode
    return when {
        code == "ERROR_OPERATION_NOT_ALLOWED" ||
            e.message?.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) == true ||
            e.message?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true -> {
            if (isTelugu) {
                "Firebase Console (Authentication -> Sign-in method -> Phone) లో ఫోన్ OTP ను ఎనేబుల్ చేయాలి. ఇప్పుడే Google సైన్ ఇన్ ఉపయోగించవచ్చు."
            } else {
                "Phone OTP is not yet enabled in Firebase Console (Authentication -> Sign-in method -> Phone). You can sign in immediately with Google above."
            }
        }
        e is FirebaseAuthInvalidCredentialsException -> {
            if (isTelugu) "చెల్లని ఫోన్ నంబర్ లేదా తప్పు OTP కోడ్. దయచేసి మళ్లీ తనిఖీ చేయండి."
            else "Invalid phone number or incorrect OTP code. Please check and try again."
        }
        e is FirebaseTooManyRequestsException -> {
            if (isTelugu) "అధిక SMS అభ్యర్థనలు వచ్చాయి. కొద్దిసేపటి తర్వాత మళ్లీ ప్రయత్నించండి."
            else "Too many SMS requests from this device. Please wait a moment and try again."
        }
        else -> e.localizedMessage
            ?: if (isTelugu) "ఫోన్ ధృవీకరణ విఫలమైంది. దయచేసి మళ్లీ ప్రయత్నించండి."
            else "Phone verification failed. Please try again."
    }
}

fun signOutUser(
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSignOutComplete: () -> Unit = {}
) {
    scope.launch {
        withContext(NonCancellable) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(AUTH_TAG, "Failed to clear credential state: ${e.message}")
            } finally {
                Firebase.auth.signOut()
                onSignOutComplete()
            }
        }
    }
}
