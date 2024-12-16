package com.tcs.games.score4.ui.waitingroom

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.R
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import com.tcs.games.score4.data.repository.GameDetailsRepository
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.utils.ImageUtils
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val preferenceManager: PreferenceManager,
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
):ViewModel() {
    fun startListeningToGameDetails(){
        gameDetailsRepository.startListeningToGameRoom(preferenceManager.currentGameId)
    }
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
    fun setImageToBot(context: Context,imageView: ImageView){
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bot))
    }
    fun updatePlayerStatus(){
        val userId=getUserAuthid()
        val data=gameDetailsRepository.gameRoom.value?.players
        data?.find{
            it.firebaseId == userId
        }!!.let {player->
            player.ready=true
        }
        if(allPlayersReady(data)){
            gameDetailsRepository.updateUserStatusAndStart(data)
        }else {
            gameDetailsRepository.updateUserStatus(data)
        }
    }
    fun getKeys():Pair<String,String>{
        val data=gameDetailsRepository.gameRoom.value!!
        return Pair(data.gameId,data.gamePassword)
    }
    fun addBot(index:Int, numberOfBots:Int){
        val data=gameDetailsRepository.gameRoom.value!!.players
        val gamesPlayed=getRandomNumber(99)
        val won=getRandomNumber(gamesPlayed)
        val bot=PlayersStatus(true,
            "is_bot",
            "i_am_a_bot",
            "Bot_${generateRandomBotName()}",
            "",
            true,
            gamesPlayed,
            won,
            0,
            ready = true,
            active = true
        )
        data.add(bot)
        if(allPlayersReady(data)){
            gameDetailsRepository.addBotAndStart(data,numberOfBots)
        }else {
            gameDetailsRepository.addBot(data,numberOfBots)
        }
    }
    fun canAddBot():Boolean{
        return gameDetailsRepository.gameRoom.value!!.numberOfBots<2
    }
    private fun allPlayersReady(data:List<PlayersStatus>):Boolean{
        var ready=0
        data.forEach{player->
            if(player.ready)
                ready++
        }
        Log.d("AddBot",ready.toString())
        return ready==4
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
    private fun generateRandomBotName(): String {
        val chars = ('a'..'z') // Range of lowercase letters
        return (1..3) // Generate three characters
            .map { chars.random() }
            .joinToString("")
    }
    private fun getRandomNumber(max:Int):Int{
        return (0..max).random()

    }
}