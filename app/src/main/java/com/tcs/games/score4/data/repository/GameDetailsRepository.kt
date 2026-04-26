package com.tcs.games.score4.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameDetailsRepository @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    preferenceManager: PreferenceManager,
) {
    private val _gameDetails = MutableStateFlow<GameRoom?>(null)
    val gameRoom: StateFlow<GameRoom?> = _gameDetails.asStateFlow()
    private lateinit var docRef: DocumentReference

    fun startListeningToGameRoom(id: String) {
        _gameDetails.value = null
        docRef = firebaseFireStore.collection("game_room_details")
            .document(id)
        
        docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("GameDetailsRepository", "Error listening to snapshot: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val room = snapshot.toObject(GameRoom::class.java)
                _gameDetails.value = room
            } else {
                Log.d("GameDetailsRepository", "No document found for ID: $id")
            }
        }
    }

    fun addBot(status: List<PlayersStatus>, numberOfBot: Int) {
        docRef.update(
            "players", status,
            "numberOfBots", numberOfBot, "lastUpdated", FieldValue.serverTimestamp()
        )
    }

    fun addBotAndStart(status: List<PlayersStatus>, numberOfBot: Int) {
        docRef.update(
            "players", status,
            "numberOfBots", numberOfBot,
            "running", true, "lastUpdated", FieldValue.serverTimestamp()
        )
    }

    fun updateUserStatus(status: List<PlayersStatus>) {
        docRef.update("players", status, "lastUpdated", FieldValue.serverTimestamp())
    }

    fun setWinner(index: Int) {
        docRef.update("winner", index, "lastUpdated", FieldValue.serverTimestamp())
    }

    suspend fun setGameRoomRestarted(newId: String): Boolean {
        return try {
            docRef.update("restart", true, "newRoomId", newId, "lastUpdated", FieldValue.serverTimestamp()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateUserStatusAndStart(status: List<PlayersStatus>) {
        docRef.update(
            "players", status,
            "running", true, "lastUpdated", FieldValue.serverTimestamp()
        )
    }

    fun getGameInformation(): Triple<List<CardInfo>, String, String> {
        val game = gameRoom.value!!
        return Triple(game.cards.toList(), game.roomId, game.timePerTurns.toString())
    }

    fun getCardsDetails(): List<CardInfo> {
        return gameRoom.value!!.cards.toList()
    }

    fun getTpt(): String {
        return gameRoom.value!!.timePerTurns.toString()
    }
}