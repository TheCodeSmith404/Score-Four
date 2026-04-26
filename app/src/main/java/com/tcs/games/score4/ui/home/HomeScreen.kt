package com.tcs.games.score4.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tcs.games.score4.R
import com.tcs.games.score4.model.UserData
import com.tcs.games.score4.ui.components.NeoButton
import com.tcs.games.score4.ui.components.NeoCard
import com.tcs.games.score4.ui.theme.NeoBlack
import com.tcs.games.score4.ui.theme.NeoYellow

@Composable
fun HomeScreen(
    userData: UserData?,
    onCreateGame: () -> Unit,
    onJoinGame: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header with Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HELLO,",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = (userData?.playerName ?: "Player").uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoBlack
                    )
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .border(3.dp, NeoBlack, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    if (userData?.profileUrl != null && userData.profileUrl != "none") {
                        AsyncImage(
                            model = userData.profileUrl,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.bot),
                            contentDescription = "Default Profile",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Main Actions
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFFFF006E) // NeoPink
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "READY TO PLAY?",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NeoButton(
                        text = "CREATE NEW GAME",
                        onClick = onCreateGame,
                        backgroundColor = NeoYellow,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            NeoButton(
                text = "JOIN GAME WITH ID",
                onClick = onJoinGame,
                backgroundColor = Color(0xFF3A86FF), // NeoBlue
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Profile Button at bottom
            NeoButton(
                text = "VIEW PROFILE",
                onClick = onProfileClick,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        userData = UserData(
            authId = "123",
            generatedId = "user_123",
            playerName = "Vansh Kushwaha",
            email = "vansh@example.com",
            profileUrl = null,
            timeCreated = 0,
            lastUpdated = 0,
            lastPlayed = 0,
            playerNumber = 1,
            isOG = true,
            isBot = false,
            numberImagesUploaded = 0,
            numberGamesPlayed = 150,
            numberGamesWon = 120,
            numberGamesLost = 30,
            numberTimesHost = 10,
            numberTimesBotHost = 0,
            numberTimesPlayedAgainstBot = 50,
            numberTimesWonAgainstBot = 45,
            numberTimesLostAgainstBot = 5,
            points = 5000,
            isGoogleLoggedIn = true,
            imageData = mutableListOf()
        ),
        onCreateGame = {},
        onJoinGame = {},
        onProfileClick = {}
    )
}
