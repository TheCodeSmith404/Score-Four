package com.tcs.games.score4.ui.login

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.tcs.games.score4.R
import com.tcs.games.score4.ui.components.NeoButton
import com.tcs.games.score4.ui.components.NeoCard
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val loadingMessage by viewModel.loadingMessage.collectAsState()

    LoginScreenContent(
        user = user,
        loadingMessage = loadingMessage,
        onNavigateToHome = onNavigateToHome,
        onAnonymousSignIn = { viewModel.signInAnonymousLy() },
        onSignInWithToken = { token -> viewModel.signInWithGoogle(token) }
    )
}

@Composable
fun LoginScreenContent(
    user: com.google.firebase.auth.FirebaseUser?,
    loadingMessage: String?,
    onNavigateToHome: () -> Unit,
    onAnonymousSignIn: () -> Unit,
    onSignInWithToken: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(user) {
        if (user != null) {
            onNavigateToHome()
        }
    }

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeoCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WELCOME",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Text(
                        text = "Score Four Multiplayer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    NeoButton(
                        text = "SIGN IN WITH GOOGLE",
                        onClick = {
                            scope.launch {
                                try {
                                    val gIO = GetSignInWithGoogleOption.Builder(
                                        serverClientId = context.getString(R.string.default_web_client_id)
                                    ).build()
                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(gIO)
                                        .build()
                                    val result = CredentialManager.create(context)
                                        .getCredential(request = request, context = context)
                                    val idTokenCredential = result.credential as? GoogleIdTokenCredential
                                    idTokenCredential?.let {
                                        onSignInWithToken(it.idToken)
                                    }
                                } catch (e: Exception) {
                                    Log.e("SignIn", "Failed: ${e.message}")
                                }
                            }
                        },
                        backgroundColor = Color(0xFFFFE600), // NeoYellow
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeoButton(
                        text = "ANONYMOUS LOGIN",
                        onClick = onAnonymousSignIn,
                        backgroundColor = Color(0xFF3A86FF), // NeoBlue
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            if (loadingMessage != null && loadingMessage != "Error") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = loadingMessage, color = Color.Black, fontWeight = FontWeight.Black)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreenContent(
        user = null,
        loadingMessage = "Creating Account...",
        onNavigateToHome = {},
        onAnonymousSignIn = {},
        onSignInWithToken = {}
    )
}
