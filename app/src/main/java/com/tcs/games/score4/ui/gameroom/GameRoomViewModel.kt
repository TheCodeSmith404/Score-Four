package com.tcs.games.score4.ui.gameroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.databinding.FragmentGameRoomBinding
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.model.gameroom.Deck
import com.tcs.games.score4.model.gameroom.GameRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import com.tcs.games.score4.data.repository.GameDeckRepository
import com.tcs.games.score4.data.repository.GameDetailsRepository
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.utils.views.PlayerIcon
import javax.inject.Inject

@HiltViewModel
class GameRoomViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
    private val gameDeckRepository: GameDeckRepository,
    private val preferenceManager: PreferenceManager,
): ViewModel() {
    private var _userIndex: Int? = null
    val previousDeck= mutableListOf<String>()
    var currentlySelectedItem:MutableLiveData<Int> =MutableLiveData(0)
    var previouslySelectedItem=-1
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
    val debounceUpdate=false
    fun startListeningToDeck(){
        gameDeckRepository.startObservingDeck(preferenceManager.currentGameId)
    }
    fun updateGameFinishedStats(userWon:Boolean){
        userRepository.updateGameFinishedStats(userRepository.user!!.authId,userWon)
    }
    fun checkIfWon(deck: List<String>):Boolean{
        val firstCharCount = deck.groupingBy { it[0] }.eachCount()
        return firstCharCount.any { it.value == 4 }
    }
    fun isUserHost():Boolean{
        return userRepository.user!!.authId==gameDetailsRepository.gameRoom.value!!.hostId
    }
    fun getRoomId():String{
        return preferenceManager.currentGameId
    }
    fun setWinner(index: Int){
        gameDetailsRepository.setWinner(index)
    }
    fun getDeck():LiveData<Deck?>{
        return gameDeckRepository.gameDeck
    }
    fun isUserCurrentlyPlaying():Boolean{
        val currentlyPlaying=gameDeckRepository.gameDeck.value?.currentlyPlaying?:0
        return userIndex==currentlyPlaying
    }
    fun getGameRoom(): LiveData<GameRoom?> {
        val temp=gameDetailsRepository.gameRoom
        return temp
    }

    fun getTurnTime(plusDelay:Boolean):Int{
        return if(plusDelay)
            gameDetailsRepository.gameRoom.value!!.timePerTurns+4
        else
            gameDetailsRepository.gameRoom.value!!.timePerTurns
    }
    fun getPlayerIcon(index:Int,binding:FragmentGameRoomBinding): PlayerIcon {
        return when(index){
            0->binding.playerA
            1->binding.playerB
            2->binding.playerC
            else->binding.playerD
        }
    }
    fun modifyDeckForBot(card:Int):Deck{
        val all=gameDeckRepository.gameDeck.value!!
        val decks=when(gameDeckRepository.gameDeck.value?.currentlyPlaying!!){
            0-> Pair(all.playerA,all.playerB)
            1-> Pair(all.playerB,all.playerC)
            2-> Pair(all.playerC,all.playerD)
            else -> Pair(all.playerD,all.playerA)
        }
        val temp=decks.first.removeAt(card)
        decks.second.add(temp)
        all.currentlyPlaying++
        all.currentlyPlaying%=4
        Log.d("Deck",all.toString())
        return all
    }
    fun modifyDeckForPlayer(card:Int):Deck{
        val all=gameDeckRepository.gameDeck.value!!
        val decks=when(userIndex){
            0-> Pair(all.playerA,all.playerB)
            1-> Pair(all.playerB,all.playerC)
            2-> Pair(all.playerC,all.playerD)
            else -> Pair(all.playerD,all.playerA)
        }
        val temp=decks.first.removeAt(card)
        decks.second.add(temp)
        all.currentlyPlaying++
        all.currentlyPlaying%=4
        Log.d("Deck",all.toString())
        return all
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
    fun getCardsDetailsFromIds(ids:MutableList<String>):MutableList<CardInfo>{
        val list= mutableListOf<CardInfo>()
        val cards=gameDetailsRepository.gameRoom.value!!.cards
        ids.forEach{id->
            val char=id[0]
            val temp=when(char){
                'a'->cards[0]
                'b'->cards[1]
                'c'->cards[2]
                else->cards[3]
            }
            list.add(temp)
        }
        return list
    }
    fun uploadDeck(deck: Deck,listener:(Boolean)->Unit){
        Log.d("Deck",deck.toString())
        gameDeckRepository.uploadDeck(deck){success->
            listener(success)
        }
    }
}