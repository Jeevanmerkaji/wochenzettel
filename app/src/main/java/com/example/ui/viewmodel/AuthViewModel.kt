package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val isFirebaseConfigured: Boolean = AuthRepository.isFirebaseConfigured

    val currentUser: StateFlow<FirebaseUser?> = AuthRepository.authStateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun clearError() {
        _authError.value = null
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authError.value = null
            AuthRepository.signInWithEmail(email, password)
                .onFailure { _authError.value = friendlyMessage(it) }
            _isAuthenticating.value = false
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authError.value = null
            AuthRepository.signUpWithEmail(email, password)
                .onFailure { _authError.value = friendlyMessage(it) }
            _isAuthenticating.value = false
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authError.value = null
            AuthRepository.signInWithGoogle(context)
                .onFailure { _authError.value = friendlyMessage(it) }
            _isAuthenticating.value = false
        }
    }

    fun signOut() {
        AuthRepository.signOut()
    }

    private fun friendlyMessage(e: Throwable): String {
        return e.message ?: "Ein unbekannter Fehler ist aufgetreten. Bitte versuche es erneut."
    }
}
