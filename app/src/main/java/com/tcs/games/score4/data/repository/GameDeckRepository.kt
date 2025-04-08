package com.tcs.games.score4.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.model.gameroom.Deck
import com.tcs.games.score4.data.PreferenceManager
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

    fun startObservingDeck(id:String){
        _gameDeck.postValue(null)
        docRef=firestore.collection("game_room_deck").document(id)
        docRef.addSnapshotListener{snapshot,error->
            if(error!=null){
                Log.e(this::class.simpleName, "Error listening to snapshot: ${error.message}")
            }
            if(snapshot!=null&&snapshot.exists()){
                Log.d(this::class.simpleName,"Snapshot triggered")
                val deck=snapshot.toObject(Deck::class.java)
                _gameDeck.postValue(deck)
            }else{
                Log.d(this::class.simpleName, "No document found for ID: $id")
            }
        }

    }
    fun uploadDeck(deck:Deck,doneListener:(Boolean)->Unit){

        val deckMap = mutableMapOf<String, Any?>().apply {
            put("currentlyPlaying", deck.currentlyPlaying)
            put("playerA", deck.playerA)
            put("playerB", deck.playerB)
            put("playerC", deck.playerC)
            put("playerD", deck.playerD)
            put("lastUpdated", FieldValue.serverTimestamp()) // Inject timestamp
        }
        docRef.set(deckMap)
            .addOnSuccessListener {
                doneListener(true)
            }
            .addOnFailureListener{
                doneListener(false)
            }
    }
}