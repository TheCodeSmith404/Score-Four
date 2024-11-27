package com.tcs.games.score4.ui.gamesettingfragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import data.PreferenceManager
import data.defaults.DefaultCardOptions
import data.repository.CreateGameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.UserData
import model.gameroom.CardInfo
import model.gameroom.Deck
import model.gameroom.GameRoom
import model.gameroom.PlayersStatus
import model.gamesettings.CardInfoAdapter
import model.gamesettings.GameKeys
import utils.convertors.TimeUtils
import utils.gamelogic.DeckCreator
import utils.gamelogic.GenerateGameIdPass
import javax.inject.Inject

@HiltViewModel
class GameSettingViewModel @Inject constructor(
    private val createGameRepository: CreateGameRepository,
    private val preferenceManager: PreferenceManager):ViewModel() {
    private val _cards=MutableLiveData(DefaultCardOptions.defaultCards)
    val cards: MutableList<CardInfoAdapter> =DefaultCardOptions.defaultCards
    fun getCardsInfo(): MutableList<CardInfo> {
        return cards.map { adapter ->
            CardInfo(
                id = adapter.id,
                name = adapter.name,
                color = adapter.color,
                imageRes = adapter.imageRes,
                icon = adapter.icon
            )
        }.toMutableList()
    }
    suspend fun createGameRoom(hostData: UserData,time:Int):Boolean {
        return withContext(Dispatchers.IO) {
            val userStatus=PlayersStatus(false,hostData.authId,hostData.generatedId,hostData.playerName,hostData.profileUrl,hostData.isOG,hostData.numberGamesPlayed,hostData.numberGamesWon,hostData.timeCreated,false,true)
            val deck= DeckCreator.createDeck()
            val roomId = "${userStatus.playerId}${userStatus.numberOfGamesPlayed}" // Generate roomId
            val idPass = GenerateGameIdPass.getIdPass()
            val gameRoom = GameRoom(
                roomId,
                idPass.first,
                idPass.second,
                userStatus.firebaseId,
                1,
                0,
                false,
                0,
                TimeUtils.getCurrentTimeInMillis(),
                -1,
                mutableListOf(userStatus),
                getCardsInfo(),
                time
            )
            val gameKeys=GameKeys(idPass.first,idPass.second,roomId,TimeUtils.getCurrentTimeInMillis())
            val result = createGameRepository.createGameRoom(roomId, deck, gameRoom,gameKeys)
            if (result.isSuccess) {
                preferenceManager.currentGameId=result.getOrNull()!!
                Log.d("GameSettingViewModel", "Game room created successfully with ID: ${result.getOrNull()}")
                true
            } else {
                Log.e("GameSettingViewModel", "Error creating game room: ${result.exceptionOrNull()}")
                false
            }
        }
    }
}