package com.tcs.games.score4.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameDetailsRepository @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    preferenceManager: PreferenceManager,
) {
    private val _gameDetails = MutableLiveData<GameRoom?>()
    val gameRoom: LiveData<GameRoom?> get() = _gameDetails
    private lateinit var docRef:DocumentReference


    fun startListeningToGameRoom(id: String) {
        _gameDetails.value=null
        docRef = firebaseFireStore.collection("game_room_details")
            .document(id)
        _gameDetails.postValue(null)
        // Add a snapshot listener to Firestore
        docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("GameDetailsRepository", "Error listening to snapshot: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                // Deserialize the document and update LiveData on the main thread
                val room = snapshot.toObject(GameRoom::class.java)
                _gameDetails.postValue(room)
            } else {
                Log.d("GameDetailsRepository", "No document found for ID: $id")
            }
        }
    }
    fun addBot(status: List<PlayersStatus>, numberOfBot:Int){
        docRef.update("players",status,
            "numberOfBots",numberOfBot)
            .addOnSuccessListener {
                Log.d("GameDetailsRepository","data_updated")
            }
            .addOnFailureListener{
                Log.d("GameDetailsRepository","failure")
            }
    }
    fun addBotAndStart(status: List<PlayersStatus>, numberOfBot: Int){
        docRef.update(
            "players",status,
            "numberOfBots",numberOfBot,
            "running",true)
            .addOnSuccessListener {
                Log.d("GameDetailsRepository","data_updated")
            }
            .addOnFailureListener{
                Log.d("GameDetailsRepository","failure")
            }
    }
    fun updateUserStatus(status:List<PlayersStatus>){
        docRef.update("players",status)
            .addOnSuccessListener {
                Log.d("GameDetailsRepository","data_updated")
            }
            .addOnFailureListener{
                Log.d("GameDetailsRepository","failure")
            }
    }
    fun setWinner(index:Int){
        docRef.update("winner",index)
            .addOnSuccessListener {
                Log.d("Winner","Winner is user $index")
            }
            .addOnFailureListener{
                Log.d("Winner","Winner not set and was user $index")
            }
    }
    suspend fun setGameRoomRestarted(newId:String): Boolean {
        return try {
            docRef.update("restart", true,"newRoomId",newId).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    fun updateUserStatusAndStart(status: List<PlayersStatus>){
        docRef.update(
            "players",status,
            "running",true)
            .addOnSuccessListener {
                Log.d("GameDetailsRepository","data_updated")
            }
            .addOnFailureListener{
                Log.d("GameDetailsRepository","failure")
            }
    }
    fun getGameInformation():Triple<List<CardInfo>,String,String>{
        val game=gameRoom.value!!
        return Triple(game.cards.toList(),game.roomId,game.timePerTurns.toString())
    }
    fun getCardsDetails():List<CardInfo>{
        return gameRoom.value!!.cards.toList()
    }
    fun getTpt():String{
        return gameRoom.value!!.timePerTurns.toString()
    }

}