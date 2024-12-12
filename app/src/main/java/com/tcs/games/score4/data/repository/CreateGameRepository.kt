package com.tcs.games.score4.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.model.gameroom.Deck
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gamesettings.GameKeys
import com.tcs.games.score4.utils.convertors.TimeUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CreateGameRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository,
) {

    // Function to create a game room in Firestore
    suspend fun createGameRoom(
        roomId: String,
        deck: Deck,
        gameRoom: GameRoom,
        gameKeys: GameKeys
    ): Result<String> {
        return try {
            coroutineScope {
                val gameRoomDeferred = async {
                    val gameRoomRef = firestore.collection("game_room_details").document(roomId)
                    gameRoomRef.set(gameRoom).await()
                }
                val deckDeferred = async {
                    val deckRef = firestore.collection("game_room_deck").document(roomId)
                    deckRef.set(deck).await()
                }
                val idPassDeferred = async {
                    val idPassRef = firestore.collection("game_room_ids").document("${gameKeys.id}${gameKeys.pass}")
                    idPassRef.set(gameKeys).await()
                }
                val userDeferred = async {
                    val accountRef = firestore.collection("accounts").document(userRepository.user!!.authId)
                    firestore.runTransaction { transaction ->
                        transaction.update(accountRef, "numberGamesPlayed", FieldValue.increment(1),"lastGamePlayed",TimeUtils.getCurrentTimeInMillis())
                    }.await()
                }
                // Wait for all requests to complete
                userDeferred.await()
                gameRoomDeferred.await()
                deckDeferred.await()
                idPassDeferred.await()
            }
            // If all succeed, return success
            Result.success(roomId)
        } catch (e: Exception) {
            Log.d("Refactoring","${e.message}")
            // If any fail, return failure
            Result.failure(e)
        }
    }

}
