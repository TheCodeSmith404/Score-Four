package com.tcs.games.score4.ui.gamesettingfragment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcs.games.score4.R
import com.tcs.games.score4.model.gamesettings.CardInfoAdapter
import com.tcs.games.score4.ui.components.NeoButton
import com.tcs.games.score4.ui.components.NeoCard
import com.tcs.games.score4.ui.theme.NeoBlack
import com.tcs.games.score4.ui.theme.NeoYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsScreen(
    viewModel: GameSettingViewModel,
    onNavigateToWaitingRoom: () -> Unit,
    onBack: () -> Unit
) {
    val cards = viewModel.cards
    
    GameSettingsScreenContent(
        cards = cards,
        onNavigateToWaitingRoom = onNavigateToWaitingRoom,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsScreenContent(
    cards: List<CardInfoAdapter>,
    onNavigateToWaitingRoom: () -> Unit,
    onBack: () -> Unit
) {
    var turnTime by remember { mutableIntStateOf(10) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp)
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
                Text("CREATE GAME", fontWeight = FontWeight.Black, fontSize = 20.sp)
                Box(modifier = Modifier.size(48.dp)) // Spacer
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Turn Time Configuration
            NeoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Turn Time (s)", fontWeight = FontWeight.Black)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = turnTime.toString(), fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = { turnTime = if (turnTime > 10) turnTime - 10 else 10 }) {
                            Text("-", fontWeight = FontWeight.Black, fontSize = 24.sp)
                        }
                        IconButton(onClick = { turnTime += 10 }) {
                            Text("+", fontWeight = FontWeight.Black, fontSize = 24.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("CARDS CONFIGURATION", fontWeight = FontWeight.Black, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    CardSettingItem(
                        card = card,
                        onIconClick = { showSheet = true },
                        onColorClick = { showSheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            NeoButton(
                text = "CREATE ROOM",
                onClick = onNavigateToWaitingRoom,
                backgroundColor = NeoYellow,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            // Options for color/icon selection
            Box(modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp)) {
                Text("Select Option", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun CardSettingItem(
    card: CardInfoAdapter,
    onIconClick: () -> Unit,
    onColorClick: () -> Unit
) {
    NeoCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, NeoBlack)
                    .background(Color.White)
                    .clickable { onIconClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(com.tcs.games.score4.data.defaults.DefaultCardOptions.getIconRes(card.icon)),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = card.name, fontWeight = FontWeight.Black)
                Text(text = "Tap to edit name", fontSize = 12.sp, color = Color.Gray)
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(com.tcs.games.score4.data.defaults.DefaultCardOptions.getColor(card.color))
                    .border(2.dp, NeoBlack)
                    .clickable { onColorClick() }
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewGameSettingsScreen() {
    GameSettingsScreenContent(
        cards = listOf(
            CardInfoAdapter("0", "Hammer", 1, 0, 1, false),
            CardInfoAdapter("1", "Queen", 2, 0, 2, false),
            CardInfoAdapter("2", "Rich", 3, 0, 3, false),
            CardInfoAdapter("3", "Coins", 4, 0, 4, false)
        ),
        onNavigateToWaitingRoom = {},
        onBack = {}
    )
}
