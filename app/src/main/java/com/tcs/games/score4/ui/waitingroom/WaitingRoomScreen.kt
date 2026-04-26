package com.tcs.games.score4.ui.waitingroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.tcs.games.score4.model.gameroom.PlayersStatus
import com.tcs.games.score4.ui.components.NeoButton
import com.tcs.games.score4.ui.components.NeoCard
import com.tcs.games.score4.ui.theme.NeoBlack
import com.tcs.games.score4.ui.theme.NeoBlue
import com.tcs.games.score4.ui.theme.NeoPink
import com.tcs.games.score4.ui.theme.NeoYellow

@Composable
fun WaitingRoomScreen(
    viewModel: WaitingRoomViewModel,
    onNavigateToGameRoom: () -> Unit,
    onNavigateToGameDetails: () -> Unit,
    onBack: () -> Unit
) {
    val gameRoom by viewModel.getGameRoomFlow().collectAsState()
    val isHost = viewModel.isUserHost()

    LaunchedEffect(Unit) {
        viewModel.startListeningToGameDetails()
    }

    LaunchedEffect(gameRoom?.running) {
        if (gameRoom?.running == true && viewModel.allPlayersReady()) {
            onNavigateToGameRoom()
        }
    }

    WaitingRoomScreenContent(
        gameRoom = gameRoom,
        isHost = isHost,
        onAddBot = { index, bots -> viewModel.addBot(bots) },
        onReady = { viewModel.updatePlayerStatus() },
        onNavigateToGameDetails = onNavigateToGameDetails,
        onBack = onBack,
        canAddBot = viewModel.canAddBot()
    )
}

@Composable
fun WaitingRoomScreenContent(
    gameRoom: com.tcs.games.score4.model.gameroom.GameRoom?,
    isHost: Boolean,
    canAddBot: Boolean,
    onAddBot: (Int, Int) -> Unit,
    onReady: () -> Unit,
    onNavigateToGameDetails: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(R.drawable.baseline_arrow_back_24), "Back", tint = NeoBlack)
                }
                Text("WAITING ROOM", fontWeight = FontWeight.Black, fontSize = 20.sp)
                IconButton(onClick = { /* Share dialog */ }) {
                    Icon(painterResource(R.drawable.baseline_share_24), "Share", tint = NeoBlack)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status Text
            val statusText = if ((gameRoom?.numberOfPlayers ?: 0) + (gameRoom?.numberOfBots ?: 0) == 4) {
                "Waiting for players to be ready..."
            } else {
                "Waiting for players to join..."
            }
            Text(text = statusText, fontWeight = FontWeight.Bold, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            // Players List
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 0 until 4) {
                    val player = gameRoom?.players?.getOrNull(i)
                    PlayerListItem(
                        player = player,
                        isHost = isHost,
                        canAddBot = canAddBot,
                        onAddBot = { onAddBot(i, (gameRoom?.numberOfBots ?: 0) + 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NeoButton(
                    text = "GAME DETAILS",
                    onClick = onNavigateToGameDetails,
                    backgroundColor = NeoBlue,
                    modifier = Modifier.weight(1f)
                )
                
                NeoButton(
                    text = "READY",
                    onClick = onReady,
                    backgroundColor = NeoYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewWaitingRoomScreen() {
    WaitingRoomScreenContent(
        gameRoom = com.tcs.games.score4.model.gameroom.GameRoom(
            players = mutableListOf(
                com.tcs.games.score4.model.gameroom.PlayersStatus(false, "1", "user_1", "Vansh", null, true, 10, 5, 0, true, true),
                com.tcs.games.score4.model.gameroom.PlayersStatus(true, "2", "bot_1", "Bot_abc", null, false, 0, 0, 0, true, true)
            ),
            numberOfPlayers = 1,
            numberOfBots = 1
        ),
        isHost = true,
        canAddBot = true,
        onAddBot = { _, _ -> },
        onReady = {},
        onNavigateToGameDetails = {},
        onBack = {}
    )
}

@Composable
fun PlayerListItem(
    player: PlayersStatus?,
    isHost: Boolean,
    canAddBot: Boolean,
    onAddBot: () -> Unit
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (player != null) Color.White else Color(0xFFEEEEEE)
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (player != null) {
                    if (player.bot) {
                        Image(
                            painter = painterResource(R.drawable.bot),
                            contentDescription = "Bot",
                            modifier = Modifier.size(48.dp).clip(CircleShape).border(2.dp, NeoBlack, CircleShape)
                        )
                    } else {
                        AsyncImage(
                            model = player.playerProfile,
                            contentDescription = player.playerName,
                            modifier = Modifier.size(48.dp).clip(CircleShape).border(2.dp, NeoBlack, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray).border(2.dp, NeoBlack, CircleShape)
                    )
                }
                
                if (player?.ready == true) {
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).size(16.dp).background(Color.Green, CircleShape).border(1.dp, NeoBlack, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (player != null) {
                Text(text = player.playerName, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.weight(1f))
                if (player.ready) {
                    Text("READY", color = Color.Green, fontWeight = FontWeight.Black)
                }
            } else {
                if (isHost && canAddBot) {
                    Text(
                        text = "ADD BOT",
                        fontWeight = FontWeight.Black,
                        color = NeoPink,
                        modifier = Modifier.clickable { onAddBot() }
                    )
                } else {
                    Text(text = "WAITING...", color = Color.Gray)
                }
            }
        }
    }
}
