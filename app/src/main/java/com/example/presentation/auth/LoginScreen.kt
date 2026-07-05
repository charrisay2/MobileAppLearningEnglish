package com.example.presentation.auth

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import java.security.SecureRandom
import android.util.Base64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val bgColor = Color(0xFFF6F8F8)
    val textMain = Color(0xFF191C1C)
    val textMuted = Color(0xFF3F4948)
    val primaryColor = Color(0xFF006A6A)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleGoogleSignIn() {
        val webClientId = BuildConfig.WEB_CLIENT_ID
        if (webClientId.isBlank() || webClientId == "YOUR_WEB_CLIENT_ID") {
            // Simulation mode when no Firebase configured
            onLoginSuccess()
            return
        }

        coroutineScope.launch {
            try {
                // Generate a nonce for extra security (optional  but recommended)
                val rawNonce = ByteArray(32)
                SecureRandom().nextBytes(rawNonce)
                val nonce = Base64.encodeToString(rawNonce, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)

                // Using GetSignInWithGoogleOption for explicit button clicks
                // This is more robust for cases where no accounts are found
                val signInOption = GetSignInWithGoogleOption.Builder(webClientId)
                    .setNonce(nonce)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                    onLoginSuccess()
                } else {
                    errorMessage = "Unrecognized credential type."
                }
                
            } catch (e: NoCredentialException) {
                Log.e("Auth", "No credentials available", e)
                errorMessage = "No Google accounts found on this device. Please add an account and try again."
            } catch (e: GetCredentialException) {
                Log.e("Auth", "Credential Manager error: ${e.type}", e)
                errorMessage = "Google sign-in error. Please ensure your device is connected to the internet."
            } catch (e: Exception) {
                Log.e("Auth", "Google sign-in failed", e)
                errorMessage = e.message ?: "Google sign in failed. Check your configuration."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo / Title
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFE0F3F2), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFFBEE8E6), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("🌍", fontSize = 40.sp)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Welcome to Lingu Master",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF002020),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Sign in to continue your learning journey",
            fontSize = 14.sp,
            color = textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        errorMessage?.let { msg ->
            Text(
                text = msg,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color(0xFFDCE5E4),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color(0xFFDCE5E4),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Forgot password?",
            color = primaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { /* TODO */ }
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign In Button
        Button(
            onClick = { onLoginSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDCE5E4))
            Text(
                " OR CONTINUE WITH ",
                color = textMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDCE5E4))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Google Sign In Button
        Surface(
            onClick = { handleGoogleSignIn() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFDCE5E4))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("G", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Sign in with Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textMain
                )
            }
        }
    }
}
