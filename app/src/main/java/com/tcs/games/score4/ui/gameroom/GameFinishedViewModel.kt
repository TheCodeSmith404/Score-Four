package com.tcs.games.score4.ui.gameroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.repository.CreateGameRepository
import com.tcs.games.score4.data.repository.GameDetailsRepository
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.model.UserData
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
import com.tcs.games.score4.model.gamesettings.GameKeys
import com.tcs.games.score4.utils.convertors.TimeUtils
import com.tcs.games.score4.utils.gamelogic.DeckCreator
import com.tcs.games.score4.utils.gamelogic.GenerateGameIdPass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GameFinishedViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
    private val createGameRepository: CreateGameRepository,
    private val preferenceManager: PreferenceManager,
    private val firestore: FirebaseFirestore,
):ViewModel() {
    private var _userIndex: Int? = null

    private val userIndex: Int
        get() {
            if (_userIndex==null) {
                val currentUserId = userRepository.user!!.authId
                val players = gameDetailsRepository.gameRoom.value!!.players
                Log.d(this::class.simpleName,"$currentUserId is $players")
                _userIndex = players.indexOfFirst { user -> user.firebaseId == currentUserId }
                Log.d(this::class.simpleName, _userIndex.toString())
            }
            return _userIndex!!
        }
    var newId=""
    private fun getGameRoom():GameRoom{
        return gameDetailsRepository.gameRoom.value!!
    }

    private suspend fun setGameRoomRestarted(id:String):Boolean{
        return gameDetailsRepository.setGameRoomRestarted(id)
    }

    suspend fun createGameRoom():Boolean {
        return withContext(Dispatchers.IO) {
            val hostData=userRepository.user!!
            hostData.numberGamesPlayed++
            val userStatus=PlayersStatus(false,hostData.authId,hostData.generatedId,hostData.playerName,hostData.profileUrl,hostData.isOG,hostData.numberGamesPlayed,hostData.numberGamesWon,hostData.timeCreated,false,true)
            val deck= DeckCreator.createDeck()
            val roomId = "${userStatus.playerId}${userStatus.numberOfGamesPlayed}" // Generate roomId
            val idPass = GenerateGameIdPass.getIdPass()
            val currentGameRoom=getGameRoom()
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
                null,
                mutableListOf(userStatus),
                currentGameRoom.cards,
                currentGameRoom.timePerTurns,
                -1,
                false,
                ""
            )
            val gameKeys= GameKeys(idPass.first,idPass.second,roomId, TimeUtils.getCurrentTimeInMillis())
            val result = createGameRepository.createGameRoom(roomId, deck, gameRoom,gameKeys)
            if (result.isSuccess) {
                val id=result.getOrNull()!!
                val done=setGameRoomRestarted(id)
                Log.d("GameSettingViewModel", "Game room created successfully with ID: ${result.getOrNull()}")
                if(done){
                    preferenceManager.currentGameId=id
                    true
                }else{
                    false
                }
            } else {
                Log.e("GameSettingViewModel", "Error creating game room: ${result.exceptionOrNull()}")
                false
            }
        }
    }

    suspend fun joinRestartedGameRoom(gameId: String): Result<Boolean> {
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
            preferenceManager.currentGameId = gameId
            Result.success(true)
        } catch (e: Exception) {
            Log.e("Firebase", "Error joining game room", e)
            Result.failure(e)
        }
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