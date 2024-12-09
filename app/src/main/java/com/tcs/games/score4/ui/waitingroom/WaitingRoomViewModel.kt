package com.tcs.games.score4.ui.waitingroom

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import data.PreferenceManager
import data.repository.GameDetailsRepository
import data.repository.UserRepository
import model.gameroom.GameRoom
import utils.ImageUtils
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val preferenceManager: PreferenceManager,
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
):ViewModel() {
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
            player.ready=true
        }
        if(allPlayersReady()){
            gameDetailsRepository.updateUserStatusAndStart(data)
        }else {
            gameDetailsRepository.updateUserStatus(data)
        }
    }
    fun allPlayersReady():Boolean{
        Log.d("Waiting room","${gameDetailsRepository.gameRoom.value!!.players}")
        var ready=0
        gameDetailsRepository.gameRoom.value!!.players.forEach{player->
            if(player.ready)
                ready++
        }
        Log.d("Waiting room",ready.toString())
        return ready==4
    }
}