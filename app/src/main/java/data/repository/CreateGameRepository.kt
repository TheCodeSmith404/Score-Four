package data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import model.gameroom.Deck
import model.gameroom.GameRoom
import model.gameroom.PlayersStatus
import model.gamesettings.GameKeys
import javax.inject.Inject

class CreateGameRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // Function to create a game room in Firestore
    suspend fun createGameRoom(
        roomId: String,
        deck: Deck,
        gameRoom: GameRoom,
        gameKeys: GameKeys
    ): Result<String> {
        return try {
            // Run all tasks concurrently in a coroutine scope
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
                // Wait for all requests to complete
                gameRoomDeferred.await()
                deckDeferred.await()
                idPassDeferred.await()
            }
            // If all succeed, return success
            Result.success(roomId)
        } catch (e: Exception) {
            // If any fail, return failure
            Result.failure(e)
        }
    }

}
