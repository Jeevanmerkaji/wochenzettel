package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.BuildConfig
import com.example.data.remote.awaitResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

/**
 * Thin wrapper around Firebase Auth, following the same "gracefully do nothing if not
 * configured" pattern as GeminiClient/GroqClient: this app ships without a google-services.json
 * by default (AI Studio template), so every entry point here fails soft instead of crashing.
 *
 * To actually enable this, you need to (once, in the Firebase console):
 * 1. Create a Firebase project and add an Android app with applicationId
 *    "com.aistudio.wochenzettel.kmfzpz" (see app/build.gradle.kts).
 * 2. Download google-services.json and place it in the app/ directory.
 * 3. In Authentication -> Sign-in method, enable "Email/Password" and "Google".
 * 4. Copy the auto-generated "Web client ID" (Authentication -> Sign-in method -> Google ->
 *    Web SDK configuration) into GOOGLE_WEB_CLIENT_ID in your .env file.
 * 5. For Google Sign-In specifically, also register your debug & release SHA-1 fingerprints
 *    under Project Settings -> Your apps.
 */
object AuthRepository {
    private const val TAG = "AuthRepository"

    // FirebaseAuth.getInstance() throws IllegalStateException ("Default FirebaseApp is not
    // initialized") when there's no google-services.json — that's the only signal we need to
    // fail soft instead of crashing the whole app.
    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth is not available: ${e.message}")
            null
        }
    }

    val isFirebaseConfigured: Boolean get() = auth != null

    val currentUserId: String? get() = auth?.currentUser?.uid
    val currentUserEmail: String? get() = auth?.currentUser?.email

    /** Emits the current user immediately, then again on every sign-in/sign-out. */
    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            trySend(null)
            awaitClose { }
        } else {
            val listener = FirebaseAuth.AuthStateListener { a -> trySend(a.currentUser) }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        val a = auth ?: return@withContext Result.failure(
            IllegalStateException("Firebase ist nicht konfiguriert (google-services.json fehlt).")
        )
        try {
            a.createUserWithEmailAndPassword(email, password).awaitResult()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Sign-up failed", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        val a = auth ?: return@withContext Result.failure(
            IllegalStateException("Firebase ist nicht konfiguriert (google-services.json fehlt).")
        )
        try {
            a.signInWithEmailAndPassword(email, password).awaitResult()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Sign-in failed", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<Unit> = withContext(Dispatchers.IO) {
        val a = auth ?: return@withContext Result.failure(
            IllegalStateException("Firebase ist nicht konfiguriert (google-services.json fehlt).")
        )
        if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isEmpty() || BuildConfig.GOOGLE_WEB_CLIENT_ID == "MY_GOOGLE_WEB_CLIENT_ID") {
            return@withContext Result.failure(
                IllegalStateException("GOOGLE_WEB_CLIENT_ID ist nicht gesetzt (siehe .env.example).")
            )
        }
        try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            a.signInWithCredential(firebaseCredential).awaitResult()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Google sign-in failed", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
    }
}
