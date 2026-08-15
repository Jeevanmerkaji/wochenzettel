package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrickRed
import com.example.ui.theme.HairlineColor
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary

@Composable
fun LoginScreen(
    isFirebaseConfigured: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onSignIn: (email: String, password: String) -> Unit,
    onSignUp: (email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val canSubmit = isFirebaseConfigured && !isLoading &&
        email.isNotBlank() && password.length >= 6

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "WOCHENZETTEL",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = InkPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (isSignUpMode) "Erstelle ein Konto, um deine Daten geräteübergreifend zu sichern."
                else "Melde dich an, um deinen Wochenplan zu synchronisieren.",
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

            if (!isFirebaseConfigured) {
                InfoBanner(
                    "Firebase ist noch nicht eingerichtet. Füge google-services.json hinzu und " +
                        "aktiviere Email/Password + Google in der Firebase-Konsole, um dich " +
                        "anzumelden."
                )
                Spacer(Modifier.height(24.dp))
            }

            errorMessage?.let { msg ->
                InfoBanner(msg, onClick = onDismissError)
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-Mail") },
                singleLine = true,
                enabled = isFirebaseConfigured && !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Passwort") },
                singleLine = true,
                enabled = isFirebaseConfigured && !isLoading,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (isSignUpMode) onSignUp(email.trim(), password) else onSignIn(email.trim(), password)
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (isSignUpMode) "Konto erstellen" else "Anmelden",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = { isSignUpMode = !isSignUpMode },
                enabled = !isLoading,
                modifier = Modifier.testTag("login_toggle_mode_button")
            ) {
                Text(
                    if (isSignUpMode) "Schon ein Konto? Anmelden" else "Noch kein Konto? Registrieren",
                    color = InkMuted
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = HairlineColor)
                Text(
                    "  oder  ",
                    color = InkMuted,
                    style = MaterialTheme.typography.bodySmall
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = HairlineColor)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onGoogleSignIn,
                enabled = isFirebaseConfigured && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_google_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = InkPrimary)
            ) {
                Text("Mit Google anmelden", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InfoBanner(text: String, onClick: (() -> Unit)? = null) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BrickRed.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            color = BrickRed,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
