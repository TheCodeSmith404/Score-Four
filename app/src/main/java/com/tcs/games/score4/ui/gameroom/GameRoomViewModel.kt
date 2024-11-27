package com.tcs.games.score4.ui.gameroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tcs.games.score4.databinding.FragmentGameRoomBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import data.repository.GameDeckRepository
import data.repository.GameDetailsRepository
import data.repository.UserRepository
import model.gameroom.CardInfo
import model.gameroom.Deck
import model.gameroom.GameRoom
import utils.views.PlayerIcon
import javax.inject.Inject

@HiltViewModel
class GameRoomViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
    private val gameDeckRepository: GameDeckRepository,
): ViewModel() {
    private var _userIndex: Int? = null
    val userIndex: Int
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
    fun getDeck():LiveData<Deck?>{
        return gameDeckRepository.gameDeck
    }
    fun getGameRoom(): LiveData<GameRoom?> {
        return gameDetailsRepository.gameRoom
    }
    fun getPlayerIcon(index:Int,binding:FragmentGameRoomBinding): PlayerIcon {
        return when(index){
            0->binding.playerA
            1->binding.playerB
            2->binding.playerC
            else->binding.playerD
        }
    }
    fun getCardDetailsFromId(id:String):CardInfo{
        val char=id[0]
        val cards=gameDetailsRepository.gameRoom.value!!.cards
        return when(char){
            'a'->cards[0]
            'b'->cards[1]
            'c'->cards[2]
            else->cards[3]
        }
    }
}