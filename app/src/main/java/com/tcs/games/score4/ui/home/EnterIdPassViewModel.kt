package com.tcs.games.score4.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import data.PreferenceManager
import data.repository.JoinGameRepository
import data.repository.UserRepository
import kotlinx.coroutines.tasks.await
import model.gameroom.GameRoom
import model.gameroom.PlayersStatus
import javax.inject.Inject

@HiltViewModel
class EnterIdPassViewModel @Inject constructor(
    val firestore: FirebaseFirestore,
    val userRepository: UserRepository,
    val joinGameRepository: JoinGameRepository,
    val preferenceManager: PreferenceManager
):ViewModel() {
    fun verifyLocally(id:String,password:String):Boolean{
        password.toIntOrNull() ?: return false
        val hashSet= mutableSetOf<Char>()
        for(char in id){
            if(!hashSet.add(char)){
                return false
            }
        }
        return true
    }
    suspend fun verifyGlobally(id: String, pass: String): String? {
        return try {
            val result = joinGameRepository.verifyUser(id, pass)
            result.fold(
                onSuccess = { owner ->
                    owner // Return true if `owner` is true
                },
                onFailure = { error ->
                    Log.d("EnterIdPassword", "Unable to verify data: ${error.message}")
                    null // Return false on failure
                }
            )
        } catch (e: Exception) {
            Log.d("EnterIdPassword", "Unexpected error: ${e.message}")
            null // Return false for unexpected exceptions
        }
    }
    suspend fun joinGameRoom(gameId: String): Task<Boolean> {
        return try {
            var result=true
            val currentUser=userRepository.user!!
            val playerStatus=PlayersStatus(
                false,
                currentUser.authId,
                currentUser.generatedId,
                currentUser.playerName,
                currentUser.profileUrl,
                currentUser.isOG,
                currentUser.numberGamesPlayed,
                currentUser.numberGamesWon,
                currentUser.timeCreated,
                ready = false,
                active = true
            )
            // Start a Firestore transaction
            firestore.runTransaction { transaction ->

                // Fetch the game room document
                val gameRoomRef = firestore.collection("game_room_details").document(gameId)
                val gameRoomSnapshot = transaction.get(gameRoomRef)

                if (gameRoomSnapshot.exists()) {
                    Log.d("firebase",gameRoomSnapshot.toString())
                    // Deserialize the document to a GameRoom object
                    val gameRoom = gameRoomSnapshot.toObject(GameRoom::class.java)
                    if (gameRoom != null) {
                        // Modify the data: Append player and update player count
                        if(gameRoom.numberOfPlayers+gameRoom.numberOfBots<=3&&!gameRoom.running) {
                            Log.d("Players","${gameRoom.numberOfPlayers+gameRoom.numberOfBots}")
                            gameRoom.players.add(playerStatus)
                            gameRoom.numberOfPlayers++
                            // Update the Firestore document with the modified data
                            transaction.set(gameRoomRef, gameRoom)
                        }else if(gameRoom.numberOfBots>0&&!gameRoom.running){
                            Log.d("Players","${gameRoom.numberOfPlayers}")
                            gameRoom.players.removeIf { player -> player.bot }
                            gameRoom.players.add(playerStatus)
                            gameRoom.numberOfPlayers++
                            gameRoom.numberOfBots--
                            transaction.set(gameRoomRef,gameRoom)
                        }else{
                            result=false
                            Log.d("Players","Condition false")
                        }
                    } else {
                        // If the document exists but data can't be parsed, return failure
                        result=false
                    }
                } else {
                    // If the game room doesn't exist
                    result=false
                }
            }.await()
            preferenceManager.currentGameId=gameId
            Tasks.forResult(result)
        } catch (e: Exception) {
            Log.d("Firebase",e.printStackTrace().toString())
            Log.d("Firebase",e.message.toString())
            // Handle any error that occurs during the transaction
            Tasks.forResult(false)
        }
    }

}