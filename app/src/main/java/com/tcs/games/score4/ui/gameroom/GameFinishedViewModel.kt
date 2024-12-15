package com.tcs.games.score4.ui.gameroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tcs.games.score4.data.repository.GameDetailsRepository
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameFinishedViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
):ViewModel() {
    private var _userIndex: Int? = null
    private val userIndex: Int
        get() {
            if (_userIndex==null) {
                val currentUserId = userRepository.user!!.authId
                val players = gameDetailsRepository.gameRoom.value!!.players
                Log.d("Deck","$currentUserId is $players")
                _userIndex = players.indexOfFirst { user -> user.firebaseId == currentUserId }
                Log.d("Deck", _userIndex.toString())
            }
            return _userIndex!!
        }
    private fun getGameRoom():GameRoom{
        return gameDetailsRepository.gameRoom.value!!
    }
    fun getGameRoomLiveData():LiveData<GameRoom?>{
        return gameDetailsRepository.gameRoom
    }
    fun getGameRoomWinner(): PlayersStatus {
        val temp=getGameRoom()
        val winner=temp.winner
        return temp.players[winner]
    }
    fun isPlayerWinner():Boolean{
        return userIndex==getGameRoom().winner
    }
    fun isUserHost():Boolean{
        return userRepository.user!!.authId==gameDetailsRepository.gameRoom.value!!.hostId
    }

}