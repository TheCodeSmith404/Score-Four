package com.tcs.games.score4.ui.splashScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val userData by viewModel.userData.collectAsState()
    
    SplashScreenContent(
        user = user,
        userData = userData,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
fun SplashScreenContent(
    user: com.google.firebase.auth.FirebaseUser?,
    userData: com.tcs.games.score4.model.UserData?,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var statusText by remember { mutableStateOf("Getting Account Details") }
    var progress by remember { mutableFloatStateOf(0.1f) }

    LaunchedEffect(user) {
        if (user == null) {
            progress = 1.0f
            kotlinx.coroutines.delay(500)
            onNavigateToLogin()
        } else {
            statusText = "Downloading configs"
            progress = 0.3f
        }
    }

    LaunchedEffect(userData) {
        if (userData != null) {
            progress = 1.0f
            statusText = "Data Received"
            kotlinx.coroutines.delay(500)
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SCORE FOUR",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(32.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(12.dp).border(2.dp, Color.Black),
            color = Color(0xFFFF006E), // NeoPink
            trackColor = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = statusText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    SplashScreenContent(
        user = null,
        userData = null,
        onNavigateToLogin = {},
        onNavigateToHome = {}
    )
}
