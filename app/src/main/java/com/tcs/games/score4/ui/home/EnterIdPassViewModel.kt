package com.tcs.games.score4.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.utils.convertors.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import com.tcs.games.score4.data.repository.JoinGameRepository
import com.tcs.games.score4.data.repository.UserRepository
import kotlinx.coroutines.tasks.await
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
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
    suspend fun joinGameRoom(gameId: String): Result<Boolean> {
        return try {
            val currentUser = userRepository.user!!
            val playerStatus = PlayersStatus(
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

            // Perform the transaction to join the game room
            firestore.runTransaction { transaction ->
                val gameRoomRef = firestore.collection("game_room_details").document(gameId)
                val gameRoomSnapshot = transaction.get(gameRoomRef)
                val accountRef = firestore.collection("accounts").document(currentUser.authId)

                if (gameRoomSnapshot.exists()) {
                    val gameRoom = gameRoomSnapshot.toObject(GameRoom::class.java)
                    if (gameRoom != null) {
                        if (gameRoom.numberOfPlayers + gameRoom.numberOfBots <= 3 && !gameRoom.running) {
                            gameRoom.players.add(playerStatus)
                            gameRoom.numberOfPlayers++
                            transaction.update(accountRef, "numberGamesPlayed", FieldValue.increment(1))
                            transaction.set(gameRoomRef, gameRoom)
                        } else if (gameRoom.numberOfBots > 0 && !gameRoom.running) {
                            gameRoom.players.removeIf { player -> player.bot }
                            gameRoom.players.add(playerStatus)
                            gameRoom.numberOfPlayers++
                            gameRoom.numberOfBots--
                            transaction.update(accountRef, "numberGamesPlayed", FieldValue.increment(1),
                                "lastGamePlayed", TimeUtils.getCurrentTimeInMillis())
                            transaction.set(gameRoomRef, gameRoom)
                        } else {
                            throw IllegalStateException("Room conditions not met")
                        }
                    } else {
                        throw IllegalArgumentException("Invalid game room data")
                    }
                } else {
                    throw IllegalStateException("Game room does not exist")
                }
            }.await()

            // Update local preferences
            preferenceManager.currentGameId = gameId
            Result.success(true)
        } catch (e: Exception) {
            Log.e("Firebase", "Error joining game room", e)
            Result.failure(e)
        }
    }


}