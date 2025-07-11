package com.tcs.games.score4.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.model.gamesettings.GameKeys
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinGameRepository @Inject constructor(
    val firestore: FirebaseFirestore,
    val userRepository: UserRepository,
    val preferenceManager: PreferenceManager,
    ) {

    suspend fun verifyUser(id: String, password: String): Result<String?> {
        val idPassRef = firestore.collection("game_room_ids")
        val documentId = "${id.uppercase(Locale.getDefault())}$password"

        return try {
            // Retrieve the document
            val documentSnapshot = idPassRef.document(documentId).get().await()
            if (documentSnapshot.exists()) {
                // Convert to Keys com.tcs.games.score4.data class
                val keys = documentSnapshot.toObject(GameKeys::class.java)
                if (keys != null) {
                    if(keys.id == id&&keys.pass==password){
                        Result.success(keys.gameId)
                    }else{
                        Result.success(null)
                    }
                } else {
                    Result.failure(Exception("Document found but failed to parse"))
                }
            } else {
                Result.failure(Exception("Document does not exist"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}