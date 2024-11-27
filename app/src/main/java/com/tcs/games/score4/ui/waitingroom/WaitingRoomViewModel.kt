package com.tcs.games.score4.ui.waitingroom

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import data.PreferenceManager
import data.repository.GameDetailsRepository
import data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.gameroom.GameRoom
import model.gameroom.PlayersStatus
import utils.ImageUtils
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val preferenceManager: PreferenceManager,
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
):ViewModel() {
    var gameRoom:GameRoom=GameRoom()
    fun isUserHost():Boolean{
         return userRepository.user!!.authId==gameDetailsRepository.gameRoom.value!!.hostId
    }
    fun fetchGameRoom(): LiveData<GameRoom?> {
        return gameDetailsRepository.gameRoom
    }
    private fun getUserAuthid():String{
        return userRepository.user!!.authId
    }
    fun setImageToPlayerIcon(context: Context, url:String, imageView: ImageView){
        ImageUtils.downloadImageFromUrlToImageView(context,url,imageView)
    }
    fun updatePlayerStatus(){
        val userId=getUserAuthid()
        val data=gameDetailsRepository.gameRoom.value!!.players
        data.find{
            it.firebaseId == userId
        }!!.let {player->
            player.isReady=true
        }
        gameDetailsRepository.updateUserStatus(data)
    }
    fun allPlayersReady(players:List<PlayersStatus>):Boolean{
        var ready=0
        players.forEach{
            if(it.isReady)
                ready++
        }
        return ready==4
    }
}