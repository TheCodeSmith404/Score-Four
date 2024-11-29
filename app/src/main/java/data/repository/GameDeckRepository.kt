package data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import data.PreferenceManager
import kotlinx.coroutines.tasks.await
import model.gameroom.Deck
import model.gameroom.GameRoom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameDeckRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val preferenceManager: PreferenceManager,
) {
    private val _gameDeck = MutableLiveData<Deck?>()
    val gameDeck: LiveData<Deck?> get() = _gameDeck
    private lateinit var docRef: DocumentReference
    init{
        val id=preferenceManager.currentGameId
        startObservingDeck(id)
    }
    private fun startObservingDeck(id:String){
        docRef=firestore.collection("game_room_deck").document(id)
        docRef.addSnapshotListener{snapshot,error->
            if(error!=null){
                Log.e("GameDeckRepository", "Error listening to snapshot: ${error.message}")
            }
            if(snapshot!=null&&snapshot.exists()){
                Log.d("DeckObserver","Snapshot triggered")
                val deck=snapshot.toObject(Deck::class.java)
                _gameDeck.postValue(deck)
            }else{
                Log.d("GameDeckRepository", "No document found for ID: $id")
            }
        }

    }
    fun uploadDeck(deck:Deck,doneListener:(Boolean)->Unit){
        docRef.set(deck)
            .addOnSuccessListener {
                doneListener(true)
            }
            .addOnFailureListener{
                doneListener(false)
            }
    }
}