package com.tcs.games.score4.ui.waitingroom

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.repository.GameDetailsRepository
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val preferenceManager: PreferenceManager,
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
) : ViewModel() {

    fun startListeningToGameDetails() {
        gameDetailsRepository.startListeningToGameRoom(preferenceManager.currentGameId)
    }

    fun isUserHost(): Boolean {
        return userRepository.user?.authId == gameDetailsRepository.gameRoom.value?.hostId
    }

    fun getGameRoomFlow(): StateFlow<GameRoom?> = gameDetailsRepository.gameRoom

    fun updatePlayerStatus() {
        val userId = userRepository.user?.authId ?: return
        val players = gameDetailsRepository.gameRoom.value?.players?.toMutableList() ?: return
        
        players.find { it.firebaseId == userId }?.let { player ->
            player.ready = true
        }

        if (allPlayersReady(players)) {
            gameDetailsRepository.updateUserStatusAndStart(players)
        } else {
            gameDetailsRepository.updateUserStatus(players)
        }
    }

    fun getKeys(): Pair<String, String> {
        val data = gameDetailsRepository.gameRoom.value ?: return "" to ""
        return Pair(data.gameId, data.gamePassword)
    }

    fun addBot(numberOfBots: Int) {
        val data = gameDetailsRepository.gameRoom.value?.players?.toMutableList() ?: return
        val gamesPlayed = (0..99).random()
        val won = (0..gamesPlayed).random()
        val bot = PlayersStatus(
            true, "is_bot", "i_am_a_bot", "Bot_${generateRandomBotName()}",
            "", true, gamesPlayed, won, 0, ready = true, active = true
        )
        data.add(bot)
        if (allPlayersReady(data)) {
            gameDetailsRepository.addBotAndStart(data, numberOfBots)
        } else {
            gameDetailsRepository.addBot(data, numberOfBots)
        }
    }

    fun canAddBot(): Boolean {
        return (gameDetailsRepository.gameRoom.value?.numberOfBots ?: 0) < 2
    }

    private fun allPlayersReady(data: List<PlayersStatus>): Boolean {
        return data.count { it.ready } == 4
    }

    fun allPlayersReady(): Boolean {
        val players = gameDetailsRepository.gameRoom.value?.players ?: return false
        return players.count { it.ready } == 4
    }

    private fun generateRandomBotName(): String {
        val chars = ('a'..'z')
        return (1..3).map { chars.random() }.joinToString("")
    }
}