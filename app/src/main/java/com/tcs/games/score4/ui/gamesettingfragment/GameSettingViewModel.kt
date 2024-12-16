package com.tcs.games.score4.ui.gamesettingfragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.defaults.DefaultCardOptions
import com.tcs.games.score4.data.repository.CreateGameRepository
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
import com.tcs.games.score4.model.gamesettings.CardInfoAdapter
import com.tcs.games.score4.model.gamesettings.GameKeys
import com.tcs.games.score4.utils.convertors.TimeUtils
import com.tcs.games.score4.utils.gamelogic.DeckCreator
import com.tcs.games.score4.utils.gamelogic.GenerateGameIdPass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.tcs.games.score4.model.UserData
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
                time,
                -1,
                false,
                ""
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