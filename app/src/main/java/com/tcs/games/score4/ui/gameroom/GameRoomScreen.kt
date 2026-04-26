package com.tcs.games.score4.ui.gameroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tcs.games.score4.R
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.model.gameroom.PlayersStatus
import com.tcs.games.score4.ui.components.NeoCard
import com.tcs.games.score4.ui.theme.NeoBlack
import com.tcs.games.score4.ui.theme.NeoYellow
import kotlin.math.roundToInt

@Composable
fun GameRoomScreen(
    viewModel: GameRoomViewModel,
    onBack: () -> Unit
) {
    val deck by viewModel.getDeckFlow().collectAsState()
    val gameRoom by viewModel.getGameRoomFlow().collectAsState()
    val selectedItem by viewModel.currentlySelectedItem.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startListeningToDeck()
    }

    GameRoomScreenContent(
        deck = deck,
        gameRoom = gameRoom,
        selectedItem = selectedItem,
        userIndex = viewModel.userIndex,
        isUserTurn = viewModel.isUserCurrentlyPlaying(),
        onSelectItem = { viewModel.selectItem(it) },
        onPlayCard = { index ->
            val newDeck = viewModel.modifyDeckForPlayer(index)
            viewModel.uploadDeck(newDeck) { /* Handle result */ }
        },
        getCardDetailsFromId = { viewModel.getCardsDetailsFromIds(it) }
    )
}

@Composable
fun GameRoomScreenContent(
    deck: com.tcs.games.score4.model.gameroom.Deck?,
    gameRoom: com.tcs.games.score4.model.gameroom.GameRoom?,
    selectedItem: Int,
    userIndex: Int,
    isUserTurn: Boolean,
    onSelectItem: (Int) -> Unit,
    onPlayCard: (Int) -> Unit,
    getCardDetailsFromId: (List<String>) -> List<CardInfo>
) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Player Avatars Grid
            val players = gameRoom?.players ?: emptyList()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                players.forEachIndexed { index, player ->
                    PlayerAvatar(
                        player = player,
                        isCurrentlyPlaying = deck?.currentlyPlaying == index,
                        isCurrentUser = userIndex == index
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Game Cards
            val userDeckIds = when (userIndex) {
                0 -> deck?.playerA
                1 -> deck?.playerB
                2 -> deck?.playerC
                else -> deck?.playerD
            } ?: emptyList()

            val cardInfos = getCardDetailsFromId(userDeckIds)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy((-40).dp),
                    contentPadding = PaddingValues(horizontal = 40.dp)
                ) {
                    itemsIndexed(cardInfos) { index, card ->
                        GameCardItem(
                            card = card,
                            isSelected = selectedItem == index,
                            isTurn = isUserTurn,
                            onSelect = { onSelectItem(index) },
                            onPlay = { onPlayCard(index) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewGameRoomScreen() {
    GameRoomScreenContent(
        deck = com.tcs.games.score4.model.gameroom.Deck(
            currentlyPlaying = 0,
            playerA = mutableListOf("a1", "b1", "c1", "d1")
        ),
        gameRoom = com.tcs.games.score4.model.gameroom.GameRoom(
            players = mutableListOf(
                com.tcs.games.score4.model.gameroom.PlayersStatus(false, "1", "user_1", "Vansh", "", true, 10, 5, 0, true, true),
                com.tcs.games.score4.model.gameroom.PlayersStatus(true, "2", "bot_1", "Bot_abc", "", false, 0, 0, 0, true, true)
            )
        ),
        selectedItem = 0,
        userIndex = 0,
        isUserTurn = true,
        onSelectItem = {},
        onPlayCard = {},
        getCardDetailsFromId = { 
            listOf(
                CardInfo("a1", "Hammer", 1, 0, 1),
                CardInfo("b1", "Queen", 2, 0, 2),
                CardInfo("c1", "Rich", 3, 0, 3),
                CardInfo("d1", "Coins", 4, 0, 4)
            )
        }
    )
}

@Composable
fun PlayerAvatar(
    player: PlayersStatus,
    isCurrentlyPlaying: Boolean,
    isCurrentUser: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(if (isCurrentlyPlaying) 4.dp else 2.dp, if (isCurrentlyPlaying) NeoYellow else NeoBlack, CircleShape)
                .padding(2.dp)
                .clip(CircleShape)
        ) {
            AsyncImage(
                model = player.playerProfile,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = if (isCurrentUser) "YOU" else player.playerName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun GameCardItem(
    card: CardInfo,
    isSelected: Boolean,
    isTurn: Boolean,
    onSelect: () -> Unit,
    onPlay: () -> Unit
) {
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(0, (offsetY + if (isSelected) -40 else 0).roundToInt()) }
            .pointerInput(isTurn) {
                if (isTurn) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            offsetY = (offsetY + dragAmount).coerceAtMost(0f)
                        },
                        onDragEnd = {
                            if (offsetY < -150) {
                                onPlay()
                            }
                            offsetY = 0f
                        }
                    )
                }
            }
            .clickable { onSelect() }
    ) {
        NeoCard(
            modifier = Modifier.size(140.dp, 200.dp),
            backgroundColor = com.tcs.games.score4.data.defaults.DefaultCardOptions.getColor(card.color)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = card.name, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Image(
                    painter = painterResource(com.tcs.games.score4.data.defaults.DefaultCardOptions.getIconRes(card.icon)),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text(text = card.name, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }
}
