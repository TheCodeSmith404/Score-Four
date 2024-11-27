package data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import data.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.gameroom.CardInfo
import model.gameroom.GameRoom
import model.gameroom.PlayersStatus
import utils.views.WaitingRoomItem
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

    init {
        val id = preferenceManager.currentGameId
        startListeningToGameRoom(id)
    }

    private fun startListeningToGameRoom(id: String) {
        docRef=firebaseFireStore.collection("game_room_details")
            .document(id)
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
    fun updateUserStatus(status:List<PlayersStatus>){
        docRef.update("players",status)
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