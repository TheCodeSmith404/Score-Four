package com.tcs.games.score4.ui.gameroom

import android.util.Log
import androidx.lifecycle.ViewModel
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.repository.GameDeckRepository
import com.tcs.games.score4.data.repository.GameDetailsRepository
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.model.gameroom.Deck
import com.tcs.games.score4.model.gameroom.GameRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GameRoomViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
    private val gameDeckRepository: GameDeckRepository,
    private val preferenceManager: PreferenceManager,
) : ViewModel() {
    private var _userIndex: Int? = null
    val previousDeck = mutableListOf<String>()
    
    private val _currentlySelectedItem = MutableStateFlow(0)
    val currentlySelectedItem: StateFlow<Int> = _currentlySelectedItem.asStateFlow()
    
    var previouslySelectedItem = -1

    val userIndex: Int
        get() {
            if (_userIndex == null) {
                val currentUserId = userRepository.user?.authId ?: ""
                val players = gameDetailsRepository.gameRoom.value?.players ?: emptyList()
                _userIndex = players.indexOfFirst { it.firebaseId == currentUserId }
            }
            return _userIndex ?: 0
        }

    fun selectItem(index: Int) {
        _currentlySelectedItem.value = index
    }

    fun startListeningToDeck() {
        gameDeckRepository.startObservingDeck(preferenceManager.currentGameId)
    }

    fun updateGameFinishedStats(userWon: Boolean) {
        userRepository.updateGameFinishedStats(userRepository.user?.authId ?: "", userWon)
    }

    fun checkIfWon(deck: List<String>): Boolean {
        if (deck.size < 4) return false
        val firstCharCount = deck.groupingBy { it[0] }.eachCount()
        return firstCharCount.any { it.value == 4 }
    }

    fun isUserHost(): Boolean {
        return userRepository.user?.authId == gameDetailsRepository.gameRoom.value?.hostId
    }

    fun getRoomId(): String = preferenceManager.currentGameId

    fun setWinner(index: Int) {
        gameDetailsRepository.setWinner(index)
    }

    fun getDeckFlow(): StateFlow<Deck?> = gameDeckRepository.gameDeck

    fun isUserCurrentlyPlaying(): Boolean {
        val currentlyPlaying = gameDeckRepository.gameDeck.value?.currentlyPlaying ?: 0
        return userIndex == currentlyPlaying
    }

    fun getGameRoomFlow(): StateFlow<GameRoom?> = gameDetailsRepository.gameRoom

    fun getTurnTime(plusDelay: Boolean): Int {
        val baseTime = gameDetailsRepository.gameRoom.value?.timePerTurns ?: 10
        return if (plusDelay) baseTime + 4 else baseTime
    }

    fun modifyDeckForBot(card: Int): Deck {
        val all = gameDeckRepository.gameDeck.value!!
        val decks = when (all.currentlyPlaying) {
            0 -> Pair(all.playerA, all.playerB)
            1 -> Pair(all.playerB, all.playerC)
            2 -> Pair(all.playerC, all.playerD)
            else -> Pair(all.playerD, all.playerA)
        }
        val temp = decks.first.removeAt(card)
        decks.second.add(temp)
        all.currentlyPlaying = (all.currentlyPlaying + 1) % 4
        return all
    }

    fun modifyDeckForPlayer(card: Int): Deck {
        val all = gameDeckRepository.gameDeck.value!!
        val decks = when (userIndex) {
            0 -> Pair(all.playerA, all.playerB)
            1 -> Pair(all.playerB, all.playerC)
            2 -> Pair(all.playerC, all.playerD)
            else -> Pair(all.playerD, all.playerA)
        }
        val temp = decks.first.removeAt(card)
        decks.second.add(temp)
        all.currentlyPlaying = (all.currentlyPlaying + 1) % 4
        return all
    }

    fun getCardDetailsFromId(id: String): CardInfo {
        val char = id[0]
        val cards = gameDetailsRepository.gameRoom.value!!.cards
        return when (char) {
            'a' -> cards[0]
            'b' -> cards[1]
            'c' -> cards[2]
            else -> cards[3]
        }
    }

    fun getCardsDetailsFromIds(ids: List<String>): List<CardInfo> {
        val cards = gameDetailsRepository.gameRoom.value?.cards ?: return emptyList()
        return ids.map { id ->
            when (id[0]) {
                'a' -> cards[0]
                'b' -> cards[1]
                'c' -> cards[2]
                else -> cards[3]
            }
        }
    }

    fun uploadDeck(deck: Deck, listener: (Boolean) -> Unit) {
        gameDeckRepository.uploadDeck(deck) { success ->
            listener(success)
        }
    }
}